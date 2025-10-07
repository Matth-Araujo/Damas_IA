package com.example.damas.Entities;

public class Movimento {
    private Posicao origem;
    private Posicao destino;
    private  Peca pecaCapturada;

    public Movimento(Posicao origem, Peca pecaCapturada, Posicao destino) {
        this.origem = origem;
        this.pecaCapturada = pecaCapturada;
        this.destino = destino;
    }

    public Posicao getOrigem() {
        return origem;
    }

    public void setOrigem(Posicao origem) {
        this.origem = origem;
    }

    public Peca getPecaCapturada() {
        return pecaCapturada;
    }

    public void setPecaCapturada(Peca pecaCapturada) {
        this.pecaCapturada = pecaCapturada;
    }

    public Posicao getDestino() {
        return destino;
    }

    public void setDestino(Posicao destino) {
        this.destino = destino;
    }
}
