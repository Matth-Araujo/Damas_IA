package com.example.damas.Entities;

import java.util.List;

public class Torneio {

    private Long id;
    private Usuario usuario;
    private List<Partida> listaPartidas;
    private int posicaoFinal;

    public Torneio() {
    }

    public Torneio(Usuario usuario, List<Partida> listaPartidas, int posicaoFinal) {
        this.usuario = usuario;
        this.listaPartidas = listaPartidas;
        this.posicaoFinal = posicaoFinal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Partida> getListaPartidas() {
        return listaPartidas;
    }

    public void setListaPartidas(List<Partida> listaPartidas) {
        this.listaPartidas = listaPartidas;
    }

    public int getPosicaoFinal() {
        return posicaoFinal;
    }

    public void setPosicaoFinal(int posicaoFinal) {
        this.posicaoFinal = posicaoFinal;
    }
}
