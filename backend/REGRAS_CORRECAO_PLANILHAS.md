### Estrutura da planilha de produtos (entrada/saída da IA)

Esta é a estrutura base usada na fase 1 (São Paulo + Simples Nacional), tomada da aba `Planilha` do arquivo de exemplo
`Planilha-Cadastro_Produto-Padaria-Brasil-MEJ Padaria modificado.xlsx` localizado em `cursor/context`.

#### Colunas principais

- **CODIGO**: identificador sequencial interno do produto (1, 2, 3, ...).
- **NOME**: nome comercial do produto (ex.: "Leite UHT", "Nescau 200g").
- **PREÇO DE VENDA**: valor de venda (decimal, pode vir vazio em alguns exemplos).
- **UNIDADE DE MEDIDA**: unidade (ex.: `UN`).
- **CODIGONCM**: código NCM no formato `0000.00.00` (ex.: `0401.10.10`, `1806.90.00`).
- **CST**: código CST genérico (não usado na fase 1).
- **CSOSN**: código de situação tributária do Simples Nacional (ex.: `102`, `500`).
- **ALIQECF**: alíquota para ECF (não usada nesta fase).
- **CSTPISENTRADA / CSTPISSAIDA**: códigos CST de PIS (entrada/saída).
- **CSTCOFINSENTRADA / CSTCOFINS SAIDA**: códigos CST de COFINS (entrada/saída).
- **PISCREDITO / COFINSCREDITO / PISDEBITO / COFINSDEBITO**: campos de crédito/débito (não usados nesta fase).
- **CFOPEI / CFOPEE / CFOPSI / CFOPSE**: CFOP de entrada/saída interna/interestadual.
- **NATRECEITAPISCOFINS**: natureza da receita para PIS/COFINS (ex.: `99`).
- **GRUPO**: grupo descritivo do produto (ex.: "Bebida láctea natural", "Achocolatado em pó").
- **CEST**: código CEST no formato `XX.XXX.00` (ex.: `17.016.00`, `17.006.00`).

#### Colunas-alvo da IA na fase 1

Na primeira versão, a IA irá **validar e corrigir automaticamente**:

- **CODIGONCM**
  - Deve ser um NCM válido existente na tabela oficial (`NCM.xlsx`).
  - Exemplo de valores na planilha de exemplo:
    - `0401.10.10` (Leite UHT)
    - `1806.90.00` (Achocolatado em pó)
    - `2106.90.30` (Chá industrializado)
    - `1101.00.10` (Farinha de trigo)
    - `0713.33.19` (Feijão)
- **CEST**
  - Deve ser um código CEST válido segundo a tabela oficial de São Paulo (`CEST SP.pdf`).
  - Exemplo de valores na planilha de exemplo:
    - `17.016.00` (leite, coco ralado, frutas secas)
    - `17.006.00` (achocolatados em pó)
    - `17.020.00` (leite condensado)
    - `17.098.00` (chá erva-mate)
    - `17.097.00` (chá industrializado)
    - `17.005.00` (farinha de trigo, fubá, amido de milho)
    - `17.003.00` (feijões, lentilha)

#### Relação entre colunas (visão de negócio)

- **CODIGONCM** ↔ **descrição do produto** (`NOME` + `GRUPO`)
  - A IA pode inferir um NCM provável usando a descrição do produto e o grupo.
  - Ex.: produtos com `GRUPO = "Achocolatado em pó"` usam o NCM `1806.90.00`.
- **CEST** ↔ **NCM** ↔ **descrição de mercadoria (CEST SP)**
  - A tabela `CEST SP.pdf` define, por CEST, quais NCMs se enquadram em cada código.
  - A IA deve usar:
    - O NCM (quando estiver correto ou corrigido).
    - A descrição do produto (`NOME`, `GRUPO`).
    - A descrição oficial da mercadoria no anexo CEST.

#### Exemplos de linhas (resumo)

- Leite UHT  
  - `NOME`: Leite UHT  
  - `CODIGONCM`: `0401.10.10`  
  - `CSOSN`: `102`  
  - `CEST`: `17.016.00`

- Nescau 200g  
  - `NOME`: Nescau 200g  
  - `CODIGONCM`: `1806.90.00`  
  - `CSOSN`: `500`  
  - `CEST`: `17.006.00`

- Farinha de trigo  
  - `NOME`: Farinha de trigo  
  - `CODIGONCM`: `1101.00.10`  
  - `CSOSN`: `102`  
  - `CEST`: `17.005.00`

### Fontaine de referência externas

- **NCM.xlsx** (`cursor/context/NCM.xlsx`)
  - Planilha oficial com colunas:
    - `Código`, `Descrição`, `Data Início`, `Data Fim`, `Ato Legal Início`, `Número`, `Ano`.
  - Usada para:
    - Validar se um NCM informado na planilha de produtos existe e está vigente.
    - Apoiar sugestões de NCM quando o campo estiver vazio ou inválido.

- **CEST SP.pdf** (`cursor/context/CEST SP.pdf`)
  - Portaria CAT 68/2019 + anexos.
  - Estrutura típica das tabelas:
    - `ITEM`, `CEST`, `NCM/SH`, `DESCRIÇÃO`.
  - Usada para:
    - Verificar se um CEST informado é válido.
    - Sugerir um CEST provável com base no NCM e na descrição da mercadoria.

### Comportamento desejado da IA (visão de alto nível)

- Recebe, para cada linha da planilha de produtos:
  - `NOME`, `GRUPO`, `CODIGONCM` atual (pode estar vazio ou errado), `CEST` atual (pode estar vazio ou errado).
- Com base nas tabelas oficiais e em exemplos corretos:
  - **Valida** se o `CODIGONCM` existe na NCM.xlsx.
  - **Valida** se o `CEST` é um código válido de SP.
  - Quando encontrar problemas:
    - **Sugere novos valores** para `CODIGONCM` e `CEST`.
    - Informa, em metadados, o valor anterior, o novo valor e o motivo/justificativa (campo que será guardado em `ai_metadata`).

