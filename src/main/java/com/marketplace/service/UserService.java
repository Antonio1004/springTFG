package com.marketplace.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.marketplace.model.PasswordResetToken;
import com.marketplace.model.User;
import com.marketplace.model.UserAdminDTO;
import com.marketplace.model.UserDTO;
import com.marketplace.model.VerificationCode;
import com.marketplace.repository.PasswordResetTokenRepository;
import com.marketplace.repository.UserRepository;
import com.marketplace.repository.VerificationCodeRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;




@Service
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private VerificationCodeRepository codeRepository;
    
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Autowired
    CloudinaryService cloudinaryService;

    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Listar todos los usuarios
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Obtener usuario por ID
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
 // Devuelve un DTO en lugar de la entidad completa
    public UserDTO getUserDTOById(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    UserDTO dto = new UserDTO();
                    dto.setName(user.getName());
                    dto.setEmail(user.getEmail());
                    dto.setPassword(user.getPassword()); // si no quieres exponer la password, omite esta línea
                    dto.setDireccion(user.getDireccion());
                    dto.setFoto(user.getFoto());
                    return dto;
                })
                .orElse(null);
    }
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
  
    
    
    // Registrar usuario nuevo
    public UserDTO saveUser(UserDTO userDto) {

        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email.");
        }

        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setName(userDto.getName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setDireccion(userDto.getDireccion());
        user.setRole("ROLE_USER");
        user.setFoto(userDto.getFoto());
        user.setTipo_bloqueo("ninguno");
        

        User saved = userRepository.save(user);
        
        // Generar token de verificación
        String code = UUID.randomUUID().toString();
        VerificationCode verificationCode = new VerificationCode(
            code,
            saved,
            LocalDateTime.now().plusHours(24)
        );
        codeRepository.save(verificationCode);

        // Enviar correo
        sendVerificationEmail(saved, code);
        return entityToDto(saved);
    }

    // Actualizar usuario (sin permitir cambiar el rol)
    public UserDTO updateUser(Long id, UserDTO userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setEmail(userDto.getEmail());

        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        // NO se actualiza el role desde aquí

        User updated = userRepository.save(user);
        return entityToDto(updated);
    }

    // Actualizar datos completos (incluyendo rol) — para Admin
    public UserDTO adminUpdateUser(Long id, UserDTO userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        user.setEmail(userDto.getEmail());

        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }


        User updated = userRepository.save(user);
        return entityToDto(updated);
    }

    // Borrar usuario
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Convertir User a UserDTO
    private UserDTO entityToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setName(user.getName());
        dto.setDireccion(user.getDireccion());
        dto.setFoto(user.getFoto());
        return dto;
    }

    // Para autenticación JWT
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),   // correcto: email
                user.getPassword(),   // correcto: password
                List.of(new SimpleGrantedAuthority(user.getRole())) // correcto: authorities
        );
    }
        
       
        private void sendVerificationEmail(User user, String token) {
            try {
                MimeMessage email = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(email, true, "UTF-8");

                String subject = "Verifica tu cuenta en ReVende";
                String verificationUrl = "https://angular-tfg.vercel.app/verify?token=" + token;

                String message = "¡Hola " + user.getEmail() + "!\n\n" +
                        "Gracias por registrarte en ReVende.\n" +
                        "Haz clic en el siguiente enlace para activar tu cuenta:\n" +
                        verificationUrl + "\n\n" +
                        "Este enlace expirará en 24 horas.";

                helper.setTo(user.getEmail());
                helper.setSubject(subject);
                helper.setText(message, true);

                mailSender.send(email);

            } catch (MessagingException e) {
                throw new RuntimeException("Error al enviar el correo de verificación", e);
            }
        }

        
        
     // ------------------- RECUPERACIÓN DE CONTRASEÑA -------------------

        public void createPasswordResetToken(String email) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario con ese correo no encontrado"));

            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken(token, user, LocalDateTime.now().plusHours(2));
            passwordResetTokenRepository.save(resetToken);

            sendPasswordResetEmail(user, token);
        }
        
        private void sendPasswordResetEmail(User user, String token) {
            try {
                MimeMessage email = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(email, true, "UTF-8");

                String subject = "Recupera tu contraseña - ReVende";
                String resetUrl = "https://angular-tfg.vercel.app/reset-password?token=" + token;

                String message = "¡Hola " + user.getEmail() + "!\n\n" +
                        "Recibimos una solicitud para cambiar tu contraseña.\n" +
                        "Haz clic en el siguiente enlace para establecer una nueva contraseña:\n" +
                        resetUrl + "\n\n" +
                        "Este enlace expirará en 24 horas.";
                helper.setTo(user.getEmail());
                helper.setSubject(subject);
                helper.setText(message, true);

                mailSender.send(email);

            } catch (MessagingException e) {
                throw new RuntimeException("Error enviando correo de recuperación", e);
            }
        }


       
        public void resetPassword(String token, String newPassword) {
            PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

            if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
                passwordResetTokenRepository.delete(resetToken);
                throw new IllegalArgumentException("Token expirado");
            }

            User user = resetToken.getUser();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            passwordResetTokenRepository.delete(resetToken);
        }
        
        public void changePassword(Long userId, String newPassword) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
        
        
        public UserDTO editUserProfile(Long idUser, String name, String direccion, MultipartFile file, boolean deleteFoto) throws IOException {
            User user = userRepository.findById(idUser)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            if (name != null) user.setName(name);
            if (direccion != null) user.setDireccion(direccion);

            if (file != null && !file.isEmpty()) {
                // Subir nueva foto a Cloudinary
                Map uploadResult = cloudinaryService.upload(file);
                String imageUrl = uploadResult.get("secure_url").toString();
                user.setFoto(imageUrl);
            } else if (deleteFoto) {
                // Eliminar foto de perfil
                user.setFoto(null);
            }

            User saved = userRepository.save(user);
            return entityToDto(saved);
        }
        public List<UserDTO> getAllUsersDTO() {
            return userRepository.findAll().stream()
                    .map(user -> new UserDTO(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            null,                // NO enviamos contraseña
                            user.getDireccion(),
                            user.getFoto(),
                            user.getTipo_bloqueo(),
                            user.getFecha_fin(),
                            user.getRole()
                    ))
                    .toList();
        }
        public void blockUser(Long userId, String tipoBloqueo, int dias, int horas) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            if (tipoBloqueo == null || tipoBloqueo.isBlank()) {
                throw new IllegalArgumentException("Debe especificarse el tipo de bloqueo");
            }

            LocalDateTime fechaFin = null;

            if ("permanente".equalsIgnoreCase(tipoBloqueo)) {
                user.setTipo_bloqueo("permanente");
                user.setFecha_fin(null);

            } else if ("temporal".equalsIgnoreCase(tipoBloqueo)) {
                if (dias <= 0 && horas <= 0) {
                    throw new IllegalArgumentException("Debe especificar al menos días o horas para un bloqueo temporal");
                }
                LocalDateTime ahora = LocalDateTime.now();
                fechaFin = ahora.plusDays(dias).plusHours(horas);

                user.setTipo_bloqueo("temporal");
                user.setFecha_fin(fechaFin);

            } else {
                throw new IllegalArgumentException("Tipo de bloqueo no válido. Debe ser 'temporal' o 'permanente'");
            }

            // Guardar cambios
            userRepository.save(user);

            // Enviar correo de notificación
            enviarCorreoBloqueo(user, tipoBloqueo, fechaFin);
        }
        private void enviarCorreoBloqueo(User user, String tipoBloqueo, LocalDateTime fechaFin) {
            try {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                String subject = "Notificación de bloqueo de cuenta - ReVende";

                String cuerpo;

                if ("permanente".equalsIgnoreCase(tipoBloqueo)) {
                	cuerpo = "Hola " + user.getName() + ",\n\n" +
                              "Su cuenta ha sido bloqueada permanentemente. " +
                              "Si cree que esto es un error, contacte con el soporte.";
                } else {
                	cuerpo = "Hola " + user.getName() + ",\n\n" +
                              "Su cuenta estará bloqueada por incumplir nuestra política hasta " + fechaFin.toString() + ". " +
                              "Después de esta fecha, podrá acceder nuevamente a su cuenta.";
                }

                helper.setTo(user.getEmail());
                helper.setSubject(subject);
                helper.setText(cuerpo, true);

                mailSender.send(message);

            } catch (MessagingException e) {
                throw new RuntimeException("Error enviando correo de bloqueo", e);
            }
        }

      
        
        public User createAdmin(UserAdminDTO dto) {

            // Comprobación básica opcional
            if (dto.getEmail() == null || dto.getEmail().isBlank()) {
                throw new IllegalArgumentException("El email es obligatorio");
            }

            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Ya existe un usuario con este email");
            }

            User admin = new User();

            admin.setName(dto.getName());
            admin.setEmail(dto.getEmail());
            admin.setPassword(passwordEncoder.encode(dto.getPassword()));
            admin.setRole("ROLE_ADMIN");
            admin.setEstado_cuenta("activa");


            return userRepository.save(admin);
        }
        
     // Eliminar un administrador por ID
        public void deleteAdmin(Long adminId, Long currentUserId) {
            // Obtener el usuario a eliminar
            User userToDelete = userRepository.findById(adminId)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

            // Solo se pueden eliminar administradores
            if (!"ROLE_ADMIN".equals(userToDelete.getRole())) {
                throw new IllegalArgumentException("Solo se pueden eliminar usuarios administradores");
            }

            // Contar cuántos administradores hay
            long totalAdmins = userRepository.findAll().stream()
                    .filter(u -> "ROLE_ADMIN".equals(u.getRole()))
                    .count();

            if (totalAdmins <= 1) {
                throw new IllegalArgumentException("Debe haber al menos un administrador en la plataforma");
            }

            // Eliminar usuario
            userRepository.delete(userToDelete);

            // Si se eliminó a sí mismo, se puede retornar un flag al controller
            if (adminId.equals(currentUserId)) {
                throw new IllegalStateException("SELF_DELETED"); // el controller lo interpretará
            }
        }


}
       












