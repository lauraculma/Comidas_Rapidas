package com.comidarapida.service;

import com.comidarapida.model.ProductoComidaRapida;
import com.comidarapida.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public ProductoComidaRapida registrarProducto(ProductoComidaRapida p) {
        return productoRepository.save(p);
    }

    public Optional<ProductoComidaRapida> consultarProducto(Long id) {
        return productoRepository.findById(id);
    }

    public ProductoComidaRapida actualizarProducto(ProductoComidaRapida p) {
        return productoRepository.save(p);
    }

    public List<ProductoComidaRapida> listarDisponibles() {
        return productoRepository.findAll();
    }
}
