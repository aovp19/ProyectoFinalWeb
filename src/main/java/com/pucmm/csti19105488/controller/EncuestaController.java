package com.pucmm.csti19105488.controller;

import com.pucmm.csti19105488.model.Encuesta;
import com.pucmm.csti19105488.model.Usuario;
import com.pucmm.csti19105488.service.EncuestaService;
import com.pucmm.csti19105488.service.UsuarioService;
import io.javalin.Javalin;
import org.bson.types.ObjectId;

public class EncuestaController {

    private static EncuestaService encuestaService = new EncuestaService();
    private static UsuarioService usuarioService = new UsuarioService();

    public static void registrarRutas(Javalin app){

        // Crear encuesta
        app.post("/encuestas", ctx -> {
            Encuesta encuesta = ctx.bodyAsClass(Encuesta.class);
            encuestaService.crearEncuesta(encuesta);
            ctx.status(201).json("Encuesta creada exitosamente");
        });

        // Listar encuestas
        app.get("/encuestas", ctx -> {
            ctx.json(encuestaService.listarTodasEncuestas());
        });

        // Listar encuestas por usuario
        app.get("/encuestas/usuario/{id}", ctx -> {
            String id = ctx.pathParam("id");
            Usuario usuario = usuarioService.buscarPorId(new ObjectId(id));
            ctx.json(encuestaService.listarEncuestasPorUsuario(usuario));
        });

        // Buscar encuesta por id
        app.get("/encuestas/{id}", ctx -> {
            String id = ctx.pathParam("id");
            Encuesta encuesta = encuestaService.buscarEncuestaPorId(new ObjectId(id));
            ctx.json(encuesta);
        });

        // Actualizar encuesta
        app.put("/encuestas/{id}", ctx -> {
           Encuesta encuesta = ctx.bodyAsClass(Encuesta.class);
           encuestaService.actualizarEncuesta(encuesta);
           ctx.status(200).json("Encuesta actualizada exitosamente");
        });

        // Eliminar encuesta
        app.delete("/encuestas/{id}", ctx -> {
            String id = ctx.pathParam("id");
            encuestaService.eliminarEncuesta(new ObjectId(id));
            ctx.status(200).json("Encuesta eliminada exitosamente");
        });

        // Sincronizar encuesta
        app.put("/encuestas/sincronizar/{id}", ctx -> {
            String id = ctx.pathParam("id");
            encuestaService.sincronizarEncuesta(encuestaService.buscarEncuestaPorId(new ObjectId(id)));
            ctx.status(200).json("Encuesta sincronizada exitosamente");
        });
    }
}
