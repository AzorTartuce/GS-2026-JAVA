---
name: estrutura-safs
description: Mapa de pacotes e papéis DDD no projeto SAFS (entities, VOs, repositories, services, factories) observado em 2026-06-08
metadata:
  type: project
---

Estrutura de pacotes em `src/br/com/fiap/space/`:
- `domain/model/`: Entidades (Sonda, SondaMineradora, SondaExploradora) e Value Objects
  (Coordenada, NivelEnergia, CompartimentoCarga — imutáveis, com equals/hashCode), além de
  enums de domínio (Recurso, Terreno).
- `domain/factory/`: SondaFactory — Factory Method estático que decide subclasse de Sonda.
- `domain/repository/`: interface SondaRepository (porta do domínio).
- `infrastructure/repository/`: SondaRepositoryImpl — adaptador em memória (Map/LinkedHashMap),
  implementa a interface do domínio. Boa separação de porta/adaptador.
- `domain/service/`: CentroDeComando — Domain Service implementado como Singleton (registra
  sondas, coleciona relatórios).
- `domain/exception/`: exceções customizadas (BateriaCriticaException, CargaExcedidaException,
  TerrenoInvalidoException) — uso correto de exceções de domínio específicas.
- `domain/interfaces/`: Recarregavel — interface comportamental usada com instanceof+cast em
  MissaoService.recarregarSonda.
- `application/`: MissaoService — orquestra casos de uso, válido como camada de aplicação
  separada do domínio.
- `presentation/`: Main.java — CLI com Scanner, menu numérico, switch tradicional.

Observações de qualidade geral (2026-06-08): separação DDD está coerente e bem aplicada
para o nível esperado — Entities, VOs, Repository (interface no domínio + impl na
infraestrutura), Domain Service e Application Service claramente distintos. Sem sinais de
técnicas avançadas demais (sem reflection, sem Spring, sem streams complexos). Ver
[[regressao_branch_z]] para um padrão atípico encontrado na branch "z".
