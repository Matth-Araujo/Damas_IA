package com.example.damas.Entities;

import com.example.damas.Enums.Cor;
import com.example.damas.Enums.Dificuldade;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class IA extends Jogador{

    private Dificuldade nivel;

    public IA(String nome, Cor cor) {
        super(nome, cor);
    }

    public Movimento jogadaAleatoria(Tabuleiro tabuleiro, Cor cor){
        List<Movimento> todosMovimentos = new ArrayList<>();
        // Coleta todos os movimentos possíveis
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

        if (todosMovimentos.isEmpty()) return null;

        // Escolhe aleatoriamente
        Random random = new Random();
        return todosMovimentos.get(random.nextInt(todosMovimentos.size()));
    }

    public Movimento jogadaMedio(Tabuleiro tabuleiro, Cor cor) {
        List<Movimento> todosMovimentos = obterTodosMovimentos(tabuleiro, cor);

        if (todosMovimentos.isEmpty()) return null;

        Movimento melhorMovimento = todosMovimentos.get(0);
        int melhorPontuacao = avaliarMovimento(melhorMovimento, tabuleiro, cor);

        for (Movimento mov : todosMovimentos) {
            int pontuacao = avaliarMovimento(mov, tabuleiro, cor);
            if (pontuacao > melhorPontuacao) {
                melhorPontuacao = pontuacao;
                melhorMovimento = mov;
            }
        }

        return melhorMovimento;
    }

    private int avaliarMovimento(Movimento mov, Tabuleiro tabuleiro, Cor cor) {
        int pontos = 0;

        // Captura vale muitos pontos
        if (mov.isCaptura()) {
            pontos += 100;
            Peca pecaCapturada = tabuleiro.getCasa(mov.getPosPecaCapturada()).getPeca();
            if (pecaCapturada.dama()) {
                pontos += 50; // Capturar dama vale mais pontos
            }
        }

        // Promover a dama
        int linhaFinal = (cor == Cor.BRANCO) ? 0 : 7;
        if (mov.getDestino().getLinha() == linhaFinal) {
            pontos += 80;
        }

        // Avancar no tabuleiro
        int direcao = (cor == Cor.BRANCO) ? -1 : 1;
        int avanco = (mov.getDestino().getLinha() - mov.getOrigem().getLinha()) * direcao;
        pontos += avanco * 5;

        // Centralizacao (controlar centro é bom)
        int distanciaCentro = Math.abs(mov.getDestino().getColuna() - 3) +
                Math.abs(mov.getDestino().getLinha() - 3);
        pontos += (6 - distanciaCentro) * 2;

        return pontos;
    }

    public Movimento jogadaDificil(Tabuleiro tabuleiro, Cor cor) {
        int profundidade = 5; // Olha 5 jogadas a frente
        return minimax(tabuleiro, profundidade, Integer.MIN_VALUE, Integer.MAX_VALUE, true, cor).movimento;
    }

    private class ResultadoMinimax {
        Movimento movimento;
        int pontuacao;

        ResultadoMinimax(Movimento m, int p) {
            this.movimento = m;
            this.pontuacao = p;
        }
    }

    private ResultadoMinimax minimax(Tabuleiro tab, int profundidade, int alfa, int beta,
                                     boolean maximizando, Cor cor) {
        // Caso base
        if (profundidade == 0 || jogoTerminado(tab)) {
            return new ResultadoMinimax(null, avaliarTabuleiro(tab, cor));
        }

        List<Movimento> movimentos = obterTodosMovimentos(tab,
                maximizando ? cor : corOposta(cor));

        if (movimentos.isEmpty()) {
            return new ResultadoMinimax(null, maximizando ? -10000 : 10000);
        }

        Movimento melhorMov = movimentos.get(0);

        if (maximizando) {
            int maxPont = Integer.MIN_VALUE;
            for (Movimento mov : movimentos) {
                Tabuleiro tabCopia = copiarTabuleiro(tab);
                tabCopia.moverPeca(mov);

                int pont = minimax(tabCopia, profundidade - 1, alfa, beta, false, cor).pontuacao;

                if (pont > maxPont) {
                    maxPont = pont;
                    melhorMov = mov;
                }

                alfa = Math.max(alfa, pont);
                if (beta <= alfa) break; // Poda
            }
            return new ResultadoMinimax(melhorMov, maxPont);
        } else {
            int minPont = Integer.MAX_VALUE;
            for (Movimento mov : movimentos) {
                Tabuleiro tabCopia = copiarTabuleiro(tab);
                tabCopia.moverPeca(mov);

                int pont = minimax(tabCopia, profundidade - 1, alfa, beta, true, cor).pontuacao;

                if (pont < minPont) {
                    minPont = pont;
                    melhorMov = mov;
                }

                beta = Math.min(beta, pont);
                if (beta <= alfa) break; // Poda
            }
            return new ResultadoMinimax(melhorMov, minPont);
        }
    }

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

    private List<Movimento> obterTodosMovimentos(Tabuleiro tabuleiro, Cor cor) {
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

        // Copia as pecas do tabuleiro original
        for (int i = 0; i < original.getN(); i++) {
            for (int j = 0; j < original.getN(); j++) {
                Posicao pos = new Posicao(i, j);
                if (original.Ocupada(pos)) {
                    Peca pecaOriginal = original.getCasa(pos).getPeca();
                    Peca pecaCopia = new Peca(pecaOriginal.getCor());

                    // Se a peca original é dama, promove a copia
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