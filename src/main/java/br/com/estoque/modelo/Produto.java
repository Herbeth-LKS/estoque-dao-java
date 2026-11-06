package br.com.estoque.modelo;

import java.util.Locale;

public record Produto(long id, String nome, double preco, int quantidade) {

    @Override
    public String toString() {
        return String.format(Locale.forLanguageTag("pt-BR"),
                "ID: %d | %s | Preço: R$ %.2f | Quantidade: %d",
                id, nome, preco, quantidade);
    }
}
