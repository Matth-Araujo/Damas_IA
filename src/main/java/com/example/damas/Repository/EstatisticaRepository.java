package com.example.damas.Repository;

import com.example.damas.Entities.Estatistica;
import com.example.damas.Entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EstatisticaRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public EstatisticaRepository(JdbcTemplate jdbcTemplate, UsuarioRepository usuarioRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.usuarioRepository = usuarioRepository;
    }

    public void salvarEstatistica(Estatistica estatistica) {
        String sql = "INSERT INTO public.estatistica " +
                "(usuario_id, vitorias, derrotas, partidas, ranking) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, estatistica.getUsuario().getId(),
                estatistica.getVitorias(), estatistica.getDerrotas(),
                estatistica.getPartidas(), estatistica.getRanking());
    }

    public void atualizarEstatistica(Estatistica estatistica) {
        String sql = "UPDATE public.estatistica SET " +
                "vitorias = ?, derrotas = ?, partidas = ?, ranking = ? WHERE usuario_id = ?";
        jdbcTemplate.update(sql, estatistica.getVitorias(), estatistica.getDerrotas(),
                estatistica.getPartidas(), estatistica.getRanking(), estatistica.getUsuario().getId());
    }

    public Estatistica getEstatisticaUsuarioId(int usuarioId) {
        String sql = "SELECT * FROM public.estatistica WHERE usuario_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{usuarioId}, (rs, rowNum) -> {
                Usuario usuario = usuarioRepository.procuraPorID(rs.getInt("usuario_id"));
                return new Estatistica(
                        rs.getInt("id"),
                        usuario,
                        rs.getInt("vitorias"),
                        rs.getInt("derrotas"),
                        rs.getInt("partidas"),
                        rs.getInt("ranking")
                );
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Estatistica> getRanking() {
        String sql = "SELECT * FROM public.estatistica ORDER BY vitorias DESC LIMIT 10";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Usuario usuario = usuarioRepository.procuraPorID(rs.getInt("usuario_id"));
            return new Estatistica(
                    rs.getInt("id"),
                    usuario,
                    rs.getInt("vitorias"),
                    rs.getInt("derrotas"),
                    rs.getInt("partidas"),
                    rs.getInt("ranking")
            );
        });
    }
}