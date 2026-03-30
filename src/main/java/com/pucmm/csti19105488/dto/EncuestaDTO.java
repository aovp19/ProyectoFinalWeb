package com.pucmm.csti19105488.dto;

import com.pucmm.csti19105488.model.Encuesta;

public class EncuestaDTO {
    public String id;
    public String nombreEncuestado;
    public String apellidoEncuestado;
    public String cedula;
    public String sector;
    public String educacion;
    public boolean sincronizado;
    public String encuestadorNombre;
    public String encuestadorApellido;
    public String encuestadorEmail;

    public EncuestaDTO(Encuesta e) {
        this.id = e.getId();
        this.nombreEncuestado = e.getNombreEncuestado();
        this.apellidoEncuestado = e.getApellidoEncuestado();
        this.cedula = e.getCedula();
        this.sector = e.getSector();
        this.educacion = e.getEducacion() != null ? e.getEducacion().name() : null;
        this.sincronizado = e.isSincronizado();
        if (e.getEncuestador() != null) {
            this.encuestadorNombre  = e.getEncuestador().getNombre();
            this.encuestadorApellido = e.getEncuestador().getApellido();
            this.encuestadorEmail   = e.getEncuestador().getEmail();
        }
    }
}