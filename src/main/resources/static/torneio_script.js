let torneioId = null;
let participantes = [];
let fase = "QUARTAS";
let partidaEmAndamento = false;

// Variáveis do jogo
let pecaSelecionada = null;
let movimentosValidos = [];

// Inicia o torneio
async function iniciarTorneio() {
    try {
        const response = await fetch('/api/torneio/iniciar', {
            method: 'POST'
        });

        const data = await response.json();

        if (response.ok) {
            torneioId = data.torneioId;
            participantes = data.participantes;

            mostrarTela('tela-chaveamento');
            renderizarChaveamento();

            alert('Torneio iniciado! Boa sorte! 🏆');
        } else {
            alert('Erro: ' + data.erro);
        }
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao iniciar torneio');
    }
}

// Renderiza o chaveamento
function renderizarChaveamento() {
    const chaveamentoDiv = document.getElementById('chaveamento');
    chaveamentoDiv.innerHTML = '';

    // Agrupa em pares
    for (let i = 0; i < participantes.length; i += 2) {
        const card = document.createElement('div');
        card.className = 'partida-card';

        const jogador1 = participantes[i];
        const jogador2 = participantes[i + 1];

        card.innerHTML = `
            <h3>Partida ${(i / 2) + 1}</h3>
            <div class="jogador">
                <div>
                    <div class="jogador-nome">${jogador1.nome}</div>
                    <div class="jogador-nivel">${jogador1.tipo === 'HUMANO' ? '👤 Você' : '🤖 ' + jogador1.nivel}</div>
                </div>
            </div>
            <div style="text-align: center; margin: 10px 0; font-size: 24px;">⚔️</div>
            <div class="jogador">
                <div>
                    <div class="jogador-nome">${jogador2.nome}</div>
                    <div class="jogador-nivel">${jogador2.tipo === 'HUMANO' ? '👤 Você' : '🤖 ' + jogador2.nivel}</div>
                </div>
            </div>
        `;

        chaveamentoDiv.appendChild(card);
    }
}

// Próxima partida
async function proximaPartida() {
    try {
        const response = await fetch('/api/torneio/proxima-partida', {
            method: 'POST'
        });

        const data = await response.json();

        if (response.ok) {
            if (data.fase && !data.tipo) {
                // Mudança de fase
                fase = data.fase;
                document.getElementById('fase-titulo').textContent =
                    data.fase === 'SEMIFINAL' ? 'Semifinais' : 'Grande Final';
                alert(data.mensagem);

                // Aguarda um pouco e continua
                setTimeout(() => proximaPartida(), 1000);
                return;
            }

            if (data.torneioFinalizado) {
                // Torneio acabou
                mostrarResultadoFinal(data);
                return;
            }

            if (data.tipo === 'BOT_VS_BOT') {
                // Partida entre bots foi simulada
                alert(`${data.jogador1} vs ${data.jogador2}\n\n✅ ${data.vencedor} venceu!`);

                // Continua automaticamente
                setTimeout(() => proximaPartida(), 1500);

            } else if (data.tipo === 'HUMANO_VS_BOT') {
                // É sua vez de jogar
                mostrarTela('tela-partida');
                partidaEmAndamento = true;

                document.getElementById('info-partida').innerHTML = `
                    <h2>Sua Partida - ${fase}</h2>
                    <p><strong>Você</strong> (Brancas) vs <strong>${data.jogador2}</strong> (Pretas)</p>
                    <p>🎯 Boa sorte!</p>
                `;

                inicializarTabuleiroTorneio();
                renderizarTabuleiroTorneio(data.tabuleiro.grid);
            }
        } else {
            alert('Erro: ' + data.erro);
        }
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao processar partida');
    }
}

