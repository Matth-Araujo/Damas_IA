// Criar tabuleiro
const board = document.getElementById('board');
const pieces = {
    // Peças normais
    'b': '⚫', // peça preta
    'w': '⚪', // peça branca

    'B': '', // dama preta
    'W': ''  // dama branca
};

const initialPosition = [
    ['', 'b', '', 'b', '', 'b', '', 'b'],
    ['b', '', 'b', '', 'b', '', 'b', ''],
    ['', 'b', '', 'b', '', 'b', '', 'b'],
    ['', '', '', '', '', '', '', ''],
    ['', '', '', '', '', '', '', ''],
    ['w', '', 'w', '', 'w', '', 'w', ''],
    ['', 'w', '', 'w', '', 'w', '', 'w'],
    ['w', '', 'w', '', 'w', '', 'w', '']
];

const files = ['a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'];

function toggleBotLevels() {
    const botLevels = document.getElementById('bot-levels');
    const botOption = document.getElementById('bot-option');

    botLevels.classList.toggle('show');
    botOption.classList.toggle('active');
}

// NOVO: Função para iniciar jogo com bot
function iniciarJogoBot(nivel) {
    console.log('Iniciando jogo com bot:', nivel);

    // Fecha o menu
    const botLevels = document.getElementById('bot-levels');
    const botOption = document.getElementById('bot-option');
    botLevels.classList.remove('show');
    botOption.classList.remove('active');

    // Mostra confirmação
    alert(`Iniciando jogo no nível: ${nivel.toUpperCase()}`);

    // Aqui você implementará a lógica do jogo
}


for (let row = 0; row < 8; row++) {
    for (let col = 0; col < 8; col++) {
        const square = document.createElement('div');
        square.className = `square ${(row + col) % 2 === 0 ? 'light' : 'dark'}`;

        const piece = initialPosition[row][col];
        if (piece) {
            square.textContent = pieces[piece];
        }

        // Adicionar coordenadas
        if (col === 7) {
            const rankCoord = document.createElement('span');
            rankCoord.className = 'coord rank';
            rankCoord.textContent = 8 - row;
            square.appendChild(rankCoord);
        }
        if (row === 7) {
            const fileCoord = document.createElement('span');
            fileCoord.className = 'coord file';
            fileCoord.textContent = files[col];
            square.appendChild(fileCoord);
        }

        board.appendChild(square);
    }
}