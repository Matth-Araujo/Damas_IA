package com.example.damas.Service;

import com.example.damas.Entities.PartidaHistorico;
import com.example.damas.Repository.PartidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartidaService {

    private final PartidaRepository partidaRepository;

    @Autowired
    public PartidaService(PartidaRepository partidaRepository) {
        this.partidaRepository = partidaRepository;
    }

    public void salvarPartida(PartidaHistorico partida) {
        partidaRepository.salvarPartida(partida);
    }

    public List<PartidaHistorico> buscarPartidasDoUsuario(int usuarioId) {
        return partidaRepository.buscarPartidasPorUsuario(usuarioId);
    }

    public List<PartidaHistorico> listarTodasPartidas() {
        return partidaRepository.listarTodasPartidas();
    }
}