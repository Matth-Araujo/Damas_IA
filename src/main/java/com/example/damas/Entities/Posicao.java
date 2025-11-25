package com.example.damas.Entities;

public class Posicao {
    private int linha;
    private int coluna;

    public Posicao(int linha, int coluna) {
        this.linha = linha;
        this.coluna = coluna;
    }

    public int getLinha() {
        return linha;
    }

    public void setLinha(int linha) {
        this.linha = linha;
    }

    public int getColuna() {
        return coluna;
    }

    public void setColuna(int coluna) {
        this.coluna = coluna;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Posicao)) return false;
        Posicao p = (Posicao) obj;
        return linha == p.linha && coluna == p.coluna;
    }

    @Override
    public int hashCode() {
        return linha * 31 + coluna;
    }
}
