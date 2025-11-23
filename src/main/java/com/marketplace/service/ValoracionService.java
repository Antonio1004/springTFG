package com.marketplace.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marketplace.model.MediaValoracionDTO;
import com.marketplace.model.User;
import com.marketplace.model.Valoracion;
import com.marketplace.model.ValoracionDTO;
import com.marketplace.repository.ProductoRepository;
import com.marketplace.repository.UserRepository;
import com.marketplace.repository.ValoracionRepository;

@Service
public class ValoracionService {
	
	@Autowired
	private ProductoRepository productoRepository;
	
	 @Autowired
	 private ValoracionRepository valoracionRepository;

	 @Autowired
	 private UserService userService;
    public ValoracionService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }
   


        @Autowired
        private UserRepository userRepository;

        public Valoracion guardarValoracion(ValoracionDTO dto) {
            // Primero obtenemos los usuarios
            User comprador = userRepository.findById(dto.getIdComprador())
                    .orElseThrow(() -> new RuntimeException("Comprador no encontrado"));
            User vendedor = userRepository.findById(dto.getIdVendedor())
                    .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));

            // Buscamos si ya existe una valoración entre estos dos
            Valoracion valoracionExistente = valoracionRepository.findByCompradorIdAndVendedorId(
                    dto.getIdComprador(), dto.getIdVendedor());

            if (valoracionExistente != null) {
                // Si existe, actualizamos los campos
                valoracionExistente.setStars(dto.getStars());
                valoracionExistente.setComment(dto.getComment());
                valoracionExistente.setDate(LocalDateTime.now());
                return valoracionRepository.save(valoracionExistente);
            }

            // Si no existe, creamos una nueva
            Valoracion valoracion = new Valoracion();
            valoracion.setStars(dto.getStars());
            valoracion.setComment(dto.getComment());
            valoracion.setDate(LocalDateTime.now());
            valoracion.setComprador(comprador);
            valoracion.setVendedor(vendedor);

            return valoracionRepository.save(valoracion);
        }

    



    public boolean puedeValorar(Long idComprador, Long idVendedor) {
        return productoRepository.existsByVendedorIdAndCompradorId(idVendedor, idComprador);
    }
    
    public Valoracion obtenerValoracionExistente(Long idComprador, Long idVendedor) {
        return valoracionRepository.findByCompradorIdAndVendedorId(idComprador, idVendedor);
    }
    
    
    
    public MediaValoracionDTO obtenerMediaValoracionesVendedor(Long idVendedor) {
        List<Valoracion> valoraciones = valoracionRepository.findAllByVendedorId(idVendedor);

        if (valoraciones.isEmpty()) {
            return new MediaValoracionDTO(0.0, 0L);
        }

        double suma = valoraciones.stream()
                                  .mapToInt(Valoracion::getStars)
                                  .sum();
        double media = suma / valoraciones.size();
        return new MediaValoracionDTO(media, (long) valoraciones.size());
    }

}
