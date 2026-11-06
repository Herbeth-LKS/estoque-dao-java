# Sistema de Estoque com Padrão DAO

Projeto acadêmico que implementa um estoque de produtos com operações CRUD
em Java, separando a lógica de negócio do acesso aos dados por meio do padrão
**DAO (Data Access Object)**.

## Integrantes

- Herbeth Lucas de Sousa Pereira

## Objetivo

Permitir cadastrar, listar, atualizar e deletar produtos. A camada de negócio
(`ProdutoService`) não conhece a origem dos dados: ela depende apenas da
interface `ProdutoDAO`. Assim, a fonte de dados pode ser trocada sem alterar
as regras de negócio.

## Arquitetura

```text
Principal
   └── ProdutoService              (regras de negócio e validações)
          └── ProdutoDAO           (interface / contrato)
                 ├── ProdutoDAOMemoria   (dados em memória)
                 └── ProdutoDAOArquivo   (dados em dados/produtos.tsv)
```

- `Produto`: modelo imutável (`record`) com id, nome, preço e quantidade.
- `ProdutoDAO`: contrato com as operações de persistência.
- `ProdutoDAOMemoria`: armazena os produtos em um `HashMap`.
- `ProdutoDAOArquivo`: lê e grava os produtos em um arquivo de texto.
- `ProdutoService`: validações e regras de negócio; recebe um `ProdutoDAO` no construtor.
- `Principal`: menu de console que permite usar qualquer uma das fontes e trocá-la em tempo de execução.

## Funcionalidades

- Cadastrar produto
- Listar produtos
- Atualizar produto
- Deletar produto
- Trocar a fonte de dados (memória ou arquivo) sem alterar a lógica de negócio

## Como executar

Pré-requisitos: JDK 17 ou superior e Maven.

```bash
mvn clean package
java -jar target/estoque-dao-1.0.0.jar
```

Também é possível compilar e executar sem gerar o JAR:

```bash
mvn compile
java -cp target/classes br.com.estoque.Principal
```

## Demonstração da troca de fonte

Ao iniciar, escolha `1` (memória) ou `2` (arquivo). A classe `Principal` cria
o serviço apenas mudando o DAO passado ao construtor:

```java
ProdutoService servico = new ProdutoService(new ProdutoDAOMemoria());
// ou
ProdutoService servico = new ProdutoService(new ProdutoDAOArquivo());
```

O restante do código do serviço e do menu é exatamente o mesmo. A opção `5`
do menu troca a fonte em tempo de execução. Para adicionar, por exemplo, um
banco de dados, basta criar `ProdutoDAOJdbc implements ProdutoDAO` e passá-lo
ao `ProdutoService`; nenhuma regra de negócio precisa ser alterada.

## Persistência em arquivo

A fonte `ProdutoDAOArquivo` grava em `dados/produtos.tsv`, criado
automaticamente na primeira gravação. Cada linha possui quatro campos
separados por tabulação: `id`, `nome`, `preço` e `quantidade`. A pasta
`dados/` é ignorada pelo Git.

Os dados em memória são perdidos ao trocar de fonte ou encerrar o programa.
Já os dados gravados no arquivo permanecem entre as execuções.

## Tecnologias

- Java 17
- Maven
- Programação orientada a objetos e padrão DAO
