-- =============================================================
-- Script SQL - Sistema de Controle de Estoque
-- Banco de dados: MySQL 8+
-- Execute este script antes de iniciar o sistema
-- =============================================================

CREATE DATABASE IF NOT EXISTS estoque_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE estoque_db;

-- -------------------------------------------------------------
-- Tabela: usuarios
-- Armazena credenciais de acesso (perfil LOJISTA)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    login       VARCHAR(60)  NOT NULL UNIQUE,
    senha       VARCHAR(255) NOT NULL,
    perfil      ENUM('LOJISTA') NOT NULL DEFAULT 'LOJISTA',
    criado_em   DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Usuário padrão: admin / admin123
INSERT INTO usuarios (login, senha, perfil)
VALUES ('admin', 'admin123', 'LOJISTA')
ON DUPLICATE KEY UPDATE login = login;

-- -------------------------------------------------------------
-- Tabela: fornecedores
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS fornecedores (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    nome        VARCHAR(120) NOT NULL,
    contato     VARCHAR(80),
    cep         VARCHAR(9),
    logradouro  VARCHAR(200),
    numero      VARCHAR(20),
    complemento VARCHAR(80),
    bairro      VARCHAR(100),
    cidade      VARCHAR(100),
    uf          CHAR(2),
    criado_em   DATETIME DEFAULT CURRENT_TIMESTAMP,
    atualizado_em DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Fornecedor de exemplo
INSERT INTO fornecedores (nome, contato, cep, logradouro, numero, bairro, cidade, uf)
VALUES ('Distribuidora Exemplo', '(11) 3333-4444', '01310-100', 'Avenida Paulista', '1578', 'Bela Vista', 'São Paulo', 'SP')
ON DUPLICATE KEY UPDATE nome = nome;

-- -------------------------------------------------------------
-- Tabela: produtos
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS produtos (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    nome                VARCHAR(150) NOT NULL,
    descricao           TEXT,
    preco_compra        DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    preco_venda         DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    quantidade_estoque  INT NOT NULL DEFAULT 0,
    estoque_minimo      INT NOT NULL DEFAULT 10,
    categoria           VARCHAR(80),
    fornecedor_id       INT,
    ativo               TINYINT(1) NOT NULL DEFAULT 1,
    criado_em           DATETIME DEFAULT CURRENT_TIMESTAMP,
    atualizado_em       DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_produto_fornecedor FOREIGN KEY (fornecedor_id)
        REFERENCES fornecedores(id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Produtos de exemplo
INSERT INTO produtos (nome, descricao, preco_compra, preco_venda, quantidade_estoque, estoque_minimo, categoria, fornecedor_id)
VALUES
    ('Notebook Dell i5', 'Notebook Dell Core i5, 8GB RAM, 256GB SSD', 2500.00, 3499.90, 15, 5, 'Informática', 1),
    ('Mouse Sem Fio Logitech', 'Mouse wireless com pilha inclusa', 45.00, 89.90, 30, 10, 'Periféricos', 1),
    ('Teclado Mecânico Redragon', 'Teclado mecânico RGB, switch Blue', 120.00, 199.90, 20, 8, 'Periféricos', 1),
    ('Monitor LG 24"', 'Monitor Full HD IPS 75Hz', 700.00, 1099.90, 8, 5, 'Monitores', 1),
    ('Cabo HDMI 2m', 'Cabo HDMI 2.0 suporte 4K', 12.00, 29.90, 50, 15, 'Cabos', 1)
ON DUPLICATE KEY UPDATE nome = nome;

-- -------------------------------------------------------------
-- Tabela: movimentacoes_estoque
-- Registra entradas (compras) e saídas (vendas)
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS movimentacoes_estoque (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    produto_id      INT NOT NULL,
    tipo            ENUM('ENTRADA','SAIDA') NOT NULL,
    quantidade      INT NOT NULL,
    valor_unitario  DECIMAL(10,2) NOT NULL,
    observacao      VARCHAR(255),
    data_movimentacao DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_mov_produto FOREIGN KEY (produto_id)
        REFERENCES produtos(id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Movimentações de exemplo (mês atual e anteriores para relatórios)
INSERT INTO movimentacoes_estoque (produto_id, tipo, quantidade, valor_unitario, observacao, data_movimentacao)
VALUES
    (1, 'ENTRADA', 10, 2500.00, 'Compra inicial', DATE_SUB(NOW(), INTERVAL 3 MONTH)),
    (2, 'ENTRADA', 20, 45.00,   'Compra inicial', DATE_SUB(NOW(), INTERVAL 3 MONTH)),
    (1, 'SAIDA',    2, 3499.90, 'Venda balcão',   DATE_SUB(NOW(), INTERVAL 2 MONTH)),
    (2, 'SAIDA',    5, 89.90,   'Venda balcão',   DATE_SUB(NOW(), INTERVAL 2 MONTH)),
    (3, 'ENTRADA', 15, 120.00,  'Reposição',      DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (1, 'SAIDA',    1, 3499.90, 'Venda online',   DATE_SUB(NOW(), INTERVAL 1 MONTH)),
    (4, 'ENTRADA',  5, 700.00,  'Novo fornecedor',DATE_SUB(NOW(), INTERVAL 15 DAY)),
    (2, 'SAIDA',    3, 89.90,   'Venda balcão',   NOW()),
    (5, 'ENTRADA', 30, 12.00,   'Compra em lote', NOW());

-- -------------------------------------------------------------
-- View auxiliar: estoque_atual (facilita consultas de relatório)
-- -------------------------------------------------------------
CREATE OR REPLACE VIEW vw_estoque_atual AS
SELECT
    p.id,
    p.nome,
    p.categoria,
    p.quantidade_estoque,
    p.preco_compra,
    p.preco_venda,
    p.estoque_minimo,
    (p.quantidade_estoque * p.preco_compra) AS valor_custo_total,
    (p.quantidade_estoque * p.preco_venda)  AS valor_venda_total,
    f.nome AS fornecedor
FROM produtos p
LEFT JOIN fornecedores f ON p.fornecedor_id = f.id
WHERE p.ativo = 1;

-- =============================================================
-- FIM DO SCRIPT
-- =============================================================
