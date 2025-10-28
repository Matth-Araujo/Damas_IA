package com.example.damas.Entities;

import com.example.damas.Enums.Cor;
import com.example.damas.Enums.StatusPartida;

import java.util.List;

public class Partida {
    private Tabuleiro tabuleiro;
    private Jogador jogador1;
    private Jogador jogador2;
    private Cor turnoAtual;
    private StatusPartida status;

    public Partida(Jogador jogador1, Jogador jogador2) {
        this.jogador1 = jogador1;
        this.jogador2 = jogador2;
    }

    public void inicarPartida(){
        this.tabuleiro = new Tabuleiro();
        this.turnoAtual = Cor.BRANCO;
        this.status = StatusPartida.EM_ANDAMENTO;
    }

    public boolean realizarJogada(Movimento movimento){
        Peca peca = tabuleiro.getCasa(movimento.getOrigem()).getPeca();
        if (peca == null || peca.getCor() != turnoAtual) {
            return false;
        }

        List<Movimento> movimentosValidos = tabuleiro.getMovimentoValido(peca, movimento.getOrigem());
        if (!movimentosValidos.contains(movimento)) {
            return false;
        }

        tabuleiro.moverPeca(movimento);

        if ((peca.getCor() == Cor.BRANCO && movimento.getDestino().getLinha() == 0) ||
                (peca.getCor() == Cor.PRETO && movimento.getDestino().getLinha() == tabuleiro.getN() - 1)) {
            peca.promover();
        }

        trocarTurno();
        status = verificarFimDeJogo();
        return true;
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
}
