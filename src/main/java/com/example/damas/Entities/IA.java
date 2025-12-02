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
     */
    private Movimento jogadaAleatoria(Partida partida, Cor cor) {
        List<Movimento> todosMovimentos = obterTodosMovimentos(partida, cor);

        if (todosMovimentos.isEmpty()) return null;

        Random random = new Random();
        return todosMovimentos.get(random.nextInt(todosMovimentos.size()));
    }

    /**
     * NIVEL MEDIO - Algoritmo Greedy com Heurística
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

        if (mov.isCaptura()) {
            pontos += 100;
            Peca pecaCapturada = tabuleiro.getCasa(mov.getPosPecaCapturada()).getPeca();
            if (pecaCapturada != null && pecaCapturada.dama()) {
                pontos += 50;
            }
        }

        int linhaFinal = (cor == Cor.BRANCO) ? 0 : 7;
        if (mov.getDestino().getLinha() == linhaFinal) {
            pontos += 80;
        }

        int direcao = (cor == Cor.BRANCO) ? -1 : 1;
        int avanco = (mov.getDestino().getLinha() - mov.getOrigem().getLinha()) * direcao;
        pontos += avanco * 5;

        int distanciaCentro = Math.abs(mov.getDestino().getColuna() - 3) +
                Math.abs(mov.getDestino().getLinha() - 3);
        pontos += (6 - distanciaCentro) * 2;

        return pontos;
    }

    /**
     * NÍVEL DIFÍCIL - Minimax com Poda Alfa-Beta
     * Simula jogadas à frente para escolher o melhor movimento
     */
    private Movimento jogadaDificil(Partida partida, Cor cor) {
        List<Movimento> todosMovimentos = obterTodosMovimentos(partida, cor);

        if (todosMovimentos.isEmpty()) return null;

        int profundidade = 4; 

        Movimento melhorMovimento = todosMovimentos.get(0);
        int melhorPontuacao = Integer.MIN_VALUE;

        for (Movimento mov : todosMovimentos) {
            // Simula o movimento
            Tabuleiro tabCopia = copiarTabuleiro(partida.getTabuleiro());
            Peca pecaCopia = tabCopia.getCasa(mov.getOrigem()).getPeca();

            // Aplica o movimento no tabuleiro copiado
            tabCopia.moverPeca(mov);

            // Verifica promoção
            if ((pecaCopia.getCor() == Cor.BRANCO && mov.getDestino().getLinha() == 0) ||
                    (pecaCopia.getCor() == Cor.PRETO && mov.getDestino().getLinha() == 7)) {
                pecaCopia.promover();
            }

            // Avalia recursivamente
            int pontuacao = minimax(tabCopia, profundidade - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false, cor);

            if (pontuacao > melhorPontuacao) {
                melhorPontuacao = pontuacao;
                melhorMovimento = mov;
            }
        }

        return melhorMovimento;
    }

    /**
     * Algoritmo Minimax com Poda Alfa-Beta
     *
     * tab Tabuleiro atual
     * profundidade Profundidade restante de busca
     * alfa Melhor valor para o maximizador
     * beta Melhor valor para o minimizador
     * maximizando True se é turno da IA, False se é do oponente
     * corIA Cor da IA (não muda durante a recursão)
     * Pontuação do melhor movimento
     */
    private int minimax(Tabuleiro tab, int profundidade, int alfa, int beta, boolean maximizando, Cor corIA) {
        // Caso base: profundidade 0 ou jogo terminado
        if (profundidade == 0 || jogoTerminado(tab)) {
            return avaliarTabuleiro(tab, corIA);
        }

        Cor corAtual = maximizando ? corIA : corOposta(corIA);
        List<Movimento> movimentos = obterTodosMovimentosTabuleiro(tab, corAtual);

        // Se não há movimentos, o jogador perdeu
        if (movimentos.isEmpty()) {
            return maximizando ? -10000 : 10000;
        }

        if (maximizando) {
            int maxPont = Integer.MIN_VALUE;

            for (Movimento mov : movimentos) {
                Tabuleiro tabCopia = copiarTabuleiro(tab);
                Peca peca = tabCopia.getCasa(mov.getOrigem()).getPeca();

                tabCopia.moverPeca(mov);

                // Verifica promoção
                if ((peca.getCor() == Cor.BRANCO && mov.getDestino().getLinha() == 0) ||
                        (peca.getCor() == Cor.PRETO && mov.getDestino().getLinha() == 7)) {
                    peca.promover();
                }

                int pont = minimax(tabCopia, profundidade - 1, alfa, beta, false, corIA);
                maxPont = Math.max(maxPont, pont);
                alfa = Math.max(alfa, pont);

                if (beta <= alfa) {
                    break; // Poda Alfa-Beta
                }
            }
            return maxPont;

        } else {
            int minPont = Integer.MAX_VALUE;

            for (Movimento mov : movimentos) {
                Tabuleiro tabCopia = copiarTabuleiro(tab);
                Peca peca = tabCopia.getCasa(mov.getOrigem()).getPeca();

                tabCopia.moverPeca(mov);

                // Verifica promoção
                if ((peca.getCor() == Cor.BRANCO && mov.getDestino().getLinha() == 0) ||
                        (peca.getCor() == Cor.PRETO && mov.getDestino().getLinha() == 7)) {
                    peca.promover();
                }

                int pont = minimax(tabCopia, profundidade - 1, alfa, beta, true, corIA);
                minPont = Math.min(minPont, pont);
                beta = Math.min(beta, pont);

                if (beta <= alfa) {
                    break; // Poda Alfa-Beta
                }
            }
            return minPont;
        }
    }

    /**
     * Avalia o estado do tabuleiro (heurística)
     * positivo = bom para a IA, negativo = bom para o player
     */
    private int avaliarTabuleiro(Tabuleiro tab, Cor corIA) {
        int pontos = 0;

        for (int i = 0; i < tab.getN(); i++) {
            for (int j = 0; j < tab.getN(); j++) {
                Posicao pos = new Posicao(i, j);
                if (tab.Ocupada(pos)) {
                    Peca peca = tab.getCasa(pos).getPeca();

                    // Valor base: peça normal = 10, dama = 30
                    int valor = peca.dama() ? 30 : 10;

                    // Bônus por posição (peças avançadas valem mais)
                    int bonusPosicao = 0;
                    if (!peca.dama()) {
                        if (peca.getCor() == Cor.BRANCO) {
                            bonusPosicao = (7 - i) * 2; // Quanto mais próximo da linha 0, melhor
                        } else {
                            bonusPosicao = i * 2; // Quanto mais próximo da linha 7, melhor
                        }
                    }

                    valor += bonusPosicao;

                    // Adiciona ou subtrai dependendo da cor
                    if (peca.getCor() == corIA) {
                        pontos += valor;
                    } else {
                        pontos -= valor;
                    }
                }
            }
        }

        return pontos;
    }

    /**
     * Obtem todos os movimentos validos para uma cor em um tabuleiro
     * (usado no Minimax, sem dependência de Partida)
     */
    private List<Movimento> obterTodosMovimentosTabuleiro(Tabuleiro tabuleiro, Cor cor) {
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

    /**
     * Obtém todos os movimentos considerando o estado de combo da partida
     */
    private List<Movimento> obterTodosMovimentos(Partida partida, Cor cor) {
        Tabuleiro tabuleiro = partida.getTabuleiro();

        // Se estiver em combo, só pode mover a peça do combo
        if (partida.isEmCombo()) {
            Posicao posCombo = partida.getPosicaoCombo();
            if (tabuleiro.Ocupada(posCombo) && tabuleiro.getCasa(posCombo).getPeca().getCor() == cor) {
                Peca peca = tabuleiro.getCasa(posCombo).getPeca();
                return tabuleiro.getMovimentoValido(peca, posCombo);
            } else {
                return new ArrayList<>();
            }
        }

        return obterTodosMovimentosTabuleiro(tabuleiro, cor);
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