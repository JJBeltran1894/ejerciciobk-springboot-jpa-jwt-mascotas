package com.krakedev.jwt.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.krakedev.jwt.entidades.Usuario;
import com.krakedev.jwt.services.UsuarioService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	private UsuarioService usuarioService;

	public AuthController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}
	
	@PostMapping("/registrar")
	public ResponseEntity<?> registrar(@RequestBody Usuario usuario){
		try {
			Usuario nuevoUsuario = usuarioService.registrar(usuario);
			return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar usuario: " + e.getMessage());
		}
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Usuario request){
		try {
			Usuario usuario = usuarioService.login(request.getUsername(),request.getPassword());
			return ResponseEntity.ok("Login existoso. Bienvenido, "+usuario.getUsername());
		} catch (Exception e ) {
			return ResponseEntity.status(401).body(e.getMessage());
			
		}
	}

	
	

}
