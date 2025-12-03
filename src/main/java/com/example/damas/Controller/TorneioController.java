package com.example.damas.Controller;

import com.example.damas.Entities.*;
import com.example.damas.Enums.Cor;
import com.example.damas.Enums.StatusPartida;
import com.example.damas.Service.TorneioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@RestController
@RequestMapping("/api/torneio")
public class TorneioController {

    private final TorneioService torneioService;

    @Autowired
    public TorneioController(TorneioService torneioService) {
        this.torneioService = torneioService;
    }

    /**
     * Inicia um novo torneio
     */
    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarTorneio(HttpSession session) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

            if (usuario == null) {
                return ResponseEntity.status(401).body(Map.of("erro", "Você precisa fazer login para participar do torneio!"));
            }

            // Cria o torneio no banco
            Long torneioId = torneioService.criarTorneio(usuario);

            // Gera chaveamento: 1 jogador + 7 IAs
            List<Jogador> participantes = torneioService.gerarChaveamento(usuario.getNome());

            // Salva no estado da sessão
            Map<String, Object> estadoTorneio = new HashMap<>();
            estadoTorneio.put("torneioId", torneioId);
            estadoTorneio.put("participantes", participantes);
            estadoTorneio.put("fase", "QUARTAS"); // QUARTAS, SEMIFINAL, FINAL
            estadoTorneio.put("partidaAtual", 0);
            estadoTorneio.put("vencedores", new ArrayList<Jogador>());

            session.setAttribute("estadoTorneio", estadoTorneio);

            // Retorna o chaveamento inicial
            List<Map<String, String>> chaveamentoJson = serializarParticipantes(participantes);

