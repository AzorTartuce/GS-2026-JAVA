package br.com.fiap.space.domain.service;

import br.com.fiap.space.domain.model.Sonda;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// Singleton Pattern: apenas uma instancia do Centro de Comando pode existir na base.
public class CentroDeComando {

    private static volatile CentroDeComando instancia;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final Map<String, Sonda> sondas = new LinkedHashMap<>();
    private final List<String> relatorios = new ArrayList<>();

    private CentroDeComando() {
    }

    public static CentroDeComando getInstance() {
        if (instancia == null) {
            synchronized (CentroDeComando.class) {
                if (instancia == null) {
                    instancia = new CentroDeComando();
                }
            }
        }
        return instancia;
    }

    public void registrarSonda(Sonda sonda) {
        sondas.put(sonda.getIdSonda(), sonda);
        System.out.printf("[Centro de Comando] Sonda %s (%s) registrada com sucesso.%n",
            sonda.getIdSonda(), sonda.getTipoSonda());
    }

    public void receberRelatorio(String relatorio) {
        String timestamp = LocalTime.now().format(FORMATTER);
        relatorios.add("[" + timestamp + "] " + relatorio);
    }

    public List<Sonda> listarSondas() {
        return new ArrayList<>(sondas.values());
    }

    public Optional<Sonda> buscarSondaPorId(String id) {
        return Optional.ofNullable(sondas.get(id));
    }

    public List<String> getRelatorios() {
        return Collections.unmodifiableList(relatorios);
    }
}
