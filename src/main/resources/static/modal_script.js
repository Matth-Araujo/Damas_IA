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

document.getElementById('signupForm').addEventListener('submit', async function(e) {
    e.preventDefault();


    const nome = document.querySelector('input[type="text"]').value;
    const email = document.querySelector('input[type="email"]').value;
    const senha = document.getElementById('password').value;


    if (!nome || !email || !senha) {
        alert('Por favor, preencha todos os campos!');
        return;
    }

    if (senha.length < 6) {
        alert('A senha deve ter pelo menos 6 caracteres!');
        return;
    }


    const usuario = {
        nome: nome,
        email: email,
        senha: senha
    };

    try {

        const response = await fetch('/api/usuarios/cadastrar', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(usuario)
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
        console.error('Erro ao cadastrar:', error);
        alert('Erro ao conectar com o servidor. Tente novamente!');
    }
});