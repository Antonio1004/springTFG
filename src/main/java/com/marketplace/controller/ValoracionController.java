package com.marketplace.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.marketplace.model.MediaValoracionDTO;
import com.marketplace.model.Valoracion;
import com.marketplace.model.ValoracionDTO;
import com.marketplace.service.ValoracionService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/valoracion")
public class ValoracionController {

    private final ValoracionService valoracionService;

    public ValoracionController(ValoracionService valoracionService) {
        this.valoracionService = valoracionService;
    }

    // Endpoint para comprobar si se puede valorar
    @GetMapping("/puede-valorar/{idComprador}/{idVendedor}")
    public ResponseEntity<Boolean> puedeValorar(
            @PathVariable Long idComprador,
            @PathVariable Long idVendedor) {

        boolean resultado = valoracionService.puedeValorar(idComprador, idVendedor);
        return ResponseEntity.ok(resultado);
    }
    
 //  Guardar una nueva valoración
    @PostMapping("/guardar/{stars}/{comment}/{idComprador}/{idVendedor}")
    public ResponseEntity<?> guardarValoracion(
            @PathVariable Integer stars,
            @PathVariable String comment,
            @PathVariable Long idComprador,
            @PathVariable Long idVendedor) {

        try {
            // Crear el DTO internamente con los valores de la ruta
            ValoracionDTO dto = new ValoracionDTO(stars, comment, idComprador, idVendedor);

            Valoracion valoracionGuardada = valoracionService.guardarValoracion(dto);

            ValoracionDTO respuestaDTO = new ValoracionDTO(
                valoracionGuardada.getStars(),
                valoracionGuardada.getComment(),
                valoracionGuardada.getComprador().getId(),
                valoracionGuardada.getVendedor().getId()
            );

            return ResponseEntity.ok(respuestaDTO);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/existente/{idComprador}/{idVendedor}")
    public ResponseEntity<ValoracionDTO> getValoracionExistente(
            @PathVariable Long idComprador,
            @PathVariable Long idVendedor) {
        Valoracion valoracion = valoracionService.obtenerValoracionExistente(idComprador, idVendedor);
        if (valoracion != null) {
            ValoracionDTO dto = new ValoracionDTO(
                valoracion.getStars(),
                valoracion.getComment(),
                valoracion.getComprador().getId(),
                valoracion.getVendedor().getId()
            );
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.ok(null); 
        }
    }
    
    @GetMapping("/media/{idVendedor}")
    public ResponseEntity<?> obtenerMediaValoraciones(@PathVariable Long idVendedor) {
        try {
            MediaValoracionDTO media = valoracionService.obtenerMediaValoracionesVendedor(idVendedor);
            return ResponseEntity.ok(media);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
