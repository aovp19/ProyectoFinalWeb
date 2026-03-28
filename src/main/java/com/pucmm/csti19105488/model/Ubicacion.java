package com.pucmm.csti19105488.model;

import dev.morphia.annotations.Embedded;


@Embedded
public class Ubicacion {

    private double latitud;
    private double longitud;

    public Ubicacion(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
}
