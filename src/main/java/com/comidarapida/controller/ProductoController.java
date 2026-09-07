package com.comidarapida.controller;

import com.comidarapida.dto.ProductoDto;
import com.comidarapida.model.ProductoComidaRapida;
import com.comidarapida.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping
    public ResponseEntity<ProductoDto> crearProducto(@RequestBody ProductoDto dto) {
        ProductoComidaRapida p = new ProductoComidaRapida();
        p.setCodigo(dto.codigo);
        p.setNombre(dto.nombre);
        p.setPrecio(dto.precio);
        p.setActivo(dto.activo);
        ProductoComidaRapida creado = productoService.registrarProducto(p);
        dto.id = creado.getIdProducto();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/disponibles")
    public ResponseEntity<?> listarDisponibles() {
        var list = productoService.listarDisponibles().stream().map(p -> {
            ProductoDto dto = new ProductoDto();
            dto.id = p.getIdProducto();
            dto.codigo = p.getCodigo();
            dto.nombre = p.getNombre();
            dto.precio = p.getPrecio();
            dto.activo = p.isActivo();
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }
}
