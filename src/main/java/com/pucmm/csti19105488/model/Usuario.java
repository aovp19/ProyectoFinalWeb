package com.pucmm.csti19105488.model;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import org.bson.types.ObjectId;

@Entity("usuarios")
public class Usuario {

    @Id
    private ObjectId id;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    @Reference
    private Rol role;
    private boolean activo;
    private String fotoBase64;


    public Usuario(String nombre, String apellido, String email, String password, Rol role) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.password = password;
        this.role = role;
        this.activo = true;
    }

    //Setters y getters
    public String getId() { return id.toHexString(); }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public Rol getRole() { return role; }
    public boolean isActivo() { return activo; }
    public String getFotoBase64() { return fotoBase64; }


    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Rol role) { this.role = role; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public void setFotoBase64(String fotoBase64) { this.fotoBase64 = fotoBase64; }
}
