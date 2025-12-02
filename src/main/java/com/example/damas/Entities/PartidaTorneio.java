package com.example.damas.Entities;

import java.time.LocalDateTime;

public class PartidaTorneio {
    private Long id;
    private Long torneioId;
    private String vencedor;
    private LocalDateTime dataPartida;
    private String nivelIA;

    public PartidaTorneio() {
    }

    public PartidaTorneio(Long torneioId, String vencedor, String nivelIA) {
        this.torneioId = torneioId;
        this.vencedor = vencedor;
        this.nivelIA = nivelIA;
        this.dataPartida = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTorneioId() {
        return torneioId;
    }

    public void setTorneioId(Long torneioId) {
        this.torneioId = torneioId;
    }

    public String getVencedor() {
        return vencedor;
    }

    public void setVencedor(String vencedor) {
        this.vencedor = vencedor;
    }

    public LocalDateTime getDataPartida() {
        return dataPartida;
    }

    public void setDataPartida(LocalDateTime dataPartida) {
        this.dataPartida = dataPartida;
    }

    public String getNivelIA() {
        return nivelIA;
    }

    public void setNivelIA(String nivelIA) {
        this.nivelIA = nivelIA;
    }
}