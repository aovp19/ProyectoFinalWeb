package com.pucmm.csti19105488.service;

import com.pucmm.csti19105488.dao.EncuestaDAO;
import com.pucmm.csti19105488.model.Encuesta;
import com.pucmm.csti19105488.model.Usuario;
import org.bson.types.ObjectId;

import java.util.List;

public class EncuestaService {

    private EncuestaDAO encuestaDAO;

    public EncuestaService(){
        this.encuestaDAO = new EncuestaDAO();
    }

    public void crearEncuesta(Encuesta encuesta){
        if (encuesta.getCedula() != null && !encuesta.getCedula().isBlank()) {
            Encuesta existente = encuestaDAO.buscarPorCedula(encuesta.getCedula());
            if (existente != null) {
                throw new RuntimeException("Ya existe una encuesta registrada con la cédula " + encuesta.getCedula());
            }
        }
        encuestaDAO.guardar(encuesta);
    }

    public void actualizarEncuesta(Encuesta encuesta){
        if (encuesta.isSincronizado()){
            throw new RuntimeException("No se puede actualizar una encuesta sincronizada");
        }
        encuestaDAO.actualizar(encuesta);
    }

    public void eliminarEncuesta(ObjectId id){
        Encuesta encuesta = encuestaDAO.buscarPorId(id);
        if(encuesta.isSincronizado()){
            throw new RuntimeException("No se puede eliminar una encuesta sincronizada");
        }
        encuestaDAO.eliminar(id);
    }

    public Encuesta buscarEncuestaPorId(ObjectId id){
        return encuestaDAO.buscarPorId(id);
    }

    public List<Encuesta> listarTodasEncuestas(){
        return encuestaDAO.buscarTodos();
    }

    public List<Encuesta> buscarEncuestasPorEncuestador(Usuario encuestador){
        return encuestaDAO.buscarPorEncuestador(encuestador);
    }

    public void sincronizarEncuesta(Encuesta encuesta){
        encuesta.setSincronizado(true);
        encuestaDAO.actualizar(encuesta);
    }

    public List<Encuesta> listarEncuestasNoSincronizadas(){
        return encuestaDAO.buscarNoSincronizados();
    }


    public Object listarEncuestasPorUsuario(Usuario usuario) {
        return encuestaDAO.buscarPorEncuestador(usuario);
    }
}
