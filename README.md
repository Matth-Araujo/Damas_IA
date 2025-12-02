Software Necessário
Software	Versão Mínima	Download
Java JDK	21	https://www.oracle.com/java/technologies/downloads/

Maven	3.9+	https://maven.apache.org/download.cgi

PostgreSQL (opcional se usar Docker)	17	https://www.postgresql.org/download/

Docker	-	https://www.docker.com/get-started/

Git	2.40+	https://git-scm.com/install/

IDE	-	IntelliJ IDEA / VS Code

💡 Dica: No IntelliJ, o JDK e o Maven já vêm instalados.
💡 Recomendação: Use Docker. Assim, você não precisa instalar o PostgreSQL no sistema operacional.

Instalação Passo a Passo
1. Clonar o Repositório
   git clone https://github.com/Matth-Araujo/Damas_IA.git
   cd Damas_IA

2. Criar o Arquivo .env

Na raiz do projeto, crie um arquivo chamado .env contendo:

POSTGRES_USER=nome_usuario
POSTGRES_PASSWORD=sua_senha
POSTGRES_DB=damasdb
DB_HOST_PORT=5432

PGADMIN_EMAIL=admin@damas.com
PGADMIN_PASSWORD=sua_senha
PGADMIN_PORT=5050

Build do Projeto
   ./gradlew build

Subir os Contêineres com Docker
   docker compose up --build -d

Configurar PgAdmin

Acesse no navegador:

http://localhost:5050


Faça login com o e-mail e senha definidos no .env.

Adicione um novo servidor com:

Host name: damas_db

Port: 5432

Maintenance DB: padrão

Username: mesmo do .env

Password: mesma do .env

Executar a Aplicação

Execute pela IDE (Run) ou via terminal.

Acesse:

http://localhost:8080

❗ Possível Erro e Solução

Se ao adicionar o servidor aparecer:

Unable to connect to server:
[Errno -2] Name does not resolve


Faça o seguinte:

Vá até a pasta Data/ do projeto e delete a pasta postgres/.

Execute:

docker compose down
docker compose up --build -d


Depois disso, tente adicionar novamente o servidor no PgAdmin.  