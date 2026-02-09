#!/usr/bin/env node
/**
 * Script para extrair conteúdo dos documentos da pasta context
 * Execute: npm install xlsx pdf-parse --no-save && node ler_documentos.js
 * Ou: npx xlsx ... (se preferir)
 */

const fs = require('fs');
const path = require('path');

const BASE = __dirname;

function lerXlsx(caminho, maxLinhas = 35) {
  try {
    const XLSX = require('xlsx');
    const workbook = XLSX.readFile(caminho);
    const resultado = [];
    for (const sheetName of workbook.SheetNames) {
      const sheet = workbook.Sheets[sheetName];
      const dados = XLSX.utils.sheet_to_json(sheet, { header: 1, defval: '' });
      resultado.push({ nome: sheetName, linhas: dados.slice(0, maxLinhas) });
    }
    return resultado;
  } catch (e) {
    return [{ nome: 'ERRO', linhas: [[e.message]] }];
  }
}

function lerDocx(caminho) {
  try {
    // docx precisa de outra lib - vamos pular se não tiver
    const mammoth = require('mammoth');
    return mammoth.extractRawText({ path: caminho })
      .then(r => r.value.split('\n').filter(l => l.trim()))
      .catch(() => ['(docx: instale mammoth com npm install mammoth)']);
  } catch (e) {
    return Promise.resolve([`(docx: ${e.message})`]);
  }
}

async function lerPdf(caminho, maxChars = 12000) {
  try {
    const { PDFParse } = require('pdf-parse');
    const dataBuffer = fs.readFileSync(caminho);
    const parser = new PDFParse({ data: dataBuffer });
    const result = await parser.getText();
    const text = result.text || result || '';
    return text.substring(0, maxChars);
  } catch (e) {
    return `(PDF: ${e.message})`;
  }
}

async function main() {
  console.log('='.repeat(60));
  console.log('LEITURA DOS DOCUMENTOS - cursor/context');
  console.log('='.repeat(60));

  // 1. NCM.xlsx
  console.log('\n## 1. NCM.xlsx (Planilha Padrão Nacional)');
  console.log('-'.repeat(40));
  const ncmPath = path.join(BASE, 'NCM.xlsx');
  if (fs.existsSync(ncmPath)) {
    const ncm = lerXlsx(ncmPath);
    ncm.forEach(({ nome, linhas }) => {
      console.log(`\nAba: ${nome}`);
      linhas.forEach((row, i) => console.log(`  ${i + 1}:`, JSON.stringify(row)));
    });
  } else {
    console.log('Arquivo não encontrado');
  }

  // 2. Planilha Cadastro Padaria
  console.log('\n\n## 2. Planilha-Cadastro_Produto-Padaria-Brasil-MEJ Padaria modificado.xlsx');
  console.log('-'.repeat(40));
  const padariaPath = path.join(BASE, 'Planilha-Cadastro_Produto-Padaria-Brasil-MEJ Padaria modificado.xlsx');
  if (fs.existsSync(padariaPath)) {
    const padaria = lerXlsx(padariaPath);
    padaria.forEach(({ nome, linhas }) => {
      console.log(`\nAba: ${nome}`);
      linhas.forEach((row, i) => console.log(`  ${i + 1}:`, JSON.stringify(row)));
    });
  } else {
    console.log('Arquivo não encontrado');
  }

  // 3. CSOSN.docx
  console.log('\n\n## 3. CSOSN.docx');
  console.log('-'.repeat(40));
  const csosnPath = path.join(BASE, 'CSOSN.docx');
  if (fs.existsSync(csosnPath)) {
    try {
      const mammoth = require('mammoth');
      const result = await mammoth.extractRawText({ path: csosnPath });
      const texto = result.value || '';
      console.log(texto || '(vazio)');
    } catch (e) {
      console.log('Erro mammoth:', e.message);
    }
  } else {
    console.log('Arquivo não encontrado');
  }

  // 4. CEST SP.pdf
  console.log('\n\n## 4. CEST SP.pdf');
  console.log('-'.repeat(40));
  const cestPath = path.join(BASE, 'CEST SP.pdf');
  if (fs.existsSync(cestPath)) {
    const texto = await lerPdf(cestPath);
    console.log(texto);
  } else {
    console.log('Arquivo não encontrado');
  }

  // 5. Codigos CST PIS COFINS
  console.log('\n\n## 5. Codigos_CST_PIS_COFINS_260205_105256.pdf');
  console.log('-'.repeat(40));
  const cstPath = path.join(BASE, 'Codigos_CST_PIS_COFINS_260205_105256.pdf');
  if (fs.existsSync(cstPath)) {
    const texto = await lerPdf(cstPath);
    console.log(texto);
  } else {
    console.log('Arquivo não encontrado');
  }

  console.log('\n' + '='.repeat(60));
  console.log('FIM DA LEITURA');
  console.log('='.repeat(60));
}

// Instalar xlsx e pdf-parse se necessário
main().catch(console.error);
