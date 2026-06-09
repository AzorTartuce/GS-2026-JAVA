---
name: regressao-branch-z
description: Na branch "z" o aluno reverteu deliberadamente vários recursos modernos de Java para versões mais "manuais"/antigas — padrão raro e oposto ao de "complexidade excessiva"
metadata:
  type: project
---

Em 2026-06-08, revisão do diff da branch "z" (vs HEAD) mostrou um padrão atípico: em vez de
introduzir técnicas avançadas demais, o aluno **substituiu recursos modernos por formas mais
verbosas/antigas**:
- `AtomicInteger` → `int` simples com `contador = contador + 1` (SondaFactory)
- `switch` expression com `yield` → cadeia de `if/else` (SondaFactory)
- `instanceof Coordenada that` (pattern matching) → `instanceof` + cast manual (Coordenada.equals)
- `Objects.hash(eixoX, eixoY)` → cálculo manual `eixoX * 31 + eixoY` (Coordenada.hashCode)
- `Optional<Sonda>` no repository → retorno de `Sonda` (pode ser null) em SondaRepository/Impl
- `synchronized` + `volatile` (double-checked locking no Singleton) → versão simples sem
  sincronização em CentroDeComando.getInstance()

**Por quê isso importa:** é o oposto do que normalmente se verifica (aluno usando algo
avançado demais). Aqui ele está *baixando* o nível de sofisticação. Pode ser:
1. O professor da disciplina pediu explicitamente para evitar Optional/switch expressions/etc.
   neste estágio (n. 2 do curso) — alinhado ao "nível esperado" do brief.
2. O aluno está tentando deixar o código "mais parecido com o que ele mesmo escreveria" após
   ter usado uma versão gerada por IA anteriormente (bom sinal de internalização).
3. Pode ter sido engano/recuo acidental.

**Como aplicar:** ao revisar este projeto, não tratar a ausência de Optional/switch
expressions/pattern matching como deficiência grave — é provável que seja intencional e até
desejável para o nível da turma. Mas vale perguntar ao aluno o motivo da mudança (pergunta
socrática) para confirmar que ele entende as duas formas e fez uma escolha consciente, não
um acidente. Também atentar: a remoção do `Optional` faz `buscarPorId` poder retornar `null`
silenciosamente — isso é um retrocesso real em boas práticas (NullPointerException risk),
vale comentar tecnicamente independente do motivo da mudança.

Relacionado: [[estrutura_safs]]
