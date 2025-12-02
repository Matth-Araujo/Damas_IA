package com.example.damas.Entities;

import java.time.LocalDateTime;

public class Torneio {
    private Long id;
    private Usuario usuario;
    private int posicaoFinal;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String status; // "EM_ANDAMENTO", "FINALIZADO"

    public Torneio() {
    }

    public Torneio(Usuario usuario) {
        this.usuario = usuario;
        this.dataInicio = LocalDateTime.now();
        this.status = "EM_ANDAMENTO";
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

    public int getPosicaoFinal() {
        return posicaoFinal;
    }

    public void setPosicaoFinal(int posicaoFinal) {
        this.posicaoFinal = posicaoFinal;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void finalizar(int posicao) {
        this.posicaoFinal = posicao;
        this.dataFim = LocalDateTime.now();
        this.status = "FINALIZADO";
    }
}