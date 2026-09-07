package com.comidarapida.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class MovimientoInventario {

    public enum Tipo { ENTRADA, SALIDA }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMovimiento;

    private Date fecha;

    private Tipo tipo;

    private int cantidad;

    @ManyToOne
    @JoinColumn(name = "id_materia_prima", nullable = false)
    private MateriaPrima materiaPrima;

    @ManyToOne
    @JoinColumn(name = "id_encargado", nullable = false)
    private EncargadoInventario encargado;

    // Getters y setters
    public Long getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(Long idMovimiento) { this.idMovimiento = idMovimiento; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public MateriaPrima getMateriaPrima() { return materiaPrima; }
    public void setMateriaPrima(MateriaPrima materiaPrima) { this.materiaPrima = materiaPrima; }

    public EncargadoInventario getEncargado() { return encargado; }
    public void setEncargado(EncargadoInventario encargado) { this.encargado = encargado; }

    // Lógica de dominio
    public void aplicarEntrada(MateriaPrima m) {
        m.incrementarStock(this.cantidad);
        // opcional: set fecha si no está
        if (this.fecha == null) this.fecha = new Date();
    }

    public void aplicarSalida(MateriaPrima m) {
        m.decrementarStock(this.cantidad);
        if (this.fecha == null) this.fecha = new Date();
    }
}
