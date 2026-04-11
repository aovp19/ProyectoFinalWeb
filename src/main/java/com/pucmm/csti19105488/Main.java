package com.pucmm.csti19105488;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.pucmm.csti19105488.config.MongoConfig;
import com.pucmm.csti19105488.controller.EncuestaController;
import com.pucmm.csti19105488.controller.UsuarioController;
import com.pucmm.csti19105488.grpc.FormularioGrpcService;
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
import io.javalin.json.JavalinJackson;
import io.javalin.rendering.template.JavalinFreemarker;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static io.javalin.apibuilder.ApiBuilder.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        // Inicializar la conección a la base de datos
        MongoConfig.getInstance();

        // Verificacion del usuario admin
        inicializarAdmin();

        //Crear el servidor Javalin
        Javalin app = Javalin.create(config -> {

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

            config.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
                mapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                mapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            }));

            // Rutas
            config.routes.apiBuilder(() -> {
                get("/", ctx -> ctx.json(java.util.Map.of(
                        "status", "ok",
                        "message", "API de Encuestas corriendo",
                        "puerto", 7000
                )));

                // Filtro JWT para rutas protegidas
                before("/encuestas", Main::filtroJwt);
                before("/encuestas/*", Main::filtroJwt);
                before("/usuarios", Main::filtroJwt);
                before("/usuarios/*",  Main::filtroJwt);
                before("/estadisticas",  Main::filtroJwt);
                before("/api/*", Main::filtroJwt);

                UsuarioController.registrarRutas();
                EncuestaController.registrarRutas();

                ws("/sync", EncuestaController::configurarWs);
            });

            config.routes.post("/grpc/formulario.FormularioService/ListarFormularios", ctx -> {
                com.fasterxml.jackson.databind.ObjectMapper mapper =
                        new com.fasterxml.jackson.databind.ObjectMapper();
                java.util.Map<String, Object> body = mapper.readValue(ctx.body(), java.util.Map.class);
                String usuarioId = (String) body.getOrDefault("usuarioId", "");
                FormularioGrpcService service = new FormularioGrpcService();
                ctx.json(service.listarParaHttp(usuarioId));
            });

            config.routes.post("/grpc/formulario.FormularioService/CrearFormulario", ctx -> {
                com.fasterxml.jackson.databind.ObjectMapper mapper =
                        new com.fasterxml.jackson.databind.ObjectMapper();
                java.util.Map<String, Object> body = mapper.readValue(ctx.body(), java.util.Map.class);
                FormularioGrpcService service = new FormularioGrpcService();
                ctx.json(service.crearParaHttp(body));
            });

            config.routes.get("/ping", ctx -> ctx.status(200).result("pong"));

        }).start(7000);;



        System.out.println("Servidor corriendo en el puerto 7000");

        try {
            io.grpc.Server grpcServer = io.grpc.ServerBuilder
                    .forPort(50051)
                    .addService(new FormularioGrpcService())
                    .build()
                    .start();

            System.out.println("Servidor gRPC corriendo en el puerto 50051");
            Runtime.getRuntime().addShutdownHook(new Thread(grpcServer::shutdown));
        } catch (IOException e) {
            System.err.println("Error al iniciar servidor gRPC: " + e.getMessage());
        }
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