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
    public List<Map<String, Object>> getRankingJogadores() {
        String sql = """
        SELECT 
            u.id,
            u.nome,
            COUNT(CASE WHEN ph.resultado = 'VITORIA' THEN 1 END) as vitorias,
            COUNT(CASE WHEN ph.resultado = 'DERROTA' THEN 1 END) as derrotas,
            COUNT(ph.id) as total_partidas,
            ROUND(
                (COUNT(CASE WHEN ph.resultado = 'VITORIA' THEN 1 END)::float / 
                NULLIF(COUNT(ph.id), 0)) * 100, 
                2
            ) as taxa_vitoria
        FROM public.usuario u
        LEFT JOIN public.partida_historico ph ON u.id = ph.usuario_id
        GROUP BY u.id, u.nome
        HAVING COUNT(ph.id) > 0
        ORDER BY vitorias DESC, taxa_vitoria DESC
        LIMIT 10
    """;

        return jdbcTemplate.queryForList(sql);
    }
}