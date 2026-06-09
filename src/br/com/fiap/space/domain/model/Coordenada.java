package br.com.fiap.space.domain.model;

public final class Coordenada {

    private static final int LIMITE_MAXIMO = 100;

    private final int eixoX;
    private final int eixoY;

    public Coordenada(int eixoX, int eixoY) {
        if (eixoX < 0 || eixoY < 0) {
            throw new IllegalArgumentException(
                String.format("Coordenadas nao podem ser negativas. Recebido: (%d, %d)", eixoX, eixoY));
        }
        if (eixoX > LIMITE_MAXIMO || eixoY > LIMITE_MAXIMO) {
            throw new IllegalArgumentException(
                String.format("Coordenadas fora dos limites da malha (max: %d). Recebido: (%d, %d)",
                    LIMITE_MAXIMO, eixoX, eixoY));
        }
        this.eixoX = eixoX;
        this.eixoY = eixoY;
    }

    public int getEixoX() {
        return eixoX;
    }

    public int getEixoY() {
        return eixoY;
    }

    public int distanciaAte(Coordenada outra) {
        return Math.abs(this.eixoX - outra.eixoX) + Math.abs(this.eixoY - outra.eixoY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coordenada)) return false;
        Coordenada that = (Coordenada) o;
        return eixoX == that.eixoX && eixoY == that.eixoY;
    }

    @Override
    public int hashCode() {
        return eixoX * 31 + eixoY;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d)", eixoX, eixoY);
    }
}
