package com.pucmm.csti19105488.dao;

import com.pucmm.csti19105488.config.MongoConfig;


import com.pucmm.csti19105488.model.Usuario;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import org.bson.types.ObjectId;

import java.util.List;

public class UsuarioDAO implements RepositorioBase<Usuario>{

    private Datastore datastore;

    public UsuarioDAO(){
        this.datastore = MongoConfig.getInstance().getDatastore();
    }

    @Override
    public void guardar(Usuario usuario) {
        datastore.save(usuario);
    }

    @Override
    public Usuario buscarPorId(ObjectId id) {
        return datastore.find(Usuario.class)
                .filter(Filters.eq("_id", id))
                .first();
    }

    @Override
    public List<Usuario> buscarTodos() {
        return datastore.find(Usuario.class).iterator().toList();
    }

    @Override
    public void actualizar(Usuario usuario) {
        datastore.merge(usuario);
    }

    @Override
    public void eliminar(ObjectId id) {
        Usuario usuario = buscarPorId(id);
        if (usuario != null) {
            datastore.delete(usuario);
        }
    }

    public Usuario buscarPorEmail(String email) {
        return datastore.find(Usuario.class)
                .filter(Filters.eq("username", email))
                .first();
    }
}
