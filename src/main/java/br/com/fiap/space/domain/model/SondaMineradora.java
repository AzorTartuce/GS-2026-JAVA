package br.com.fiap.space.domain.model;

import br.com.fiap.space.domain.exception.BateriaCriticaException;
import br.com.fiap.space.domain.exception.CargaExcedidaException;
import br.com.fiap.space.domain.interfaces.Recarregavel;

public class SondaMineradora extends Sonda implements Recarregavel {

    private static final double CONSUMO_MINERACAO = 15.0;
    private static final double VOLUME_EXTRACAO_PADRAO = 10.0;

    private CompartimentoCarga carga;
    private final Recurso recursoAlvo;

    public SondaMineradora(String idSonda, NivelEnergia bateria, Coordenada posicaoInicial,
                           CompartimentoCarga carga, Recurso recursoAlvo) {
        super(idSonda, bateria, posicaoInicial);
        this.carga = carga;
        this.recursoAlvo = recursoAlvo;
    }

    @Override
    protected void realizarAcaoLocal() throws BateriaCriticaException, CargaExcedidaException {
        System.out.printf("[%s] Iniciando extracao de %s...%n", idSonda, recursoAlvo);

        if (carga.isCheia()) {
            throw new CargaExcedidaException(
                String.format("Compartimento de carga lotado! Capacidade maxima atingida: %s", carga));
        }

        double volumeAExtrair = Math.min(VOLUME_EXTRACAO_PADRAO, carga.getVolumeDisponivel());

        this.bateria = this.bateria.consumir(CONSUMO_MINERACAO);
        this.carga = this.carga.adicionar(volumeAExtrair);

        System.out.printf("[%s] Extracao concluida! %.1f L de %s coletados. Carga atual: %s%n",
            idSonda, volumeAExtrair, recursoAlvo, carga);
    }

    @Override
    protected boolean isTerrenoPermitido(Terreno terreno) {
        return !terreno.isRestritoPorRodas();
    }

    @Override
    public String getTipoSonda() {
        return "Sonda Mineradora";
    }

    @Override
    public void conectarBase() {
        System.out.printf("[%s] Conectando a base para recarga e descarga...%n", idSonda);
        this.bateria = this.bateria.recarregar();
        this.carga = this.carga.esvaziar();
        System.out.printf("[%s] Recarga completa! Recursos descarregados. Bateria: %s%n", idSonda, bateria);
    }

    @Override
    protected String gerarRelatorio() {
        return super.gerarRelatorio() +
            String.format(" | Carga: %s | Recurso: %s", carga, recursoAlvo);
    }

    public CompartimentoCarga getCarga() {
        return carga;
    }

    public Recurso getRecursoAlvo() {
        return recursoAlvo;
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Carga: %s | Recurso: %s", carga, recursoAlvo);
    }
}
