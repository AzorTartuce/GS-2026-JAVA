package br.com.fiap.space.domain.model;

public enum Recurso {

    GELO("Gelo de Agua", 0.5),
    REGOLITO("Regolito Lunar", 1.2),
    TITANIO("Titanio", 4.5);

    private final String nome;
    private final double pesoPorUnidade;

    Recurso(String nome, double pesoPorUnidade) {
        if (pesoPorUnidade <= 0) {
            throw new IllegalArgumentException("Peso por unidade deve ser estritamente positivo.");
        }
        this.nome = nome;
        this.pesoPorUnidade = pesoPorUnidade;
    }

    public String getNome() {
        return nome;
    }

    public double getPesoPorUnidade() {
        return pesoPorUnidade;
    }

    @Override
    public String toString() {
        return nome;
    }
}
