package com.comidarapida.model;

import jakarta.persistence.*;

@Entity
public class MateriaPrima {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMateriaPrima;

    private int codigo;
    private String nombre;
    private int stockActual;
    private String estado;
    private String unidadMedida;

    // Getters y setters
    public Long getIdMateriaPrima() { return idMateriaPrima; }
    public void setIdMateriaPrima(Long idMateriaPrima) { this.idMateriaPrima = idMateriaPrima; }

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    // Lógica de dominio
    public boolean hayStock(int cantidad) {
        return this.stockActual >= cantidad;
    }

    public void incrementarStock(int cantidad) {
        this.stockActual += cantidad;
    }

    public void decrementarStock(int cantidad) {
        if (cantidad > this.stockActual) throw new IllegalArgumentException("Stock insuficiente");
        this.stockActual -= cantidad;
    }
}
