package br.com.fiap.space.application;

import br.com.fiap.space.domain.exception.BateriaCriticaException;
import br.com.fiap.space.domain.exception.CargaExcedidaException;
import br.com.fiap.space.domain.exception.TerrenoInvalidoException;
import br.com.fiap.space.domain.factory.SondaFactory;
import br.com.fiap.space.domain.interfaces.Recarregavel;
import br.com.fiap.space.domain.model.Coordenada;
import br.com.fiap.space.domain.model.Recurso;
import br.com.fiap.space.domain.model.Sonda;
import br.com.fiap.space.domain.model.Terreno;
import br.com.fiap.space.domain.service.CentroDeComando;

import java.util.List;

public class MissaoService {

    private CentroDeComando centroDeComando;

    public MissaoService() {
        this.centroDeComando = CentroDeComando.getInstance();
    }

    public Sonda lancarSonda(String tipo, Recurso recurso) {
        Sonda sonda;

        if (recurso != null) {
            sonda = SondaFactory.criarSonda(tipo, recurso);
        } else {
            sonda = SondaFactory.criarSonda(tipo);
        }

        centroDeComando.registrarSonda(sonda);
        return sonda;
    }

    public List<Sonda> listarSondas() {
        return centroDeComando.listarSondas();
    }

    public void executarMissao(String idSonda, int x, int y, Terreno terreno)
            throws BateriaCriticaException, TerrenoInvalidoException, CargaExcedidaException {

        Sonda sonda = centroDeComando.buscarSondaPorId(idSonda);

        if (sonda == null) {
            throw new IllegalArgumentException("Sonda com ID '" + idSonda + "' nao encontrada.");
        }

        Coordenada destino = new Coordenada(x, y);
        sonda.executarRotinaAutonoma(destino, terreno);
    }

    public void recarregarSonda(String idSonda) {
        Sonda sonda = centroDeComando.buscarSondaPorId(idSonda);

        if (sonda == null) {
            throw new IllegalArgumentException("Sonda com ID '" + idSonda + "' nao encontrada.");
        }

        if (sonda instanceof Recarregavel) {
            Recarregavel sondaRecarregavel = (Recarregavel) sonda;
            sondaRecarregavel.conectarBase();
        } else {
            System.out.println("[Sistema] Esta sonda nao implementa recarga por base.");
        }
    }

    public List<String> obterRelatorios() {
        return centroDeComando.getRelatorios();
    }
}
