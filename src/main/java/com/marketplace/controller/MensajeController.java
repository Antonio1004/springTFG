package com.marketplace.controller;

import java.util.List;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.marketplace.model.Mensaje;
import com.marketplace.model.MensajeDTO;
import com.marketplace.model.UserDTO;
import com.marketplace.service.MensajeService;


@RestController
@RequestMapping("/mensajes")

public class MensajeController {
	@Autowired
    private MensajeService mensajeService;

	@PostMapping("/enviar")
    public ResponseEntity<Void> enviarMensaje(
            @RequestParam String mensaje,
            @RequestParam Long idComprador,
            @RequestParam Long idVendedor) {

        // Crear DTO internamente
		System.out.println(mensaje);
		System.out.println(idComprador);
		System.out.println(idVendedor);

        MensajeDTO dto = new MensajeDTO();
        dto.setMensaje(mensaje);
        dto.setIdComprador(idComprador);
        dto.setIdVendedor(idVendedor);

        // Aquí se puede hacer toda la lógica de guardar en BD y enviar correo
        mensajeService.enviarMensaje(dto);

        // No devolvemos nada, solo un 200 OK
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ver/{mensajeId}")
    public ResponseEntity<Mensaje> verMensaje(@PathVariable Long mensajeId) {
        try {
            Mensaje mensaje = mensajeService.marcarComoLeido(mensajeId);
            return ResponseEntity.ok(mensaje);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).body(null);
        }
    }
    
    @GetMapping("/mis-conversaciones/{userId}")
    public List<UserDTO> getMisConversaciones(@PathVariable Long userId) {
        return mensajeService.getConversaciones(userId);
    }



}
