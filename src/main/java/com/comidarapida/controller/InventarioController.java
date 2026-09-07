package com.comidarapida.controller;

import com.comidarapida.dto.MateriaPrimaDto;
import com.comidarapida.model.EncargadoInventario;
import com.comidarapida.model.MateriaPrima;
import com.comidarapida.model.MovimientoInventario;
import com.comidarapida.repository.MateriaPrimaRepository;
import com.comidarapida.service.InventarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final MateriaPrimaRepository materiaRepo;
    private final InventarioService inventarioService;

    public InventarioController(MateriaPrimaRepository materiaRepo, InventarioService inventarioService) {
        this.materiaRepo = materiaRepo;
        this.inventarioService = inventarioService;
    }

    @PostMapping("/materias")
    public ResponseEntity<MateriaPrimaDto> registrarMateria(@RequestBody MateriaPrimaDto dto) {
        MateriaPrima m = new MateriaPrima();
        m.setCodigo(dto.codigo);
        m.setNombre(dto.nombre);
        m.setStockActual(dto.stockActual);
        m.setUnidadMedida(dto.unidadMedida);
        MateriaPrima saved = materiaRepo.save(m);
        dto.id = saved.getIdMateriaPrima();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/entrada")
    public ResponseEntity<?> registrarEntrada(@RequestParam Long idMateria, @RequestParam int cantidad, @RequestParam Long encargadoId) {
        MateriaPrima m = materiaRepo.findById(idMateria).orElseThrow(() -> new IllegalArgumentException("Materia prima no encontrada"));
        EncargadoInventario encargado = new EncargadoInventario();
        encargado.setIdUsuario(encargadoId);
        MovimientoInventario mov = inventarioService.registrarEntrada(m, cantidad, encargado);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/salida")
    public ResponseEntity<?> registrarSalida(@RequestParam Long idMateria, @RequestParam int cantidad, @RequestParam Long encargadoId) {
        MateriaPrima m = materiaRepo.findById(idMateria).orElseThrow(() -> new IllegalArgumentException("Materia prima no encontrada"));
        EncargadoInventario encargado = new EncargadoInventario();
        encargado.setIdUsuario(encargadoId);
        MovimientoInventario mov = inventarioService.registrarSalida(m, cantidad, encargado);
        return ResponseEntity.ok().build();
    }
}
