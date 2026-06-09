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
import br.com.fiap.space.domain.repository.SondaRepository;
import br.com.fiap.space.domain.service.CentroDeComando;
import br.com.fiap.space.infrastructure.repository.SondaRepositoryImpl;

import java.util.List;

public class MissaoService {

    private final CentroDeComando centroDeComando;
    private final SondaRepository sondaRepository;

    public MissaoService() {
        this.centroDeComando = CentroDeComando.getInstance();
        this.sondaRepository = new SondaRepositoryImpl();
    }

    public Sonda lancarSonda(String tipo, Recurso recurso) {
        Sonda sonda;

        if (recurso != null) {
            sonda = SondaFactory.criarSonda(tipo, recurso);
        } else {
            sonda = SondaFactory.criarSonda(tipo);
        }

        sondaRepository.salvar(sonda);
        centroDeComando.registrarSonda(sonda);
        return sonda;
    }

    public List<Sonda> listarSondas() {
        return sondaRepository.listarTodas();
    }

    public void executarMissao(String idSonda, int x, int y, Terreno terreno)
            throws BateriaCriticaException, TerrenoInvalidoException, CargaExcedidaException {

        Sonda sonda = sondaRepository.buscarPorId(idSonda);

        if (sonda == null) {
            throw new IllegalArgumentException("Sonda com ID '" + idSonda + "' nao encontrada.");
        }

        Coordenada destino = new Coordenada(x, y);
        sonda.executarRotinaAutonoma(destino, terreno);
    }

    public void recarregarSonda(String idSonda) {
        Sonda sonda = sondaRepository.buscarPorId(idSonda);

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
