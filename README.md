# SAFS - Surface Autonomous Fleet System

**Global Solution FIAP 2026 - 1º Semestre**
**Curso:** Engenharia de Software | **Disciplina:** Domain Driven Design - Java
**Professor:** Eduardo dos Santos Ramos | **Turma:** 2ESPG

---

## Sobre o Projeto

O SAFS (Surface Autonomous Fleet System) é um sistema de gestão de frota autônoma de superfície desenvolvido no contexto da Nova Economia Espacial. O sistema simula o gerenciamento de sondas robóticas em missões de exploração e mineração na Lua ou em Marte, onde o delay de comunicação com a Terra torna o controle remoto inviável.

O **Comandante de Missão** interage com o sistema via terminal (CLI), configurando parâmetros estratégicos, lançando sondas, atribuindo coordenadas de extração e monitorando o status da frota — sem precisar pilotar cada robô manualmente.

---

## Como Rodar o Projeto

### Pré-requisito

- [Java 17+](https://www.oracle.com/java/technologies/downloads/)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) (Community Edition é gratuita)

### Passo a passo no IntelliJ IDEA

**1. Abrir o projeto**

- Abra o IntelliJ IDEA
- Clique em **File → Open**
- Selecione a pasta `GS-Projeto` e clique em **OK**

**2. Marcar a pasta src como raiz de código**

- No painel esquerdo (Project), clique com o botão direito na pasta `src`
- Vá em **Mark Directory as → Sources Root**
- A pasta `src` ficará azul — isso indica que o IntelliJ reconhece os arquivos Java

**3. Executar o sistema**

- Navegue até o arquivo `src/br/com/fiap/space/presentation/Main.java`
- Clique duas vezes para abrir
- Clique no ícone **▶** verde ao lado do método `main`
- Ou pressione **Shift + F10**

**4. Interagir com o menu**

O terminal do IntelliJ abrirá na parte inferior da tela. Digite o número da opção desejada e pressione **Enter**:

```
+--------------------------------------------+
|            MENU PRINCIPAL - SAFS           |
+--------------------------------------------+
|  1. Lancar Nova Sonda                      |
|  2. Listar Sondas Ativas                   |
|  3. Executar Missao (Atribuir Coordenada)  |
|  4. Recarregar Sonda na Base               |
|  5. Exibir Relatorios da Missao            |
|  0. Encerrar Sistema                       |
+--------------------------------------------+
Comandante, selecione uma opcao:
```

### Exemplo de uso rápido para testar

```
1  → Lancar sonda
1  → Tipo: MINERACAO
1  → Recurso: Gelo de Agua

3  → Executar missao
SND-001  → ID da sonda
5        → Coordenada X
10       → Coordenada Y
1        → Terreno: Planicie

5  → Ver relatorio gerado

0  → Encerrar
```

---

## Arquitetura - Domain Driven Design (DDD)

O projeto segue rigorosamente a separação em camadas do DDD:

```
br.com.fiap.space
├── presentation/      Camada de Apresentação (CLI via Scanner)
├── application/       Camada de Aplicação (Casos de Uso)
├── domain/            Camada de Domínio (Coração do Sistema)
│   ├── model/         Entidades e Value Objects
│   ├── exception/     Exceções de Domínio
│   ├── factory/       Padrão Factory Method
│   ├── interfaces/    Interfaces de Domínio
│   ├── repository/    Interfaces de Repositório
│   └── service/       Serviços de Domínio (Singleton)
└── infrastructure/    Camada de Infraestrutura (Repositórios em Memória)
```

---

## Estrutura de Arquivos

```
GS-Projeto/
├── README.md
└── src/
    └── br/com/fiap/space/
        ├── presentation/
        │   └── Main.java
        ├── application/
        │   └── MissaoService.java
        ├── domain/
        │   ├── model/
        │   │   ├── Sonda.java                (Entidade Abstrata)
        │   │   ├── SondaMineradora.java       (Entidade)
        │   │   ├── SondaExploradora.java      (Entidade)
        │   │   ├── Coordenada.java            (Value Object)
        │   │   ├── NivelEnergia.java          (Value Object)
        │   │   ├── CompartimentoCarga.java    (Value Object)
        │   │   ├── Recurso.java               (Enum)
        │   │   └── Terreno.java               (Enum)
        │   ├── exception/
        │   │   ├── BateriaCriticaException.java
        │   │   ├── CargaExcedidaException.java
        │   │   └── TerrenoInvalidoException.java
        │   ├── factory/
        │   │   └── SondaFactory.java
        │   ├── interfaces/
        │   │   └── Recarregavel.java
        │   ├── repository/
        │   │   └── SondaRepository.java
        │   └── service/
        │       └── CentroDeComando.java
        └── infrastructure/
            └── repository/
                └── SondaRepositoryImpl.java
```

---

## Padrões de Projeto Implementados

### 1. Factory Method — `SondaFactory`

Decide qual subclasse de `Sonda` instanciar com base no tipo informado pelo usuário. Gera IDs únicos automaticamente no formato `SND-001`, `SND-002`, etc.

```java
SondaFactory.criarSonda("MINERACAO", Recurso.GELO);  // retorna SondaMineradora
SondaFactory.criarSonda("EXPLORACAO");               // retorna SondaExploradora
```

### 2. Singleton — `CentroDeComando`

Garante que apenas **uma instância** do Centro de Comando exista em toda a aplicação, controlando o registro de todas as sondas ativas.

```java
CentroDeComando centro = CentroDeComando.getInstance();
```

### 3. Template Method — `executarRotinaAutonoma()`

Define o esqueleto fixo do algoritmo de missão na classe abstrata `Sonda`. O passo da ação local é um **hook** implementado de forma diferente por cada subclasse.

```
Passo 1 → validarStatusSistema()   verifica bateria e restrições de terreno
Passo 2 → mover()                  desloca a sonda consumindo energia
Passo 3 → realizarAcaoLocal()      HOOK: minera ou mapeia (polimorfismo)
Passo 4 → enviarRelatorio()        envia dados ao CentroDeComando
```

---

## Entidades e Value Objects

### Entidades

| Classe | Descrição |
|---|---|
| `Sonda` | Classe abstrata. Possui ID único, bateria, posição e terreno atual. |
| `SondaMineradora` | Extrai recursos do solo. Possui `CompartimentoCarga` e `Recurso` alvo. Não acessa Crateras. |
| `SondaExploradora` | Mapeia terreno com sensores de longo alcance. Acessa qualquer terreno. |

### Value Objects (imutáveis)

| Classe | Descrição |
|---|---|
| `Coordenada` | Par (X, Y) na malha da missão entre 0 e 100. Imutável — movimentos criam nova instância. |
| `NivelEnergia` | Energia atual e máxima da sonda. Operações de consumo retornam nova instância. |
| `CompartimentoCarga` | Volume ocupado e máximo da sonda mineradora. Adições retornam nova instância. |

### Enums

| Enum | Valores |
|---|---|
| `Recurso` | `GELO` (0.5 kg/L), `REGOLITO` (1.2 kg/L), `TITANIO` (4.5 kg/L) |
| `Terreno` | `PLANICIE` (x1.0), `SOLO_ROCHOSO` (x1.5), `CRATERA` (x2.0 — restrito a sondas com rodas) |

---

## Exceções de Domínio

O sistema nunca estoura exceções genéricas. Todas as situações de erro de negócio possuem exceções próprias e são exibidas de forma amigável no console com mensagens de orientação ao Comandante.

| Exceção | Quando é lançada |
|---|---|
| `BateriaCriticaException` | Tentativa de mover ou minerar sem energia suficiente. |
| `CargaExcedidaException` | Tentativa de extrair com o compartimento de carga lotado. |
| `TerrenoInvalidoException` | `SondaMineradora` tentando acessar uma `Cratera`. |

---

## Interface `Recarregavel`

Implementada por `SondaMineradora` e `SondaExploradora`. O método `conectarBase()` recarrega a bateria ao máximo e, no caso da mineradora, esvazia o compartimento de carga.

```java
public interface Recarregavel {
    void conectarBase();
}
```

---

## Regras de Negócio

- **Movimento:** consumo = distância (Manhattan) × 5.0 × multiplicador do terreno
- **Mineração:** consumo fixo de 15.0 energia por operação, extrai 10.0 L por vez
- **Mapeamento:** consumo fixo de 5.0 energia por varredura, cobre raio de 25 metros
- **Coordenadas:** limitadas ao intervalo de 0 a 100 em X e Y
- **Sonda Mineradora** não pode acessar `CRATERA` — lança `TerrenoInvalidoException`
- **Sonda Exploradora** pode navegar em qualquer tipo de terreno

---

## Linguagem Ubíqua (Glossário do Domínio)

| Termo | Significado |
|---|---|
| **Sonda** | Veículo autônomo (Rover) que opera na superfície. |
| **Coordenada** | Ponto exato na malha do terreno (X, Y). |
| **NivelEnergia** | Capacidade de energia da sonda. Toda ação consome energia. |
| **Terreno** | Característica do solo que afeta o consumo de bateria. |
| **Recurso** | Material extraído (Gelo, Regolito, Titânio). |
| **CompartimentoCarga** | Armazém interno da sonda mineradora com capacidade máxima. |
| **CentroDeComando** | Sistema central (Singleton) que registra sondas e recebe relatórios. |

---

## Autores

Desenvolvido para a Global Solution FIAP 2026 — Turma 2ESPG
Disciplina: Domain Driven Design - Java
Professor: Eduardo dos Santos Ramos
