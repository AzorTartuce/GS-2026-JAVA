package br.com.fiap.space.domain.factory;

import br.com.fiap.space.domain.model.*;

import java.util.concurrent.atomic.AtomicInteger;

// Factory Method Pattern: decide qual subclasse de Sonda instanciar.
public class SondaFactory {

    private static final AtomicInteger contador = new AtomicInteger(0);

    private static final double BATERIA_MAXIMA = 100.0;
    private static final double VOLUME_CARGA_MAXIMO = 50.0;
    private static final double ALCANCE_SENSOR_PADRAO = 25.0;

    private SondaFactory() {
    }

    public static Sonda criarSonda(String tipo) {
        return criarSonda(tipo, Recurso.GELO);
    }

    public static Sonda criarSonda(String tipo, Recurso recurso) {
        int numero = contador.incrementAndGet();
        String id = String.format("SND-%03d", numero);
        NivelEnergia bateria = new NivelEnergia(BATERIA_MAXIMA, BATERIA_MAXIMA);
        Coordenada base = new Coordenada(0, 0);

        return switch (tipo.toUpperCase().trim()) {
            case "MINERACAO" -> {
                CompartimentoCarga carga = new CompartimentoCarga(VOLUME_CARGA_MAXIMO);
                yield new SondaMineradora(id, bateria, base, carga, recurso);
            }
            case "EXPLORACAO" -> new SondaExploradora(id, bateria, base, ALCANCE_SENSOR_PADRAO);
            default -> throw new IllegalArgumentException(
                "Tipo de sonda invalido: '" + tipo + "'. Use 'MINERACAO' ou 'EXPLORACAO'.");
        };
    }
}
