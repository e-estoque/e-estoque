# Sistema de Controle de Estoque com Atendimento ao Cliente

Sistema Java completo rodando exclusivamente no **terminal (linha de comando)**, com:
- ✅ Dois perfis: **Lojista** (admin) e **Cliente** (público)
- ✅ CRUD completo com gerenciamento de estoque e movimentações
- ✅ Integração REST com **API ViaCEP** (busca de endereço por CEP) - Diferencial ★★
- ✅ Relatórios de estoque, gastos mensais e previsão futura
- ✅ Dashboard com resumo do mês
- ✅ Todos os requisitos de OOP, herança, interface, exceções, laços e decisão

---

## Estrutura do Projeto

```
estoque-system/
├── pom.xml                          # Maven - dependências
├── README.md
└── src/main/java/com/estoque/
    ├── Main.java                    # Ponto de entrada
    ├── model/                       # Entidades do domínio
    │   ├── Pessoa.java              # Classe abstrata (herança)
    │   ├── Fornecedor.java          # extends Pessoa
    │   ├── Cliente.java             # extends Pessoa
    │   ├── Produto.java
    │   ├── MovimentacaoEstoque.java
    │   ├── Endereco.java            # Composição com Pessoa
    │   └── Usuario.java
    ├── dao/                         # Regras de acesso a dados e negócio
    │   ├── ConexaoDB.java           # Gerenciador de configuração / conexão
    │   ├── ProdutoDAO.java          # CRUD Produto
    │   ├── FornecedorDAO.java       # CRUD Fornecedor
    │   ├── MovimentacaoDAO.java     # CRUD Movimentação (com transação)
    │   └── UsuarioDAO.java          # Autenticação
    ├── api/
    │   └── ViaCepClient.java        # ★★ Integração REST ViaCEP
    ├── relatorio/
    │   ├── Relatorio.java           # Interface (contrato)
    │   ├── RelatorioEstoque.java    # implements Relatorio
    │   ├── RelatorioGastosMensais.java
    │   └── RelatorioPrevisao.java
    ├── view/                        # Interface com usuário (terminal)
    │   ├── MenuPrincipal.java
    │   ├── MenuLojista.java         # Painel administrativo completo
    │   └── MenuCliente.java         # Consulta pública de produtos
    └── util/
        └── ConsoleUtil.java         # Utilitários de I/O e formatação
```

---

## Pré-requisitos

| Requisito | Versão mínima |
|-----------|--------------|
| Java (JDK) | 17 |
| Maven | 3.8+ |

---

## Instalação e execução

### Opção A - Maven (recomendado)

```bash
# Na raiz do projeto (onde está o pom.xml):
mvn clean package

# Executar o JAR gerado:
java -jar target/estoque-system.jar
```

O `maven-assembly-plugin` gera um JAR "fat" com todas as dependências incluídas.

### Opção B - Compilação manual (sem Maven)

1. Baixe o JAR necessário:
   - `gson-2.10.1.jar`

2. Compile:
```bash
javac -cp ".:gson-2.10.1.jar" \
      -d out \
      src/main/java/com/estoque/**/*.java \
      src/main/java/com/estoque/*.java
```

3. Execute:
```bash
java -cp ".:out:gson-2.10.1.jar" \
     com.estoque.Main
```

> **Windows:** substitua `:` por `;` no classpath.

---

## Integração com a API ViaCEP ★★

### Como funciona

Ao cadastrar ou editar um **fornecedor**, o sistema solicita o CEP e faz uma chamada HTTP GET para:

```
https://viacep.com.br/ws/{CEP}/json/
```

Exemplo de resposta da API para o CEP `01310-100`:
```json
{
  "cep": "01310-100",
  "logradouro": "Avenida Paulista",
  "bairro": "Bela Vista",
  "localidade": "São Paulo",
  "uf": "SP"
}
```

O sistema exibe o endereço encontrado e pede confirmação. O usuário pode:
- **Confirmar** → campos preenchidos automaticamente (só digita número e complemento)
- **Recusar** → preenche manualmente (edita os campos)

### Tratamento de erros

