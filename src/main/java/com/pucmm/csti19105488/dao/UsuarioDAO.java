package com.pucmm.csti19105488.dao;

import com.pucmm.csti19105488.config.MongoConfig;


import com.pucmm.csti19105488.model.Usuario;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import dev.morphia.query.updates.UpdateOperators;
import org.bson.types.ObjectId;

import java.util.List;

public class UsuarioDAO implements RepositorioBase<Usuario>{

    private Datastore datastore;

    public UsuarioDAO(){
        this.datastore = MongoConfig.getInstance().getDatastore();
    }

    @Override
    public void guardar(Usuario usuario) {
        usuario.setActivo(true);
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

    public List<Usuario> buscarActivos() {
        return datastore.find(Usuario.class)
                .filter(Filters.eq("activo", true))
                .iterator().toList();
    }

    @Override
    public void actualizar(Usuario usuario) {datastore.merge(usuario);}

    public void actualizarActivo(ObjectId id, boolean activo) {
        Usuario usuario = buscarPorId(id);
        if (usuario != null) {
            usuario.setActivo(activo);
            datastore.save(usuario);
        }
        else{
            throw new RuntimeException("Usuario no encontrado");
        }
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
                .filter(Filters.eq("email", email))
                .first();
    }
}
