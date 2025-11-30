package com.example.damas.Entities;

import com.example.damas.Enums.Cor;

import java.util.ArrayList;
import java.util.List;

public class Tabuleiro {
    private final int n = 8;
    private Casa[][] casa;

    public Tabuleiro() {
        casa = new Casa[n][n];
        inicializar();
        posPecasinicial();
    }

    public int getN() {
        return n;
    }

    public Casa getCasa(Posicao posicao) {
        return casa[posicao.getLinha()][posicao.getColuna()];
    }

    public void addPeca(Peca peca, Posicao posicao) {
        casa[posicao.getLinha()][posicao.getColuna()].setPeca(peca);
    }

    public void remPeca(Posicao posicao) {
        casa[posicao.getLinha()][posicao.getColuna()].setPeca(null);
    }

    public boolean Ocupada(Posicao posicao) {
        return casa[posicao.getLinha()][posicao.getColuna()].Ocupada();
    }

    public Peca moverPeca(Movimento mov) {
        Peca peca = getCasa(mov.getOrigem()).getPeca();
        remPeca(mov.getOrigem());
        addPeca(peca, mov.getDestino());
        if (mov.isCaptura()) {
            remPeca(mov.getPosPecaCapturada());
        }
        return peca;
    }

    public List<Movimento> getMovimentoValido(Peca peca, Posicao pos) {
        // Verifica se ha capturas obrigatorias para essa cor
        List<Movimento> capturasObrigatorias = getMovimentosObrigatorios(peca.getCor());

        // Se ha capturas disponiveis, so elas sao validas
        if (!capturasObrigatorias.isEmpty()) {
            // Retorna apenas as capturas dessa peca especifica
            List<Movimento> capturasDestaPeca = new ArrayList<>();
            for (Movimento m : capturasObrigatorias) {
                if (m.getOrigem().equals(pos)) {
                    capturasDestaPeca.add(m);
                }
            }
            return capturasDestaPeca;
        }

        // Se nao ha capturas, retorna movimentos normais
        if (peca.dama()) {
            return getMovimentosDama(pos);
        } else {
            return getMovimentosPecaNormal(peca, pos);
        }
    }

