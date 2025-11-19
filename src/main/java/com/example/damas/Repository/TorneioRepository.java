package com.example.damas.Repository;

import com.example.damas.Entities.Torneio;
import com.example.damas.Entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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

    public void salvarTorneio(Torneio torneio) {
        String sql = "INSERT INTO public.torneio (usuario_id, posicao_final) VALUES (?, ?)";
        jdbcTemplate.update(sql, torneio.getUsuario().getId(), torneio.getPosicaoFinal());
    }

    public Torneio procuraPorID(Long id) {
        String sql = "SELECT id, usuario_id, posicao_final FROM public.torneio WHERE id = ?";
        List<Torneio> torneios = jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> {
            Torneio torneio = new Torneio();
            torneio.setId(rs.getLong("id"));
            int usuarioId = rs.getInt("usuario_id");
            Usuario usuario = usuarioRepository.procuraPorID(Math.toIntExact(usuarioId)); // Assuming procuraPorID takes int
            torneio.setUsuario(usuario);
            torneio.setPosicaoFinal(rs.getInt("posicao_final"));
            // listaPartidas is not handled here
            return torneio;
        });
        return torneios.isEmpty() ? null : torneios.get(0);
    }

    public List<Torneio> listarTodos() {
        String sql = "SELECT id, usuario_id, posicao_final FROM public.torneio";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Torneio torneio = new Torneio();
            torneio.setId(rs.getLong("id"));
            Long usuarioId = rs.getLong("usuario_id");
            Usuario usuario = usuarioRepository.procuraPorID(Math.toIntExact(usuarioId));
            torneio.setUsuario(usuario);
            torneio.setPosicaoFinal(rs.getInt("posicao_final"));
            return torneio;
        });
    }
}