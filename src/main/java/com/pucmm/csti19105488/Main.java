package com.pucmm.csti19105488;

import com.pucmm.csti19105488.config.MongoConfig;
import com.pucmm.csti19105488.dao.UsuarioDAO;
import com.pucmm.csti19105488.model.Rol;
import com.pucmm.csti19105488.model.Usuario;
import com.pucmm.csti19105488.service.UsuarioService;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinFreemarker;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static void main() {

        // Inicializar la conección a la base de datos
        MongoConfig.getInstance();

        //Crear el servidor Javalin
        Javalin app = Javalin.create(config -> {

            // Configuracion de archivos estaticos
            config.staticFiles.add(staticFilesConfig -> {
                staticFilesConfig.hostedPath = "/";
                staticFilesConfig.directory = "/publico";
                staticFilesConfig.location = Location.CLASSPATH;
            });

            // Configuracion de plantillas
            config.fileRenderer(new JavalinFreemarker());

            // CORS para permitir peticiones de todas partes
            config.bundledPlugins.enableCors(cors -> {
               cors.addRule(rule -> rule.anyHost());
            });

        }).start(7000);

        // Verificacion del usuario admin
        inicializarAdmin();

        // Para atrapar cualquier excepcion durante el desarrollo
        app.exception(Exception.class, (e, ctx) -> {
            ctx.status(400).json(e.getMessage());
        });
    }

    private static void inicializarAdmin() {
        UsuarioService usuarioService = new UsuarioService();
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        // Buscar si ya existe un usuario admin
        Usuario admin = usuarioDAO.buscarPorEmail("admin@ce.pucmm.edu.do");

        // Si no existe se crea
        if (admin == null){
            admin = new Usuario(
                    "Administrador",
                    "Sistema",
                    "admin@ce.pucmm.edu.do",
                    "admin123",
                    Rol.ADMIN);
            usuarioService.registrarUsuario(admin);
        } else {
            System.out.println("El usuario admin ya existe");
        }

    }
}
