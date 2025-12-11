package com.marketplace.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.marketplace.model.ConversacionResumenDTO;
import com.marketplace.model.Mensaje;
import com.marketplace.model.MensajeDTO;
import com.marketplace.model.Producto;
import com.marketplace.model.User;
import com.marketplace.model.UserDTO;
import com.marketplace.repository.MensajeRepository;
import com.marketplace.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MensajeService {
	 @Autowired
	    private MensajeRepository mensajeRepository;

	    @Autowired
	    private UserService userService;
	    
	    @Autowired
	    private ProductoService productoService;

	    @Autowired
	    private JavaMailSender mailSender;
	    
	    public String enviarMensaje(MensajeDTO mensajeDTO) {

	        User comprador = userService.getUserById(mensajeDTO.getIdEmisor());
	        if (comprador == null) {
	            throw new RuntimeException("El comprador no existe");
	        }

	        User vendedor = userService.getUserById(mensajeDTO.getIdReceptor());
	        if (vendedor == null) {
	            throw new RuntimeException("El vendedor no existe");
	        }

	        Producto producto = productoService.getProductoById(mensajeDTO.getProductoId());
	        if (producto == null) {
	            throw new RuntimeException("El producto no existe");
	        }

	        Mensaje mensaje = new Mensaje();
	        mensaje.setEmisor(comprador);
	        mensaje.setReceptor(vendedor);
	        mensaje.setProducto(producto);
	        mensaje.setMensaje(mensajeDTO.getMensaje());
	        mensaje.setLeido(false);
	        mensaje.setFecha(LocalDateTime.now());

	        mensajeRepository.save(mensaje);

	        try {
	            enviarCorreoVendedor(vendedor, mensaje.getId());
	        } catch (Exception e) {
	            return "Mensaje guardado, pero fallo de correo";
	        }

	        return "Mensaje enviado correctamente";
	    }
	    private void enviarCorreoVendedor(User vendedor, Long mensajeId) {
	        try {
	            MimeMessage message = mailSender.createMimeMessage();
	            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

	            String subject = "Nuevo mensaje en ReVende";
	            String url = "https://angular-tfg.vercel.app/ver-mensaje/" + mensajeId;

	            String text = "¡Hola " + vendedor.getName() + "!\n\n" +
	                      "Has recibido un nuevo mensaje.\n" +
	                      "Haz clic en el siguiente enlace para leerlo:\n" +
	                      url + "\n\n" +
	                      "Cuando lo abras, se marcará como leído automáticamente.";
	            helper.setTo(vendedor.getEmail());
	            helper.setSubject(subject);
	            helper.setText(text, true);

	            mailSender.send(message);

	        } catch (MessagingException e) {
	            throw new RuntimeException("Error enviando correo al vendedor", e);
	        }
	    }
	   
	    
	    public MensajeDTO marcarComoLeido(Long mensajeId) {
	        Mensaje mensaje = mensajeRepository.findById(mensajeId)
	                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
	        
	        // Marcamos como leído
	        mensaje.setLeido(true);
	        mensajeRepository.save(mensaje);

	        // Creamos el DTO usando el constructor completo
	        MensajeDTO dto = new MensajeDTO(
	            mensaje.getEmisor().getId(),
	            mensaje.getReceptor().getId(),
	            mensaje.getProducto().getId(),         // productoId
	            mensaje.getProducto().getTitle(),      // nombreProducto
	            mensaje.getMensaje(),
	            mensaje.getFecha(),
	            mensaje.isLeido()
	        );

	        return dto;
	    }


	    public List<ConversacionResumenDTO> getConversaciones(Long userId) {
	        List<Mensaje> mensajes = mensajeRepository.findMensajesRelacionados(userId);

	        Map<String, ConversacionResumenDTO> conversacionesMap = new HashMap<>();

	        for (Mensaje m : mensajes) {
	            Long otroUsuarioId = m.getEmisor().getId().equals(userId) ? m.getReceptor().getId() : m.getEmisor().getId();
	            Long productoId = m.getProducto().getId();
	            String clave = otroUsuarioId + "-" + productoId; // clave única por usuario + producto

	            // Si aún no existe, agregamos la conversación
	            conversacionesMap.putIfAbsent(clave, new ConversacionResumenDTO(
	                    otroUsuarioId,
	                    productoId,
	                    m.getEmisor().getId().equals(userId) ? m.getReceptor().getName() : m.getEmisor().getName(),
	                    m.getProducto().getTitle()
	            ));
	        }

	        return new ArrayList<>(conversacionesMap.values());
	    }

	    

	    public List<MensajeDTO> getConversacion(Long user1Id, Long user2Id, Long productoId) {

	        List<Mensaje> mensajes = mensajeRepository
	                .obtenerConversacionEntreUsuariosYProducto(user1Id, user2Id, productoId);

	        return mensajes.stream()
	                .sorted(Comparator.comparing(Mensaje::getFecha))
	                .map(m -> new MensajeDTO(
	                        m.getEmisor().getId(),
	                        m.getReceptor().getId(),
	                        m.getProducto().getId(),
	                        m.getProducto().getTitle(), 
	                        m.getMensaje(),             
	                        m.getFecha(),
	                        m.isLeido()
	                ))
	                .collect(Collectors.toList());

	    }

}
