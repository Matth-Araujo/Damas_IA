package com.example.damas.Entities;

public class Movimento {
    private Posicao origem;
    private Posicao destino;
    private Posicao posPecaCapturada;
    private boolean isCaptura;

    public Movimento(Posicao origem, Posicao destino, Posicao posPecaCapturada) {
        this.origem = origem;
        this.destino = destino;
        this.posPecaCapturada = posPecaCapturada;
        this.isCaptura = true;
    }

    public Movimento(Posicao origem, Posicao destino) {
        this.origem = origem;
        this.destino = destino;
        this.isCaptura = false;
    }

    public Posicao getOrigem() {
        return origem;
    }

    public Posicao getDestino() {
        return destino;
    }

    public Posicao getPosPecaCapturada() {
        return posPecaCapturada;
    }

    public boolean isCaptura() {
        return isCaptura;
    }
}
