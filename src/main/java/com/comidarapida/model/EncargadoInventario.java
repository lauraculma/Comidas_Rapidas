package com.comidarapida.model;

import jakarta.persistence.Entity;

@Entity
public class EncargadoInventario extends Usuario {

    public MovimientoInventario registrarEntrada(MateriaPrima materiaPrima, int cantidad) {
        MovimientoInventario m = new MovimientoInventario();
        m.setTipo(MovimientoInventario.Tipo.ENTRADA);
        m.setCantidad(cantidad);
        m.setFecha(new java.util.Date());
        m.setMateriaPrima(materiaPrima);
        m.aplicarEntrada(materiaPrima);
        return m;
    }

    public MovimientoInventario registrarSalida(MateriaPrima materiaPrima, int cantidad) {
        if (!materiaPrima.hayStock(cantidad)) {
            throw new IllegalArgumentException("Stock insuficiente");
        }
        MovimientoInventario m = new MovimientoInventario();
        m.setTipo(MovimientoInventario.Tipo.SALIDA);
        m.setCantidad(cantidad);
        m.setFecha(new java.util.Date());
        m.setMateriaPrima(materiaPrima);
        m.aplicarSalida(materiaPrima);
        return m;
    }
}
