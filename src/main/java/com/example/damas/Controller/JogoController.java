package com.example.damas.Controller;

import com.example.damas.Entities.*;
import com.example.damas.Enums.Cor;
import com.example.damas.Enums.Dificuldade;
import com.example.damas.Enums.StatusPartida;
import com.example.damas.Service.PartidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jogo")
public class JogoController {

    private final PartidaService partidaService;

    @Autowired
    public JogoController(PartidaService partidaService) {
        this.partidaService = partidaService;
    }

    /**
     * Inicia uma nova partida contra a IA
     */
    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarPartida(@RequestBody Map<String, String> dados, HttpSession session) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

            String nomeJogador = (usuario != null) ? usuario.getNome() : "Jogador";
            String nivel = dados.get("nivel"); // "FACIL", "MEDIO", "DIFICIL"

            Dificuldade dificuldade = Dificuldade.valueOf(nivel.toUpperCase());

            // Cria jogadores
            JogadorHumano jogador = new JogadorHumano(nomeJogador, Cor.BRANCO);
            IA bot = new IA("Bot", Cor.PRETO, dificuldade);

            // Cria e inicia partida
            Partida partida = new Partida(jogador, bot);
            partida.inicarPartida();

            // Salva na sessão
            session.setAttribute("partidaAtual", partida);

