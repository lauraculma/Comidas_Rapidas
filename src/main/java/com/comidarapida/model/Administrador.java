package com.comidarapida.model;

import jakarta.persistence.Entity;

@Entity
public class Administrador extends Usuario {

    public void cambiarPrecioProducto(ProductoComidaRapida producto, double nuevoPrecio) {
        producto.setPrecio(nuevoPrecio);
    }

    public void cambiarEstadoProducto(ProductoComidaRapida producto, boolean activo) {
        producto.setActivo(activo);
    }

    public void desactivarUsuario(Usuario usuario) {
        usuario.desactivar();
    }
}
