package com.example.damas.Entities;

public class Estatistica {

    private int id;
    private Usuario usuario;
    private int vitorias;
    private int derrotas;
    private int partidas;
    private int ranking;

    public Estatistica() {
    }

    public Estatistica(int id, Usuario usuario, int vitorias, int derrotas, int partidas, int ranking) {
        this.id = id;
        this.usuario = usuario;
        this.vitorias = vitorias;
        this.derrotas = derrotas;
        this.partidas = partidas;
        this.ranking = ranking;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int getVitorias() {
        return vitorias;
    }

    public void setVitorias(int vitorias) {
        this.vitorias = vitorias;
    }

    public int getDerrotas() {
        return derrotas;
    }

    public void setDerrotas(int derrotas) {
        this.derrotas = derrotas;
    }

    public int getPartidas() {
        return partidas;
    }

    public void setPartidas(int partidas) {
        this.partidas = partidas;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }
}
