package br.com.estoque.servico;

import br.com.estoque.dao.ProdutoDAO;
import br.com.estoque.modelo.Produto;

import java.util.List;
import java.util.Optional;

/**
 * Camada de negócio. Depende apenas do contrato ProdutoDAO, portanto
 * funciona com qualquer fonte de dados que implemente esse contrato.
 */
public class ProdutoService {

    private final ProdutoDAO produtoDAO;

    public ProdutoService(ProdutoDAO produtoDAO) {
        this.produtoDAO = produtoDAO;
    }

    public Produto criar(String nome, double preco, int quantidade) {
        validar(nome, preco, quantidade);
        return produtoDAO.salvar(new Produto(0, nome.trim(), preco, quantidade));
    }

    public List<Produto> listar() {
        return produtoDAO.listar();
    }

    public Optional<Produto> buscarPorId(long id) {
        return produtoDAO.buscarPorId(id);
    }

    public boolean atualizar(long id, String nome, double preco, int quantidade) {
        validar(nome, preco, quantidade);
        if (produtoDAO.buscarPorId(id).isEmpty()) {
            return false;
        }
        produtoDAO.atualizar(new Produto(id, nome.trim(), preco, quantidade));
        return true;
    }

    public boolean deletar(long id) {
        return produtoDAO.deletar(id);
    }

    private void validar(String nome, double preco, int quantidade) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do produto é obrigatório.");
        }
        if (!Double.isFinite(preco) || preco < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo.");
        }
        if (quantidade < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativa.");
        }
    }
}
