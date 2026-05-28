package br.com.fiap.space.domain.model;

public enum Terreno {

    PLANICIE("Planicie", 1.0, false),
    SOLO_ROCHOSO("Solo Rochoso", 1.5, false),
    CRATERA("Cratera", 2.0, true);

    private final String tipoSolo;
    private final double multiplicadorConsumo;
    private final boolean restritoPorRodas;

    Terreno(String tipoSolo, double multiplicadorConsumo, boolean restritoPorRodas) {
        this.tipoSolo = tipoSolo;
        this.multiplicadorConsumo = multiplicadorConsumo;
        this.restritoPorRodas = restritoPorRodas;
    }

    public String getTipoSolo() {
        return tipoSolo;
    }

    public double getMultiplicadorConsumo() {
        return multiplicadorConsumo;
    }

    public boolean isRestritoPorRodas() {
        return restritoPorRodas;
    }

    @Override
    public String toString() {
        return tipoSolo;
    }
}
