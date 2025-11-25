package com.example.damas.Entities;

import java.time.LocalDateTime;

public class PartidaHistorico {
    private Long id;
    private Usuario jogador;
    private String adversario;
    private String resultado;
    private LocalDateTime dataHora;
    private Integer movimentos;
    private String nivel;
    private Integer duracaoSegundos;


    public PartidaHistorico() {}

    public PartidaHistorico(Usuario jogador, String adversario, String resultado, Integer movimentos, String nivel) {
        this.jogador = jogador;
        this.adversario = adversario;
        this.resultado = resultado;
        this.dataHora = LocalDateTime.now();
        this.movimentos = movimentos;
        this.nivel = nivel;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getJogador() {
        return jogador;
    }

    public void setJogador(Usuario jogador) {
        this.jogador = jogador;
    }

    public String getAdversario() {
        return adversario;
    }

    public void setAdversario(String adversario) {
        this.adversario = adversario;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Integer getMovimentos() {
        return movimentos;
    }

    public void setMovimentos(Integer movimentos) {
        this.movimentos = movimentos;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public Integer getDuracaoSegundos() {
        return duracaoSegundos;
    }

    public void setDuracaoSegundos(Integer duracaoSegundos) {
        this.duracaoSegundos = duracaoSegundos;
    }
}