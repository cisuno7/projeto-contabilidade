# Resumo dos Documentos - Contexto IA Contabilidade

Este documento foi gerado automaticamente pelo script `ler_documentos.js` para apoiar o desenvolvimento da IA de correção de planilhas.

---

## 1. NCM.xlsx (Planilha Padrão Nacional)

**Abas:** Tabela NCM  
**Vigência:** 27/01/2026 | Resolução Gecex nº 812/2025

### Colunas
| Coluna | Descrição |
|--------|-----------|
| Código | Código NCM (ex: 0101.21.00, 0401.10.10) |
| Descrição | Descrição do produto |
| Data Início | Data Início vigência |
| Data Fim | Data Fim vigência |
| Ato Legal Início | Ex: Res Camex |
| Número | Número da resolução |
| Ano | Ano |

### Exemplos de códigos NCM
- `01` - Animais vivos
- `0101.21.00` - Reprodutores de raça pura (cavalos)
- `0401.10.10` - Leite UHT
- `1806.90.00` - Achocolatado em pó
- `2106.90.30` - Chá industrializado
- `1101.00.10` - Farinha de trigo

---

## 2. Planilha Cadastro Produto (Padaria Brasil - MEJ)

**Tipo:** Planilha de cadastro de produtos que a IA deve corrigir.

### Colunas (23 no total)
| Coluna | Exemplo | Uso IA |
|--------|---------|--------|
| CODIGO | 1, 2, 3... | ID do produto |
| NOME | Leite UHT, Nescau 200g | Nome do produto |
| PREÇO DE VENDA | 12.25, 18 | - |
| UNIDADE DE MEDIDA | UN | - |
| **CODIGONCM** | 0401.10.10, 1806.90.00 | **Corrigir/preencher** |
| CST | 0 | - |
| **CSOSN** | 102, 500 | **Corrigir/preencher** |
| ALIQECF | - | - |
| CSTPISENTRADA, CSTPISSAIDA, etc. | 99, 1 | - |
| CFOPEI, CFOPEE, CFOPSI, CFOPSE | 1102, 5102 | - |
| NATRECEITAPISCOFINS | 99 | - |
| GRUPO | Bebida láctea natural, Achocolatado | - |
| **CEST** | 17.016.00, 17.006.00 | **Corrigir/preencher** |

### Colunas que a IA deve corrigir (Simples Nacional + SP)
1. **CODIGONCM** - Código NCM (formato: 1234.56.78)
2. **CSOSN** - Código Simples Nacional (ex: 102, 500)
3. **CEST** - Código Específico ST (formato: 17.016.00)

### Valores CSOSN usados na planilha
- **102** - Tributada pelo Simples Nacional sem permissão de crédito
- **500** - Tributada pelo Simples Nacional com permissão de crédito

### Formato CEST
- Ex: `17.016.00`, `17.006.00`, `17.020.00`, `17.033.00`
- Alguns produtos: `–` (hífen) quando não se aplica

### Mapeamento NCM → CEST (exemplos da planilha)
| NCM | Produto | CEST | CSOSN |
|-----|---------|------|-------|
| 0401.10.10 | Leite UHT | 17.016.00 | 102 |
| 1806.90.00 | Nescau/Toddy | 17.006.00 | 500 |
| 0402.99.90 | Leite condensado | 17.020.00 | 500 |
| 0903.00.10 | Chá (erva-mate) | 17.098.00 | 102 |
| 2106.90.30 | Chá industrializado | 17.097.00 | 102 |
| 1101.00.10 | Farinha de trigo | 17.005.00 | 102 |
| 1701.99.00 | Açúcar refinado | 17.001.00 | 500 |
| 0713.33.19 | Feijão | 17.003.00 | 102 |

---

## 3. CEST SP.pdf (Portaria CAT 68/2019)

**Fonte:** Portaria CAT 68, de 13-12-2019 (DOE 17-12-2019)  
**Tema:** Mercadorias sujeitas à substituição tributária (ICMS) no Estado de São Paulo.

### Estrutura dos anexos
- **ANEXO I** - Fumo (CEST 04.001.00, 04.002.00)
- **ANEXO II** - Cimento (CEST 05.001.00)
- **ANEXO III** - Cerveja, refrigerante, água (CEST 03.001.00, 03.002.00...)
- ... (Anexos I a XXII)

### Formato das tabelas no PDF
| ITEM | CEST | NCM/SH | DESCRIÇÃO |
|------|------|--------|-----------|
| 1 | 04.001.00 | 2402 | Charutos, cigarrilhas e cigarros |
| 2 | 04.002.00 | 2403.1 | Tabaco para fumar |
| 1 | 05.001.00 | 2523 | Cimento |

**Observação:** O CEST no PDF usa formato `XX.XXX.00` (ex: 04.001.00), enquanto na planilha Padaria usa `17.016.00` (número completo).

---

## 4. CSOSN.docx

O conteúdo não foi extraído automaticamente (formato Word). Referência manual: tabela de códigos CSOSN do Simples Nacional.

### Códigos CSOSN comuns (Simples Nacional)
- **101** - Tributada com permissão de crédito
- **102** - Tributada sem permissão de crédito
- **500** - Tributada pelo Simples Nacional com permissão de crédito
- **900** - Outros

---

## 5. Codigos_CST_PIS_COFINS.pdf

Relacionado a CST (Lucro Real/Presumido). **Não será usado na fase atual** (apenas Simples Nacional).

---

## Regras de Negócio Resumidas

| Código | Planilha Referência | Escopo Fase 1 |
|--------|---------------------|---------------|
| **NCM** | NCM.xlsx (nacional) | Usar para validar/corrigir CODIGONCM |
| **CEST** | CEST SP.pdf | Usar para SP - validar/corrigir CEST |
| **CSOSN** | CSOSN.docx + planilha padrão | Simples Nacional - validar/corrigir CSOSN |
| CST | - | Não usar por enquanto |

---

## Como executar o script novamente

```bash
cd cursor/context
node ler_documentos.js
```

Ou para salvar em arquivo:
```bash
node ler_documentos.js > resumo_documentos.txt
```
