package com.marketplace.controller;

import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.marketplace.model.ConversacionResumenDTO;
import com.marketplace.model.MensajeDTO;
import com.marketplace.service.MensajeService;

@RestController
@RequestMapping("/mensajes")
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    // 🔹 Enviar mensaje (ahora con producto)
    @PostMapping("/enviar")
    public ResponseEntity<Void> enviarMensaje(
            @RequestParam String mensaje,
            @RequestParam Long idEmisor,
            @RequestParam Long idReceptor,
            @RequestParam Long idProducto) {

        MensajeDTO dto = new MensajeDTO();
        dto.setMensaje(mensaje);
        dto.setIdEmisor(idEmisor);
        dto.setIdReceptor(idReceptor);
        dto.setProductoId(idProducto);

        mensajeService.enviarMensaje(dto);
        return ResponseEntity.ok().build();
    }

    // 🔹 Ver un mensaje individual (marcar como leído), incluyendo nombre de producto
    @GetMapping("/ver/{mensajeId}")
    public ResponseEntity<MensajeDTO> verMensaje(
            @PathVariable Long mensajeId) {
        try {
            MensajeDTO dto = mensajeService.marcarComoLeido(mensajeId);
            // Aquí el nombre del producto ya viene del DTO modificado en el servicio
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(null);
        }
    }

    // 🔹 Obtener lista de conversaciones (solo tabla resumen)
    @GetMapping("/mis-conversaciones/{userId}")
    public List<ConversacionResumenDTO> getMisConversaciones(@PathVariable Long userId) {
        return mensajeService.getConversaciones(userId);
    }

    // 🔹 Obtener conversación completa entre dos usuarios sobre un producto
    @GetMapping("/conversacion/{user1Id}/{user2Id}/{productoId}")
    public ResponseEntity<List<MensajeDTO>> getConversacion(
            @PathVariable Long user1Id,
            @PathVariable Long user2Id,
            @PathVariable Long productoId) {

        List<MensajeDTO> mensajes = mensajeService.getConversacion(user1Id, user2Id, productoId);
        return ResponseEntity.ok(mensajes);
    }
}
