let rankingCarregado = false;

// Alterna entre abas
function mostrarAba(aba) {
    // Remove active de todas as abas
    document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.aba-content').forEach(content => content.classList.remove('active'));

    // Adiciona active na aba selecionada
    if (aba === 'historico') {
        document.querySelector('.tab:first-child').classList.add('active');
        document.getElementById('aba-historico').classList.add('active');
    } else {
        document.querySelector('.tab:last-child').classList.add('active');
        document.getElementById('aba-ranking').classList.add('active');

        // Carrega ranking apenas uma vez
        if (!rankingCarregado) {
            carregarRanking();
            rankingCarregado = true;
        }
    }
}

// Carrega historico de partidas
async function carregarHistorico() {
    const loading = document.getElementById('loading-historico');
    const semPartidas = document.getElementById('sem-partidas');
    const lista = document.getElementById('historico-lista');

    try {
        const response = await fetch('/api/partidas/historico');

        if (!response.ok) {
            if (response.status === 401) {
                alert('Você precisa fazer login para ver o histórico!');
                window.location.href = '/login';
                return;
            }
            throw new Error('Erro ao carregar histórico');
        }

        const partidas = await response.json();

        loading.style.display = 'none';

        if (partidas.length === 0) {
            semPartidas.style.display = 'block';
            return;
        }

        partidas.forEach(partida => {
            const card = criarCardPartida(partida);
            lista.appendChild(card);
        });

    } catch (error) {
        console.error('Erro:', error);
        loading.textContent = 'Erro ao carregar histórico. Tente novamente.';
    }
}

// Carrega ranking de jogadores
async function carregarRanking() {
    const loading = document.getElementById('loading-ranking');
    const semRanking = document.getElementById('sem-ranking');
    const lista = document.getElementById('ranking-lista');

    try {
        const response = await fetch('/api/usuarios/ranking');

        if (!response.ok) {
            throw new Error('Erro ao carregar ranking');
        }

        const ranking = await response.json();

        loading.style.display = 'none';

        if (ranking.length === 0) {
            semRanking.style.display = 'block';
            return;
        }

        ranking.forEach((jogador, index) => {
            const item = criarItemRanking(jogador, index + 1);
            lista.appendChild(item);
        });

    } catch (error) {
        console.error('Erro:', error);
        loading.textContent = 'Erro ao carregar ranking. Tente novamente.';
    }
}

function criarCardPartida(partida) {
    const card = document.createElement('div');
    card.className = 'partida-card';

    const icones = {
        'VITORIA': '🏆',
        'DERROTA': '😞',
        'EMPATE': '🤝'
    };

    const data = new Date(partida.dataHora);
    const dataFormatada = data.toLocaleDateString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });

    card.innerHTML = `
        <div class="resultado-icon">${icones[partida.resultado]}</div>
        <div class="partida-info">
            <div class="partida-titulo">vs ${partida.adversario}</div>
            <div class="partida-detalhes">
                <span>📅 ${dataFormatada}</span>
                <span>🎯 ${partida.movimentos} movimentos</span>
                ${partida.nivel ? `<span>⚙️ Nível: ${partida.nivel}</span>` : ''}
            </div>
        </div>
        <div class="partida-resultado resultado-${partida.resultado.toLowerCase()}">
            ${partida.resultado}
        </div>
    `;

    return card;
}

function criarItemRanking(jogador, posicao) {
    const item = document.createElement('div');
    item.className = 'ranking-item';

    let medalIcon = '';
    let posicaoClass = '';

    if (posicao === 1) {
        medalIcon = '🥇';
        posicaoClass = 'top1';
    } else if (posicao === 2) {
        medalIcon = '🥈';
        posicaoClass = 'top2';
    } else if (posicao === 3) {
        medalIcon = '🥉';
        posicaoClass = 'top3';
    }

    const taxaVitoria = jogador.taxa_vitoria || 0;

    item.innerHTML = `
        ${medalIcon ? `<div class="medal-icon">${medalIcon}</div>` : ''}
        <div class="ranking-posicao ${posicaoClass}">#${posicao}</div>
        <div class="ranking-info">
            <div class="ranking-nome">${jogador.nome}</div>
            <div class="ranking-stats">
                <span>🏆 ${jogador.vitorias} vitórias</span>
                <span>💔 ${jogador.derrotas} derrotas</span>
                <span>🎮 ${jogador.total_partidas} partidas</span>
            </div>
        </div>
        <div class="ranking-taxa">${taxaVitoria.toFixed(1)}%</div>
    `;

    return item;
}

// Carrega o historico quando a pagina carregar
document.addEventListener('DOMContentLoaded', carregarHistorico);