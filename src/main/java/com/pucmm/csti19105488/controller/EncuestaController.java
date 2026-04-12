package com.pucmm.csti19105488.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pucmm.csti19105488.dto.EncuestaDTO;
import com.pucmm.csti19105488.model.Encuesta;
import com.pucmm.csti19105488.model.Usuario;
import com.pucmm.csti19105488.service.EncuestaService;
import com.pucmm.csti19105488.service.UsuarioService;
import io.javalin.Javalin;
import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.javalin.apibuilder.ApiBuilder.*;

public class EncuestaController {

    private static EncuestaService encuestaService = new EncuestaService();
    private static UsuarioService usuarioService = new UsuarioService();

    // WebSocket: sesiones activas
    private static final Map<WsContext, String> wsSessions = new ConcurrentHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void registrarRutas(){

        path("/encuestas", () -> {

            // Crear encuesta
            post( ctx -> {
                Encuesta encuesta = ctx.bodyAsClass(Encuesta.class);

                String email = ctx.attribute("email");
                System.out.println("Email del token: " + email);
                Usuario encuestador = usuarioService.buscarPorEmail(email);
                //System.out.println("Encuestador encontrado: " + (encuestador != null ? encuestador.getEmail() : "NULL"));
                encuesta.setEncuestador(encuestador);
                encuesta.setSincronizado(true);
                encuesta.setFechaRegistro(LocalDateTime.now());
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

            //Batch sync por REST (fallback cuando no hay WebSocket)
            path("/sync", () -> {
                post(ctx -> {
                    String email = ctx.attribute("email");


                    Map<String, Object> body = mapper.readValue(ctx.body(), Map.class);
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> records =
                            (List<Map<String, Object>>) body.get("records");

                    if (records == null || records.isEmpty()) {
                        ctx.status(400).json(Map.of("error", "No se recibieron registros"));
                        return;
                    }

                    List<Object>              syncedIds = new ArrayList<>();
                    List<Map<String, Object>> errors    = new ArrayList<>();

                    for (Map<String, Object> record : records) {
                        try {
                            Encuesta encuesta   = mapper.convertValue(record, Encuesta.class);
                            Usuario  encuestador = usuarioService.buscarPorEmail(email);
                            encuesta.setEncuestador(encuestador);
                            encuesta.setSincronizado(true);
                            encuesta.setFechaRegistro(LocalDateTime.now());
                            encuestaService.crearEncuesta(encuesta);
                            syncedIds.add(record.get("localId"));
                        } catch (Exception e) {
                            errors.add(Map.of(
                                    "localId", record.getOrDefault("localId", -1),
                                    "error",   e.getMessage()
                            ));
                        }
                    }

                    ctx.status(200).json(Map.of("syncedIds", syncedIds, "errors", errors));
                });
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

        // API REST FORMAL (para cliente externo)
        path("/api", () -> {
            path("/formularios", () -> {

                // GET /api/formularios?usuarioId=xxx
                get(ctx -> {
                    String usuarioId = ctx.queryParam("usuarioId");
                    List encuestas;
                    if (usuarioId != null && !usuarioId.isEmpty()) {
                        Usuario usuario = usuarioService.buscarPorId(new ObjectId(usuarioId));
                        encuestas = encuestaService.listarEncuestasPorUsuario(usuario)
                                .stream()
                                .map(EncuestaDTO::new)
                                .toList();
                    } else {
                        encuestas = encuestaService.listarTodasEncuestas()
                                .stream()
                                .map(EncuestaDTO::new)
                                .toList();
                    }
                    ctx.json(encuestas);
                });

                // POST /api/formularios
                post(ctx -> {
                    String email = ctx.attribute("email");
                    Encuesta encuesta = ctx.bodyAsClass(Encuesta.class);
                    Usuario encuestador = usuarioService.buscarPorEmail(email);
                    encuesta.setEncuestador(encuestador);
                    encuesta.setSincronizado(true);
                    encuesta.setFechaRegistro(LocalDateTime.now());
                    encuestaService.crearEncuesta(encuesta);
                    ctx.status(201).json(Map.of("mensaje", "Formulario creado exitosamente"));
                });
            });
        });
    }

    // WebSocket handler
    public static void configurarWs(WsConfig ws) {
        ws.onConnect(ctx -> {
            wsSessions.put(ctx, ctx.sessionId());
            System.out.println("[WS] Conectado: " + ctx.sessionId());
            ctx.send(mapper.writeValueAsString(Map.of(
                    "type",    "CONNECTED",
                    "payload", Map.of("sessionId", ctx.sessionId())
            )));
        });

        ws.onMessage(ctx -> {
            try {
                Map<String, Object> msg = mapper.readValue(ctx.message(), Map.class);
                String type = (String) msg.get("type");
                if ("BATCH_SYNC".equals(type)) {
                    handleWsBatchSync(ctx, msg);
                }
            } catch (Exception e) {
                ctx.send(mapper.writeValueAsString(Map.of(
                        "type", "ERROR",
                        "payload", Map.of("message", e.getMessage())
                )));
            }
        });

        ws.onClose(ctx -> {
            wsSessions.remove(ctx);
            System.out.println("[WS] Desconectado: " + ctx.sessionId());
        });

        ws.onError(ctx -> {
            wsSessions.remove(ctx);
            System.err.println("[WS] Error: " + ctx.sessionId());
        });
    }

    @SuppressWarnings("unchecked")
    private static void handleWsBatchSync(WsContext ctx, Map<String, Object> msg) throws Exception {
        Map<String, Object>       payload   = (Map<String, Object>) msg.get("payload");
        List<Map<String, Object>> records   = (List<Map<String, Object>>) payload.get("records");
        List<Object>              syncedIds = new ArrayList<>();
        List<Map<String, Object>> errors    = new ArrayList<>();

        for (Map<String, Object> record : records) {
            try {
                Encuesta encuesta = mapper.convertValue(record, Encuesta.class);
                encuesta.setSincronizado(true);
                encuesta.setFechaRegistro(LocalDateTime.now());
                encuestaService.crearEncuesta(encuesta);
                syncedIds.add(record.get("localId"));
            } catch (Exception e) {
                errors.add(Map.of(
                        "localId", record.getOrDefault("localId", -1),
                        "error",   e.getMessage()
                ));
            }
        }

        ctx.send(mapper.writeValueAsString(Map.of(
                "type",    "ACK",
                "payload", Map.of("syncedIds", syncedIds, "errors", errors)
        )));
    }
}
