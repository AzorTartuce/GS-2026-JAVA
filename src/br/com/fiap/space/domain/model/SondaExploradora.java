package br.com.fiap.space.domain.model;

import br.com.fiap.space.domain.exception.BateriaCriticaException;
import br.com.fiap.space.domain.exception.CargaExcedidaException;
import br.com.fiap.space.domain.interfaces.Recarregavel;

public class SondaExploradora extends Sonda implements Recarregavel {

    private static final double CONSUMO_MAPEAMENTO = 5.0;

    private final double alcanceSensor;
    private int areasExploradas;

    public SondaExploradora(String idSonda, NivelEnergia bateria, Coordenada posicaoInicial,
                            double alcanceSensor) {
        super(idSonda, bateria, posicaoInicial);
        if (alcanceSensor <= 0) {
            throw new IllegalArgumentException("Alcance do sensor deve ser estritamente maior que zero.");
        }
        this.alcanceSensor = alcanceSensor;
        this.areasExploradas = 0;
    }

    @Override
    protected void realizarAcaoLocal() throws BateriaCriticaException, CargaExcedidaException {
        System.out.printf("[%s] Ativando sensores de longo alcance (raio: %.1f m)...%n",
            idSonda, alcanceSensor);

        this.bateria = this.bateria.consumir(CONSUMO_MAPEAMENTO);
        this.areasExploradas++;

        double areaMapeada = Math.PI * alcanceSensor * alcanceSensor;
        System.out.printf("[%s] Mapeamento concluido! Area varrida: %.1f m2 | Total de areas exploradas: %d%n",
            idSonda, areaMapeada, areasExploradas);
    }

    @Override
    protected boolean isTerrenoPermitido(Terreno terreno) {
        return true; // exploradora pode navegar em qualquer terreno
    }

    @Override
    protected double getConsumoAcaoLocal() {
        return CONSUMO_MAPEAMENTO;
    }

    @Override
    public String getTipoSonda() {
        return "Sonda Exploradora";
    }

    @Override
    public void conectarBase() {
        System.out.printf("[%s] Conectando a base para recarga e download de dados...%n", idSonda);
        this.bateria = this.bateria.recarregar();
        System.out.printf("[%s] Recarga completa! Dados de %d area(s) enviados ao servidor. Bateria: %s%n",
            idSonda, areasExploradas, bateria);
    }

    @Override
    protected String gerarRelatorio() {
        return super.gerarRelatorio() +
            String.format(" | Sensor: %.1f m | Areas Exploradas: %d", alcanceSensor, areasExploradas);
    }

    public double getAlcanceSensor() {
        return alcanceSensor;
    }

    public int getAreasExploradas() {
        return areasExploradas;
    }

    @Override
    public String toString() {
        return super.toString() +
            String.format(" | Sensor: %.1fm | Areas: %d", alcanceSensor, areasExploradas);
    }
}
