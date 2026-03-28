package com.pucmm.csti19105488.dao;
import org.bson.types.ObjectId;
import java.util.List;

    public interface RepositorioBase<T>{
        void guardar(T entidad);
        T buscarPorId (ObjectId id);
        List<T> buscarTodos();
        void actualizar(T entidad);
        void eliminar(ObjectId id);
    }

