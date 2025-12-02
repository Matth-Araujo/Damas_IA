package com.example.damas.Entities;

import com.example.damas.Enums.Cor;
import com.example.damas.Enums.StatusPartida;

import java.util.List;

public class Partida {
    private Long id;
    private Tabuleiro tabuleiro;
    private Jogador jogador1;
    private Jogador jogador2;
    private Cor turnoAtual;
    private StatusPartida status;

    private int contadorMovimentos = 0;
    private int movimentosSemCaptura = 0;
    private long tempoInicio;
    private static final int LIMITE_MOVIMENTOS_SEM_CAPTURA = 40;

    private boolean emCombo = false;
    private Posicao posicaoCombo = null;

    public Partida(Jogador jogador1, Jogador jogador2) {
        this.jogador1 = jogador1;
        this.jogador2 = jogador2;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    public Cor getTurnoAtual() {
        return turnoAtual;
    }

    public StatusPartida getStatus() {
        return status;
    }

    public Jogador getJogador1() {
        return jogador1;
    }

    public Jogador getJogador2() {
        return jogador2;
    }

    public int getContadorMovimentos() {
        return contadorMovimentos;
    }

    public int getDuracaoSegundos() {
        return (int) ((System.currentTimeMillis() - tempoInicio) / 1000);
    }

    public boolean isEmCombo() {
        return emCombo;
    }

    public Posicao getPosicaoCombo() {
        return posicaoCombo;
    }

    public void inicarPartida(){
        this.tabuleiro = new Tabuleiro();
        this.turnoAtual = Cor.BRANCO;
        this.status = StatusPartida.EM_ANDAMENTO;
        this.contadorMovimentos = 0;
        this.movimentosSemCaptura = 0;
        this.tempoInicio = System.currentTimeMillis();
        this.emCombo = false;
        this.posicaoCombo = null;
    }

    // Este metodo funciona tanto para IA quanto para Humano
    public boolean processarTurno() {
        if (status != StatusPartida.EM_ANDAMENTO) {
            return false;
        }

        Jogador jogadorAtual = (turnoAtual == Cor.BRANCO) ? jogador1 : jogador2;

        // Para jogador humano, o fluxo é controlado pelo Controller (um movimento por requisição)
        if (jogadorAtual instanceof JogadorHumano) {
            Movimento movimento = jogadorAtual.escolherJogada(this);
            if (movimento != null) {
                return realizarJogada(movimento);
            }
            return false;
        }

        // Para IA
        boolean jogadaRealizadaNoTurno = false;
        while (true) {
            Movimento movimento = jogadorAtual.escolherJogada(this);

            if (movimento == null) {
                break; // IA não tem mais movimentos (ou não escolheu), sai do loop
            }

            if (!realizarJogada(movimento)) {
                // A IA escolheu um movimento inválido, o que não deveria acontecer.
                // Saida para evitar um loop infinito.
                break;
            }
            jogadaRealizadaNoTurno = true;

            // Se a IA não estiver mais em um estado de combo, seu turno terminou.
            if (!emCombo) {
                break;
            }
            // Se emCombo for verdadeiro, o loop continua e a IA fará outra captura.
        }
        return jogadaRealizadaNoTurno;
    }

    public boolean realizarJogada(Movimento movimento){
        Peca peca = tabuleiro.getCasa(movimento.getOrigem()).getPeca();
        if (peca == null || peca.getCor() != turnoAtual) {
            return false;
        }

        if (emCombo && !movimento.getOrigem().equals(posicaoCombo)) {
            return false;
        }

        List<Movimento> movimentosValidos = tabuleiro.getMovimentoValido(peca, movimento.getOrigem());
        if (!movimentosValidos.contains(movimento)) {
            return false;
        }

        tabuleiro.moverPeca(movimento);
        contadorMovimentos++;

        if (movimento.isCaptura()) {
            movimentosSemCaptura = 0;
        } else {
            movimentosSemCaptura++;
        }

        if ((peca.getCor() == Cor.BRANCO && movimento.getDestino().getLinha() == 0) ||
                (peca.getCor() == Cor.PRETO && movimento.getDestino().getLinha() == tabuleiro.getN() - 1)) {
            peca.promover();
        }

        if (movimento.isCaptura() && podeCapturarNovamente(movimento.getDestino(), turnoAtual)) {
            emCombo = true;
            posicaoCombo = movimento.getDestino();
            return true;
        }

        emCombo = false;
        posicaoCombo = null;

        trocarTurno();

        if (movimentosSemCaptura >= LIMITE_MOVIMENTOS_SEM_CAPTURA) {
            status = StatusPartida.EMPATE;
            return true;
        }

        status = verificarFimDeJogo();
        return true;
    }

    private boolean podeCapturarNovamente(Posicao posAtual, Cor cor) {
        if (!tabuleiro.Ocupada(posAtual)) {
            return false;
        }

        Peca peca = tabuleiro.getCasa(posAtual).getPeca();
        List<Movimento> capturasPossiveis = tabuleiro.getMovimentoValido(peca, posAtual);

        for (Movimento mov : capturasPossiveis) {
            if (mov.isCaptura()) {
                return true;
            }
        }
        return false;
    }

    public StatusPartida verificarFimDeJogo(){
        if (semPecas(Cor.BRANCO) || semMovimentos(Cor.BRANCO)) {
            return StatusPartida.VITORIA_PRETO;
        }
        if (semPecas(Cor.PRETO) || semMovimentos(Cor.PRETO)) {
            return StatusPartida.VITORIA_BRANCO;
        }
        return StatusPartida.EM_ANDAMENTO;
    }

    public void trocarTurno(){
        turnoAtual = (turnoAtual == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
    }

    private boolean semPecas(Cor cor) {
        for (int i = 0; i < tabuleiro.getN(); i++) {
            for (int j = 0; j < tabuleiro.getN(); j++) {
                Posicao pos = new Posicao(i, j);
                if (tabuleiro.Ocupada(pos) && tabuleiro.getCasa(pos).getPeca().getCor() == cor) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean semMovimentos(Cor cor) {
        for (int i = 0; i < tabuleiro.getN(); i++) {
            for (int j = 0; j < tabuleiro.getN(); j++) {
                Posicao pos = new Posicao(i, j);
                if (tabuleiro.Ocupada(pos) && tabuleiro.getCasa(pos).getPeca().getCor() == cor) {
                    Peca peca = tabuleiro.getCasa(pos).getPeca();
                    if (!tabuleiro.getMovimentoValido(peca, pos).isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public int getMovimentosSemCaptura() {
        return movimentosSemCaptura;
    }
}