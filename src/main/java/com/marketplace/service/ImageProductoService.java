package com.marketplace.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.marketplace.model.ImageProducto;

import com.marketplace.repository.ImageProductoRepository;


@Service
public class ImageProductoService {
	
	@Autowired
	ImageProductoRepository imageProductoRepository;
	
	
	public ImageProducto saveImageProducto( ImageProducto imgPro) {
		return imageProductoRepository.save(imgPro);
	}
	 public void deleteById(Long id) {
	        if (imageProductoRepository.existsById(id)) {
	            imageProductoRepository.deleteById(id);
	        } else {
	            throw new RuntimeException("Imagen no encontrada con ID: " + id);
	        }
	    }
}
