package com.example.damas.Entities;

import com.example.damas.Enums.Cor;
import com.example.damas.Enums.Dificuldade;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IA extends Jogador {

    private Dificuldade nivel;

    public IA(String nome, Cor cor, Dificuldade nivel) {
        super(nome, cor);
        this.nivel = nivel;
    }

    @Override
    public Movimento escolherJogada(Partida partida) {
        if (nivel == null) {
            nivel = Dificuldade.MEDIO; // Default
        }

        switch (nivel) {
            case FACIL:
                return jogadaAleatoria(partida, getCor());
            case MEDIO:
                return jogadaMedio(partida, getCor());
            case DIFICIL:
                return jogadaDificil(partida, getCor());
            default:
                return jogadaAleatoria(partida, getCor());
        }
    }


    /**
     * NIVEL FACIL - Jogadas Aleatórias
     * Escolhe um movimento válido aleatoriamente
     */
    private Movimento jogadaAleatoria(Partida partida, Cor cor) {
        List<Movimento> todosMovimentos = obterTodosMovimentos(partida, cor);

        if (todosMovimentos.isEmpty()) return null;

        Random random = new Random();
        return todosMovimentos.get(random.nextInt(todosMovimentos.size()));
    }

    /**
     * NIVEL MEDIO - Algoritmo Greedy com Heurística
     * Avalia cada movimento e escolhe o melhor baseado em pontuação
     */
    private Movimento jogadaMedio(Partida partida, Cor cor) {
        List<Movimento> todosMovimentos = obterTodosMovimentos(partida, cor);

        if (todosMovimentos.isEmpty()) return null;

        Movimento melhorMovimento = todosMovimentos.get(0);
        int melhorPontuacao = avaliarMovimento(melhorMovimento, partida.getTabuleiro(), cor);

        for (Movimento mov : todosMovimentos) {
            int pontuacao = avaliarMovimento(mov, partida.getTabuleiro(), cor);
            if (pontuacao > melhorPontuacao) {
                melhorPontuacao = pontuacao;
                melhorMovimento = mov;
            }
        }

        return melhorMovimento;
    }

    /**
     * Função de avaliação heurística para movimentos
     */
    private int avaliarMovimento(Movimento mov, Tabuleiro tabuleiro, Cor cor) {
        int pontos = 0;

        // Captura vale muitos pontos
        if (mov.isCaptura()) {
            pontos += 100;
            Peca pecaCapturada = tabuleiro.getCasa(mov.getPosPecaCapturada()).getPeca();
            if (pecaCapturada.dama()) {
                pontos += 50; // Capturar dama vale mais
            }
        }

        // Promover a dama
        int linhaFinal = (cor == Cor.BRANCO) ? 0 : 7;
        if (mov.getDestino().getLinha() == linhaFinal) {
            pontos += 80;
        }

        // Avançar no tabuleiro
        int direcao = (cor == Cor.BRANCO) ? -1 : 1;
        int avanco = (mov.getDestino().getLinha() - mov.getOrigem().getLinha()) * direcao;
        pontos += avanco * 5;

        // Centralização (controlar centro é bom)
        int distanciaCentro = Math.abs(mov.getDestino().getColuna() - 3) +
                Math.abs(mov.getDestino().getLinha() - 3);
        pontos += (6 - distanciaCentro) * 2;

        return pontos;
    }

    /**
     * NÍVEL DIFÍCIL - Minimax com Poda Alfa-Beta
     * Simula 5 jogadas à frente para escolher o melhor movimento
     */
    private Movimento jogadaDificil(Partida partida, Cor cor) {
        int profundidade = 5;
        // O minimax é complexo e sua adaptação para o estado de combo da partida
        // exigiria uma refatoração mais profunda. Por enquanto, ele delega
        // para o nível médio para garantir funcionalidade.
        // TODO: Adaptar Minimax para lidar com o estado de combo da Partida.
        return jogadaMedio(partida, cor);
    }

    private class ResultadoMinimax {
        Movimento movimento;
        int pontuacao;

        ResultadoMinimax(Movimento m, int p) {
            this.movimento = m;
            this.pontuacao = p;
        }
    }

    /**
     * Algoritmo Minimax com Poda Alfa-Beta
     */
    private ResultadoMinimax minimax(Tabuleiro tab, int profundidade, int alfa, int beta,
                                     boolean maximizando, Cor cor) {
        // ... (O código do minimax permanece o mesmo por enquanto)
        return null; // Placeholder
    }

    /**
     * Avalia o estado do tabuleiro
     */
    private int avaliarTabuleiro(Tabuleiro tab, Cor cor) {
        int pontos = 0;

        for (int i = 0; i < tab.getN(); i++) {
            for (int j = 0; j < tab.getN(); j++) {
                Posicao pos = new Posicao(i, j);
                if (tab.Ocupada(pos)) {
                    Peca peca = tab.getCasa(pos).getPeca();
                    int valor = peca.dama() ? 30 : 10;

                    if (peca.getCor() == cor) {
                        pontos += valor;
                    } else {
                        pontos -= valor;
                    }
                }
            }
        }

        return pontos;
    }

    private List<Movimento> obterTodosMovimentos(Partida partida, Cor cor) {
        Tabuleiro tabuleiro = partida.getTabuleiro();

        // Se estiver em combo, só pode mover a peça do combo
        if (partida.isEmCombo()) {
            Posicao posCombo = partida.getPosicaoCombo();
            if (tabuleiro.Ocupada(posCombo) && tabuleiro.getCasa(posCombo).getPeca().getCor() == cor) {
                Peca peca = tabuleiro.getCasa(posCombo).getPeca();
                return tabuleiro.getMovimentoValido(peca, posCombo);
            } else {
                return new ArrayList<>(); // Não deveria acontecer, mas por segurança
            }
        }

        // Se não estiver em combo, busca todos os movimentos
        List<Movimento> todosMovimentos = new ArrayList<>();
        for (int i = 0; i < tabuleiro.getN(); i++) {
            for (int j = 0; j < tabuleiro.getN(); j++) {
                Posicao pos = new Posicao(i, j);
                if (tabuleiro.Ocupada(pos) && tabuleiro.getCasa(pos).getPeca().getCor() == cor) {
                    List<Movimento> movs = tabuleiro.getMovimentoValido(
                            tabuleiro.getCasa(pos).getPeca(), pos
                    );
                    todosMovimentos.addAll(movs);
                }
            }
        }
        return todosMovimentos;
    }

    private boolean jogoTerminado(Tabuleiro tab) {
        boolean temBrancas = false;
        boolean temPretas = false;

        for (int i = 0; i < tab.getN(); i++) {
            for (int j = 0; j < tab.getN(); j++) {
                Posicao pos = new Posicao(i, j);
                if (tab.Ocupada(pos)) {
                    Peca peca = tab.getCasa(pos).getPeca();
                    if (peca.getCor() == Cor.BRANCO) {
                        temBrancas = true;
                    } else {
                        temPretas = true;
                    }
                }
            }
        }

        return !temBrancas || !temPretas;
    }

    private Cor corOposta(Cor cor) {
        return (cor == Cor.BRANCO) ? Cor.PRETO : Cor.BRANCO;
    }

    private Tabuleiro copiarTabuleiro(Tabuleiro original) {
        Tabuleiro copia = new Tabuleiro();

        // Remove todas as peças iniciais
        for (int i = 0; i < copia.getN(); i++) {
            for (int j = 0; j < copia.getN(); j++) {
                Posicao pos = new Posicao(i, j);
                copia.remPeca(pos);
            }
        }

        // Copia as peças do tabuleiro original
        for (int i = 0; i < original.getN(); i++) {
            for (int j = 0; j < original.getN(); j++) {
                Posicao pos = new Posicao(i, j);
                if (original.Ocupada(pos)) {
                    Peca pecaOriginal = original.getCasa(pos).getPeca();
                    Peca pecaCopia = new Peca(pecaOriginal.getCor());

                    if (pecaOriginal.dama()) {
                        pecaCopia.promover();
                    }

                    copia.addPeca(pecaCopia, pos);
                }
            }
        }

        return copia;
    }

    public Dificuldade getNivel() {
        return nivel;
    }

    public void setNivel(Dificuldade nivel) {
        this.nivel = nivel;
    }
}