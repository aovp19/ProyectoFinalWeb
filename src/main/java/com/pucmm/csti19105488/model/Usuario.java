package com.pucmm.csti19105488.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import org.bson.types.ObjectId;

@Entity("usuarios")
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true) // para que el json inore campos desconocidos y no de error
public class Usuario {

    @Id
    private ObjectId id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private Rol rol;
    private boolean activo = true;
    private String fotoBase64;


    public Usuario() { }
    public Usuario(String nombre, String apellido, String email, String password, Rol rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.activo = true;
    }

    //Setters y getters
    public String getId() { return id != null ? id.toHexString() : null; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Rol getRol() { return rol; }
    public boolean isActivo() { return activo; }
    public String getFotoBase64() { return fotoBase64; }

    public void setId(ObjectId id) {this.id = id;}
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRol(Rol rol) { this.rol = rol; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public void setFotoBase64(String fotoBase64) { this.fotoBase64 = fotoBase64; }
}
