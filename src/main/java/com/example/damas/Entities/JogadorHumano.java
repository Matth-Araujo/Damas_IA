package com.example.damas.Entities;

import com.example.damas.Enums.Cor;

import java.util.List;

public class JogadorHumano extends Jogador {

    // Armazena o ultimo movimento escolhido pelo jogador
    private Movimento movimentoPendente;

    public JogadorHumano(String nome, Cor cor) {
        super(nome, cor);
        this.movimentoPendente = null;
    }


    @Override
    public Movimento escolherJogada(Partida partida) {
        // O movimento já foi escolhido pela interface web
        // e armazenado em movimentoPendente
        Movimento mov = this.movimentoPendente;
        this.movimentoPendente = null; // Limpa após uso
        return mov;
    }

    /**
     * origem Posição de origem da peça
     * destino Posição de destino
     * tabuleiro Tabuleiro atual para validação
     * Retorna true se o movimento é válido, false caso contrário
     */
    public boolean definirJogada(Posicao origem, Posicao destino, Tabuleiro tabuleiro, Partida partida) {
        // Se esta em combo, so pode mover a peça que esta capturando
        if (partida.isEmCombo() && !origem.equals(partida.getPosicaoCombo())) {
            return false;
        }

        // Verifica se há peça na origem
        if (!tabuleiro.Ocupada(origem)) {
            return false;
        }

        Peca peca = tabuleiro.getCasa(origem).getPeca();

        // Verifica se é uma peça do jogador
        if (peca.getCor() != getCor()) {
            return false;
        }

        // Obtém todos os movimentos válidos para essa peça
        List<Movimento> movimentosValidos = tabuleiro.getMovimentoValido(peca, origem);

        // Procura o movimento que corresponde ao destino escolhido
        for (Movimento mov : movimentosValidos) {
            if (mov.getDestino().equals(destino)) {
                this.movimentoPendente = mov;
                return true;
            }
        }

        // Movimento inválido
        return false;
    }

    /**
     * Retorna todos os movimentos válidos disponíveis para o jogador
     */
    public List<Movimento> obterMovimentosDisponiveis(Tabuleiro tabuleiro) {
        List<Movimento> todosMovimentos = new java.util.ArrayList<>();

        for (int i = 0; i < tabuleiro.getN(); i++) {
            for (int j = 0; j < tabuleiro.getN(); j++) {
                Posicao pos = new Posicao(i, j);
                if (tabuleiro.Ocupada(pos) &&
                        tabuleiro.getCasa(pos).getPeca().getCor() == getCor()) {

                    Peca peca = tabuleiro.getCasa(pos).getPeca();
                    List<Movimento> movs = tabuleiro.getMovimentoValido(peca, pos);
                    todosMovimentos.addAll(movs);
                }
            }
        }

        return todosMovimentos;
    }

    /**
     * Retorna movimentos válidos para uma peça específica
     */
    public List<Movimento> obterMovimentosDaPeca(Posicao posicao, Tabuleiro tabuleiro) {
        if (!tabuleiro.Ocupada(posicao)) {
            return new java.util.ArrayList<>();
        }

        Peca peca = tabuleiro.getCasa(posicao).getPeca();

        if (peca.getCor() != getCor()) {
            return new java.util.ArrayList<>();
        }

        return tabuleiro.getMovimentoValido(peca, posicao);
    }

    /**
     * Verifica se o jogador tem movimentos disponíveis
     * Retorna true se tem movimentos validos, false caso contrario
     */
    public boolean temMovimentosDisponiveis(Tabuleiro tabuleiro) {
        return !obterMovimentosDisponiveis(tabuleiro).isEmpty();
    }

    // Verifica se o jogador deve continuar capturando
    public boolean deveCapturarNovamente(Posicao posicaoAtual, Tabuleiro tabuleiro) {
        if (!tabuleiro.Ocupada(posicaoAtual)) {
            return false;
        }

        Peca peca = tabuleiro.getCasa(posicaoAtual).getPeca();
        if (peca.getCor() != getCor()) {
            return false;
        }

        List<Movimento> movimentos = tabuleiro.getMovimentoValido(peca, posicaoAtual);

        for (Movimento mov : movimentos) {
            if (mov.isCaptura()) {
                return true;
            }
        }

        return false;
    }

    public Movimento getMovimentoPendente() {
        return movimentoPendente;
    }

    public void setMovimentoPendente(Movimento movimentoPendente) {
        this.movimentoPendente = movimentoPendente;
    }
}