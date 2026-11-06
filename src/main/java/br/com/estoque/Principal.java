package br.com.estoque;

import br.com.estoque.dao.ProdutoDAO;
import br.com.estoque.dao.ProdutoDAOArquivo;
import br.com.estoque.dao.ProdutoDAOMemoria;
import br.com.estoque.modelo.Produto;
import br.com.estoque.servico.ProdutoService;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/**
 * Demonstração da aplicação. A mesma camada de negócio (ProdutoService)
 * é usada com as duas fontes de dados. A troca é feita apenas trocando
 * a implementação de ProdutoDAO passada ao construtor do serviço.
 */
public class Principal {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== ESTOQUE COM PADRÃO DAO ===");

        ProdutoService servico = new ProdutoService(escolherFonteDeDados());
        int opcao;

        do {
            exibirMenu();
            opcao = lerInteiro("Opção: ");

            try {
                switch (opcao) {
                    case 1 -> cadastrar(servico);
                    case 2 -> listar(servico);
                    case 3 -> atualizar(servico);
                    case 4 -> deletar(servico);
                    case 5 -> {
                        servico = new ProdutoService(escolherFonteDeDados());
                        System.out.println("Fonte de dados alterada com sucesso.");
                    }
                    case 0 -> System.out.println("Encerrando...");
                    default -> System.out.println("Opção inválida.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Erro ao acessar os dados: " + e.getMessage());
            }
        } while (opcao != 0);
    }

    private static ProdutoDAO escolherFonteDeDados() {
        System.out.println("\nEscolha a fonte de dados:");
        System.out.println("1 - Memória");
        System.out.println("2 - Arquivo (dados/produtos.tsv)");

        while (true) {
            int opcao = lerInteiro("Fonte: ");
            if (opcao == 1) {
                return new ProdutoDAOMemoria();
            }
            if (opcao == 2) {
                return new ProdutoDAOArquivo(Path.of("dados", "produtos.tsv"));
            }
            System.out.println("Opção inválida.");
        }
    }

    private static void exibirMenu() {
        System.out.println("\n--- MENU ---");
        System.out.println("1 - Cadastrar produto");
        System.out.println("2 - Listar produtos");
        System.out.println("3 - Atualizar produto");
        System.out.println("4 - Deletar produto");
        System.out.println("5 - Trocar fonte de dados");
        System.out.println("0 - Sair");
    }

    private static void cadastrar(ProdutoService servico) {
        String nome = lerTexto("Nome: ");
        double preco = lerDouble("Preço: ");
        int quantidade = lerInteiro("Quantidade: ");

        Produto produto = servico.criar(nome, preco, quantidade);
        System.out.println("Produto cadastrado com sucesso: " + produto);
    }

    private static void listar(ProdutoService servico) {
        List<Produto> produtos = servico.listar();
        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }
        System.out.println("\nProdutos cadastrados:");
        produtos.forEach(System.out::println);
    }

    private static void atualizar(ProdutoService servico) {
        long id = lerLong("ID do produto: ");
        if (servico.buscarPorId(id).isEmpty()) {
            System.out.println("Produto não encontrado.");
            return;
        }

        String nome = lerTexto("Novo nome: ");
        double preco = lerDouble("Novo preço: ");
        int quantidade = lerInteiro("Nova quantidade: ");

        if (servico.atualizar(id, nome, preco, quantidade)) {
            System.out.println("Produto atualizado com sucesso.");
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    private static void deletar(ProdutoService servico) {
        long id = lerLong("ID do produto: ");
        if (servico.deletar(id)) {
            System.out.println("Produto deletado com sucesso.");
        } else {
            System.out.println("Produto não encontrado.");
        }
    }

    private static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return SCANNER.nextLine().trim();
    }

    private static int lerInteiro(String mensagem) {
        while (true) {
            try {
                return Integer.parseInt(lerTexto(mensagem));
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private static long lerLong(String mensagem) {
        while (true) {
            try {
                return Long.parseLong(lerTexto(mensagem));
            } catch (NumberFormatException e) {
                System.out.println("Digite um número válido.");
            }
        }
    }

    private static double lerDouble(String mensagem) {
        while (true) {
            try {
                return Double.parseDouble(lerTexto(mensagem).replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Digite um valor válido.");
            }
        }
    }
}
