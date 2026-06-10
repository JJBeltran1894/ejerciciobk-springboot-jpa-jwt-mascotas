package com.krakedev.jwt.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.krakedev.jwt.entidades.Usuario;
import com.krakedev.jwt.repositories.UsuarioRepository;

@Service
public class UsuarioService {
	
	private UsuarioRepository usuarioRepositorio;
	
	public UsuarioService(UsuarioRepository usuarioRepositorio) {
		this.usuarioRepositorio = usuarioRepositorio;
	}
	
	public Usuario registrar(Usuario usuario) {
		return usuarioRepositorio.save(usuario);
	}
	
	public Usuario login(String username, String password) {
		Optional<Usuario> usuario = usuarioRepositorio.findByUsername(username);
		
		if(usuario.isPresent()) {
			Usuario u = usuario.get();
			if(u.getPassword().equals(password)) {
				return u;
			}
			
		}
		return null;
	}
	
	

}
