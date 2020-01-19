# Java Pong UDP Client Server

Two player client server pong game.
Server starts at random port.
Player can enter and leave multiple active rooms managed by the thread started at that port.

## Getting Started

Run two Client classes, one for each SWING GUI and run the Server class. Press enter on each window, wait for the countdown end and play.

### Prerequisites

Have ready Java 8 to make sure all classes will actually compile.

### Actually good portuguese instructions bellow

SERVIDOR

PARA COMPILAR ARQUIVOS
ENTRE NO DIRETÒRIO ---> /java-pong-udp-client-server/server/src/main
Execute o script ---> find . -name "*.java" | xargs javac

ENTRE NESTE CAMINHO PARA EXECUÇÂO -> /java-pong-udp-client-server/server/src;
EXECUTE -> $ java -cp ./ main.Server

CASO O SCRIPT DE COMPILACAO NAO FUNCIONE
--> utilize o Eclipse diretamente para compilar o arquivos, é apenas necessário adicionar o projeto no eclipse, que o mesmo é compilado. Abrir então o diretório 
 /java-pong-udp-client-server/server/bin;
EXECUTE COM O COMANDO ABAIXO
EXECUTE -> $ java -cp ./ main.Server

OU DIRETAMENTE ABRIR O DIRETORIO BIN
ENTRE NESTE CAMINHO PARA EXECUÇÂO -> /java-pong-udp-client-server/server/bin;
EXECUTE -> $ java -cp ./ main.Server

CASO O SCRIPT FUNCIONE
ENTRE NESTE CAMINHO PARA EXECUÇÂO -> /java-pong-udp-client-server/server/src;
EXECUTE -> $ java -cp ./ main.Server

ENFIM:

Por exemplo, o servidor será o endereço: 192.168.80.63;

O server executará em uma porta qualquer.

------- ATENÇÂO

----->> A porta em execução do servidor será mostrada no terminal em que o mesmo foi inicializado.

-------

CLIENTE


PARA COMPILAR ARQUIVOS NO LINUX
ENTRE NO DIRETÒRIO ---> /java-pong-udp-client-server/client/src/main
Execute o script ---> find . -name "*.java" | xargs javac

CASO O SCRIPT DE COMPILACAO NAO FUNCIONE
--> utilize o Eclipse diretamente para compilar o arquivos, é apenas necessário adicionar o projeto no eclipse, que o mesmo é compilado. Abrir então o diretório 
 /java-pong-udp-client-server/client/bin;
EXECUTE COM O COMANDO ABAIXO
--> $ java -cp ./ main.Client 192.168.80.63 35221 4 2 JOGADOR_1

OU DIRETAMENTE ABRIR O DIRETORIO BIN 
ENTRE NESTE CAMINHO PARA EXECUÇÂO -> /java-pong-udp-client-server/client/bin;
EXECUÇÂO DE EXEMPLO  --> $ java -cp ./ main.Client 192.168.80.63 35221 4 2 JOGADOR_1

CASO O SCRIPT FUNCIONE

ENTRE NESTE CAMINHO PARA EXECUÇÂO -> /java-pong-udp-client-server/client/src;
EXECUÇÂO DE EXEMPLO  --> $ java -cp ./ main.Client 192.168.80.63 35221 4 2 JOGADOR_1
(ENDERECO SERVIDOR) (PORTA) (QUANTIDADE ROUNDS) (QUANTIDADE PONTOS POR ROUNDS) (NOME)

ENFIM:

192.168.80.63: É o endereço do servidor.

35221: É a porta em que o servidor está em execução. A mesma porta que foi apresentada no terminal de execução do servidor.

4: É o número máximo de rounds de uma partida. Pode ser qualquer número.

2: É o número máximo de pontos de um jogador em uma partida. Pode ser qualquer número.

JOGADOR_1: É apenas um nome que foi escolhido para o jogador.


Exemplo utilização de um servidor com dois clientes.

/java-pong-udp-client-server/server/bin -> $ java -cp ./ main.Server

/java-pong-udp-client-server/client/bin -> $ java -cp ./ main.Client 192.168.80.63 35221 4 2 JOGADOR_1

/java-pong-udp-client-server/client/bin -> $ java -cp ./ main.Client 192.168.80.63 35221 4 2 JOGADOR_2