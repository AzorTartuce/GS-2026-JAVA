package br.com.fiap.space.domain.model;

import br.com.fiap.space.domain.exception.BateriaCriticaException;

public final class NivelEnergia {

    private final double capacidadeAtual;
    private final double capacidadeMaxima;

    public NivelEnergia(double capacidadeAtual, double capacidadeMaxima) {
        if (capacidadeMaxima <= 0) {
            throw new IllegalArgumentException("Capacidade maxima deve ser maior que zero.");
        }
        if (capacidadeAtual < 0) {
            throw new IllegalArgumentException("Capacidade atual nao pode ser negativa.");
        }
        if (capacidadeAtual > capacidadeMaxima) {
            throw new IllegalArgumentException("Capacidade atual nao pode exceder a maxima.");
        }
        this.capacidadeAtual = capacidadeAtual;
        this.capacidadeMaxima = capacidadeMaxima;
    }

    public NivelEnergia consumir(double quantidade) throws BateriaCriticaException {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade de consumo nao pode ser negativa.");
        }
        if (quantidade > this.capacidadeAtual) {
            throw new BateriaCriticaException(
                String.format("Bateria critica! Tentativa de consumir %.1f unidades, mas apenas %.1f disponiveis.",
                    quantidade, this.capacidadeAtual));
        }
        return new NivelEnergia(this.capacidadeAtual - quantidade, this.capacidadeMaxima);
    }

    public NivelEnergia recarregar() {
        return new NivelEnergia(this.capacidadeMaxima, this.capacidadeMaxima);
    }

    public boolean isInsuficiente(double quantidade) {
        return quantidade > this.capacidadeAtual;
    }

    public double getCapacidadeAtual() {
        return capacidadeAtual;
    }

    public double getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public double getPercentual() {
        return capacidadeMaxima > 0 ? (capacidadeAtual / capacidadeMaxima) * 100.0 : 0;
    }

    @Override
    public String toString() {
        return String.format("%.1f/%.1f (%.0f%%)", capacidadeAtual, capacidadeMaxima, getPercentual());
    }
}
