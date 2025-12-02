let rankingCarregado = false;
let torneiosCarregados = false;
let nivelRankingAtual = 'geral';

// Alterna entre abas principais
function mostrarAba(aba) {
    document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.aba-content').forEach(content => content.classList.remove('active'));

    if (aba === 'historico') {
        document.querySelector('.tab:nth-child(1)').classList.add('active');
        document.getElementById('aba-historico').classList.add('active');
    } else if (aba === 'torneios') {
        document.querySelector('.tab:nth-child(2)').classList.add('active');
        document.getElementById('aba-torneios').classList.add('active');

        if (!torneiosCarregados) {
            carregarTorneios();
            torneiosCarregados = true;
        }
    } else if (aba === 'ranking') {
        document.querySelector('.tab:nth-child(3)').classList.add('active');
        document.getElementById('aba-ranking').classList.add('active');

        if (!rankingCarregado) {
            carregarRanking('geral');
            rankingCarregado = true;
        }
    }
}

// Alterna entre rankings (geral, fácil, médio, difícil)
function mostrarRanking(nivel) {
    nivelRankingAtual = nivel;

    // Remove active de todos os botões
    document.querySelectorAll('.ranking-tab').forEach(tab => tab.classList.remove('active'));

    // Adiciona active no botão clicado
    const botoes = document.querySelectorAll('.ranking-tab');
    if (nivel === 'geral') {
        botoes[0].classList.add('active');
    } else if (nivel === 'facil') {
        botoes[1].classList.add('active');
    } else if (nivel === 'medio') {
        botoes[2].classList.add('active');
    } else if (nivel === 'dificil') {
        botoes[3].classList.add('active');
    }

    // Converte para maiúsculo antes de chamar
    carregarRanking(nivel);
}

