# SAFS - Surface Autonomous Fleet System

**Global Solution FIAP 2026 - 1º Semestre**
**Curso:** Engenharia de Software | **Disciplina:** Domain Driven Design - Java
**Professor:** Eduardo dos Santos Ramos | **Turma:** 2ESPG

---

## Sobre o Projeto

O SAFS (Surface Autonomous Fleet System) é um sistema de gestão de frota autônoma de superfície desenvolvido no contexto da Nova Economia Espacial. O sistema simula o gerenciamento de sondas robóticas (rovers e drones) em missões de exploração e mineração na Lua ou em Marte, onde o delay de comunicação com a Terra torna o controle remoto inviável.

O **Comandante de Missão** interage com o sistema via terminal (CLI), configurando parâmetros estratégicos, lançando sondas, atribuindo coordenadas de extração e monitorando o status da frota — tudo sem "pilotar" manualmente cada robô.

---

## Arquitetura - Domain Driven Design (DDD)

O projeto segue rigorosamente a separação em camadas do DDD:

```
br.com.fiap.space
├── presentation/          Camada de Apresentação (CLI via Scanner)
├── application/           Camada de Aplicação (Casos de Uso)
├── domain/                Camada de Domínio (Coração do Sistema)
│   ├── model/             Entidades e Value Objects
│   ├── exception/         Exceções de Domínio
│   ├── factory/           Padrão Factory Method
│   ├── interfaces/        Interfaces de Domínio
│   ├── repository/        Interfaces de Repositório
│   └── service/           Serviços de Domínio (Singleton)
└── infrastructure/        Camada de Infraestrutura (Repositórios em Memória)
```

---

## Estrutura de Arquivos

```
GS-Projeto/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── br/com/fiap/space/
                ├── presentation/
                │   └── Main.java
                ├── application/
                │   └── MissaoService.java
                ├── domain/
                │   ├── model/
                │   │   ├── Sonda.java               (Entidade Abstrata)
                │   │   ├── SondaMineradora.java      (Entidade)
                │   │   ├── SondaExploradora.java     (Entidade)
                │   │   ├── Coordenada.java           (Value Object)
                │   │   ├── NivelEnergia.java         (Value Object)
                │   │   ├── CompartimentoCarga.java   (Value Object)
                │   │   ├── Recurso.java              (Enum/VO)
                │   │   └── Terreno.java              (Enum/VO)
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

Decide qual subclasse de `Sonda` instanciar com base no tipo informado pelo usuário. Gera automaticamente IDs únicos no formato `SND-001`, `SND-002`, etc.

```java
SondaFactory.criarSonda("MINERACAO", Recurso.GELO);   // → SondaMineradora
SondaFactory.criarSonda("EXPLORACAO");                 // → SondaExploradora
```

### 2. Singleton — `CentroDeComando`

Garante que apenas **uma instância** do Centro de Comando exista em toda a aplicação. Implementado com double-checked locking para segurança em ambientes concorrentes.

```java
CentroDeComando centro = CentroDeComando.getInstance();
```

### 3. Template Method — `executarRotinaAutonoma()`

Define o esqueleto fixo do algoritmo de missão na classe abstrata `Sonda`. O passo de ação local é um **hook** implementado polimorficamente por cada subclasse.

```
1. validarStatusSistema()  → verifica bateria e restrições de terreno
2. mover()                 → desloca a sonda consumindo energia
3. realizarAcaoLocal()     → HOOK: minera (SondaMineradora) ou mapeia (SondaExploradora)
4. enviarRelatorio()       → envia dados ao CentroDeComando
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
| `Coordenada` | Par (X, Y) na malha da missão (0–100). Imutável — movimentos criam nova instância. |
| `NivelEnergia` | Energia atual e máxima da sonda. Operações de consumo retornam nova instância. |
| `CompartimentoCarga` | Volume ocupado/máximo da sonda mineradora. Adições retornam nova instância. |

### Enums

| Enum | Valores |
|---|---|
| `Recurso` | `GELO` (0.5 kg/L), `REGOLITO` (1.2 kg/L), `TITANIO` (4.5 kg/L) |
| `Terreno` | `PLANICIE` (x1.0), `SOLO_ROCHOSO` (x1.5), `CRATERA` (x2.0 — restrito a rodas) |

---

## Exceções de Domínio

O sistema **nunca** deixa estourar exceções genéricas. Todas as situações de erro de negócio são tratadas com exceções customizadas e exibidas de forma amigável no console.

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

## Linguagem Ubíqua (Glossário do Domínio)

| Termo | Significado |
|---|---|
| **Sonda** | Veículo autônomo (Rover/Drone) que opera na superfície. |
| **Coordenada** | Ponto exato na malha do terreno (X, Y). |
| **Bateria / NivelEnergia** | Capacidade de energia da sonda. Toda ação consome energia. |
| **Terreno** | Característica do solo que afeta o consumo de bateria. |
| **Recurso** | Material extraído (Gelo, Regolito, Titânio). |
| **CompartimentoCarga** | Armazém interno da sonda mineradora. Possui capacidade máxima. |
| **CentroDeComando** | Sistema central que registra sondas e recebe relatórios de missão. |

---

## Como Executar

### Pré-requisitos

- Java 17+
- Maven 3.6+

### Compilar e gerar o JAR

```bash
mvn package
```

### Executar o sistema

```bash
java -jar target/safs.jar
```

### Menu do Sistema

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
```

---

## Exemplo de Fluxo de Missão

```
1. Lançar SondaMineradora (SND-001) com alvo: Gelo de Água
2. Atribuir coordenada (5, 10) em terreno Planície
   → Diagnóstico OK
   → Deslocamento: distância 15, consumo 75.0 energia
   → Extração: 10.0 L de Gelo coletados
   → Relatório enviado ao Centro de Comando
3. Listar sondas: SND-001 | Bateria: 10% | Carga: 20%
4. Recarregar SND-001 na base → Bateria 100% | Carga descarregada
```

---

## Regras de Negócio

- **Movimento:** consumo = distância (Manhattan) × 5.0 × multiplicador do terreno
- **Mineração:** consumo fixo de 15.0 energia por operação, extrai 10.0 L por vez
- **Mapeamento:** consumo fixo de 5.0 energia por varredura de sensor (raio 25m)
- **Coordenadas:** limitadas ao intervalo [0, 100] em X e Y
- **Sonda Mineradora** não pode acessar `CRATERA` (lança `TerrenoInvalidoException`)
- **Sonda Exploradora** pode navegar em qualquer tipo de terreno

---

## Autores

Desenvolvido para a Global Solution FIAP 2026 — Turma 2ESPG
Disciplina: Domain Driven Design - Java
