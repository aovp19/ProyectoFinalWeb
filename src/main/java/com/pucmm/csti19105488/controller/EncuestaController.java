package com.pucmm.csti19105488.controller;

import com.pucmm.csti19105488.dto.EncuestaDTO;
import com.pucmm.csti19105488.model.Encuesta;
import com.pucmm.csti19105488.model.Usuario;
import com.pucmm.csti19105488.service.EncuestaService;
import com.pucmm.csti19105488.service.UsuarioService;
import org.bson.types.ObjectId;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;

public class EncuestaController {

    private static EncuestaService encuestaService = new EncuestaService();
    private static UsuarioService usuarioService = new UsuarioService();

    public static void registrarRutas(){

        path("/encuestas", () -> {

            // Crear encuesta
            post( ctx -> {
                Encuesta encuesta = ctx.bodyAsClass(Encuesta.class);

                String email = ctx.attribute("email");
                System.out.println("Email del token: " + email);
                Usuario encuestador = usuarioService.buscarPorEmail(email);
                System.out.println("Encuestador encontrado: " + (encuestador != null ? encuestador.getEmail() : "NULL"));
                encuesta.setEncuestador(encuestador);
                encuesta.setSincronizado(true);

                encuestaService.crearEncuesta(encuesta);
                ctx.status(201).json("Encuesta creada exitosamente");
            });

            // Listar encuestas
            get( ctx -> {
                ctx.json(encuestaService.listarTodasEncuestas()
                        .stream()
                        .map(EncuestaDTO::new)
                        .toList());
            });

            path("/{id}", () -> {

                // Buscar encuesta por id
                get(ctx -> {
                    String id = ctx.pathParam("id");
                    Encuesta encuesta = encuestaService.buscarEncuestaPorId(new ObjectId(id));
                    ctx.json(encuesta);
                });


                // Actualizar encuesta
                put(ctx -> {
                    Encuesta encuesta = ctx.bodyAsClass(Encuesta.class);
                    encuestaService.actualizarEncuesta(encuesta);
                    ctx.status(200).json("Encuesta actualizada exitosamente");
                });


                // Eliminar encuesta
                delete( ctx -> {
                    String id = ctx.pathParam("id");
                    encuestaService.eliminarEncuesta(new ObjectId(id));
                    ctx.status(200).json("Encuesta eliminada exitosamente");
                });

                path("/sincronizar", () -> {

                    // Sincronizar encuesta
                    put(ctx -> {
                        String id = ctx.pathParam("id");
                        encuestaService.sincronizarEncuesta(encuestaService.buscarEncuestaPorId(new ObjectId(id)));
                        ctx.status(200).json("Encuesta sincronizada exitosamente");
                    });
                });
            });


            // Listar encuestas por usuario
            path("/usuario/{id}", () -> {

                get( ctx -> {
                    String id = ctx.pathParam("id");
                    Usuario usuario = usuarioService.buscarPorId(new ObjectId(id));
                    System.out.println("Buscando encuestas para usuario: " + usuario.getEmail());
                    List encuestas = encuestaService.listarEncuestasPorUsuario(usuario);
                    System.out.println("Encuestas encontradas: " + encuestas.size());
                    ctx.json(encuestas);
                });
            });
        });
    }
}