// Inicializa o tabuleiro do torneio
function inicializarTabuleiroTorneio() {
    const board = document.getElementById('board-torneio');
    board.innerHTML = '';

    for (let row = 0; row < 8; row++) {
        for (let col = 0; col < 8; col++) {
            const square = document.createElement('div');
            square.className = `square ${(row + col) % 2 === 0 ? 'light' : 'dark'}`;
            square.dataset.row = row;
            square.dataset.col = col;

            square.addEventListener('click', () => clicarCasaTorneio(row, col));

            board.appendChild(square);
        }
    }
}

// Renderiza o tabuleiro
function renderizarTabuleiroTorneio(grid) {
    const pieces = {
        'b': '⚫',
        'w': '⚪',
        'B': '♛',
        'W': '♕'
    };

    for (let row = 0; row < 8; row++) {
        for (let col = 0; col < 8; col++) {
            const square = document.querySelector(`#board-torneio [data-row="${row}"][data-col="${col}"]`);
            const piece = grid[row][col];

            const oldPiece = square.querySelector('.piece');
            if (oldPiece) oldPiece.remove();

            square.classList.remove('selected', 'valid-move', 'capture-move');

            if (piece && pieces[piece]) {
                const pieceDiv = document.createElement('div');
                pieceDiv.className = 'piece';
                pieceDiv.textContent = pieces[piece];
                square.appendChild(pieceDiv);
            }
        }
    }
}

// Clica em uma casa do torneio
async function clicarCasaTorneio(row, col) {
    if (!partidaEmAndamento) return;

    const square = document.querySelector(`#board-torneio [data-row="${row}"][data-col="${col}"]`);

    if (square.classList.contains('valid-move') || square.classList.contains('capture-move')) {
        await realizarJogadaTorneio(pecaSelecionada.row, pecaSelecionada.col, row, col);
        limparSelecaoTorneio();
        return;
    }

    const peca = square.querySelector('.piece');
    if (!peca) {
        limparSelecaoTorneio();
        return;
    }

    if (peca.textContent === '⚪' || peca.textContent === '♕') {
        await selecionarPecaTorneio(row, col);
    }
}

// Seleciona uma peça
async function selecionarPecaTorneio(row, col) {
    limparSelecaoTorneio();

    pecaSelecionada = { row, col };

    const square = document.querySelector(`#board-torneio [data-row="${row}"][data-col="${col}"]`);
    square.classList.add('selected');

    try {
        const response = await fetch('/api/jogo/movimentos-validos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ linha: row, coluna: col })
        });

        const data = await response.json();

        if (response.ok) {
            movimentosValidos = data.movimentos;

            movimentosValidos.forEach(mov => {
                const destSquare = document.querySelector(`#board-torneio [data-row="${mov.linha}"][data-col="${mov.coluna}"]`);
                if (mov.captura === 1) {
                    destSquare.classList.add('capture-move');
                } else {
                    destSquare.classList.add('valid-move');
                }
            });
        }
    } catch (error) {
        console.error('Erro:', error);
    }
}

// Realiza uma jogada no torneio
async function realizarJogadaTorneio(origemLinha, origemColuna, destinoLinha, destinoColuna) {
    try {
        const response = await fetch('/api/jogo/jogar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                origemLinha,
                origemColuna,
                destinoLinha,
                destinoColuna
            })
        });

        const data = await response.json();

        if (response.ok) {
            renderizarTabuleiroTorneio(data.tabuleiro.grid);

            if (data.emCombo) {
                await selecionarPecaTorneio(data.posicaoCombo.linha, data.posicaoCombo.coluna);
                return;
            }


            // Verifica se a partida acabou
            if (data.status !== 'EM_ANDAMENTO') {
                partidaEmAndamento = false;

                let resultado;
                if (data.status === 'VITORIA_BRANCO') {
                    resultado = 'VITORIA';
                    alert('Você venceu esta partida!');
                } else if (data.status === 'VITORIA_PRETO') {
                    resultado = 'DERROTA';
                    alert('Você perdeu esta partida...');
                } else {
                    resultado = 'EMPATE';
                    alert('Empate!');
                }

                // Registra o resultado
                await registrarResultadoTorneio(resultado);
            }

        } else {
            alert('Erro: ' + data.erro);
        }
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao realizar jogada');
    }
}

