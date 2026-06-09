package br.com.fiap.space.domain.service;

import br.com.fiap.space.domain.model.Sonda;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Singleton Pattern: apenas uma instancia do Centro de Comando pode existir na base.
public class CentroDeComando {

    private static CentroDeComando instancia;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final List<String> relatorios = new ArrayList<>();

    private CentroDeComando() {
    }

    public static CentroDeComando getInstance() {
        if (instancia == null) {
            instancia = new CentroDeComando();
        }
        return instancia;
    }

    public void registrarSonda(Sonda sonda) {
        System.out.printf("[Centro de Comando] Sonda %s (%s) registrada com sucesso.%n",
            sonda.getIdSonda(), sonda.getTipoSonda());
    }

    public void receberRelatorio(String relatorio) {
        String timestamp = LocalTime.now().format(FORMATTER);
        relatorios.add("[" + timestamp + "] " + relatorio);
    }

    public List<String> getRelatorios() {
        return Collections.unmodifiableList(relatorios);
    }
}
