function closeModal() {
    window.location.href = '/';
}

function togglePassword() {
    const passwordInput = document.getElementById('password');
    const toggleBtn = document.querySelector('.toggle-password');

    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        toggleBtn.textContent = '🐵';
    } else {
        passwordInput.type = 'password';
        toggleBtn.textContent = '🙈';
    }
}

document.getElementById('LoginForm').addEventListener('submit', async function(e) {
    e.preventDefault();

    const email = document.querySelector('input[type="email"]').value;
    const senha = document.getElementById('password').value;

    if (!email || !senha) {
        alert('Por favor, preencha todos os campos!');
        return;
    }

    const dadosLogin = {
        email: email,
        senha: senha
    };

    try {
        const response = await fetch('/api/usuarios/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(dadosLogin)
        });

        if (response.ok) {
            const mensagem = await response.text();
            alert(mensagem);
            window.location.href = '/';
        } else {
            const erro = await response.text();
            alert('Erro: ' + erro);
        }
    } catch (error) {
        console.error('Erro ao fazer login:', error);
        alert('Erro ao conectar com o servidor. Tente novamente!');
    }
});