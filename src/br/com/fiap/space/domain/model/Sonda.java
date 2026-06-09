package br.com.fiap.space.domain.model;

import br.com.fiap.space.domain.exception.BateriaCriticaException;
import br.com.fiap.space.domain.exception.CargaExcedidaException;
import br.com.fiap.space.domain.exception.TerrenoInvalidoException;
import br.com.fiap.space.domain.service.CentroDeComando;

public abstract class Sonda {

    protected static final double CONSUMO_BASE_MOVIMENTO = 5.0;

    protected final String idSonda;
    protected NivelEnergia bateria;
    protected Coordenada posicaoAtual;
    protected Terreno terrenoAtual;

    protected Sonda(String idSonda, NivelEnergia bateria, Coordenada posicaoAtual) {
        if (idSonda == null || idSonda.isBlank()) {
            throw new IllegalArgumentException("ID da sonda nao pode ser nulo ou vazio.");
        }
        this.idSonda = idSonda;
        this.bateria = bateria;
        this.posicaoAtual = posicaoAtual;
        this.terrenoAtual = Terreno.PLANICIE;
    }

    // === TEMPLATE METHOD PATTERN ===
    public final void executarRotinaAutonoma(Coordenada destino, Terreno terreno)
            throws BateriaCriticaException, TerrenoInvalidoException, CargaExcedidaException {
        System.out.println("\n[" + idSonda + "] >>> INICIANDO ROTINA AUTONOMA <<<");

        // Passo 1: Validar status do sistema (Bateria e Diagnostico)
        validarStatusSistema(destino, terreno);

        // Passo 2: Deslocar ate a coordenada
        mover(destino, terreno);

        // Passo 3: Hook - realizar acao local (polimorfismo)
        realizarAcaoLocal();

        // Passo 4: Enviar relatorio ao Centro de Comando
        enviarRelatorio();

        System.out.println("[" + idSonda + "] >>> ROTINA CONCLUIDA COM SUCESSO <<<\n");
    }

    protected void validarStatusSistema(Coordenada destino, Terreno terreno)
            throws BateriaCriticaException, TerrenoInvalidoException {
        System.out.println("[" + idSonda + "] Diagnostico: Verificando status do sistema...");

        if (!isTerrenoPermitido(terreno)) {
            throw new TerrenoInvalidoException(
                String.format("Terreno '%s' e restrito para %s (veiculo com rodas nao pode acessar crateras).",
                    terreno.getTipoSolo(), getTipoSonda()));
        }

        int distancia = posicaoAtual.distanciaAte(destino);
        double consumoDeslocamento = distancia * CONSUMO_BASE_MOVIMENTO * terreno.getMultiplicadorConsumo();
        double consumoTotal = consumoDeslocamento + getConsumoAcaoLocal();

        if (bateria.isInsuficiente(consumoTotal)) {
            if (consumoTotal > bateria.getCapacidadeMaxima()) {
                throw new BateriaCriticaException(
                    String.format("Missao impossivel! Esta rota exige %.1f de energia, mas a capacidade maxima da bateria e %.1f. Escolha um destino mais proximo ou um terreno menos hostil.",
                        consumoTotal, bateria.getCapacidadeMaxima()));
            }
            throw new BateriaCriticaException(
                String.format("Energia insuficiente! Necessario: %.1f (deslocamento: %.1f + acao: %.1f) | Disponivel: %.1f. Recarregue a sonda antes de prosseguir.",
                    consumoTotal, consumoDeslocamento, getConsumoAcaoLocal(), bateria.getCapacidadeAtual()));
        }

        System.out.println("[" + idSonda + "] Diagnostico: OK. Todos os sistemas operacionais.");
    }

    protected void mover(Coordenada destino, Terreno terreno) throws BateriaCriticaException {
        int distancia = posicaoAtual.distanciaAte(destino);
        double consumo = distancia * CONSUMO_BASE_MOVIMENTO * terreno.getMultiplicadorConsumo();

        System.out.printf("[%s] Deslocando de %s para %s | Distancia: %d | Consumo: %.1f%n",
            idSonda, posicaoAtual, destino, distancia, consumo);

        this.bateria = this.bateria.consumir(consumo);
        this.posicaoAtual = destino;
        this.terrenoAtual = terreno;

        System.out.printf("[%s] Chegou em %s | Bateria: %s%n", idSonda, posicaoAtual, bateria);
    }

    // Hook - implementado polimorficamente por cada subclasse
    protected abstract void realizarAcaoLocal() throws BateriaCriticaException, CargaExcedidaException;

    protected abstract boolean isTerrenoPermitido(Terreno terreno);

    protected abstract double getConsumoAcaoLocal();

    public abstract String getTipoSonda();

    protected void enviarRelatorio() {
        String relatorio = gerarRelatorio();
        CentroDeComando.getInstance().receberRelatorio(relatorio);
        System.out.println("[" + idSonda + "] Relatorio enviado ao Centro de Comando.");
    }

    protected String gerarRelatorio() {
        return String.format("RELATORIO [%s | %s] | Pos: %s | Bateria: %s | Terreno: %s",
            idSonda, getTipoSonda(), posicaoAtual, bateria, terrenoAtual);
    }

    public String getIdSonda() {
        return idSonda;
    }

    public NivelEnergia getBateria() {
        return bateria;
    }

    public Coordenada getPosicaoAtual() {
        return posicaoAtual;
    }

    public Terreno getTerrenoAtual() {
        return terrenoAtual;
    }

    @Override
    public String toString() {
        return String.format("%-20s | ID: %-8s | Pos: %-10s | Bateria: %s",
            getTipoSonda(), idSonda, posicaoAtual, bateria);
    }
}
