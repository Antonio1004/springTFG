package com.marketplace.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.marketplace.model.PasswordResetToken;
import com.marketplace.model.User;
import com.marketplace.model.UserDTO;
import com.marketplace.model.VerificationCode;
import com.marketplace.repository.PasswordResetTokenRepository;
import com.marketplace.repository.UserRepository;
import com.marketplace.repository.VerificationCodeRepository;




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
            String subject = "Verifica tu cuenta en ReVende";
            String verificationUrl = "http://localhost:4200/verify?token=" + token;

            String message = "¡Hola " + user.getEmail() + "!\n\n" +
                    "Gracias por registrarte en ReVende.\n" +
                    "Haz clic en el siguiente enlace para activar tu cuenta:\n" +
                    verificationUrl + "\n\n" +
                    "Este enlace expirará en 24 horas.";

            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(user.getEmail());
            email.setSubject(subject);
            email.setText(message);

            mailSender.send(email);
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
            String subject = "Recupera tu contraseña - ReVende";
            String resetUrl = "http://localhost:4200/reset-password?token=" + token;

            String message = "¡Hola " + user.getEmail() + "!\n\n" +
                    "Recibimos una solicitud para cambiar tu contraseña.\n" +
                    "Haz clic en el siguiente enlace para establecer una nueva contraseña:\n" +
                    resetUrl + "\n\n" +
                    "Este enlace expirará en 24 horas.";

            SimpleMailMessage email = new SimpleMailMessage();
            email.setTo(user.getEmail());
            email.setSubject(subject);
            email.setText(message);

            mailSender.send(email);
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
}
       












