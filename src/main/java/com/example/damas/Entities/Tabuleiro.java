package com.example.damas.Entities;

import com.example.damas.Enums.Cor;


public class Tabuleiro {
    private final int n = 8;
    private Casa[][] casa;


    public Tabuleiro() {
        casa = new  Casa[n][n];
        inicializar();
        posPecasinicial();
    }

    public int getN(){
        return n;
    }

    public Casa getCasa(Posicao posicao){
        return casa[posicao.getLinha()][posicao.getColuna()];
    }

    public void addPeca(Peca peca,Posicao posicao){
        casa[posicao.getLinha()][posicao.getColuna()].setPeca(peca);
    }

    public void remPeca(Posicao posicao){
        casa[posicao.getLinha()][posicao.getColuna()].setPeca(null);

    }

    public boolean Ocupada(Posicao posicao){
        return casa[posicao.getLinha()][posicao.getColuna()].Ocupada();
    }

    //public Peca moverPeca(Movimento mov){return }

    //public List<Movimento> getMovimentoValido(Peca peca){return}



    private void inicializar(){
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                Cor corCasa = (i + j) % 2 == 0 ? Cor.BRANCO : Cor.PRETO;
                casa[i][j] = new Casa(new Posicao(i,j),corCasa);
            }
        }
    }

    private void posPecasinicial(){
        for(int i = 0; i < 3; i++){
            for(int j = 0; j < n; j++){
                if (casa[i][j].getCorCasa() == Cor.PRETO)
                    casa[i][j].setPeca(new Peca(Cor.PRETO));
            }
        }
        for(int i = 5; i < 8; i++){
            for(int j = 0; j < n; j++){
                if (casa[i][j].getCorCasa() == Cor.PRETO)
                    casa[i][j].setPeca(new Peca(Cor.BRANCO));
            }
        }
    }

}
