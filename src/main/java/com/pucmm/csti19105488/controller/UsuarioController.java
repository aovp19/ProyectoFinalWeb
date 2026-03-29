package com.pucmm.csti19105488.controller;

import com.pucmm.csti19105488.model.Usuario;
import com.pucmm.csti19105488.service.UsuarioService;
import com.pucmm.csti19105488.util.JwtUtil;
import org.bson.types.ObjectId;

import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class UsuarioController {

    private static UsuarioService usuarioService = new UsuarioService();

    public static void registrarRutas(){
        
        // Crear usuario
        path("/usuarios", () -> {
            post(ctx -> {
                Usuario usuario = ctx.bodyAsClass(Usuario.class);
                usuarioService.registrarUsuario(usuario);
                ctx.status(201).json("Usuario creado exitosamente");
                // Si el metodo lanza una excepcion, se captura y en el main la lee
            });

            //Listar usuariod
            get(ctx -> ctx.json(usuarioService.listarUsuarios()));

            path("/{id}", () -> {

                // Actualizar usuario
                put(ctx -> {
                    Usuario usuario = ctx.bodyAsClass(Usuario.class);
                    usuarioService.actualizarUsuario(usuario);
                    ctx.status(200).json("Usuario actualizado exitosamente");
                });
            });

            // Eliminar usuario
            delete(ctx -> {
                String id = ctx.pathParam("id");
                usuarioService.eliminarUsuario(new ObjectId(id));
                ctx.status(200).json("Usuario eliminado exitosamente");
            });
        });


        // Login
       post("/login", ctx -> {
           Usuario usuario = ctx.bodyAsClass(Usuario.class);
           Usuario autenticado = usuarioService.autenticar(usuario.getEmail(), usuario.getPassword());
           String token = JwtUtil.generarToken(autenticado);
           ctx.status(200).json(Map.of("token", autenticado));
       });
    }
}
