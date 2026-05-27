package br.com.fiap.space.domain.model;

import br.com.fiap.space.domain.exception.CargaExcedidaException;

public final class CompartimentoCarga {

    private final double volumeOcupado;
    private final double volumeMaximo;

    public CompartimentoCarga(double volumeMaximo) {
        this(0.0, volumeMaximo);
    }

    public CompartimentoCarga(double volumeOcupado, double volumeMaximo) {
        if (volumeMaximo <= 0) {
            throw new IllegalArgumentException("Volume maximo deve ser maior que zero.");
        }
        if (volumeOcupado < 0) {
            throw new IllegalArgumentException("Volume ocupado nao pode ser negativo.");
        }
        if (volumeOcupado > volumeMaximo) {
            throw new IllegalArgumentException("Volume ocupado nao pode exceder o maximo.");
        }
        this.volumeOcupado = volumeOcupado;
        this.volumeMaximo = volumeMaximo;
    }

    public CompartimentoCarga adicionar(double volume) throws CargaExcedidaException {
        if (volume <= 0) {
            throw new IllegalArgumentException("Volume a adicionar deve ser positivo.");
        }
        if (volumeOcupado + volume > volumeMaximo) {
            throw new CargaExcedidaException(
                String.format("Carga excedida! Tentativa de adicionar %.1f L, mas apenas %.1f L disponiveis.",
                    volume, getVolumeDisponivel()));
        }
        return new CompartimentoCarga(this.volumeOcupado + volume, this.volumeMaximo);
    }

    public CompartimentoCarga esvaziar() {
        return new CompartimentoCarga(0.0, this.volumeMaximo);
    }

    public boolean isCheia() {
        return volumeOcupado >= volumeMaximo;
    }

    public double getVolumeDisponivel() {
        return volumeMaximo - volumeOcupado;
    }

    public double getVolumeOcupado() {
        return volumeOcupado;
    }

    public double getVolumeMaximo() {
        return volumeMaximo;
    }

    public double getPercentual() {
        return volumeMaximo > 0 ? (volumeOcupado / volumeMaximo) * 100.0 : 0;
    }

    @Override
    public String toString() {
        return String.format("%.1f/%.1f L (%.0f%%)", volumeOcupado, volumeMaximo, getPercentual());
    }
}
