package com.pucmm.csti19105488.model;

import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Reference;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Entity("encuestas")
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true) // para que el json inore campos desconocidos y no de error
public class Encuesta {

    @Id
    private ObjectId id;
    @Reference
    private Usuario encuestador;
    private String nombreEncuestado;
    private String apellidoEncuestado;
    private String cedula;
    private String sector;
    private NivelEducativo educacion;
    private Ubicacion ubicacion;
    private LocalDateTime fechaRegistro;
    private String fotoBase64;
    private boolean sincronizado;

    public Encuesta() { }
    public Encuesta(Usuario encuestador, String nombreEncuestado, String apellidoEncuestado, String cedula, String sector, NivelEducativo educacion, Ubicacion ubicacion) {
        this.encuestador = encuestador;
        this.nombreEncuestado = nombreEncuestado;
        this.apellidoEncuestado = apellidoEncuestado;
        this.cedula = cedula;
        this.sector = sector;
        this.educacion = educacion;
        this.fechaRegistro = LocalDateTime.now();
        this.ubicacion = ubicacion;
        this.sincronizado = false;
    }

    //Getters y setters
    public String getId() { return id.toHexString(); }
    public Usuario getEncuestador() { return encuestador; }
    public String getNombreEncuestado() { return nombreEncuestado; }
    public String getApellidoEncuestado() { return apellidoEncuestado; }
    public String getCedula() {return cedula; }
    public String getSector() { return sector; }
    public NivelEducativo getEducacion() { return educacion; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public Ubicacion getUbicacion() { return ubicacion; }
    public String getFotoBase64() { return fotoBase64; }
    public boolean isSincronizado() { return sincronizado; }

    public void setId(ObjectId id) { this.id = id; }
    public void setEncuestador(Usuario encuestador) { this.encuestador = encuestador; }
    public void setNombreEncuestado(String nombreEncuestado) { this.nombreEncuestado = nombreEncuestado; }
    public void setApellidoEncuestado(String apellidoEncuestado) { this.apellidoEncuestado = apellidoEncuestado; }
    public void setCedula(String cedula) {this.cedula = cedula;}
    public void setSector(String sector) { this.sector = sector; }
    public void setEducacion(NivelEducativo educacion) { this.educacion = educacion; }
    public void setUbicacion(Ubicacion ubicacion) { this.ubicacion = ubicacion; }
    public void setFotoBase64(String fotoBase64) { this.fotoBase64 = fotoBase64; }
    public void setSincronizado(boolean sincronizado) { this.sincronizado = sincronizado; }

}
