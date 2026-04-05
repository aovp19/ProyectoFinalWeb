package com.pucmm.csti19105488.dto;

import com.pucmm.csti19105488.model.Encuesta;

import java.time.LocalDateTime;

public class EncuestaDTO {
    public String id;
    public String nombreEncuestado;
    public String apellidoEncuestado;
    public String cedula;
    public String sector;
    public String educacion;
    public boolean sincronizado;
    public Double latitud;
    public Double longitud;
    public String encuestadorNombre;
    public String encuestadorApellido;
    public String encuestadorEmail;
    public LocalDateTime fechaRegistro;

    public EncuestaDTO(Encuesta e) {
        this.id = e.getId();
        this.nombreEncuestado = e.getNombreEncuestado();
        this.apellidoEncuestado = e.getApellidoEncuestado();
        this.cedula = e.getCedula();
        this.sector = e.getSector();
        this.educacion = e.getEducacion() != null ? e.getEducacion().name() : null;
        this.sincronizado = e.isSincronizado();
        this.fechaRegistro = e.getFechaRegistro();
        if (e.getEncuestador() != null) {
            this.encuestadorNombre  = e.getEncuestador().getNombre();
            this.encuestadorApellido = e.getEncuestador().getApellido();
            this.encuestadorEmail   = e.getEncuestador().getEmail();
        }
        if (e.getUbicacion() != null) {
            this.latitud = e.getUbicacion().getLatitud();
            this.longitud = e.getUbicacion().getLongitud();
        }
    }
}