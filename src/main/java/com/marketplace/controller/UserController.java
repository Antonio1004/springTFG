package com.marketplace.controller;



import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.marketplace.model.Credential;
import com.marketplace.model.ErrorResponse;
import com.marketplace.model.Producto;
import com.marketplace.model.User;
import com.marketplace.model.UserAdminDTO;
import com.marketplace.model.UserDTO;
import com.marketplace.model.VerificationCode;
import com.marketplace.repository.UserRepository;
import com.marketplace.repository.VerificationCodeRepository;
import com.marketplace.security.TokenUtils;
import com.marketplace.service.CloudinaryService;
import com.marketplace.service.ProductoService;
import com.marketplace.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuarios", description = "Controlador para la gestión de usuarios en ReVende")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private VerificationCodeRepository tokenRepository;
    
    @Autowired
    CloudinaryService cloudinaryService;

    @Autowired
    private AuthenticationManager authenticationManager;

    // ---------- Registro ----------
    @Operation(summary = "Registrar usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existente")
    })
    @PostMapping("/registrar")
    public ResponseEntity<?> registerUser(
            @ModelAttribute UserDTO regDto,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Validación: correo electrónico duplicado
            if (userService.getUserByEmail(regDto.getEmail()).isPresent()) {
                response.put("error", "Ya existe un usuario registrado con ese correo electrónico.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Subir foto si existe
            String imageUrl = null;
            if (file != null && !file.isEmpty()) {
                Map uploadResult = cloudinaryService.upload(file);
                imageUrl = uploadResult.get("secure_url").toString();
                regDto.setFoto(imageUrl); // asignar URL de la foto al DTO
            }

            // Guardar usuario
            UserDTO saved = userService.saveUser(regDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), 400));
        } catch (Exception e) {
            response.put("error", "Error al registrar el usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


 // ---------- Login ----------
    @Operation(summary = "Login de usuario", description = "Autentica al usuario y devuelve un token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "403", description = "Cuenta bloqueada permanentemente"),
        @ApiResponse(responseCode = "423", description = "Cuenta bloqueada temporalmente")
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Credential credential) {
        
        Map<String, String> response = new HashMap<>();

        try {

            // Buscar usuario por email
            Optional<User> optionalUser = userService.getUserByEmail(credential.getEmail());
            if (optionalUser.isEmpty()) {
                response.put("error", "Usuario no encontrado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            User user = optionalUser.get();

            // Verificar estado de cuenta
            if (user.getEstado_cuenta().equals("pendiente")) {
                response.put("error", "Cuenta no verificada. Revisa tu correo.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // --------- VERIFICACIÓN DE BLOQUEO ---------

            // 1. Bloqueo permanente
            if ("permanente".equalsIgnoreCase(user.getTipo_bloqueo())) {
                response.put("error", "Cuenta bloqueada permanentemente");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // 2. Bloqueo temporal
            if ("temporal".equalsIgnoreCase(user.getTipo_bloqueo())) {

                LocalDateTime ahora = LocalDateTime.now();

                // Fecha fin aún no ha pasado → SIGUE BLOQUEADO
                if (user.getFecha_fin() != null && user.getFecha_fin().isAfter(ahora)) {
                    response.put("error", "Cuenta bloqueada temporalmente. Revise su correo para más información");
                    response.put("hasta", user.getFecha_fin().toString());
                    return ResponseEntity.status(HttpStatus.LOCKED).body(response); // 423
                }

                // Si la fecha fin ya pasó → DESBLOQUEAR AUTOMÁTICAMENTE
                if (user.getFecha_fin() != null && user.getFecha_fin().isBefore(ahora)) {
                    user.setTipo_bloqueo("ninguno");
                    user.setFecha_fin(null);
                    userRepository.save(user);
                }
            }

            // --------- AUTENTICACIÓN NORMAL ---------

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credential.getEmail(), credential.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority())
                    .orElse("ROLE_USER");

            String token = TokenUtils.generateToken(
                    userDetails.getUsername(),
                    role,
                    user.getFoto(),
                    user.getName(),
                    user.getId()
            );

            return ResponseEntity.ok(Map.of("token", token));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Contraseña Incorrecta"));
        }
    }

 // ---------------- VERIFICAR CUENTA ----------------
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Boolean>> verifyUser(@RequestParam("token") String token) {
        Map<String, Boolean> response = new HashMap<>();

        try {
            Optional<VerificationCode> optionalToken = tokenRepository.findByCode(token);
            if (optionalToken.isEmpty()) {
                response.put("verified", false);
                return ResponseEntity.badRequest().body(response);
            }

            VerificationCode verificationToken = optionalToken.get();
            User user = verificationToken.getUser();

            if (user == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                // Token expirado o inválido
                tokenRepository.delete(verificationToken);
                response.put("verified", false);
                return ResponseEntity.badRequest().body(response);
            }

            // Si el usuario aún no estaba activo, activarlo
            if (user.getEstado_cuenta().equals("pendiente")) {
                user.setEstado_cuenta("activa");
                userRepository.save(user);

            }

            // Eliminar el token después de verificar
            tokenRepository.delete(verificationToken);

            response.put("verified", true);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            response.put("verified", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @Operation(summary = "Listar todos los usuarios (solo admin)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista devuelta correctamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    
    @GetMapping("/list")
    public ResponseEntity<List<UserDTO>> listAllUsers() {
        List<UserDTO> usuarios = userService.getAllUsersDTO();
        return ResponseEntity.ok(usuarios);
    }

    
 // ---------------- RECUPERAR CONTRASEÑA ----------------
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        try {
            userService.createPasswordResetToken(email);
            return ResponseEntity.ok(Map.of("message", "Correo de recuperación enviado"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), 400));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("token") String token, @RequestParam("password") String password) {
        try {
            userService.resetPassword(token, password);
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), 400));
        }
    }
    
    @GetMapping("/get/{userId}")
    public ResponseEntity<UserDTO> obtenerUsuario(@PathVariable Long userId) {
        UserDTO userDTO = userService.getUserDTOById(userId);
        if (userDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userDTO);
    }
    
    @GetMapping("/vendedores-comprados/{compradorId}")
    public ResponseEntity<List<UserDTO>> getVendedoresQueHeComprado(@PathVariable Long compradorId) {

        // Obtener todos los productos comprados por el usuario
        List<Producto> productosComprados = productoService.getAllComprasByCompradorId(compradorId);

        // Extraer vendedores únicos
        List<UserDTO> vendedores = productosComprados.stream()
                .map(Producto::getVendedor)
                .filter(v -> v != null)
                .distinct()
                .map(v -> new UserDTO(
                        v.getId(),
                        v.getName(),
                        v.getEmail(),
                        null,           // password NO SE ENVÍA
                        v.getDireccion(),
                        v.getFoto(),
                        v.getTipo_bloqueo(),
                        v.getFecha_fin(),
                        v.getRole()
                ))
                .toList();

        return ResponseEntity.ok(vendedores);
    }
    @Operation(summary = "Cambiar contraseña de usuario logueado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Usuario no encontrado o datos inválidos")
    })
    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestParam Long idUser,
            @RequestParam String password) {

        try {
            userService.changePassword(idUser, password);
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ocurrió un error al actualizar la contraseña"));
        }
    }

    
    
    @PatchMapping("/edit-profile/{idUser}")
    public ResponseEntity<?> editUserProfile(
            @PathVariable Long idUser,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String direccion,
            @RequestParam(required = false) MultipartFile file,
            @RequestParam(required = false) Boolean deleteFoto
    ) {
        try {
            UserDTO updatedUser = userService.editUserProfile(idUser, name, direccion, file, deleteFoto != null && deleteFoto);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al actualizar perfil"));
        }
    }
    
    @PatchMapping("/block/{userId}")
    public ResponseEntity<?> blockUser(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> body) {

        try {
            String tipoBloqueo = (String) body.get("tipo_bloqueo");
            Integer dias = body.get("dias") != null ? (Integer) body.get("dias") : 0;
            Integer horas = body.get("horas") != null ? (Integer) body.get("horas") : 0;

            userService.blockUser(userId, tipoBloqueo, dias, horas);

            return ResponseEntity.ok(Map.of("message", "Usuario bloqueado correctamente"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error al bloquear usuario"));
        }
    }
    @PostMapping("/createAdmin")
    public ResponseEntity<?> createAdmin(@RequestBody UserAdminDTO dto) {
        try {
            User newAdmin = userService.createAdmin(dto);
            return ResponseEntity.ok(Map.of(
                    "message", "Administrador creado correctamente",
                    "adminId", newAdmin.getId()
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al crear administrador"));
        }
    }

}