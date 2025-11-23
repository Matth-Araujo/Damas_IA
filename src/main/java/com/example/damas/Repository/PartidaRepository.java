package com.example.damas.Repository;

import com.example.damas.Entities.PartidaHistorico;
import com.example.damas.Entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class PartidaRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public PartidaRepository(JdbcTemplate jdbcTemplate, UsuarioRepository usuarioRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.usuarioRepository = usuarioRepository;
    }

    public void salvarPartida(PartidaHistorico partida) {
        String sql = "INSERT INTO public.partida_historico (usuario_id, adversario, resultado, data_hora, movimentos, nivel, duracao_segundos) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                partida.getJogador().getId(),
                partida.getAdversario(),
                partida.getResultado(),
                Timestamp.valueOf(partida.getDataHora()),
                partida.getMovimentos(),
                partida.getNivel(),
                partida.getDuracaoSegundos()
        );
    }

    public List<PartidaHistorico> buscarPartidasPorUsuario(int usuarioId) {
        String sql = "SELECT * FROM public.partida_historico WHERE usuario_id = ? ORDER BY data_hora DESC";
        return jdbcTemplate.query(sql, new Object[]{usuarioId}, (rs, rowNum) -> {
            PartidaHistorico partida = new PartidaHistorico();
            partida.setId(rs.getLong("id"));

            Usuario usuario = usuarioRepository.procuraPorID(rs.getInt("usuario_id"));
            partida.setJogador(usuario);

            partida.setAdversario(rs.getString("adversario"));
            partida.setResultado(rs.getString("resultado"));
            partida.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
            partida.setMovimentos(rs.getInt("movimentos"));
            partida.setNivel(rs.getString("nivel"));
            partida.setDuracaoSegundos(rs.getInt("duracao_segundos"));

            return partida;
        });
    }

    public List<PartidaHistorico> listarTodasPartidas() {
        String sql = "SELECT * FROM public.partida_historico ORDER BY data_hora DESC LIMIT 100";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            PartidaHistorico partida = new PartidaHistorico();
            partida.setId(rs.getLong("id"));

            Usuario usuario = usuarioRepository.procuraPorID(rs.getInt("usuario_id"));
            partida.setJogador(usuario);

            partida.setAdversario(rs.getString("adversario"));
            partida.setResultado(rs.getString("resultado"));
            partida.setDataHora(rs.getTimestamp("data_hora").toLocalDateTime());
            partida.setMovimentos(rs.getInt("movimentos"));
            partida.setNivel(rs.getString("nivel"));
            partida.setDuracaoSegundos(rs.getInt("duracao_segundos"));

            return partida;
        });
    }
}