// Carrega ranking
async function carregarRanking(nivel) {
    const loading = document.getElementById('loading-ranking');
    const semRanking = document.getElementById('sem-ranking');
    const lista = document.getElementById('ranking-lista');

    loading.style.display = 'block';
    lista.innerHTML = '';
    semRanking.style.display = 'none';

    try {
        const endpoint = nivel === 'geral'
            ? '/api/usuarios/ranking/geral'
            : `/api/usuarios/ranking/${nivel}`;

        console.log('Carregando ranking de:', endpoint); // DEBUG

        const response = await fetch(endpoint);

        if (!response.ok) {
            throw new Error('Erro ao carregar ranking');
        }

        const ranking = await response.json();

        console.log('Ranking recebido:', ranking); // DEBUG

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
        console.error('Erro ao carregar ranking:', error);
        loading.textContent = 'Erro ao carregar ranking. Tente novamente.';
        loading.style.display = 'block';
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

// Carrega torneios do usuário
async function carregarTorneios() {
    const loading = document.getElementById('loading-torneios');
    const semTorneios = document.getElementById('sem-torneios');
    const lista = document.getElementById('torneios-lista');

    console.log(' Carregando torneios'); // DEBUG

    try {
        const response = await fetch('/api/torneio/meus-torneios');

        console.log(' Response status:', response.status); // DEBUG

        if (!response.ok) {
            if (response.status === 401) {
                alert('Você precisa fazer login!');
                window.location.href = '/login';
                return;
            }
            const errorText = await response.text();
            console.error('❌ Erro na resposta:', errorText); // DEBUG
            throw new Error('Erro ao carregar torneios');
        }

        const torneios = await response.json();
        console.log(' Torneios recebidos:', torneios); // DEBUG

        loading.style.display = 'none';

        if (torneios.length === 0) {
            semTorneios.style.display = 'block';
            console.log(' Nenhum torneio encontrado'); // DEBUG
            return;
        }

        torneios.forEach((torneio, index) => {
            const card = criarCardTorneio(torneio, index + 1);
            lista.appendChild(card);
        });

    } catch (error) {
        console.error(' Erro ao carregar torneios:', error);
        loading.textContent = 'Erro ao carregar torneios. Tente novamente.';
    }
}

// Carrega ranking
async function carregarRanking(nivel) {
    const loading = document.getElementById('loading-ranking');
    const semRanking = document.getElementById('sem-ranking');
    const lista = document.getElementById('ranking-lista');

    loading.style.display = 'block';
    lista.innerHTML = '';
    semRanking.style.display = 'none';

    console.log('🔄 Carregando ranking:', nivel); // DEBUG

    try {
        const endpoint = nivel === 'geral'
            ? '/api/usuarios/ranking/geral'
            : `/api/usuarios/ranking/${nivel.toUpperCase()}`; // CORRIGIDO: converte para maiúsculo

        console.log('📡 Endpoint:', endpoint); // DEBUG

        const response = await fetch(endpoint);

        console.log('📡 Response status:', response.status); // DEBUG

        if (!response.ok) {
            const errorText = await response.text();
            console.error('❌ Erro na resposta:', errorText); // DEBUG
            throw new Error('Erro ao carregar ranking');
        }

        const ranking = await response.json();

        console.log('✅ Ranking recebido:', ranking); // DEBUG

        loading.style.display = 'none';

        if (ranking.length === 0) {
            semRanking.style.display = 'block';
            console.log('⚠️ Nenhum jogador no ranking'); // DEBUG
            return;
        }

        ranking.forEach((jogador, index) => {
            const item = criarItemRanking(jogador, index + 1);
            lista.appendChild(item);
        });

    } catch (error) {
        console.error('❌ Erro ao carregar ranking:', error);
        loading.textContent = 'Erro ao carregar ranking. Tente novamente.';
        loading.style.display = 'block';
    }
}

// Cria card de partida
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

// Cria card de torneio
function criarCardTorneio(torneio, numero) {
    const card = document.createElement('div');
    card.className = 'torneio-card';

    card.innerHTML = `
        <div class="torneio-medalha">${torneio.medalha}</div>
        <div class="torneio-info">
            <div class="torneio-titulo">Torneio #${numero}</div>
            <div class="torneio-detalhes">
                <span>📊 Posição: ${torneio.posicaoFinal}º lugar</span>
            </div>
        </div>
    `;

    return card;
}


// Cria item de ranking de JOGADORES
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
    const melhorPartida = jogador.melhor_partida || 'N/A';
    const mediaMovimentos = jogador.media_movimentos || 0;

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
            <div class="ranking-stats" style="margin-top: 5px;">
                <span>🎯 Melhor: ${melhorPartida} mov</span>
                <span>📊 Média: ${mediaMovimentos} mov</span>
            </div>
        </div>
        <div class="ranking-taxa">${taxaVitoria.toFixed(1)}%</div>
    `;

    return item;
}

// Ver chaveamento do torneio
async function verChaveamento(torneioId) {
    try {
        const response = await fetch(`/api/torneio/${torneioId}/chaveamento`);
        const chaveamento = await response.json();

        if (response.ok) {
            mostrarModalChaveamento(chaveamento);
        } else {
            alert('Erro ao carregar chaveamento');
        }
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao carregar chaveamento');
    }
}

// Mostra modal com o chaveamento
function mostrarModalChaveamento(chaveamento) {
    const modal = document.getElementById('modal-chaveamento');
    const conteudo = document.getElementById('modal-conteudo');

    let html = '<h2>🏆 Chaveamento do Torneio</h2>';

    const fases = ['QUARTAS', 'SEMIFINAL', 'FINAL'];
    const nomeFases = {
        'QUARTAS': 'Quartas de Final',
        'SEMIFINAL': 'Semifinais',
        'FINAL': 'Final'
    };

    fases.forEach(fase => {
        if (chaveamento[fase] && chaveamento[fase].length > 0) {
            html += `
                <div class="chaveamento-fase">
                    <h3>${nomeFases[fase]}</h3>
                    <div class="chaveamento-partidas">
            `;

            chaveamento[fase].forEach((partida, index) => {
                html += `
                    <div class="chaveamento-partida">
                        <h4>Partida ${index + 1}</h4>
                        <div class="chaveamento-jogador ${partida.vencedor === partida.jogador1 ? 'vencedor' : ''}">
                            ${partida.jogador1}
                        </div>
                        <div style="text-align: center; margin: 5px 0;">⚔️</div>
                        <div class="chaveamento-jogador ${partida.vencedor === partida.jogador2 ? 'vencedor' : ''}">
                            ${partida.jogador2}
                        </div>
                    </div>
                `;
            });

            html += `</div></div>`;
        }
    });

    conteudo.innerHTML = html;
    modal.classList.add('active');
}

function fecharModal() {
    document.getElementById('modal-chaveamento').classList.remove('active');
}

// Carrega o historico quando a pagina carregar
document.addEventListener('DOMContentLoaded', carregarHistorico);