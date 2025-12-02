package com.example.damas.Repository;

import com.example.damas.Entities.PartidaTorneio;
import com.example.damas.Entities.Torneio;
import com.example.damas.Entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
public class TorneioRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public TorneioRepository(JdbcTemplate jdbcTemplate, UsuarioRepository usuarioRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.usuarioRepository = usuarioRepository;
    }

    public Long salvarTorneio(Torneio torneio) {
        String sql = "INSERT INTO public.torneio (usuario_id, posicao_final) VALUES (?, ?) RETURNING id";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, torneio.getUsuario().getId());
            ps.setInt(2, torneio.getPosicaoFinal());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public void atualizarTorneio(Torneio torneio) {
        String sql = "UPDATE public.torneio SET posicao_final = ? WHERE id = ?";
        jdbcTemplate.update(sql, torneio.getPosicaoFinal(), torneio.getId());
    }

    public Torneio procuraPorID(Long id) {
        String sql = "SELECT id, usuario_id, posicao_final FROM public.torneio WHERE id = ?";
        List<Torneio> torneios = jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> {
            Torneio torneio = new Torneio();
            torneio.setId(rs.getLong("id"));
            int usuarioId = rs.getInt("usuario_id");
            Usuario usuario = usuarioRepository.procuraPorID(usuarioId);
            torneio.setUsuario(usuario);
            torneio.setPosicaoFinal(rs.getInt("posicao_final"));
            return torneio;
        });
        return torneios.isEmpty() ? null : torneios.get(0);
    }

    public void salvarPartidaTorneio(PartidaTorneio partida) {
        String sql = "INSERT INTO public.partida (torneio_id, vencedor, data_partida, nivelia) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                partida.getTorneioId(),
                partida.getVencedor(),
                Timestamp.valueOf(partida.getDataPartida()),
                partida.getNivelIA()
        );
    }

    public List<PartidaTorneio> buscarPartidasDoTorneio(Long torneioId) {
        String sql = "SELECT * FROM public.partida WHERE torneio_id = ? ORDER BY data_partida";
        return jdbcTemplate.query(sql, new Object[]{torneioId}, (rs, rowNum) -> {
            PartidaTorneio partida = new PartidaTorneio();
            partida.setId(rs.getLong("id"));
            partida.setTorneioId(rs.getLong("torneio_id"));
            partida.setVencedor(rs.getString("vencedor"));
            partida.setDataPartida(rs.getTimestamp("data_partida").toLocalDateTime());
            partida.setNivelIA(rs.getString("nivelia"));
            return partida;
        });
    }

    public List<Torneio> listarTorneiosDoUsuario(int usuarioId) {
        String sql = "SELECT id, usuario_id, posicao_final FROM public.torneio WHERE usuario_id = ? ORDER BY id DESC";
        return jdbcTemplate.query(sql, new Object[]{usuarioId}, (rs, rowNum) -> {
            Torneio torneio = new Torneio();
            torneio.setId(rs.getLong("id"));
            Usuario usuario = usuarioRepository.procuraPorID(rs.getInt("usuario_id"));
            torneio.setUsuario(usuario);
            torneio.setPosicaoFinal(rs.getInt("posicao_final"));
            return torneio;
        });
    }
}