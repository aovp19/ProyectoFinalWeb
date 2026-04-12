package com.pucmm.csti19105488.grpc;

import com.pucmm.csti19105488.model.Encuesta;
import com.pucmm.csti19105488.model.NivelEducativo;
import com.pucmm.csti19105488.model.Ubicacion;
import com.pucmm.csti19105488.model.Usuario;
import com.pucmm.csti19105488.service.EncuestaService;
import com.pucmm.csti19105488.service.UsuarioService;
import com.pucmm.csti19105488.grpc.FormularioProto.*;
import com.pucmm.csti19105488.grpc.FormularioServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.bytebuddy.asm.Advice;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.List;

public class FormularioGrpcService extends FormularioServiceGrpc.FormularioServiceImplBase {

    private final EncuestaService encuestaService = new EncuestaService();
    private final UsuarioService  usuarioService  = new UsuarioService();

    @Override
    public void listarFormularios(ListarRequest request,
                                  StreamObserver<ListarResponse> responseObserver) {
        try {
            List<Encuesta> encuestas;

            if (!request.getUsuarioId().isEmpty()) {
                Usuario usuario = usuarioService.buscarPorId(
                        new org.bson.types.ObjectId(request.getUsuarioId()));
                encuestas = encuestaService.listarEncuestasPorUsuario(usuario);
            } else {
                encuestas = encuestaService.listarTodasEncuestas();
            }

            ListarResponse.Builder response = ListarResponse.newBuilder();

            for (Encuesta e : encuestas) {
                FormularioMessage.Builder msg = FormularioMessage.newBuilder()
                        .setId(e.getId())
                        .setNombreEncuestado(e.getNombreEncuestado() != null ? e.getNombreEncuestado() : "")
                        .setApellidoEncuestado(e.getApellidoEncuestado() != null ? e.getApellidoEncuestado() : "")
                        .setSector(e.getSector() != null ? e.getSector() : "")
                        .setEducacion(e.getEducacion() != null ? e.getEducacion().toString() : "")
                        .setSincronizado(e.isSincronizado());

                if (e.getUbicacion() != null) {
                    msg.setLatitud(e.getUbicacion().getLatitud())
                            .setLongitud(e.getUbicacion().getLongitud());
                }
                if (e.getFotoBase64() != null) {
                    msg.setFotoBase64(e.getFotoBase64());
                }
                if (e.getEncuestador() != null) {
                    msg.setEncuestadorNombre(
                            e.getEncuestador().getNombre() + " " + e.getEncuestador().getApellido());
                }

                response.addFormularios(msg.build());
            }

            responseObserver.onNext(response.build());
            responseObserver.onCompleted();

        } catch (Exception ex) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(ex.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void crearFormulario(FormularioRequest request,
                                StreamObserver<FormularioResponse> responseObserver) {
        try {
            Usuario encuestador = usuarioService.buscarPorEmail(request.getEncuestadorEmail());

            Encuesta encuesta = new Encuesta();
            encuesta.setNombreEncuestado(request.getNombreEncuestado());
            encuesta.setApellidoEncuestado(request.getApellidoEncuestado());
            encuesta.setCedula(request.getCedulaEncuestado());
            encuesta.setSector(request.getSector());
            encuesta.setEducacion(NivelEducativo.valueOf(request.getEducacion()));
            encuesta.setUbicacion(new Ubicacion(request.getLatitud(), request.getLongitud()));
            encuesta.setEncuestador(encuestador);
            encuesta.setSincronizado(true);
            encuesta.setFechaRegistro(LocalDateTime.now());
            if (!request.getFotoBase64().isEmpty()) {
                encuesta.setFotoBase64(request.getFotoBase64());
            }

            encuestaService.crearEncuesta(encuesta);

            responseObserver.onNext(FormularioResponse.newBuilder()
                    .setExitoso(true)
                    .setMensaje("Formulario creado exitosamente")
                    .build());
            responseObserver.onCompleted();

        } catch (Exception ex) {
            responseObserver.onNext(FormularioResponse.newBuilder()
                    .setExitoso(false)
                    .setMensaje("Error: " + ex.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    // Métodos helper para el endpoint HTTP/Connect
    public java.util.Map<String, Object> listarParaHttp(String usuarioId) {
        try {
            java.util.List<Encuesta> encuestas;
            if (usuarioId != null && !usuarioId.isEmpty()) {
                Usuario usuario = usuarioService.buscarPorId(new ObjectId(usuarioId));
                encuestas = encuestaService.listarEncuestasPorUsuario(usuario);
            } else {
                encuestas = encuestaService.listarTodasEncuestas();
            }

            java.util.List<java.util.Map<String, Object>> lista = new java.util.ArrayList<>();
            for (Encuesta e : encuestas) {
                java.util.Map<String, Object> item = new java.util.HashMap<>();
                item.put("id",                 e.getId());
                item.put("nombreEncuestado",   e.getNombreEncuestado());
                item.put("apellidoEncuestado", e.getApellidoEncuestado());
                item.put("sector",             e.getSector());
                item.put("educacion",          e.getEducacion() != null ? e.getEducacion().toString() : "");
                item.put("sincronizado",       e.isSincronizado());
                item.put("fotoBase64",         e.getFotoBase64());
                item.put("encuestadorNombre",  e.getEncuestador() != null ?
                        e.getEncuestador().getNombre() + " " + e.getEncuestador().getApellido() : "");
                if (e.getUbicacion() != null) {
                    item.put("latitud",  e.getUbicacion().getLatitud());
                    item.put("longitud", e.getUbicacion().getLongitud());
                }
                lista.add(item);
            }
            return java.util.Map.of("formularios", lista);
        } catch (Exception e) {
            return java.util.Map.of("error", e.getMessage());
        }
    }

    public java.util.Map<String, Object> crearParaHttp(java.util.Map<String, Object> body) {
        try {
            String email = (String) body.get("encuestadorEmail");
            Usuario encuestador = usuarioService.buscarPorEmail(email);

            Encuesta encuesta = new Encuesta();
            encuesta.setNombreEncuestado((String) body.get("nombreEncuestado"));
            encuesta.setApellidoEncuestado((String) body.get("apellidoEncuestado"));
            encuesta.setCedula((String) body.get("cedula"));
            encuesta.setSector((String) body.get("sector"));
            encuesta.setEducacion(NivelEducativo.valueOf((String) body.get("educacion")));

            double lat = body.get("latitud") instanceof Number ? ((Number) body.get("latitud")).doubleValue() : 0;
            double lng = body.get("longitud") instanceof Number ? ((Number) body.get("longitud")).doubleValue() : 0;
            encuesta.setUbicacion(new Ubicacion(lat, lng));
            encuesta.setEncuestador(encuestador);
            encuesta.setSincronizado(true);
            encuesta.setFechaRegistro(LocalDateTime.now());

            String foto = (String) body.get("fotoBase64");
            if (foto != null && !foto.isEmpty()) encuesta.setFotoBase64(foto);

            encuestaService.crearEncuesta(encuesta);
            return java.util.Map.of("exitoso", true, "mensaje", "Formulario creado exitosamente");

        } catch (Exception e) {
            return java.util.Map.of("exitoso", false, "mensaje", "Error: " + e.getMessage());
        }
    }
}