package br.com.estoque.dao;

import br.com.estoque.modelo.Produto;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de acesso aos dados. A camada de negócio depende apenas desta
 * interface, e não de uma fonte de dados concreta.
 */
public interface ProdutoDAO {

    Produto salvar(Produto produto);

    List<Produto> listar();

    Optional<Produto> buscarPorId(long id);

    void atualizar(Produto produto);

    boolean deletar(long id);
}
