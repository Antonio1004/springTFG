package com.marketplace.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.marketplace.model.Mensaje;
import com.marketplace.model.MensajeDTO;
import com.marketplace.model.User;
import com.marketplace.model.UserDTO;
import com.marketplace.repository.MensajeRepository;
import com.marketplace.repository.UserRepository;

@Service
public class MensajeService {
	 @Autowired
	    private MensajeRepository mensajeRepository;

	    @Autowired
	    private UserService userService;

	    @Autowired
	    private JavaMailSender mailSender;
	    
	    public String enviarMensaje(MensajeDTO mensajeDTO) {
	        // Verificar que el comprador existe
	        User comprador = userService.getUserById(mensajeDTO.getIdComprador());
	        if (comprador == null) {
	            throw new RuntimeException("El comprador no existe");
	        }

	        // Verificar que el vendedor existe
	        User vendedor = userService.getUserById(mensajeDTO.getIdVendedor());
	        if (vendedor == null) {
	            throw new RuntimeException("El vendedor no existe");
	        }

	        // Crear el objeto Mensaje
	        Mensaje mensaje = new Mensaje();
	        mensaje.setEmisor(comprador);
	        mensaje.setReceptor(vendedor);
	        mensaje.setMensaje(mensajeDTO.getMensaje());
	        mensaje.setLeido(false);
	        mensaje.setFecha(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

	        // Guardar el mensaje en la base de datos
	        mensajeRepository.save(mensaje);

	        // Enviar correo al vendedor
	        try {
	            enviarCorreoVendedor(vendedor, mensaje.getId());
	        } catch (Exception e) {
	            e.printStackTrace();
	            // El mensaje sigue guardado aunque falle el correo
	            return "Mensaje guardado, pero fallo al enviar correo: " + e.getMessage();
	        }

	        return "Mensaje enviado correctamente y correo notificado";
	    }

	    private void enviarCorreoVendedor(User vendedor, Long mensajeId) {
	        String subject = "Nuevo mensaje en ReVende";
	        String url = "http://localhost:4200/ver-mensaje/" + mensajeId;

	        String text = "¡Hola " + vendedor.getName() + "!\n\n" +
	                      "Has recibido un nuevo mensaje.\n" +
	                      "Haz clic en el siguiente enlace para leerlo:\n" +
	                      url + "\n\n" +
	                      "Cuando lo abras, se marcará como leído automáticamente.";

	        SimpleMailMessage email = new SimpleMailMessage();
	        email.setTo(vendedor.getEmail());
	        email.setSubject(subject);
	        email.setText(text);

	        // Este es el paso clave: usar JavaMailSender con App Password
	        mailSender.send(email);
	    }
	    
	    // Método para marcar mensaje como leído
	    public Mensaje marcarComoLeido(Long mensajeId) {
	        Mensaje mensaje = mensajeRepository.findById(mensajeId)
	                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
	        mensaje.setLeido(true);
	        return mensajeRepository.save(mensaje);
	    }
	    
	    public List<UserDTO> getConversaciones(Long userId) {
	        List<Long> ids = new ArrayList<>();
	        ids.addAll(mensajeRepository.findReceptoresByEmisor(userId));
	        ids.addAll(mensajeRepository.findEmisoresByReceptor(userId));

	        // Eliminar duplicados
	        Set<Long> partnerIds = new HashSet<>(ids);

	        return partnerIds.stream()
	                .map(id -> userService.getUserById(id))
	                .filter(Objects::nonNull)
	                .map(u -> new UserDTO(u.getName(), u.getEmail(), null, u.getDireccion(), u.getFoto()))
	                .collect(Collectors.toList());
	    }


	    

}
