package com.example.damas.Entities;

import com.example.damas.Enums.Cor;

public class Casa {
    private Posicao posicao;
    private Peca peca;
    private Cor corCasa;

    public Casa(Posicao posicao, Cor corCasa) {
        this.posicao = posicao;
        this.peca = null;
        this.corCasa = corCasa;
    }

    public boolean Ocupada(){
        return this.peca != null;
    }

    public Posicao getPosicao() {
        return posicao;
    }

    public void setPosicao(Posicao posicao) {
        this.posicao = posicao;
    }

    public Peca getPeca() {
        return peca;
    }

    public void setPeca(Peca peca) {
        this.peca = peca;
    }

    public Cor getCorCasa() {
        return corCasa;
    }

    public void setCorCasa(Cor corCasa) {
        this.corCasa = corCasa;
    }
}
