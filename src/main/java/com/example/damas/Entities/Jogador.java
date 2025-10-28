package com.example.damas.Entities;

import com.example.damas.Enums.Cor;

abstract class Jogador {
    private String nome;
    private Cor cor;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    abstract Movimento fazerjogada(Tabuleiro tabuleiro);
}