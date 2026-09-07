package com.comidarapida.model;

import jakarta.persistence.*;

@Entity
public class ComposicionProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComposicion;

    private int cantidadMateriaPrima;

    @ManyToOne
    private ProductoComidaRapida producto;

    @ManyToOne
    private MateriaPrima materiaPrima;

    // Getters y setters
    public Long getIdComposicion() { return idComposicion; }
    public void setIdComposicion(Long idComposicion) { this.idComposicion = idComposicion; }

    public int getCantidadMateriaPrima() { return cantidadMateriaPrima; }
    public void setCantidadMateriaPrima(int cantidadMateriaPrima) { this.cantidadMateriaPrima = cantidadMateriaPrima; }

    public ProductoComidaRapida getProducto() { return producto; }
    public void setProducto(ProductoComidaRapida producto) { this.producto = producto; }

    public MateriaPrima getMateriaPrima() { return materiaPrima; }
    public void setMateriaPrima(MateriaPrima materiaPrima) { this.materiaPrima = materiaPrima; }

    // Lógica del dominio
    public void actualizarCantidad(int nuevaCantidad) { this.cantidadMateriaPrima = nuevaCantidad; }
}
