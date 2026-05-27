package br.com.fiap.space.application;

import br.com.fiap.space.domain.exception.BateriaCriticaException;
import br.com.fiap.space.domain.exception.CargaExcedidaException;
import br.com.fiap.space.domain.exception.TerrenoInvalidoException;
import br.com.fiap.space.domain.factory.SondaFactory;
import br.com.fiap.space.domain.interfaces.Recarregavel;
import br.com.fiap.space.domain.model.*;
import br.com.fiap.space.domain.service.CentroDeComando;

import java.util.List;
import java.util.Optional;

public class MissaoService {

    private final CentroDeComando centroDeComando;

    public MissaoService() {
        this.centroDeComando = CentroDeComando.getInstance();
    }

    public Sonda lancarSonda(String tipo, Recurso recurso) {
        Sonda sonda = (recurso != null)
            ? SondaFactory.criarSonda(tipo, recurso)
            : SondaFactory.criarSonda(tipo);
        centroDeComando.registrarSonda(sonda);
        return sonda;
    }

    public List<Sonda> listarSondas() {
        return centroDeComando.listarSondas();
    }

    public void executarMissao(String idSonda, int x, int y, Terreno terreno)
            throws BateriaCriticaException, TerrenoInvalidoException, CargaExcedidaException {
        Optional<Sonda> sondaOpt = centroDeComando.buscarSondaPorId(idSonda);
        if (sondaOpt.isEmpty()) {
            throw new IllegalArgumentException("Sonda com ID '" + idSonda + "' nao encontrada.");
        }
        Coordenada destino = new Coordenada(x, y);
        sondaOpt.get().executarRotinaAutonoma(destino, terreno);
    }

    public void recarregarSonda(String idSonda) {
        Optional<Sonda> sondaOpt = centroDeComando.buscarSondaPorId(idSonda);
        if (sondaOpt.isEmpty()) {
            throw new IllegalArgumentException("Sonda com ID '" + idSonda + "' nao encontrada.");
        }
        Sonda sonda = sondaOpt.get();
        if (sonda instanceof Recarregavel recarregavel) {
            recarregavel.conectarBase();
        } else {
            System.out.println("[Sistema] Esta sonda nao implementa recarga por base.");
        }
    }

    public List<String> obterRelatorios() {
        return centroDeComando.getRelatorios();
    }
}
