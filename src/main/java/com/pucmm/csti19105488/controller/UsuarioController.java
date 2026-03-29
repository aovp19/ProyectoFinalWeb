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

            //Listar usuarios activos -> se muestra por default
            get(ctx -> ctx.json(usuarioService.listarUsuariosActivos()));

            // Listar todos los usuarios
            path("/todos", () -> {
                get(ctx -> ctx.json(usuarioService.listarTodos()));
            });

            path("/{id}", () -> {

                // Actualizar usuario
                put(ctx -> {
                    String id = ctx.pathParam("id");
                    Usuario usuario = ctx.bodyAsClass(Usuario.class);
                    usuario.setId(new ObjectId(id));
                    usuarioService.actualizarUsuario(usuario);
                    ctx.status(200).json("Usuario actualizado exitosamente");
                });

                // Eliminar usuario
                delete(ctx -> {
                    String id = ctx.pathParam("id");
                    usuarioService.eliminarUsuario(new ObjectId(id));
                    ctx.status(200).json("Usuario eliminado exitosamente");
                });

                path("/activar", () -> {
                    put(ctx -> {
                        String id = ctx.pathParam("id");
                        usuarioService.activarUsuario(new ObjectId (id));
                        ctx.status(200).json("Usuario activado exitosamente");
                    });
                });
            });
        });

        // Login
       post("/login", ctx -> {
           Usuario usuario = ctx.bodyAsClass(Usuario.class);
           Usuario autenticado = usuarioService.autenticar(usuario.getEmail(), usuario.getPassword());
           String token = JwtUtil.generarToken(autenticado);
           ctx.status(200).json(Map.of("token", token, "rol", autenticado.getRol().toString()));
       });
    }
}
