package br.com.estoque.dao;

import br.com.estoque.modelo.Produto;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProdutoDAOMemoria implements ProdutoDAO {

    private final Map<Long, Produto> produtos = new HashMap<>();
    private long proximoId = 1;

    @Override
    public Produto salvar(Produto produto) {
        Produto novo = new Produto(proximoId++, produto.nome(), produto.preco(), produto.quantidade());
        produtos.put(novo.id(), novo);
        return novo;
    }

    @Override
    public List<Produto> listar() {
        return produtos.values().stream()
                .sorted(Comparator.comparingLong(Produto::id))
                .toList();
    }

    @Override
    public Optional<Produto> buscarPorId(long id) {
        return Optional.ofNullable(produtos.get(id));
    }

    @Override
    public void atualizar(Produto produto) {
        produtos.put(produto.id(), produto);
    }

    @Override
    public boolean deletar(long id) {
        return produtos.remove(id) != null;
    }
}
