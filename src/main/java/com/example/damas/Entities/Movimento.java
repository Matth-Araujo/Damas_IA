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


    public boolean isMovimentoSimples() {
        int distLinha = Math.abs(destino.getLinha() - origem.getLinha());
        int distColuna = Math.abs(destino.getColuna() - origem.getColuna());
        return distLinha == 1 && distColuna == 1;
    }

    public boolean isMovimentoLongo() {
        int distLinha = Math.abs(destino.getLinha() - origem.getLinha());
        int distColuna = Math.abs(destino.getColuna() - origem.getColuna());
        return distLinha > 1 && distColuna > 1 && distLinha == distColuna;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Movimento)) return false;
        Movimento m = (Movimento) obj;
        return origem.equals(m.origem) && destino.equals(m.destino);
    }

    @Override
    public int hashCode() {
        return origem.hashCode() * 31 + destino.hashCode();
    }
}