package com.comidarapida.model;

import jakarta.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_usuario")
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    private String contraseña;
    private String nombre;
    private String apellido;
    private String documento;
    private String correoElectronico;
    private String telefono;
    private boolean activo = true;

    // Getters y setters
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public boolean isActivo() { return activo; }
    protected void setActivo(boolean activo) { this.activo = activo; }

    // Comportamientos del dominio
    public boolean validarCredenciales(String correo, String contraseña) {
        if (correo == null || contraseña == null) return false;
        return correo.equals(this.correoElectronico) && contraseña.equals(this.contraseña) && this.activo;
    }

    public void actualizarDatosContacto(String correo, String telefono) {
        if (correo != null && !correo.isBlank()) this.correoElectronico = correo;
        if (telefono != null && !telefono.isBlank()) this.telefono = telefono;
    }

    public void desactivar() {
        this.activo = false;
    }
}
