package com.example.damas.Service;

import com.example.damas.Entities.*;
import com.example.damas.Enums.Cor;
import com.example.damas.Enums.Dificuldade;
import com.example.damas.Enums.StatusPartida;
import com.example.damas.Repository.TorneioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TorneioService {

    private final TorneioRepository torneioRepository;

    @Autowired
    public TorneioService(TorneioRepository torneioRepository) {
        this.torneioRepository = torneioRepository;
    }

    /**
     * Cria um novo torneio com 1 jogador humano + 7 IAs
     */
    public Long criarTorneio(Usuario usuario) {
        Torneio torneio = new Torneio(usuario);
        torneio.setPosicaoFinal(0); // Ainda não terminou
        return torneioRepository.salvarTorneio(torneio);
    }

    /**
     * Gera o chaveamento inicial: Jogador + 7 IAs de níveis variados
     */
    public List<Jogador> gerarChaveamento(String nomeJogador) {
        List<Jogador> participantes = new ArrayList<>();

        // Jogador humano sempre nas brancas
        participantes.add(new JogadorHumano(nomeJogador, Cor.BRANCO));

        // 7 IAs com níveis variados
        participantes.add(new IA("Bot Fácil 1", Cor.PRETO, Dificuldade.FACIL));
        participantes.add(new IA("Bot Fácil 2", Cor.PRETO, Dificuldade.FACIL));
        participantes.add(new IA("Bot Médio 1", Cor.PRETO, Dificuldade.MEDIO));
        participantes.add(new IA("Bot Médio 2", Cor.PRETO, Dificuldade.MEDIO));
        participantes.add(new IA("Bot Médio 3", Cor.PRETO, Dificuldade.MEDIO));
        participantes.add(new IA("Bot Difícil 1", Cor.PRETO, Dificuldade.DIFICIL));
        participantes.add(new IA("Bot Difícil 2", Cor.PRETO, Dificuldade.DIFICIL));

        return participantes;
    }

    /**
     * Simula partida entre 2 bots e retorna o vencedor
     */
    public Jogador simularPartidaBots(Jogador bot1, Jogador bot2) {
        // Dificuldade maior sempre ganha
        if (bot1 instanceof IA && bot2 instanceof IA) {
            IA ia1 = (IA) bot1;
            IA ia2 = (IA) bot2;

            int nivel1 = getNivelNumerico(ia1.getNivel());
            int nivel2 = getNivelNumerico(ia2.getNivel());

            if (nivel1 > nivel2) {
                return bot1;
            } else if (nivel2 > nivel1) {
                return bot2;
            } else {
                // Mesma dificuldade: escolhe aleatoriamente
                return new Random().nextBoolean() ? bot1 : bot2;
            }
        }

        // Se não forem ambos IAs, retorna o primeiro (não deveria acontecer)
        return bot1;
    }

    private int getNivelNumerico(Dificuldade dificuldade) {
        switch (dificuldade) {
            case FACIL: return 1;
            case MEDIO: return 2;
            case DIFICIL: return 3;
            default: return 0;
        }
    }

    /**
     * Salva o resultado de uma partida do torneio
     */
    public void salvarPartida(Long torneioId, String vencedor, String nivelIA) {
        PartidaTorneio partida = new PartidaTorneio(torneioId, vencedor, nivelIA);
        torneioRepository.salvarPartidaTorneio(partida);
    }

    /**
     * Finaliza o torneio e salva a posição final do jogador
     */
    public void finalizarTorneio(Long torneioId, int posicaoFinal) {
        Torneio torneio = torneioRepository.procuraPorID(torneioId);
        if (torneio != null) {
            torneio.setPosicaoFinal(posicaoFinal);
            torneioRepository.atualizarTorneio(torneio);
        }
    }

    public List<PartidaTorneio> buscarPartidasDoTorneio(Long torneioId) {
        return torneioRepository.buscarPartidasDoTorneio(torneioId);
    }

    public List<Torneio> listarTorneiosDoUsuario(int usuarioId) {
        return torneioRepository.listarTorneiosDoUsuario(usuarioId);
    }
}