            return ResponseEntity.ok(Map.of(
                    "mensagem", "Partida iniciada com sucesso!",
                    "tabuleiro", serializarTabuleiro(partida.getTabuleiro()),
                    "turno", partida.getTurnoAtual().toString()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * Realiza uma jogada do jogador humano
     */
    @PostMapping("/jogar")
    public ResponseEntity<?> realizarJogada(@RequestBody Map<String, Integer> dados, HttpSession session) {
        try {
            Partida partida = getPartidaFromSession(session);

            if (partida == null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Nenhuma partida em andamento"));
            }

            if (partida.getStatus() != StatusPartida.EM_ANDAMENTO) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Partida já finalizada"));
            }

            // coordenadas
            int origemLinha = dados.get("origemLinha");
            int origemColuna = dados.get("origemColuna");
            int destinoLinha = dados.get("destinoLinha");
            int destinoColuna = dados.get("destinoColuna");

            Posicao origem = new Posicao(origemLinha, origemColuna);
            Posicao destino = new Posicao(destinoLinha, destinoColuna);

            // Valida se é o turno do jogador
            if (partida.getTurnoAtual() != Cor.BRANCO) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Não é seu turno!"));
            }

            // Verifica se tem capturas obrigatorias
            List<Movimento> capturasObrigatorias = partida.getTabuleiro().getMovimentosObrigatorios(Cor.BRANCO);
            if (!capturasObrigatorias.isEmpty()) {
                boolean isMoveACapture = false;
                for (Movimento captura : capturasObrigatorias) {
                    if (captura.getOrigem().equals(origem) && captura.getDestino().equals(destino)) {
                        isMoveACapture = true;
                        break;
                    }
                }
                if (!isMoveACapture) {
                    return ResponseEntity.badRequest().body(Map.of("erro", "Captura obrigatória pendente!"));
                }
            }
            
            // Pega o jogador humano
            JogadorHumano jogador = (JogadorHumano) partida.getJogador1();

            // Define a jogada
            boolean jogadaValida = jogador.definirJogada(origem, destino, partida.getTabuleiro(), partida);

            if (!jogadaValida) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Movimento inválido!"));
            }

            // Processa o turno do jogador
            partida.processarTurno();

            Map<String, Object> resposta = new HashMap<>();
            resposta.put("tabuleiro", serializarTabuleiro(partida.getTabuleiro()));
            resposta.put("turno", partida.getTurnoAtual().toString());
            resposta.put("status", partida.getStatus().toString());
            resposta.put("emCombo", partida.isEmCombo());

            // Se ainda esta em combo, nao é turno do bot
            if (partida.isEmCombo()) {
                resposta.put("mensagem", "Continue capturando!");
                resposta.put("posicaoCombo", Map.of(
                        "linha", partida.getPosicaoCombo().getLinha(),
                        "coluna", partida.getPosicaoCombo().getColuna()
                ));
                return ResponseEntity.ok(resposta);
            }

            // Se a partida acabou
            if (partida.getStatus() != StatusPartida.EM_ANDAMENTO) {
                salvarHistorico(partida, session);
                resposta.put("mensagem", "Partida finalizada: " + partida.getStatus());
                return ResponseEntity.ok(resposta);
            }

            // Turno do bot
            if (partida.getTurnoAtual() == Cor.PRETO) {
                partida.processarTurno(); // Bot joga automaticamente

                resposta.put("tabuleiro", serializarTabuleiro(partida.getTabuleiro()));
                resposta.put("turno", partida.getTurnoAtual().toString());
                resposta.put("status", partida.getStatus().toString());
                resposta.put("mensagem", "Bot jogou!");

                // Verifica se o jogo acabou após jogada do bot
                if (partida.getStatus() != StatusPartida.EM_ANDAMENTO) {
                    salvarHistorico(partida, session);
                    resposta.put("mensagem", "Partida finalizada: " + partida.getStatus());
                }
            }

            return ResponseEntity.ok(resposta);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * Retorna o estado atual da partida
     */
    @GetMapping("/estado")
    public ResponseEntity<?> obterEstado(HttpSession session) {
        try {
            Partida partida = getPartidaFromSession(session);

            if (partida == null) {
                return ResponseEntity.ok(Map.of("partidaAtiva", false));
            }

            return ResponseEntity.ok(Map.of(
                    "partidaAtiva", true,
                    "tabuleiro", serializarTabuleiro(partida.getTabuleiro()),
                    "turno", partida.getTurnoAtual().toString(),
                    "status", partida.getStatus().toString(),
                    "movimentos", partida.getContadorMovimentos(),
                    "emCombo", partida.isEmCombo()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * Retorna movimentos validos para uma peça
     */
    @PostMapping("/movimentos-validos")
    public ResponseEntity<?> obterMovimentosValidos(@RequestBody Map<String, Integer> dados, HttpSession session) {
        try {
            Partida partida = getPartidaFromSession(session);

            if (partida == null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Nenhuma partida em andamento"));
            }

            int linha = dados.get("linha");
            int coluna = dados.get("coluna");

            Posicao posicao = new Posicao(linha, coluna);

            if (!partida.getTabuleiro().Ocupada(posicao)) {
                return ResponseEntity.ok(Map.of("movimentos", List.of()));
            }

            Peca peca = partida.getTabuleiro().getCasa(posicao).getPeca();
            List<Movimento> movimentos = partida.getTabuleiro().getMovimentoValido(peca, posicao);

            List<Map<String, Integer>> movimentosJson = movimentos.stream()
                    .map(m -> Map.of(
                            "linha", m.getDestino().getLinha(),
                            "coluna", m.getDestino().getColuna(),
                            "captura", m.isCaptura() ? 1 : 0
                    ))
                    .toList();

            return ResponseEntity.ok(Map.of("movimentos", movimentosJson));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * Desiste da partida
     */
    @PostMapping("/desistir")
    public ResponseEntity<?> desistir(HttpSession session) {
        try {
            Partida partida = getPartidaFromSession(session);

            if (partida == null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Nenhuma partida em andamento"));
            }

            // Salva como derrota
            salvarHistoricoDerrota(partida, session);

            session.removeAttribute("partidaAtual");
            session.removeAttribute("partidaTorneio");


            return ResponseEntity.ok(Map.of("mensagem", "Você desistiu da partida"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    // Métodos auxiliares
    private Partida getPartidaFromSession(HttpSession session) {
        Partida partida = (Partida) session.getAttribute("partidaTorneio");
        if (partida == null) {
            partida = (Partida) session.getAttribute("partidaAtual");
        }
        return partida;
    }
    private Map<String, Object> serializarTabuleiro(Tabuleiro tabuleiro) {
        String[][] grid = new String[8][8];

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Posicao pos = new Posicao(i, j);
                if (tabuleiro.Ocupada(pos)) {
                    Peca peca = tabuleiro.getCasa(pos).getPeca();
                    if (peca.getCor() == Cor.BRANCO) {
                        grid[i][j] = peca.dama() ? "W" : "w";
                    } else {
                        grid[i][j] = peca.dama() ? "B" : "b";
                    }
                } else {
                    grid[i][j] = "";
                }
            }
        }

        return Map.of("grid", grid);
    }

    private void salvarHistorico(Partida partida, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) return;

        String resultado;
        if (partida.getStatus() == StatusPartida.VITORIA_BRANCO) {
            resultado = "VITORIA";
        } else if (partida.getStatus() == StatusPartida.VITORIA_PRETO) {
            resultado = "DERROTA";
        } else {
            resultado = "EMPATE";
        }

        IA bot = (IA) partida.getJogador2();

        PartidaHistorico historico = new PartidaHistorico(
                usuario,
                "Bot (" + bot.getNivel() + ")",
                resultado,
                partida.getContadorMovimentos(),
                bot.getNivel().toString()
        );

        historico.setDuracaoSegundos(partida.getDuracaoSegundos());

        partidaService.salvarPartida(historico);
    }

    private void salvarHistoricoDerrota(Partida partida, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

        if (usuario == null) return;

        IA bot = (IA) partida.getJogador2();

        PartidaHistorico historico = new PartidaHistorico(
                usuario,
                "Bot (" + bot.getNivel() + ")",
                "DERROTA",
                partida.getContadorMovimentos(),
                bot.getNivel().toString()
        );

        historico.setDuracaoSegundos(partida.getDuracaoSegundos());

        partidaService.salvarPartida(historico);
    }
}