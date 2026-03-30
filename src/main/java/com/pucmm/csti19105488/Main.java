package com.pucmm.csti19105488;

import com.pucmm.csti19105488.config.MongoConfig;
import com.pucmm.csti19105488.controller.EncuestaController;
import com.pucmm.csti19105488.controller.UsuarioController;
import com.pucmm.csti19105488.dao.UsuarioDAO;
import com.pucmm.csti19105488.model.Rol;
import com.pucmm.csti19105488.model.Usuario;
import com.pucmm.csti19105488.service.UsuarioService;
import com.pucmm.csti19105488.util.JwtUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinFreemarker;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;

import static io.javalin.apibuilder.ApiBuilder.before;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        // Inicializar la conección a la base de datos
        MongoConfig.getInstance();

        // Verificacion del usuario admin
        inicializarAdmin();

        //Crear el servidor Javalin
       Javalin.create(config -> {

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

           // Manejador de excepciones global
           config.routes.exception(Exception.class, (e, ctx) -> {
               ctx.status(500).result(e.getMessage());
           });

           // Rutas
           config.routes.apiBuilder(() -> {

               // Filtro JWT para rutas protegidas
               before("/encuestas", Main::filtroJwt);
               before("/encuestas/*", Main::filtroJwt);
               before("/usuarios", Main::filtroJwt);
               before("/usuarios/*",  Main::filtroJwt);

               UsuarioController.registrarRutas();
               EncuestaController.registrarRutas();
           });


        }).start(7000);
        System.out.println("Servidor corriendo en el puerto 7000");
    }

    private static void filtroJwt(@NotNull Context ctx) {

        // Su único propósito es preguntar si puede proceder
        if(ctx.method().equals("OPTIONS")) return;

        String headerAutenticacion = ctx.header("Authorization");
        if(headerAutenticacion == null || !headerAutenticacion.startsWith("Bearer")){
            throw new UnauthorizedResponse("Debe autenticarse para acceder a esta ruta.");
        }

        String token = headerAutenticacion.replace("Bearer", "").trim();
        try {
            Claims claims = JwtUtil.verificarToken(token);
            ctx.attribute("email", claims.get("email", String.class));
            ctx.attribute("rol", claims.get("rol", String.class));
        } catch (Exception e) {
            throw new ForbiddenResponse("Token invalido o expirado: " + e.getMessage());
        }
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
            System.out.println("Usuario admin creado exitosamente");
        } else {
            System.out.println("El usuario admin ya existe");
        }
    }
}