            return ResponseEntity.ok(Map.of(
                    "torneioId", torneioId,
                    "mensagem", "Torneio iniciado com sucesso!",
                    "participantes", chaveamentoJson,
                    "totalPartidas", 7 // 4 quartas + 2 semi + 1 final
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * Retorna o estado atual do torneio
     */
    @GetMapping("/estado")
    public ResponseEntity<?> obterEstado(HttpSession session) {
        try {
            Map<String, Object> estadoTorneio = (Map<String, Object>) session.getAttribute("estadoTorneio");

            if (estadoTorneio == null) {
                return ResponseEntity.ok(Map.of("torneioAtivo", false));
            }

            List<Jogador> participantes = (List<Jogador>) estadoTorneio.get("participantes");
            List<Jogador> vencedores = (List<Jogador>) estadoTorneio.get("vencedores");
            String fase = (String) estadoTorneio.get("fase");
            int partidaAtual = (int) estadoTorneio.get("partidaAtual");

            return ResponseEntity.ok(Map.of(
                    "torneioAtivo", true,
                    "fase", fase,
                    "partidaAtual", partidaAtual,
                    "totalParticipantes", participantes.size(),
                    "vencedores", vencedores.size()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * Processa a próxima partida do torneio
     */
    @PostMapping("/proxima-partida")
    public ResponseEntity<?> proximaPartida(HttpSession session) {
        try {
            Map<String, Object> estadoTorneio = (Map<String, Object>) session.getAttribute("estadoTorneio");

            if (estadoTorneio == null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Nenhum torneio em andamento"));
            }

            List<Jogador> participantes = (List<Jogador>) estadoTorneio.get("participantes");
            List<Jogador> vencedores = (List<Jogador>) estadoTorneio.get("vencedores");
            String fase = (String) estadoTorneio.get("fase");
            int partidaAtual = (int) estadoTorneio.get("partidaAtual");
            Long torneioId = (Long) estadoTorneio.get("torneioId");

            // Determina os jogadores da partida atual
            int index = partidaAtual * 2;

            if (index >= participantes.size()) {
                // Avança para a próxima fase
                if (fase.equals("QUARTAS") && vencedores.size() == 4) {
                    estadoTorneio.put("fase", "SEMIFINAL");
                    estadoTorneio.put("participantes", new ArrayList<>(vencedores));
                    estadoTorneio.put("vencedores", new ArrayList<>());
                    estadoTorneio.put("partidaAtual", 0);
                    session.setAttribute("estadoTorneio", estadoTorneio);

                    // RETORNA OS NOVOS PARTICIPANTES
                    return ResponseEntity.ok(Map.of(
                            "fase", "SEMIFINAL",
                            "mensagem", "Quartas de final concluídas! Iniciando semifinais.",
                            "participantes", serializarParticipantes(vencedores)
                    ));

                } else if (fase.equals("SEMIFINAL") && vencedores.size() == 2) {
                    estadoTorneio.put("fase", "FINAL");
                    estadoTorneio.put("participantes", new ArrayList<>(vencedores));
                    estadoTorneio.put("vencedores", new ArrayList<>());
                    estadoTorneio.put("partidaAtual", 0);
                    session.setAttribute("estadoTorneio", estadoTorneio);

                    // RETORNA OS FINALISTAS
                    return ResponseEntity.ok(Map.of(
                            "fase", "FINAL",
                            "mensagem", "Semifinais concluídas! Preparando a grande final!",
                            "participantes", serializarParticipantes(vencedores)
                    ));

                } else if (fase.equals("FINAL") && vencedores.size() == 1) {
                    // Torneio finalizado
                    Jogador campeao = vencedores.get(0);

                    int posicaoFinal = (campeao instanceof JogadorHumano) ? 1 : participantes.size();
                    torneioService.finalizarTorneio(torneioId, posicaoFinal);

                    session.removeAttribute("estadoTorneio");

                    return ResponseEntity.ok(Map.of(
                            "torneioFinalizado", true,
                            "campeao", campeao.getNome(),
                            "posicaoFinal", posicaoFinal,
                            "posicaoJogador", posicaoFinal,
                            "mensagem", (campeao instanceof JogadorHumano)
                                    ? "🏆 PARABÉNS! Você é o campeão do torneio!"
                                    : "😢 Você foi eliminado. O campeão foi " + campeao.getNome()
                    ));
                }
            }

            Jogador jogador1 = participantes.get(index);
            Jogador jogador2 = participantes.get(index + 1);

            // Se ambos sao bots, simula a partida automaticamente
            if (jogador1 instanceof IA && jogador2 instanceof IA) {
                Jogador vencedor = torneioService.simularPartidaBots(jogador1, jogador2);
                vencedores.add(vencedor);

                // Salva no banco
                String nivelIA = ((IA) vencedor).getNivel().toString();
                torneioService.salvarPartida(torneioId, vencedor.getNome(), nivelIA);

                estadoTorneio.put("partidaAtual", partidaAtual + 1);
                estadoTorneio.put("vencedores", vencedores);
                session.setAttribute("estadoTorneio", estadoTorneio);

                return ResponseEntity.ok(Map.of(
                        "tipo", "BOT_VS_BOT",
                        "jogador1", jogador1.getNome(),
                        "jogador2", jogador2.getNome(),
                        "vencedor", vencedor.getNome(),
                        "mensagem", vencedor.getNome() + " venceu automaticamente!",
                        "proximaPartida", true
                ));
            }

            // Se é jogador vs bot, cria a partida
            Partida partida = new Partida(jogador1, jogador2);
            partida.inicarPartida();

            session.setAttribute("partidaTorneio", partida);
            session.setAttribute("jogadoresTorneio", List.of(jogador1, jogador2));

            return ResponseEntity.ok(Map.of(
                    "tipo", "HUMANO_VS_BOT",
                    "jogador1", jogador1.getNome(),
                    "jogador2", jogador2.getNome(),
                    "tabuleiro", serializarTabuleiro(partida.getTabuleiro()),
                    "turno", partida.getTurnoAtual().toString(),
                    "mensagem", "Sua partida começou! Boa sorte!"
            ));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * Registra o resultado de uma partida do jogador
     */
    @PostMapping("/registrar-resultado")
    public ResponseEntity<?> registrarResultado(@RequestBody Map<String, String> dados, HttpSession session) {
        try {
            Map<String, Object> estadoTorneio = (Map<String, Object>) session.getAttribute("estadoTorneio");
            Partida partida = (Partida) session.getAttribute("partidaTorneio");

            if (estadoTorneio == null || partida == null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Nenhuma partida de torneio em andamento"));
            }

            String resultado = dados.get("resultado"); // "VITORIA" ou "DERROTA"
            List<Jogador> jogadores = (List<Jogador>) session.getAttribute("jogadoresTorneio");
            List<Jogador> vencedores = (List<Jogador>) estadoTorneio.get("vencedores");
            int partidaAtual = (int) estadoTorneio.get("partidaAtual");
            Long torneioId = (Long) estadoTorneio.get("torneioId");

            Jogador vencedor;
            if (resultado.equals("VITORIA")) {
                vencedor = jogadores.get(0); // Jogador humano
            } else {
                vencedor = jogadores.get(1); // Bot
            }

            vencedores.add(vencedor);

            // Salva no banco
            String nivelIA = (vencedor instanceof IA) ? ((IA) vencedor).getNivel().toString() : "JOGADOR";
            torneioService.salvarPartida(torneioId, vencedor.getNome(), nivelIA);

            estadoTorneio.put("partidaAtual", partidaAtual + 1);
            estadoTorneio.put("vencedores", vencedores);
            session.setAttribute("estadoTorneio", estadoTorneio);

            // Limpa a partida atual
            session.removeAttribute("partidaTorneio");
            session.removeAttribute("jogadoresTorneio");

            if (resultado.equals("DERROTA")) {
                // Jogador foi eliminado
                String fase = (String) estadoTorneio.get("fase");
                int posicaoFinal = calcularPosicaoEliminacao(fase, partidaAtual);
                torneioService.finalizarTorneio(torneioId, posicaoFinal);

                session.removeAttribute("estadoTorneio");

                return ResponseEntity.ok(Map.of(
                        "torneioFinalizado", true,
                        "eliminado", true,
                        "posicaoFinal", posicaoFinal,
                        "mensagem", "Você foi eliminado na fase de " + fase + ". Posição final: " + posicaoFinal + "º lugar"
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "vencedor", vencedor.getNome(),
                    "mensagem", "Partida finalizada! " + vencedor.getNome() + " avança no torneio.",
                    "continua", true
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * Desiste do torneio
     */
    @PostMapping("/desistir")
    public ResponseEntity<?> desistir(HttpSession session) {
        try {
            Map<String, Object> estadoTorneio = (Map<String, Object>) session.getAttribute("estadoTorneio");

            if (estadoTorneio == null) {
                return ResponseEntity.badRequest().body(Map.of("erro", "Nenhum torneio em andamento"));
            }

            Long torneioId = (Long) estadoTorneio.get("torneioId");
            String fase = (String) estadoTorneio.get("fase");
            int partidaAtual = (int) estadoTorneio.get("partidaAtual");

            int posicaoFinal = calcularPosicaoEliminacao(fase, partidaAtual);
            torneioService.finalizarTorneio(torneioId, posicaoFinal);

            session.removeAttribute("estadoTorneio");
            session.removeAttribute("partidaTorneio");
            session.removeAttribute("jogadoresTorneio");

            return ResponseEntity.ok(Map.of(
                    "mensagem", "Você desistiu do torneio",
                    "posicaoFinal", posicaoFinal
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    /**
     * Serializa lista de participantes para JSON
     */
    private List<Map<String, String>> serializarParticipantes(List<Jogador> participantes) {
        List<Map<String, String>> chaveamentoJson = new ArrayList<>();
        for (int i = 0; i < participantes.size(); i++) {
            Jogador j = participantes.get(i);
            chaveamentoJson.add(Map.of(
                    "id", String.valueOf(i),
                    "nome", j.getNome(),
                    "tipo", (j instanceof JogadorHumano) ? "HUMANO" : "IA",
                    "nivel", (j instanceof IA) ? ((IA) j).getNivel().toString() : "N/A"
            ));
        }
        return chaveamentoJson;
    }

    private int calcularPosicaoEliminacao(String fase, int partidaAtual) {
        switch (fase) {
            case "QUARTAS":
                return 8 - (partidaAtual * 2);
            case "SEMIFINAL":
                return 3 + partidaAtual;
            case "FINAL":
                return 2;
            default:
                return 8;
        }
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

    @GetMapping("/meus-torneios")
    public ResponseEntity<?> meusTorneios(HttpSession session) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");

            if (usuario == null) {
                return ResponseEntity.status(401).body(Map.of("erro", "Usuário não está logado!"));
            }

            List<Torneio> torneios = torneioService.listarTorneiosDoUsuario(usuario.getId());

            List<Map<String, Object>> torneiosJson = new java.util.ArrayList<>();
            for (Torneio t : torneios) {
                torneiosJson.add(Map.of(
                        "id", t.getId(),
                        "posicaoFinal", t.getPosicaoFinal(),
                        "medalha", getMedalha(t.getPosicaoFinal())
                ));
            }

            return ResponseEntity.ok(torneiosJson);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    private String getMedalha(int posicao) {
        switch (posicao) {
            case 1: return "🥇";
            case 2: return "🥈";
            case 3: return "🥉";
            default: return String.valueOf(posicao) + "º";
        }
    }
}