// Registra resultado da partida
async function registrarResultadoTorneio(resultado) {
    try {
        const response = await fetch('/api/torneio/registrar-resultado', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ resultado: resultado })
        });

        const data = await response.json();

        if (response.ok) {
            if (data.torneioFinalizado) {
                mostrarResultadoFinal(data);
            } else {
                // Continua o torneio
                alert(data.mensagem);
                mostrarTela('tela-chaveamento');

                // Aguarda e vai para a próxima partida
                setTimeout(() => proximaPartida(), 2000);
            }
        }
    } catch (error) {
        console.error('Erro:', error);
    }
}

// Limpa seleção
function limparSelecaoTorneio() {
    pecaSelecionada = null;
    movimentosValidos = [];

    document.querySelectorAll('#board-torneio .square').forEach(square => {
        square.classList.remove('selected', 'valid-move', 'capture-move');
    });
}


// Mostra resultado final
function mostrarResultadoFinal(data) {
    mostrarTela('tela-resultado');

    const resultadoDiv = document.getElementById('resultado-final');

    console.log('📊 Dados do resultado:', data); // DEBUG

    // Verifica se o jogador é o campeão
    const jogadorCampeao = data.campeao && (
        data.campeao.includes('Jogador') ||
        data.campeao === 'Você' ||
        data.posicaoFinal === 1
    );

    if (jogadorCampeao || data.posicaoFinal === 1) {
        // Você é o campeão
        resultadoDiv.className = 'resultado-final campeao';
        resultadoDiv.innerHTML = `
            <h2>🏆 PARABÉNS! VOCÊ É O CAMPEÃO! 🏆</h2>
            <p>🥇 1º Lugar</p>
            <p>Você venceu todos os adversários e conquistou o torneio!</p>
        `;
    } else {
        // Você foi eliminado
        resultadoDiv.className = 'resultado-final eliminado';

        let posicao = data.posicaoFinal || data.posicaoJogador || 0;
        let mensagemPosicao = '';

        if (posicao === 2) {
            mensagemPosicao = '🥈 Vice-Campeão! Você chegou à final!';
        } else if (posicao === 3 || posicao === 4) {
            mensagemPosicao = `🥉 ${posicao}º Lugar - Semifinalista`;
        } else if (posicao > 0) {
            mensagemPosicao = `${posicao}º Lugar`;
        } else {
            mensagemPosicao = 'Você foi eliminado';
        }

        const campeaoTexto = data.campeao ? `<p>🏆 Campeão: ${data.campeao}</p>` : '';

        resultadoDiv.innerHTML = `
            <h2>${data.eliminado ? 'Você foi eliminado' : 'Torneio Finalizado'}</h2>
            <p>${mensagemPosicao}</p>
            <p>${data.mensagem || ''}</p>
            ${campeaoTexto}
        `;
    }
}

// Desiste do torneio
async function desistirTorneio() {
    if (!confirm('Tem certeza que deseja desistir do torneio?')) {
        return;
    }

    try {
        const response = await fetch('/api/torneio/desistir', {
            method: 'POST'
        });

        const data = await response.json();

        if (response.ok) {
            alert(data.mensagem + '\nPosição final: ' + data.posicaoFinal + 'º lugar');
            window.location.href = '/';
        }
    } catch (error) {
        console.error('Erro:', error);
    }
}


function mostrarTela(idTela) {
    document.querySelectorAll('.tela').forEach(tela => {
        tela.classList.remove('active');
    });
    document.getElementById(idTela).classList.add('active');
}

// Verifica se o usuário está logado ao carregar
document.addEventListener('DOMContentLoaded', async () => {
    try {
        const response = await fetch('/api/usuarios/verificar-login');
        const data = await response.json();

        if (!data.logado) {
            alert('Você precisa fazer login para participar do torneio!');
            window.location.href = '/login';
        }
    } catch (error) {
        console.error('Erro ao verificar login:', error);
    }
});