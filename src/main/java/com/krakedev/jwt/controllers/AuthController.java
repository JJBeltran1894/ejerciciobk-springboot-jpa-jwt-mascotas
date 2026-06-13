package com.krakedev.jwt.controllers;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.krakedev.jwt.entidades.Usuario;
import com.krakedev.jwt.services.UsuarioService;
import com.krakedev.jwt.utils.JwtUtil;

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
			if(usuario != null) {
				String token = JwtUtil.generarToken(usuario.getUsername(), usuario.getRol());
				//return ResponseEntity.ok("Login existoso. Bienvenido, "+usuario.getUsername());
				return ResponseEntity.ok(Map.of("token: ",token));
			}else{
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
			}

		} catch (Exception e ) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
			
		}
	}
	
	@GetMapping("/perfil")
	public ResponseEntity<?> perfil(@RequestHeader(value="Authorization", required = false) String authHeader){
		try{
			if(authHeader == null || !authHeader.startsWith("Bearer ")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body("Acceso Denegado: Debe proveer un token Bearer valido en la cebecera Authorization");
			}
			String token = authHeader.substring(7);
			
			DecodedJWT datosToken = JwtUtil.validarToken(token);
			
			if(datosToken ==null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acceso Denegado: Token invalido o expirado" );
			}
			
			String usuario = datosToken.getSubject();
			String rol = datosToken.getClaim("rol").asString();
			
			if ("ADMIN".equals(rol)) {
				return ResponseEntity.ok(Map.of(
						"Mensaje", "Bienvenido al sistema del refugio de mascotas - Eres Administrador", 
						"Usuario", usuario,
						"Rol", rol,
						"Estatus", "Autenticado Existosamente"
						));	
			}else if("USER".equals(rol)){
				return ResponseEntity.ok(Map.of(
						"Mensaje", "Bienvenido al sistema del refugio de mascotas - Eres Usuario con permisos basicos", 
						"Usuario", usuario,
						"Rol", rol,
						"Estatus", "Autenticado Existosamente"
						));	
			}else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Rol no autorizado a acceder a este modulo");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalido o expirado");
		}
			
			
	}

}