    // Busca todos os movimentos de captura disponíveis para uma cor
    public List<Movimento> getMovimentosObrigatorios(Cor cor) {
        List<Movimento> capturas = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Posicao pos = new Posicao(i, j);
                if (Ocupada(pos) && getCasa(pos).getPeca().getCor() == cor) {
                    Peca peca = getCasa(pos).getPeca();
                    if (peca.dama()) {
                        capturas.addAll(getCapturasDama(pos));
                    } else {
                        capturas.addAll(getCapturasPecaNormal(peca, pos));
                    }
                }
            }
        }
        return capturas;
    }

    private List<Movimento> getCapturasPecaNormal(Peca peca, Posicao pos) {
        List<Movimento> movimentos = new ArrayList<>();
        int direcao = (peca.getCor() == Cor.BRANCO) ? -1 : 1;

        // Capturas para frente
        Posicao p3 = new Posicao(pos.getLinha() + direcao * 2, pos.getColuna() - 2);
        Posicao pecaCapturada1 = new Posicao(pos.getLinha() + direcao, pos.getColuna() - 1);
        if (isPosicaoValida(p3) && !Ocupada(p3) && Ocupada(pecaCapturada1) && getCasa(pecaCapturada1).getPeca().getCor() != peca.getCor()) {
            movimentos.add(new Movimento(pos, p3, pecaCapturada1));
        }

        Posicao p4 = new Posicao(pos.getLinha() + direcao * 2, pos.getColuna() + 2);
        Posicao pecaCapturada2 = new Posicao(pos.getLinha() + direcao, pos.getColuna() + 1);
        if (isPosicaoValida(p4) && !Ocupada(p4) && Ocupada(pecaCapturada2) && getCasa(pecaCapturada2).getPeca().getCor() != peca.getCor()) {
            movimentos.add(new Movimento(pos, p4, pecaCapturada2));
        }

        // Capturas para trás
        int direcaoInversa = -direcao;
        Posicao p5 = new Posicao(pos.getLinha() + direcaoInversa * 2, pos.getColuna() - 2);
        Posicao pecaCapturada3 = new Posicao(pos.getLinha() + direcaoInversa, pos.getColuna() - 1);
        if (isPosicaoValida(p5) && !Ocupada(p5) && Ocupada(pecaCapturada3) && getCasa(pecaCapturada3).getPeca().getCor() != peca.getCor()) {
            movimentos.add(new Movimento(pos, p5, pecaCapturada3));
        }

        Posicao p6 = new Posicao(pos.getLinha() + direcaoInversa * 2, pos.getColuna() + 2);
        Posicao pecaCapturada4 = new Posicao(pos.getLinha() + direcaoInversa, pos.getColuna() + 1);
        if (isPosicaoValida(p6) && !Ocupada(p6) && Ocupada(pecaCapturada4) && getCasa(pecaCapturada4).getPeca().getCor() != peca.getCor()) {
            movimentos.add(new Movimento(pos, p6, pecaCapturada4));
        }
        return movimentos;
    }

    private List<Movimento> getCapturasDama(Posicao pos) {
        List<Movimento> movimentos = new ArrayList<>();
        int[] direcoes = {-1, 1};

        for (int dLinha : direcoes) {
            for (int dColuna : direcoes) {
                boolean encontrouPeca = false;
                Posicao pecaCapturada = null;

                for (int i = 1; i < n; i++) {
                    Posicao p = new Posicao(pos.getLinha() + dLinha * i, pos.getColuna() + dColuna * i);
                    if (!isPosicaoValida(p)) break;

                    if (Ocupada(p)) {
                        if (getCasa(p).getPeca().getCor() != getCasa(pos).getPeca().getCor()) {
                            if (!encontrouPeca) {
                                encontrouPeca = true;
                                pecaCapturada = p;
                            } else { // segunda peça inimiga na mesma linha
                                break;
                            }
                        } else { // peça aliada
                            break;
                        }
                    } else { // casa vazia
                        if (encontrouPeca) {
                            movimentos.add(new Movimento(pos, p, pecaCapturada));
                        }
                    }
                }
            }
        }
        return movimentos;
    }

    private List<Movimento> getMovimentosPecaNormal(Peca peca, Posicao pos) {
        List<Movimento> movimentos = new ArrayList<>();
        int direcao = (peca.getCor() == Cor.BRANCO) ? -1 : 1;

        // Movimentos simples (sem captura)
        Posicao p1 = new Posicao(pos.getLinha() + direcao, pos.getColuna() - 1);
        if (isPosicaoValida(p1) && !Ocupada(p1)) {
            movimentos.add(new Movimento(pos, p1));
        }

        Posicao p2 = new Posicao(pos.getLinha() + direcao, pos.getColuna() + 1);
        if (isPosicaoValida(p2) && !Ocupada(p2)) {
            movimentos.add(new Movimento(pos, p2));
        }

        // Capturas (diagonal esquerda)
        Posicao p3 = new Posicao(pos.getLinha() + direcao * 2, pos.getColuna() - 2);
        Posicao pecaCapturada1 = new Posicao(pos.getLinha() + direcao, pos.getColuna() - 1);
        if (isPosicaoValida(p3) && !Ocupada(p3) &&
                Ocupada(pecaCapturada1) &&
                getCasa(pecaCapturada1).getPeca().getCor() != peca.getCor()) {
            movimentos.add(new Movimento(pos, p3, pecaCapturada1));
        }

        // Capturas (diagonal direita)
        Posicao p4 = new Posicao(pos.getLinha() + direcao * 2, pos.getColuna() + 2);
        Posicao pecaCapturada2 = new Posicao(pos.getLinha() + direcao, pos.getColuna() + 1);
        if (isPosicaoValida(p4) && !Ocupada(p4) &&
                Ocupada(pecaCapturada2) &&
                getCasa(pecaCapturada2).getPeca().getCor() != peca.getCor()) {
            movimentos.add(new Movimento(pos, p4, pecaCapturada2));
        }

        // NOVO: Capturas para trás (peça normal também pode capturar para trás)
        int direcaoInversa = -direcao;

        // Captura para trás (diagonal esquerda)
        Posicao p5 = new Posicao(pos.getLinha() + direcaoInversa * 2, pos.getColuna() - 2);
        Posicao pecaCapturada3 = new Posicao(pos.getLinha() + direcaoInversa, pos.getColuna() - 1);
        if (isPosicaoValida(p5) && !Ocupada(p5) &&
                Ocupada(pecaCapturada3) &&
                getCasa(pecaCapturada3).getPeca().getCor() != peca.getCor()) {
            movimentos.add(new Movimento(pos, p5, pecaCapturada3));
        }

        // Captura para trás (diagonal direita)
        Posicao p6 = new Posicao(pos.getLinha() + direcaoInversa * 2, pos.getColuna() + 2);
        Posicao pecaCapturada4 = new Posicao(pos.getLinha() + direcaoInversa, pos.getColuna() + 1);
        if (isPosicaoValida(p6) && !Ocupada(p6) &&
                Ocupada(pecaCapturada4) &&
                getCasa(pecaCapturada4).getPeca().getCor() != peca.getCor()) {
            movimentos.add(new Movimento(pos, p6, pecaCapturada4));
        }

        return movimentos;
    }

    private List<Movimento> getMovimentosDama(Posicao pos) {
        List<Movimento> movimentos = new ArrayList<>();
        int[] direcoes = {-1, 1};

        for (int dLinha : direcoes) {
            for (int dColuna : direcoes) {
                boolean encontrouPeca = false;
                Posicao pecaCapturada = null;

                for (int i = 1; i < n; i++) {
                    Posicao p = new Posicao(pos.getLinha() + dLinha * i, pos.getColuna() + dColuna * i);

                    if (!isPosicaoValida(p)) {
                        break;
                    }

                    if (Ocupada(p)) {
                        // Encontrou uma peça
                        if (getCasa(p).getPeca().getCor() != getCasa(pos).getPeca().getCor()) {
                            if (!encontrouPeca) {
                                // Primeira peça inimiga encontrada
                                encontrouPeca = true;
                                pecaCapturada = p;
                            } else {
                                // Já tinha encontrado uma peça antes - não pode pular duas peças
                                break;
                            }
                        } else {
                            // Peça aliada - para nessa direção
                            break;
                        }
                    } else {
                        // Casa vazia
                        if (encontrouPeca) {
                            // Pode capturar e continuar até o final
                            movimentos.add(new Movimento(pos, p, pecaCapturada));
                        } else {
                            // Movimento simples
                            movimentos.add(new Movimento(pos, p));
                        }
                    }
                }
            }
        }
        return movimentos;
    }

    private boolean isPosicaoValida(Posicao pos) {
        return pos.getLinha() >= 0 && pos.getLinha() < n &&
                pos.getColuna() >= 0 && pos.getColuna() < n;
    }

    private void inicializar() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Cor corCasa = (i + j) % 2 == 0 ? Cor.BRANCO : Cor.PRETO;
                casa[i][j] = new Casa(new Posicao(i, j), corCasa);
            }
        }
    }

    private void posPecasinicial() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < n; j++) {
                if (casa[i][j].getCorCasa() == Cor.PRETO) {
                    casa[i][j].setPeca(new Peca(Cor.PRETO));
                }
            }
        }

        for (int i = 5; i < 8; i++) {
            for (int j = 0; j < n; j++) {
                if (casa[i][j].getCorCasa() == Cor.PRETO) {
                    casa[i][j].setPeca(new Peca(Cor.BRANCO));
                }
            }
        }
    }
}
