package br.com.fiap.space.domain.repository;

import br.com.fiap.space.domain.model.Sonda;

import java.util.List;
import java.util.Optional;

public interface SondaRepository {

    void salvar(Sonda sonda);

    Optional<Sonda> buscarPorId(String id);

    List<Sonda> listarTodas();

    boolean existePorId(String id);
}
