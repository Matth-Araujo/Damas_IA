let pecaSelecionada = null;
let movimentosValidos = [];
let partidaAtiva = false;
let emCombo = false;
let boardLocked = false;

// Inicializa o tabuleiro
function inicializarTabuleiro() {
    const board = document.getElementById('board');
    board.innerHTML = '';

    for (let row = 0; row < 8; row++) {
        for (let col = 0; col < 8; col++) {
            const square = document.createElement('div');
            square.className = `square ${(row + col) % 2 === 0 ? 'light' : 'dark'}`;
            square.dataset.row = row;
            square.dataset.col = col;

            square.addEventListener('click', () => clicarCasa(row, col));

            // Adiciona coordenadas
            if (col === 7) {
                const rankCoord = document.createElement('span');
                rankCoord.className = 'coord rank';
                rankCoord.textContent = 8 - row;
                square.appendChild(rankCoord);
            }
            if (row === 7) {
                const fileCoord = document.createElement('span');
                fileCoord.className = 'coord file';
                fileCoord.textContent = String.fromCharCode(97 + col); // a-h
                square.appendChild(fileCoord);
            }

            board.appendChild(square);
        }
    }
}

// Inicia nova partida
async function iniciarJogo(nivel) {
    try {
        const response = await fetch('/api/jogo/iniciar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nivel: nivel })
        });

        const data = await response.json();

        if (response.ok) {
            partidaAtiva = true;
            await atualizarTabuleiroComAnimacao(data.tabuleiro.grid);
            atualizarStatus('Sua vez! (Brancas)');

            // Esconde opções de nível
            document.getElementById('bot-levels').classList.remove('show');
            document.getElementById('bot-option').classList.remove('active');
        } else {
            alert('Erro ao iniciar partida: ' + data.erro);
        }
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao conectar com o servidor');
    }
}

//Adiciona animação de fade para suavizar a atualização
async function atualizarTabuleiroComAnimacao(grid) {
    const board = document.getElementById('board');
    board.classList.add('fading-out');

    await new Promise(resolve => setTimeout(resolve, 400)); // Espera o fade-out

    renderizarTabuleiro(grid); // Atualiza o tabuleiro enquanto invisível
    board.classList.remove('fading-out'); // Causa o fade-in do novo estado
}

// Renderiza o tabuleiro com as peças
function renderizarTabuleiro(grid) {
    const pieces = {
        'b': '⚫', // peça preta
        'w': '⚪', // peça branca
        'B': '♛', // DAMA PRETA
        'W': '♕'  // DAMA BRANCA
    };

    for (let row = 0; row < 8; row++) {
        for (let col = 0; col < 8; col++) {
            const square = document.querySelector(`[data-row="${row}"][data-col="${col}"]`);
            const piece = grid[row][col];

            // Remove peça anterior
            const oldPiece = square.querySelector('.piece');
            if (oldPiece) oldPiece.remove();

            // Remove destaque
            square.classList.remove('selected', 'valid-move', 'capture-move');

            // Adiciona nova peça
            if (piece && pieces[piece]) {
                const pieceDiv = document.createElement('div');
                pieceDiv.className = 'piece';
                pieceDiv.textContent = pieces[piece];
                square.appendChild(pieceDiv);
            }
        }
    }
}

// Clica em uma casa
async function clicarCasa(row, col) {
    if (boardLocked) return;

    if (!partidaAtiva) {
        alert('Inicie uma partida primeiro!');
        return;
    }

    const square = document.querySelector(`[data-row="${row}"][data-col="${col}"]`);

    // Se clicou em um movimento válido
    if (square.classList.contains('valid-move') || square.classList.contains('capture-move')) {
        await realizarJogada(pecaSelecionada.row, pecaSelecionada.col, row, col);
        limparSelecao();
        return;
    }

    // Se clicou em uma casa vazia, desmarca
    const peca = square.querySelector('.piece');
    if (!peca) {
        limparSelecao();
        return;
    }

    // Se clicou em uma peça branca, seleciona
    if (peca.textContent === '⚪' || peca.textContent === '♕') {
        await selecionarPeca(row, col);
    }
}

// Seleciona uma peça
async function selecionarPeca(row, col) {
    limparSelecao();

    pecaSelecionada = { row, col };

    // Destaca a peça selecionada
    const square = document.querySelector(`[data-row="${row}"][data-col="${col}"]`);
    square.classList.add('selected');

    // Busca movimentos válidos
    try {
        const response = await fetch('/api/jogo/movimentos-validos', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ linha: row, coluna: col })
        });

        const data = await response.json();

        if (response.ok) {
            movimentosValidos = data.movimentos;

            // Destaca movimentos válidos
            movimentosValidos.forEach(mov => {
                const destSquare = document.querySelector(`[data-row="${mov.linha}"][data-col="${mov.coluna}"]`);
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

// Realiza uma jogada
async function realizarJogada(origemLinha, origemColuna, destinoLinha, destinoColuna) {
    boardLocked = true;
    document.querySelector('.main-content').classList.add('locked');

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
            await atualizarTabuleiroComAnimacao(data.tabuleiro.grid);

            if (data.emCombo) {
                emCombo = true;
                atualizarStatus('Continue capturando!');
                // Seleciona automaticamente a peça que deve continuar
                await selecionarPeca(data.posicaoCombo.linha, data.posicaoCombo.coluna);
            } else {
                emCombo = false;
                atualizarStatus(data.mensagem || 'Jogada realizada!');
            }

            // Verifica se o jogo acabou
            if (data.status !== 'EM_ANDAMENTO') {
                partidaAtiva = false;
                setTimeout(() => {
                    alert(data.mensagem);
                }, 500);
            }

        } else {
            alert('Erro: ' + data.erro);
        }
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao realizar jogada');
    } finally {
        boardLocked = false;
        document.querySelector('.main-content').classList.remove('locked');
    }
}

// Limpa a seleção
function limparSelecao() {
    pecaSelecionada = null;
    movimentosValidos = [];

    document.querySelectorAll('.square').forEach(square => {
        square.classList.remove('selected', 'valid-move', 'capture-move');
    });
}

// Atualiza o status do jogo
function atualizarStatus(mensagem) {
    const statusDiv = document.querySelector('.player-info span') ||
        document.querySelector('.opponent-info span');
    if (statusDiv) {
        statusDiv.textContent = mensagem;
    }
}

// Desistir da partida
async function desistirPartida() {
    if (!partidaAtiva) {
        alert('Nenhuma partida em andamento');
        return;
    }

    if (!confirm('Tem certeza que deseja desistir?')) {
        return;
    }

    try {
        const response = await fetch('/api/jogo/desistir', {
            method: 'POST'
        });

        if (response.ok) {
            partidaAtiva = false;
            alert('Você desistiu da partida');
            inicializarTabuleiro();
        }
    } catch (error) {
        console.error('Erro:', error);
    }
}

// Modifica a função toggleBotLevels existente
function toggleBotLevels() {
    const botLevels = document.getElementById('bot-levels');
    const botOption = document.getElementById('bot-option');

    botLevels.classList.toggle('show');
    botOption.classList.toggle('active');
}

// Modifica a função iniciarJogoBot existente
function iniciarJogoBot(nivel) {
    iniciarJogo(nivel);
}

// Inicializa quando a página carrega
document.addEventListener('DOMContentLoaded', () => {
    inicializarTabuleiro();
});