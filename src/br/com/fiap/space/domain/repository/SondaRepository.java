package br.com.fiap.space.domain.repository;

import br.com.fiap.space.domain.model.Sonda;

import java.util.List;

public interface SondaRepository {

    void salvar(Sonda sonda);

    Sonda buscarPorId(String id);

    List<Sonda> listarTodas();

    boolean existePorId(String id);
}
