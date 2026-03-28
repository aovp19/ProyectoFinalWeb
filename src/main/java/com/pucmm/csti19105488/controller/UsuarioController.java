package com.pucmm.csti19105488.controller;

import com.pucmm.csti19105488.model.Usuario;
import com.pucmm.csti19105488.service.UsuarioService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

public class UsuarioController {

    private static UsuarioService usuarioService = new UsuarioService();

    public static void registrarRutas(Javalin app){
        
        // Crear usuario
        app.post("/usuarios", ctx -> {
            Usuario usuario = ctx.bodyAsClass(Usuario.class);
            usuarioService.registrarUsuario(usuario);
            ctx.status(201).json("Usuario creado exitosamente");
            // Si el metodo lanza una excepcion, se captura y en el main la lee
        });

        //Listar usuariod
        app.get("/usuarios", ctx -> {
           ctx.json(usuarioService.listarUsuarios());
        });

        // Login
        app.post("/login", ctx -> {
           Usuario usuario = ctx.bodyAsClass(Usuario.class);
           Usuario autenticado = usuarioService.autenticar(usuario.getEmail(), usuario.getPassword());
           ctx.status(200).json(autenticado);
        });

        // Actualizar usuario
        app.put("/usuarios/{id}", ctx -> {
          Usuario usuario = ctx.bodyAsClass(Usuario.class);
          usuarioService.actualizarUsuario(usuario);
          ctx.status(200).json("Usuario actualizado exitosamente");
        });

        // Eliminar usuario
        app.delete("/usuarios/{id}", ctx -> {
            String id = ctx.pathParam("id");
            usuarioService.eliminarUsuario(new ObjectId(id));
            ctx.status(200).json("Usuario eliminado exitosamente");
        });
        
    }


}
