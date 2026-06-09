package br.com.fiap.space.infrastructure.repository;

import br.com.fiap.space.domain.model.Sonda;
import br.com.fiap.space.domain.repository.SondaRepository;

import java.util.*;

// Simulacao de banco de dados em memoria utilizando Map.
public class SondaRepositoryImpl implements SondaRepository {

    private final Map<String, Sonda> armazenamento = new LinkedHashMap<>();

    @Override
    public void salvar(Sonda sonda) {
        armazenamento.put(sonda.getIdSonda(), sonda);
    }

    @Override
    public Sonda buscarPorId(String id) {
        return armazenamento.get(id);
    }

    @Override
    public List<Sonda> listarTodas() {
        return new ArrayList<>(armazenamento.values());
    }

    @Override
    public boolean existePorId(String id) {
        return armazenamento.containsKey(id);
    }
}
