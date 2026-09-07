package com.comidarapida.controller;

import com.comidarapida.dto.MateriaPrimaDto;
import com.comidarapida.model.EncargadoInventario;
import com.comidarapida.model.MateriaPrima;
import com.comidarapida.model.MovimientoInventario;
import com.comidarapida.repository.MateriaPrimaRepository;
import com.comidarapida.service.InventarioService;
import com.comidarapida.repository.MovimientoInventarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
public class InventarioController {

    private final MateriaPrimaRepository materiaRepo;
    private final MovimientoInventarioRepository movimientoRepo;
    private final InventarioService inventarioService;

    public InventarioController(MateriaPrimaRepository materiaRepo, MovimientoInventarioRepository movimientoRepo, InventarioService inventarioService) {
        this.materiaRepo = materiaRepo;
        this.movimientoRepo = movimientoRepo;
        this.inventarioService = inventarioService;
    }

    // --- ENDPOINTS REST (se mantienen como API) ---
    @ResponseBody
    @PostMapping("/api/inventario/materias")
    public ResponseEntity<MateriaPrimaDto> registrarMateriaApi(@RequestBody MateriaPrimaDto dto) {
        MateriaPrima m = new MateriaPrima();
        m.setCodigo(dto.codigo);
        m.setNombre(dto.nombre);
        m.setStockActual(dto.stockActual);
        m.setUnidadMedida(dto.unidadMedida);
        MateriaPrima saved = materiaRepo.save(m);
        dto.id = saved.getIdMateriaPrima();
        return ResponseEntity.ok(dto);
    }

    @ResponseBody
    @PostMapping("/api/inventario/entrada")
    public ResponseEntity<?> registrarEntradaApi(@RequestParam Long idMateria, @RequestParam int cantidad, @RequestParam Long encargadoId) {
        MateriaPrima m = materiaRepo.findById(idMateria).orElseThrow(() -> new IllegalArgumentException("Materia prima no encontrada"));
        EncargadoInventario encargado = new EncargadoInventario();
        encargado.setIdUsuario(encargadoId);
        MovimientoInventario mov = inventarioService.registrarEntrada(m, cantidad, encargado);
        return ResponseEntity.ok().build();
    }

    @ResponseBody
    @PostMapping("/api/inventario/salida")
    public ResponseEntity<?> registrarSalidaApi(@RequestParam Long idMateria, @RequestParam int cantidad, @RequestParam Long encargadoId) {
        MateriaPrima m = materiaRepo.findById(idMateria).orElseThrow(() -> new IllegalArgumentException("Materia prima no encontrada"));
        EncargadoInventario encargado = new EncargadoInventario();
        encargado.setIdUsuario(encargadoId);
        MovimientoInventario mov = inventarioService.registrarSalida(m, cantidad, encargado);
        return ResponseEntity.ok().build();
    }

    // --- ENDPOINTS MVC (Thymeleaf) ---
    @GetMapping("/inventario")
    public String listarInventario(Model model) {
        List<MateriaPrima> materias = materiaRepo.findAll();
        model.addAttribute("materias", materias);
        return "inventario";
    }

    @GetMapping("/inventario/nueva-materia")
    public String nuevaMateria(Model model) {
        model.addAttribute("materiaPrima", new MateriaPrima());
        return "materia-form";
    }

    @PostMapping("/inventario/materias/guardar")
    public String guardarMateria(@ModelAttribute("materiaPrima") MateriaPrima materia, RedirectAttributes redirect) {
        materiaRepo.save(materia);
        redirect.addFlashAttribute("success", "Materia prima registrada");
        return "redirect:/inventario";
    }

    @GetMapping("/inventario/movimientos/nuevo")
    public String nuevoMovimiento(Model model, @RequestParam(value = "materiaId", required = false) Long materiaId) {
        model.addAttribute("movimiento", new MovimientoInventario());
        model.addAttribute("materias", materiaRepo.findAll());
        model.addAttribute("selectedMateriaId", materiaId);
        return "movimiento-form";
    }

    @GetMapping("/inventario/movimientos")
    public String listarMovimientos(Model model) {
        List<MovimientoInventario> movimientos = movimientoRepo.findAll();
        model.addAttribute("movimientos", movimientos);
        return "movimientos";
    }

    @PostMapping("/inventario/movimientos/guardar")
    public String guardarMovimiento(@RequestParam(value = "materiaPrimaId", required = true) Long materiaPrimaId,
                                    @ModelAttribute MovimientoInventario movimiento,
                                    @SessionAttribute(value = "usuarioId", required = false) Long usuarioId,
                                    RedirectAttributes redirect) {
        // Validar parámetro
        if (materiaPrimaId == null) {
            redirect.addFlashAttribute("error", "Debe seleccionar una materia prima.");
            return "redirect:/inventario/movimientos/nuevo";
        }

        var optMateria = materiaRepo.findById(materiaPrimaId);
        if (optMateria.isEmpty()) {
            redirect.addFlashAttribute("error", "Materia prima no encontrada.");
            return "redirect:/inventario/movimientos/nuevo";
        }

        MateriaPrima materia = optMateria.get();

        // Validar stock para salidas
        if (movimiento.getTipo() == MovimientoInventario.Tipo.SALIDA) {
            if (!materia.hayStock(movimiento.getCantidad())) {
                redirect.addFlashAttribute("error", "No se puede registrar la salida: la cantidad solicitada (" + movimiento.getCantidad() + ") es mayor al stock disponible (" + materia.getStockActual() + ").");
                return "redirect:/inventario/movimientos/nuevo";
            }
        }

        EncargadoInventario encargado = new EncargadoInventario();
        if (usuarioId != null) encargado.setIdUsuario(usuarioId);

        try {
            if (movimiento.getTipo() == MovimientoInventario.Tipo.ENTRADA) {
                inventarioService.registrarEntrada(materia, movimiento.getCantidad(), encargado);
                redirect.addFlashAttribute("success", "Entrada registrada");
            } else {
                inventarioService.registrarSalida(materia, movimiento.getCantidad(), encargado);
                redirect.addFlashAttribute("success", "Salida registrada");
            }
            return "redirect:/inventario";
        } catch (IllegalArgumentException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/inventario/movimientos/nuevo";
        }
    }
}