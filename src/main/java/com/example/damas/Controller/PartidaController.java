package com.example.damas.Controller;

import com.example.damas.Entities.PartidaHistorico;
import com.example.damas.Entities.Usuario;
import com.example.damas.Service.PartidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/api/partidas")
public class PartidaController {

    private final PartidaService partidaService;

    @Autowired
    public PartidaController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    @GetMapping("/historico")
    public ResponseEntity<?> buscarHistorico(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) {
            return ResponseEntity.status(401).body("Usuário não está logado!");
        }

        List<PartidaHistorico> partidas = partidaService.buscarPartidasDoUsuario(usuario.getId());
        return ResponseEntity.ok(partidas);
    }

    @PostMapping("/salvar")
    public ResponseEntity<String> salvarPartida(@RequestBody PartidaHistorico partida, HttpSession session) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

            if (usuario == null) {
                return ResponseEntity.status(401).body("Usuário não está logado!");
            }

            partida.setJogador(usuario);
            partidaService.salvarPartida(partida);

            return ResponseEntity.ok("Partida salva com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao salvar partida: " + e.getMessage());
        }
    }
}