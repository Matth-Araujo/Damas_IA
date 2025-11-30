package com.example.damas.Entities;

import com.example.damas.Enums.Cor;

public abstract class Jogador {
    private String nome;
    private Cor cor;

    public Jogador(String nome, Cor cor) {
        this.nome = nome;
        this.cor = cor;
    }

    public abstract Movimento escolherJogada(Partida partida);

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Cor getCor() {
        return cor;
    }

    public void setCor(Cor cor) {
        this.cor = cor;
    }
}