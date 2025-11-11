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
import com.marketplace.model.User;
import com.marketplace.model.UserDTO;
import com.marketplace.model.VerificationCode;
import com.marketplace.repository.UserRepository;
import com.marketplace.repository.VerificationCodeRepository;
import com.marketplace.security.TokenUtils;
import com.marketplace.service.CloudinaryService;
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
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Credential credential) {
    	Map<String, String> response = new HashMap<>();
        try {
        	
        	// Verificar que exista el usuario
            Optional<User> optionalUser = userService.getUserByEmail(credential.getEmail());
            if (optionalUser.isEmpty()) {
                response.put("error", "Usuario no encontrado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            User user = optionalUser.get();

            // Verificar que el usuario esté activo/verificado
            if (user.getEstado_cuenta().equals("pendiente")) {
                response.put("error", "Cuenta no verificada. Revisa tu correo para activarla.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credential.getEmail(), credential.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority())
                    .orElse("ROLE_USER");

            String token = TokenUtils.generateToken(userDetails.getUsername(), role, user.getFoto(),user.getName(),user.getId());

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

}