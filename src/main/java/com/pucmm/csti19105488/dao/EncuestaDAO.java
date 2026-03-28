package com.pucmm.csti19105488.dao;

import com.pucmm.csti19105488.config.MongoConfig;
import com.pucmm.csti19105488.model.Encuesta;
import com.pucmm.csti19105488.model.Usuario;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import org.bson.types.ObjectId;

import java.util.List;

public class EncuestaDAO implements RepositorioBase<Encuesta>{

    private Datastore datastore;

    public EncuestaDAO(){
        this.datastore = MongoConfig.getInstance().getDatastore();
    }

    @Override
    public void guardar(Encuesta encuesta) {
        datastore.save(encuesta);
    }

    @Override
    public Encuesta buscarPorId(ObjectId id) {
        return datastore.find(Encuesta.class)
                .filter(Filters.eq("_id", id))
                .first(); // Porque se espera un solo resultado
    }

    @Override
    public List<Encuesta> buscarTodos() {
        return datastore.find(Encuesta.class).iterator().toList();
    }

    @Override
    public void actualizar(Encuesta encuesta) {
        datastore.merge(encuesta);
    }

    @Override
    public void eliminar(ObjectId id) {
        Encuesta encuesta = buscarPorId(id);
        if (encuesta != null) {
            datastore.delete(encuesta);
        }
    }

    public List<Encuesta> buscarPorEncuestador(Usuario encuestador) {
        return datastore.find(Encuesta.class)
                .filter(Filters.eq("encuestador", encuestador))
                .iterator().toList();
    }

    public List<Encuesta> buscarNoSincronizados(){
        return datastore.find(Encuesta.class)
                .filter(Filters.eq("sincronizado", false))
                .iterator().toList();
    }
}
