package com.pucmm.csti19105488.service;

import com.pucmm.csti19105488.dao.UsuarioDAO;
import com.pucmm.csti19105488.model.Rol;
import com.pucmm.csti19105488.model.Usuario;
import org.bson.types.ObjectId;

import java.util.List;

public class UsuarioService {
    
    private UsuarioDAO usuarioDAO;
    
    public UsuarioService(){
        this.usuarioDAO = new UsuarioDAO();
    }
    
    public void registrarUsuario(Usuario usuario){
        // Se verifica que no haya un usuario con el mismo email
        if (usuarioDAO.buscarPorEmail(usuario.getEmail()) != null) {
            throw new RuntimeException("Ya existe un usuario con ese email");
        }
        // Luego se encripta la contraseña con el uso de JWT
        usuarioDAO.guardar(usuario);
    }

    public Usuario autenticar (String email, String password){
        Usuario usuario = usuarioDAO.buscarPorEmail(email);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        if (!usuario.getPassword().equals(password)) {
            throw new RuntimeException("Contraseña incorrecta.");
            // Se verfica con el JWT para autenticar al usuario
        }
        return usuario;
    }

    public List<Usuario> listarUsuariosActivos(){ return usuarioDAO.buscarActivos(); }

    public List<Usuario> listarTodos() { return usuarioDAO.buscarTodos();}

    public Usuario buscarPorId (ObjectId id){
        return usuarioDAO.buscarPorId(id);
    }

    public void actualizarUsuario(Usuario usuarioActualizado) {
        System.out.println("Usuario recibido - activo: " + usuarioActualizado.isActivo());
        System.out.println("Usuario recibido - id: " + usuarioActualizado.getId());

        Usuario usuarioExistente = usuarioDAO.buscarPorId(new ObjectId(usuarioActualizado.getId()));
        System.out.println("Usuario existente - activo: " + usuarioExistente.isActivo());

        if (usuarioExistente == null) {
            throw new RuntimeException("Usuario no encontrado");
        }

        if (usuarioExistente.getRol() == Rol.ADMIN &&
                usuarioActualizado.getRol() != Rol.ADMIN) {
            throw new RuntimeException("No se puede cambiar el rol de un administrador");
        }

        usuarioActualizado.setActivo(usuarioExistente.isActivo());
        System.out.println("Antes de guardar - activo: " + usuarioActualizado.isActivo());
        usuarioDAO.actualizar(usuarioActualizado);

        Usuario verificar = usuarioDAO.buscarPorId(new ObjectId(usuarioActualizado.getId()));
        System.out.println("Despues de guardar - activo: " + verificar.isActivo());
    }

    public void activarUsuario(ObjectId id) {
        Usuario usuario = usuarioDAO.buscarPorId(id);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        usuarioDAO.actualizarActivo(id, true);
    }

    public void eliminarUsuario(ObjectId id) {
        Usuario usuario = usuarioDAO.buscarPorId(id);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        if (usuario.getRol() == Rol.ADMIN) {
            throw new RuntimeException("No se puede eliminar un usuario con rol ADMIN");
        }
        // En vez de eliminar, desactiva
        usuarioDAO.actualizarActivo(id, false);
    }
}