| Situação | Comportamento |
|----------|--------------|
| CEP inválido (8 dígitos) | `IllegalArgumentException` → entrada manual |
| CEP não cadastrado (`{"erro":true}`) | Aviso → entrada manual |
| Timeout ou sem rede | `IOException` → fallback manual |
| Status HTTP ≠ 200 | `IOException` → fallback manual |

### CEPs para testar

```
01310-100 → Av. Paulista, São Paulo/SP
20040-020 → Av. Rio Branco, Rio de Janeiro/RJ
30130-110 → Av. Afonso Pena, Belo Horizonte/MG
01001000  → (sem hífen) Praça da Sé, São Paulo/SP
99999999  → CEP inválido (testa o fallback)
```

---

## Passo a Passo para Testar

1. Execute o JAR gerado com `java -jar target/estoque-system.jar`.
2. No menu principal, escolha a opção de **Lojista** e use as credenciais padrão:
   - login: `admin`
   - senha: `admin123`
3. Explore o menu administrativo:
   - dashboard com resumo do estoque e valores
   - cadastro e edição de fornecedores
   - cadastro e edição de produtos
   - registro de movimentações de entrada e saída
   - relatórios de estoque, gastos mensais e previsão
4. Volte ao menu principal e acesse o perfil **Cliente** para consultar produtos disponíveis e obter o contato da loja.

---

## Observações de uso

- Caso a API ViaCEP esteja indisponível, o sistema permite a entrada manual dos dados de endereço.
- A aplicação foi desenvolvida para rodar exclusivamente no terminal, sem interface gráfica.

---

## Requisitos do Projeto - Mapeamento

| Requisito | Implementação |
|-----------|---------------|
| `if/else if/else` | `MenuLojista.exibirDashboard()`, `MenuCliente.verDetalhes()` |
| `switch/case` | `MenuPrincipal`, `MenuLojista`, `MenuCliente` (todos os menus) |
| `break` | `MenuLojista.listarTodasMovimentacoes()` (limite de 50 registros) |
| `continue` | `MenuCliente.listarProdutosDisponiveis()` (pula sem estoque), `menuRelatorios()` |
| `return` | `MenuLojista.iniciar()` (acesso negado), todos os DAOs |
| `try/catch/finally` | `MenuLojista.buscarEnderecoViaCep()`, todos os DAOs |
| `for` | Todos os relatórios, listagens, menus de relatório |
| `while` | Todos os menus (laço principal), `ConexaoDB`, `ConsoleUtil.lerString` |
| `do-while` | `MenuLojista.autenticar()` (até 3 tentativas), `ConsoleUtil.lerString` |
| Classes e objetos | Todos os modelos e serviços |
| Encapsulamento | Todos os modelos (`private` + getters/setters) |
| Herança | `Fornecedor` e `Cliente` extends `Pessoa` (abstrata) |
| Composição | `Pessoa` tem `Endereco` |
| Interface | `Relatorio` com `gerar()` e `getTitulo()` |
| Classes concretas | `RelatorioEstoque`, `RelatorioGastosMensais`, `RelatorioPrevisao` |
| CRUD Produto | `ProdutoDAO` (inserir, listar, buscar, atualizar, excluir) |
| CRUD Fornecedor | `FornecedorDAO` (inserir, listar, buscar, atualizar, excluir) |
| CRUD Movimentação | `MovimentacaoDAO` (com transação atômica) |
| Relatórios | Estoque, gastos mensais, previsão futura |
| Dashboard | `MenuLojista.exibirDashboard()` |
| API REST ViaCEP ★★ | `ViaCepClient.buscarEndereco()` via `HttpURLConnection` |
| Interface terminal | Apenas `Scanner` + `System.out` - sem Swing/JavaFX |

---

## Dependências (pom.xml)

```xml
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>
```

---

## Segurança e Boas Práticas

- Transação com rollback na movimentação de estoque (`MovimentacaoDAO.registrar`)
- Soft delete em produtos (`ativo = 0`) preserva histórico de movimentações
- `try-with-resources` / `ConexaoDB.fechar()` para fechamento seguro de conexões
- Timeout de 8 segundos para chamadas ViaCEP

---

## Problemas Comuns

- ViaCEP indisponível: use entrada manual de endereço.
