package com.example.damas.Controller;

import com.example.damas.Entities.Usuario;
import com.example.damas.Service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<String> cadastrarUsuario(@RequestBody Usuario usuario) {
        try {
            usuarioService.cadastrarUsuario(usuario);
            return ResponseEntity.ok("Usuário cadastrado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao cadastrar usuário: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credenciais, HttpSession session) {
        try {
            String email = credenciais.get("email");
            String senha = credenciais.get("senha");

            if (email == null || senha == null) {
                return ResponseEntity.badRequest().body("Email e senha são obrigatórios!");
            }

            Usuario usuario = usuarioService.fazerLogin(email, senha);

            if (usuario != null) {
                
                session.setAttribute("usuarioLogado", usuario);
                session.setAttribute("usuarioId", usuario.getId());
                session.setAttribute("usuarioNome", usuario.getNome());

                return ResponseEntity.ok("Login realizado com sucesso!");
            } else {
                return ResponseEntity.status(401).body("Email ou senha incorretos!");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao fazer login: " + e.getMessage());
        }
    }


    @GetMapping("/verificar-login")
    public ResponseEntity<?> verificarLogin(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario != null) {
            return ResponseEntity.ok(Map.of(
                    "logado", true,
                    "nome", usuario.getNome(),
                    "email", usuario.getEmail()
            ));
        } else {
            return ResponseEntity.ok(Map.of("logado", false));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("Logout realizado com sucesso!");
    }

    @GetMapping("/ranking")
    public ResponseEntity<?> getRanking() {
        try {
            List<Map<String, Object>> ranking = usuarioService.getRanking();
            return ResponseEntity.ok(ranking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar ranking: " + e.getMessage());
        }
    }
}