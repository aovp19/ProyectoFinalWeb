package com.pucmm.csti19105488.service;

import com.pucmm.csti19105488.dao.UsuarioDAO;
import com.pucmm.csti19105488.model.Usuario;

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
        usuarioDAO.guardar(usuario);
    }

    public Usuario autenticar (String email, String password){
        Usuario usuario = usuarioDAO.buscarPorEmail(email);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        if (!usuario.getPassword().equals(password)) {
            throw new RuntimeException("Contraseña incorrecta.");
        }
        return usuario;
    }

}
