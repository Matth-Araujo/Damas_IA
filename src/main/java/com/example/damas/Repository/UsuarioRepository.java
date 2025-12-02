package com.example.damas.Repository;

import com.example.damas.Entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class UsuarioRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UsuarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void salvarusuario(Usuario usuario) {
        String sql = "INSERT INTO public.usuario (nome, email, senha) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, usuario.getNome(), usuario.getEmail(), usuario.getSenha());
    }

    public void alterarSenha(Usuario usuario) {
        String sql = "UPDATE public.usuario SET senha = ? WHERE id = ?";
        jdbcTemplate.update(sql, usuario.getSenha(), usuario.getId());
    }

    public Usuario procuraPorID(int id) {
        String sql = "SELECT * FROM public.usuario WHERE id = ?";
        List<Usuario> usuarios = jdbcTemplate.query(sql, new Object[]{id}, (rs, rowNum) -> {
            Usuario usuario = new Usuario();
            usuario.setId(rs.getInt("id"));
            usuario.setNome(rs.getString("nome"));
            usuario.setEmail(rs.getString("email"));
            usuario.setSenha(rs.getString("senha"));
            return usuario;
        });
        return usuarios.isEmpty() ? null : usuarios.get(0);
    }

    public boolean emailJaExiste(String email) {
        String sql = "SELECT COUNT(*) FROM public.usuario WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    public Usuario procurarPorEmail(String email) {
        String sql = "SELECT * FROM public.usuario WHERE email = ?";
        List<Usuario> usuarios = jdbcTemplate.query(sql, new Object[]{email}, (rs, rowNum) -> {
            Usuario usuario = new Usuario();
            usuario.setId(rs.getInt("id"));
            usuario.setNome(rs.getString("nome"));
            usuario.setEmail(rs.getString("email"));
            usuario.setSenha(rs.getString("senha"));
            return usuario;
        });
        return usuarios.isEmpty() ? null : usuarios.get(0);
    }


    // RANKING DE MELHORES JOGADORES
    public List<Map<String, Object>> getRankingJogadores() {
        String sql = """
    SELECT 
        u.id,
        u.nome,
        COUNT(CASE WHEN ph.resultado = 'VITORIA' THEN 1 END) as vitorias,
        COUNT(CASE WHEN ph.resultado = 'DERROTA' THEN 1 END) as derrotas,
        COUNT(ph.id) as total_partidas,
        MIN(CASE WHEN ph.resultado = 'VITORIA' THEN ph.movimentos END) as melhor_partida,
        ROUND(CAST(AVG(CASE WHEN ph.resultado = 'VITORIA' THEN ph.movimentos END) AS numeric), 1) as media_movimentos,
        ROUND(
            CAST(
                (COUNT(CASE WHEN ph.resultado = 'VITORIA' THEN 1 END)::float / 
                NULLIF(COUNT(ph.id), 0)) * 100 
                AS numeric
            ), 
            2
        ) as taxa_vitoria
    FROM public.usuario u
    LEFT JOIN public.partida_historico ph ON u.id = ph.usuario_id
    GROUP BY u.id, u.nome
    HAVING COUNT(ph.id) > 0
    ORDER BY taxa_vitoria DESC, vitorias DESC, melhor_partida ASC
    LIMIT 50
    """;

        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    // RANKING GERAL
    public List<Map<String, Object>> getRankingGeral() {
        return getRankingJogadores();
    }

    // RANKING POR NÍVEL
    public List<Map<String, Object>> getRankingPorNivel(String nivel) {
        String sql = """
    SELECT
    u.id,
    u.nome,
    COUNT(CASE WHEN ph.resultado = 'VITORIA' THEN 1 END) as vitorias,
    COUNT(CASE WHEN ph.resultado = 'DERROTA' THEN 1 END) as derrotas,
    COUNT(ph.id) as total_partidas,
    MIN(CASE WHEN ph.resultado = 'VITORIA' THEN ph.movimentos END) as melhor_partida,
    ROUND(CAST(AVG(CASE WHEN ph.resultado = 'VITORIA' THEN ph.movimentos END) AS numeric), 1) as media_movimentos,
    ROUND(
        CAST(
            (COUNT(CASE WHEN ph.resultado = 'VITORIA' THEN 1 END)::float /
            NULLIF(COUNT(ph.id), 0)) * 100
            AS numeric), 2) as taxa_vitoria
    FROM public.usuario u
    INNER JOIN public.partida_historico ph ON u.id = ph.usuario_id
    WHERE ph.nivel = ?
    GROUP BY u.id, u.nome
    HAVING COUNT(ph.id) > 0
    ORDER BY taxa_vitoria DESC, vitorias DESC, melhor_partida ASC
    LIMIT 10
    """;

        try {
            return jdbcTemplate.queryForList(sql, nivel);
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}