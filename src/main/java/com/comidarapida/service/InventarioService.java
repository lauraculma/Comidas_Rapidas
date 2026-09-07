package com.comidarapida.service;

import com.comidarapida.model.MateriaPrima;
import com.comidarapida.model.MovimientoInventario;
import com.comidarapida.model.EncargadoInventario;
import com.comidarapida.repository.MateriaPrimaRepository;
import com.comidarapida.repository.MovimientoInventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class InventarioService {

    private final MateriaPrimaRepository materiaRepo;
    private final MovimientoInventarioRepository movimientoRepo;

    public InventarioService(MateriaPrimaRepository materiaRepo, MovimientoInventarioRepository movimientoRepo) {
        this.materiaRepo = materiaRepo;
        this.movimientoRepo = movimientoRepo;
    }

    @Transactional
    public MovimientoInventario registrarEntrada(MateriaPrima materia, int cantidad, EncargadoInventario encargado) {
        MovimientoInventario m = new MovimientoInventario();
        m.setTipo(MovimientoInventario.Tipo.ENTRADA);
        m.setCantidad(cantidad);
        m.setFecha(new java.util.Date());
        m.setMateriaPrima(materia);
        m.setEncargado(encargado);
        m.aplicarEntrada(materia);
        materiaRepo.save(materia);
        return movimientoRepo.save(m);
    }

    @Transactional
    public MovimientoInventario registrarSalida(MateriaPrima materia, int cantidad, EncargadoInventario encargado) {
        if (!materia.hayStock(cantidad)) throw new IllegalArgumentException("Stock insuficiente");
        MovimientoInventario m = new MovimientoInventario();
        m.setTipo(MovimientoInventario.Tipo.SALIDA);
        m.setCantidad(cantidad);
        m.setFecha(new java.util.Date());
        m.setMateriaPrima(materia);
        m.setEncargado(encargado);
        m.aplicarSalida(materia);
        materiaRepo.save(materia);
        return movimientoRepo.save(m);
    }

    public Optional<MateriaPrima> consultarMateriaPrima(Long id) {
        return materiaRepo.findById(id);
    }
}
