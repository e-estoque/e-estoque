# Sistema de Controle de Estoque com Atendimento ao Cliente

Sistema Java completo rodando exclusivamente no **terminal (linha de comando)**, com:
- ✅ Dois perfis: **Lojista** (admin) e **Cliente** (público)
- ✅ CRUD completo via **PreparedStatement** + MySQL
- ✅ Integração REST com **API ViaCEP** (busca de endereço por CEP) - Diferencial ★★
- ✅ Relatórios de estoque, gastos mensais e previsão futura
- ✅ Dashboard com resumo do mês
- ✅ Todos os requisitos de OOP, herança, interface, exceções, laços e decisão

---

## Estrutura do Projeto

```
estoque-system/
├── pom.xml                          # Maven - dependências MySQL + Gson
├── db.properties                    # Configuração do banco (EDITE ANTES DE USAR)
├── schema.sql                       # Script SQL completo (execute no MySQL)
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
    ├── dao/                         # Acesso a dados (PreparedStatement)
    │   ├── ConexaoDB.java           # Gerenciador de conexão
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
| MySQL | 8.0+ |

---

## Configuração do Banco de Dados

### 1. Criar o banco e as tabelas

Conecte ao MySQL e execute o script SQL:

```bash
mysql -u root -p < schema.sql
```

Ou cole o conteúdo de `schema.sql` diretamente no MySQL Workbench / DBeaver.

O script:
- Cria o banco `estoque_db`
- Cria as tabelas `usuarios`, `fornecedores`, `produtos`, `movimentacoes_estoque`
- Insere dados de exemplo (produtos, fornecedor e movimentações para os relatórios)
- Cria a view `vw_estoque_atual`
- Usuário padrão: **login:** `admin` | **senha:** `admin123`

### 2. Editar db.properties

Abra o arquivo `db.properties` na raiz do projeto e ajuste:

```properties
db.url=jdbc:mysql://localhost:3306/estoque_db?useSSL=false&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8
db.usuario=root
db.senha=SUA_SENHA_AQUI          ← altere esta linha
db.driver=com.mysql.cj.jdbc.Driver

loja.whatsapp=(11) 91234-5678    ← número exibido ao cliente
loja.nome=Minha Loja
```

---

## Compilação e Execução

### Opção A - Maven (recomendado)

```bash
# Na raiz do projeto (onde está o pom.xml):
mvn clean package

# Executar o JAR gerado:
java -jar target/estoque-system.jar
```

O `maven-assembly-plugin` gera um JAR "fat" com todas as dependências incluídas.

### Opção B - Compilação manual (sem Maven)

1. Baixe os JARs manualmente:
   - `mysql-connector-j-8.3.0.jar`
   - `gson-2.10.1.jar`

2. Compile:
```bash
javac -cp ".:mysql-connector-j-8.3.0.jar:gson-2.10.1.jar" \
      -d out \
      src/main/java/com/estoque/**/*.java \
      src/main/java/com/estoque/*.java
```

3. Execute:
```bash
java -cp ".:out:mysql-connector-j-8.3.0.jar:gson-2.10.1.jar" \
     com.estoque.Main
```

> **Windows:** substitua `:` por `;` nos classpath.

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

### 1. Verificar conexão ao iniciar

Ao executar, o sistema testa a conexão e exibe:
```
✔  Conexão com o banco de dados: OK
```

### 2. Área do Lojista

```
[1] Lojista → login: admin | senha: admin123
```

#### 2.1 Dashboard
```
[5] Dashboard Resumo
```
Exibe: total de produtos, valor em estoque, vendas e gastos do mês, lucro líquido.

#### 2.2 Cadastrar Fornecedor com ViaCEP
```
[2] Gerenciar Fornecedores → [2] Cadastrar
Nome: Distribuidora Teste
Contato: (21) 9999-0000
CEP: 20040-020
→ Sistema busca ViaCEP automaticamente
→ Confirmar dados: S
Número: 1500
Complemento: Sala 301
```

#### 2.3 Cadastrar Produto
```
[1] Gerenciar Produtos → [3] Cadastrar
Nome: Produto Teste
Categoria: Eletrônicos
Preço compra: 100,00
Preço venda: 199,90
Quantidade: 20
Estoque mínimo: 5
```

#### 2.4 Registrar Venda (Saída)
```
[3] Movimentações → [2] Registrar SAÍDA
ID do produto: (ID do produto criado)
Quantidade: 3
Valor unitário: 199.90
Observação: Venda balcão
```

#### 2.5 Relatórios
```
[4] Relatórios
[1] Estoque Atual       → lista com margens de lucro e alertas
[2] Gastos Mensais      → gastos e vendas por mês, lucro bruto
[3] Previsão de Gastos  → estimativa para o próximo mês
```

### 3. Área do Cliente

```
[2] Cliente (sem login)
[1] Ver produtos disponíveis  → lista com preço e estoque
[3] Ver detalhes             → descrição, disponibilidade
[0] Voltar → exibe WhatsApp do vendedor
```

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
| Banco relacional | MySQL com `PreparedStatement` em todos os DAOs |
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
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.3.0</version>
</dependency>
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.10.1</version>
</dependency>
```

---

## Segurança e Boas Práticas

- Senhas armazenadas em texto simples no banco (para fins didáticos - use BCrypt em produção)
- `PreparedStatement` em todas as queries (prevenção de SQL Injection)
- Transação com rollback na movimentação de estoque (`MovimentacaoDAO.registrar`)
- Soft delete em produtos (`ativo = 0`) preserva histórico de movimentações
- `try-with-resources` / `ConexaoDB.fechar()` para fechamento seguro de conexões
- Timeout de 8 segundos para chamadas ViaCEP

---

## Problemas Comuns

**"Arquivo db.properties não encontrado"**
→ Certifique-se de executar o JAR no diretório que contém `db.properties`, ou coloque-o em `src/main/resources/`.

**"Access denied for user 'root'"**
→ Verifique a senha em `db.properties`.

**"Unknown database estoque_db"**
→ Execute o `schema.sql` antes de iniciar.

**ViaCEP retorna erro de rede**
→ O sistema faz fallback automático para entrada manual. Verifique se tem acesso à internet.
