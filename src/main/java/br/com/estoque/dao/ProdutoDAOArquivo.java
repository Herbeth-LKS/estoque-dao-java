package br.com.estoque.dao;

import br.com.estoque.modelo.Produto;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementação que persiste os produtos em um arquivo de texto.
 * Cada linha contém: id, nome, preço e quantidade, separados por tabulação.
 */
public class ProdutoDAOArquivo implements ProdutoDAO {

    private final Path arquivo;
    private final Map<Long, Produto> produtos = new HashMap<>();
    private long proximoId = 1;

    public ProdutoDAOArquivo() {
        this(Path.of("dados", "produtos.tsv"));
    }

    public ProdutoDAOArquivo(Path arquivo) {
        this.arquivo = arquivo;
        carregar();
    }

    @Override
    public Produto salvar(Produto produto) {
        Produto novo = new Produto(proximoId++, produto.nome(), produto.preco(), produto.quantidade());
        produtos.put(novo.id(), novo);
        persistir();
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
        persistir();
    }

    @Override
    public boolean deletar(long id) {
        boolean removido = produtos.remove(id) != null;
        if (removido) {
            persistir();
        }
        return removido;
    }

    private void carregar() {
        if (!Files.exists(arquivo)) {
            return;
        }
        try {
            for (String linha : Files.readAllLines(arquivo, StandardCharsets.UTF_8)) {
                if (linha.isBlank()) {
                    continue;
                }
                String[] campos = linha.split("\t", -1);
                if (campos.length != 4) {
                    continue;
                }
                Produto produto = new Produto(
                        Long.parseLong(campos[0]),
                        desescapar(campos[1]),
                        Double.parseDouble(campos[2]),
                        Integer.parseInt(campos[3]));
                produtos.put(produto.id(), produto);
                proximoId = Math.max(proximoId, produto.id() + 1);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao ler o arquivo " + arquivo, e);
        }
    }

    private void persistir() {
        try {
            if (arquivo.getParent() != null) {
                Files.createDirectories(arquivo.getParent());
            }
            List<String> linhas = produtos.values().stream()
                    .sorted(Comparator.comparingLong(Produto::id))
                    .map(p -> p.id() + "\t" + escapar(p.nome()) + "\t" + p.preco() + "\t" + p.quantidade())
                    .toList();
            Files.write(arquivo, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao gravar o arquivo " + arquivo, e);
        }
    }

    private String escapar(String texto) {
        return texto.replace("\\", "\\\\")
                .replace("\t", "\\t")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private String desescapar(String texto) {
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < texto.length(); i++) {
            char caractere = texto.charAt(i);
            if (caractere == '\\' && i + 1 < texto.length()) {
                char proximo = texto.charAt(++i);
                resultado.append(switch (proximo) {
                    case 't' -> '\t';
                    case 'n' -> '\n';
                    case 'r' -> '\r';
                    default -> proximo;
                });
            } else {
                resultado.append(caractere);
            }
        }
        return resultado.toString();
    }
}
