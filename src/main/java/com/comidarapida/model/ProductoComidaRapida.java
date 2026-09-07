package com.comidarapida.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ProductoComidaRapida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;

    private String codigo;
    private String nombre;
    private double precio;
    private boolean activo = true;
    private String imagen;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL)
    private List<ComposicionProducto> composiciones = new ArrayList<>();

    // Getters y setters
    public Long getIdProducto() { return idProducto; }
    public void setIdProducto(Long idProducto) { this.idProducto = idProducto; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public List<ComposicionProducto> getComposiciones() { return composiciones; }
    public void addComposicion(ComposicionProducto c) { this.composiciones.add(c); }

    // Lógica de dominio
    public boolean calculaDisponibilidad() {
        if (!activo) return false;
        for (ComposicionProducto cp : composiciones) {
            if (!cp.getMateriaPrima().hayStock(cp.getCantidadMateriaPrima())) return false;
        }
        return true;
    }

    // Backwards-compatible alias for templates that call calcularDisponibilidad()
    public boolean calcularDisponibilidad() {
        return calculaDisponibilidad();
    }
}
