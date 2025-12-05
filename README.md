# 🎮 Damas IA - Sistema de Jogo de Damas com Inteligência Artificial

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

Sistema completo de jogo de damas brasileiro com três níveis de IA (Fácil, Médio e Difícil), sistema de torneios, ranking global e histórico de partidas.

---

## ✨ Características

### 🎯 Jogabilidade
- ✅ Jogo de damas brasileiro completo com todas as regras oficiais
- ✅ Capturas obrigatórias e combos múltiplos
- ✅ Promoção automática a dama
- ✅ Detecção de empate (40 movimentos sem captura)
- ✅ Interface visual intuitiva com SVG

### 🤖 Inteligência Artificial
- **Nível Fácil**: Movimentos aleatórios
- **Nível Médio**: Algoritmo Greedy com heurística
- **Nível Difícil**: Minimax com Poda Alfa-Beta (profundidade 4)

### 🏆 Sistema de Torneios
- Torneio eliminatório com 8 participantes (1 jogador + 7 IAs)
- Chaveamento visual dinâmico
- Fases: Quartas de Final → Semifinais → Final
- Simulação automática de partidas entre bots
- Salvamento de resultados e posição final

### 📊 Ranking e Estatísticas
- Ranking global de jogadores
- Rankings separados por nível de dificuldade (Fácil, Médio, Difícil)
- Histórico completo de partidas
- Estatísticas detalhadas (vitórias, derrotas, melhor partida, média de movimentos)

### 👤 Sistema de Usuários
- Cadastro e login com criptografia BCrypt
- Sessões persistentes
- Perfil personalizado com estatísticas

---

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.x**
    - Spring Web
    - Spring Security
    - Spring JDBC
- **PostgreSQL 17**
- **Maven**

### Frontend
- **HTML5**
- **CSS3** (Design responsivo)
- **JavaScript ES6+** (Vanilla)
- **SVG** para as peças do jogo

### Segurança
- **BCrypt** para hash de senhas
- **HttpSession** para gerenciamento de sessões

### Padrões Utilizados
- **MVC** (Model-View-Controller)
- **Repository Pattern**
- **Service Layer Pattern**
- **Strategy Pattern** (para IAs)
- **Polimorfismo** (Jogador → JogadorHumano/IA)

---

## 🎮 Funcionalidades

### 1. Sistema de Jogo
- Tabuleiro 8x8 com peças SVG
- Clique para selecionar e mover
- Destaque visual de movimentos válidos (verde) e capturas (vermelho)
- Capturas múltiplas obrigatórias (combo)
- Promoção automática a dama na última linha
- Contagem de movimentos sem captura para empate

### 2. Modos de Jogo
- **Partida Avulsa**: Jogue contra IA nos três níveis
- **Torneio**: Compita contra 7 IAs em formato eliminatório

### 3. Histórico e Estatísticas
- Visualize todas as suas partidas
- Filtre por nível de dificuldade
- Veja estatísticas completas (vitórias, derrotas, média de movimentos)

### 4. Ranking Global
- Ranking geral de todos os jogadores
- Rankings específicos por nível de IA
- Top 10 jogadores com mais vitórias

---

## 📋 Pré-requisitos

| Software | Versão Mínima | Download |
|----------|---------------|----------|
| Java JDK | 21 | [Oracle Java](https://www.oracle.com/java/technologies/downloads/) |
| Maven | 3.9+ | [Apache Maven](https://maven.apache.org/download.cgi) |
| PostgreSQL | 17 | [PostgreSQL](https://www.postgresql.org/download/) |
| Docker | - | [Docker](https://www.docker.com/get-started/) |
| Git | 2.40+ | [Git SCM](https://git-scm.com/install/) |

> 💡 **Dica**: No IntelliJ IDEA, o JDK e o Maven já vêm instalados.  
> 💡 **Recomendação**: Use Docker para evitar instalação manual do PostgreSQL.

---

## 🚀 Instalação

### 1️⃣ Clonar o Repositório

```bash
git clone https://github.com/Matth-Araujo/Damas_IA.git
cd Damas_IA
```

### 2️⃣ Criar o Arquivo `.env`

Na raiz do projeto, crie um arquivo chamado `.env` com o seguinte conteúdo:

```env
# Configurações do PostgreSQL
POSTGRES_USER=nome_usuario
POSTGRES_PASSWORD=sua_senha
POSTGRES_DB=damasdb
DB_HOST_PORT=5432

# Configurações do PgAdmin
PGADMIN_EMAIL=admin@damas.com
PGADMIN_PASSWORD=sua_senha
PGADMIN_PORT=5050
```

### 3️⃣ Build do Projeto

```bash
./gradlew build
```

### 4️⃣ Subir os Contêineres com Docker

```bash
docker compose up --build -d
```

### 5️⃣ Configurar o PgAdmin

1. Acesse no navegador: **http://localhost:5050**
2. Faça login com o e-mail e senha definidos no `.env`
3. Adicione um novo servidor com as seguintes configurações:
    - **Host name**: `damas_db`
    - **Port**: `5432`
    - **Maintenance DB**: `postgres` (padrão)
    - **Username**: mesmo do `.env`
    - **Password**: mesma do `.env`

### 6️⃣ Executar a Aplicação

Execute pela IDE (botão Run) ou via terminal:

```bash
./gradlew bootRun
```

Acesse a aplicação em: **http://localhost:8080**

---

## ⚠️ Solução de Problemas

### Erro: "Unable to connect to server: [Errno -2] Name does not resolve"

Se este erro aparecer ao adicionar o servidor no PgAdmin:

1. Vá até a pasta `Data/` do projeto e delete a pasta `postgres/`
2. Execute os comandos:

```bash
docker compose down
docker compose up --build -d
```

3. Tente adicionar novamente o servidor no PgAdmin

---

## 👨‍💻 Autor

**Matheus Araujo**
**Davi Abud**

- GitHub: [@Matth-Araujo](https://github.com/Matth-Araujo)

---

## 🤝 Contribuições

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues e pull requests.

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

---

