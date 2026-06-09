package br.com.fiap.space.domain.factory;

import br.com.fiap.space.domain.model.*;

// Factory Method Pattern: decide qual subclasse de Sonda instanciar.
public class SondaFactory {

    private static int contador = 0;

    private static final double BATERIA_MAXIMA = 100.0;
    private static final double VOLUME_CARGA_MAXIMO = 50.0;
    private static final double ALCANCE_SENSOR_PADRAO = 25.0;

    private SondaFactory() {
    }

    public static Sonda criarSonda(String tipo) {
        return criarSonda(tipo, Recurso.GELO);
    }

    public static Sonda criarSonda(String tipo, Recurso recurso) {
        contador = contador + 1;
        String id = String.format("SND-%03d", contador);
        NivelEnergia bateria = new NivelEnergia(BATERIA_MAXIMA, BATERIA_MAXIMA);
        Coordenada base = new Coordenada(0, 0);

        String tipoFormatado = tipo.toUpperCase().trim();

        if (tipoFormatado.equals("MINERACAO")) {
            CompartimentoCarga carga = new CompartimentoCarga(VOLUME_CARGA_MAXIMO);
            return new SondaMineradora(id, bateria, base, carga, recurso);
        } else if (tipoFormatado.equals("EXPLORACAO")) {
            return new SondaExploradora(id, bateria, base, ALCANCE_SENSOR_PADRAO);
        } else {
            throw new IllegalArgumentException(
                "Tipo de sonda invalido: '" + tipo + "'. Use 'MINERACAO' ou 'EXPLORACAO'.");
        }
    }
}
