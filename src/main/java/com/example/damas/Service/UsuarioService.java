package com.example.damas.Service;

import com.example.damas.Entities.Usuario;
import com.example.damas.Repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public void cadastrarUsuario(Usuario usuario) throws Exception {
        // Validar nome
        if (usuario.getNome() == null || usuario.getNome().trim().isEmpty()) {
            throw new Exception("Nome é obrigatório!");
        }
        if (usuario.getNome().length() < 3) {
            throw new Exception("Nome deve ter pelo menos 3 caracteres!");
        }

        // Validar email
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new Exception("Email é obrigatório!");
        }
        if (!validarEmail(usuario.getEmail())) {
            throw new Exception("Email inválido!");
        }

        // Verificar se o email já existe
        if (usuarioRepository.emailJaExiste(usuario.getEmail())) {
            throw new Exception("Este email já está cadastrado!");
        }

        // Validar senha forte
        if (usuario.getSenha() == null || usuario.getSenha().isEmpty()) {
            throw new Exception("Senha é obrigatória!");
        }
        if (!validarSenhaForte(usuario.getSenha())) {
            throw new Exception("Senha fraca! A senha deve ter pelo menos 6 caracteres, incluindo letras e números.");
        }

        // Criptografar a senha antes de salvar
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);

        // Salva no banco de dados
        usuarioRepository.salvarusuario(usuario);
    }

    public Usuario buscarPorId(int id) {
        return usuarioRepository.procuraPorID(id);
    }


    public Usuario fazerLogin(String email, String senha) {
        // Busca o usuario pelo email
        Usuario usuario = usuarioRepository.procurarPorEmail(email);

        if (usuario == null) {
            return null;
        }

        // Verifica se a senha digitada corresponde ao hash armazenado
        boolean senhaCorreta = passwordEncoder.matches(senha, usuario.getSenha());

        if (senhaCorreta) {
            // Remove a senha antes de retornar
            usuario.setSenha(null);
            return usuario;
        }

        // Senha incorreta
        return null;
    }

    public boolean verificarLogin(String email, String senhaDigitada) {
        Usuario usuario = usuarioRepository.procurarPorEmail(email);
        if (usuario == null) {
            return false;
        }
        // Compara a senha digitada com o hash armazenado
        return passwordEncoder.matches(senhaDigitada, usuario.getSenha());
    }

    public Usuario buscarPorEmail(String email) {
        Usuario usuario = usuarioRepository.procurarPorEmail(email);
        if (usuario != null) {

            usuario.setSenha(null);
        }
        return usuario;
    }

    private boolean validarEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private boolean validarSenhaForte(String senha) {
        // Requisitos:
        // - Mínimo 6 caracteres
        // - Pelo menos uma letra
        // - Pelo menos um número
        // - Apenas letras e números (sem caracteres especiais)
        if (senha == null || senha.length() < 6) {
            return false;
        }

        boolean temLetra = false;
        boolean temNumero = false;

        for (char c : senha.toCharArray()) {
            if (Character.isLetter(c)) {
                temLetra = true;
            } else if (Character.isDigit(c)) {
                temNumero = true;
            } else {
                // Contém caractere especial, não permitido
                return false;
            }
        }

        return temLetra && temNumero;
    }
    public List<Map<String, Object>> getRanking() {
        return usuarioRepository.getRankingJogadores();
    }
}