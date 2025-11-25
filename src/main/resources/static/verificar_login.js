
async function verificarLogin() {
    try {
        const response = await fetch('/api/usuarios/verificar-login');
        const data = await response.json();

        if (data.logado) {
            console.log('Usuário logado:', data.nome);
            mostrarUsuarioLogado(data);
        } else {
            console.log('Usuário não está logado');
            mostrarUsuarioDeslogado();
        }
    } catch (error) {
        console.error('Erro ao verificar login:', error);
    }
}


function mostrarUsuarioLogado(usuario) {

    const sidebar = document.querySelector('.sidebar-left');
    if (sidebar) {

        const btnSignup = sidebar.querySelector('.btn-signup');
        const btnLogin = sidebar.querySelector('.btn-login');

        if (btnSignup) btnSignup.style.display = 'none';
        if (btnLogin) btnLogin.style.display = 'none';


        const userInfo = document.createElement('div');
        userInfo.style.cssText = 'padding: 12px; color: #007BFF; text-align: center; font-weight: bold; margin-top: auto;';
        userInfo.textContent = usuario.nome;


        const footer = sidebar.querySelector('.footer-sidebar');
        if (footer) {
            sidebar.insertBefore(userInfo, footer);
        } else {
            sidebar.appendChild(userInfo);
        }


        const btnLogout = document.createElement('button');
        btnLogout.className = 'btn-login';
        btnLogout.textContent = 'Sair';
        btnLogout.onclick = fazerLogout;
        btnLogout.style.marginTop = '10px';

        if (footer) {
            sidebar.insertBefore(btnLogout, footer);
        } else {
            sidebar.appendChild(btnLogout);
        }
    }


    const playerInfo = document.querySelector('.player-info span');
    if (playerInfo) {
        playerInfo.textContent = usuario.nome;
    }


    const sidebarLeftName = document.querySelector('.sidebar-left .logo + div');
    if (sidebarLeftName) {
        sidebarLeftName.textContent = usuario.nome;
        sidebarLeftName.style.color = '#81b64c';
    }
}

function mostrarUsuarioDeslogado() {

    const playerInfo = document.querySelector('.player-info span');
    if (playerInfo) {
        playerInfo.textContent = 'Jogador';
    }
}


async function fazerLogout() {
    try {
        const response = await fetch('/api/usuarios/logout', {
            method: 'POST'
        });

        if (response.ok) {
            alert('Logout realizado com sucesso!');
            window.location.href = '/';
        }
    } catch (error) {
        console.error('Erro ao fazer logout:', error);
    }
}


document.addEventListener('DOMContentLoaded', verificarLogin);