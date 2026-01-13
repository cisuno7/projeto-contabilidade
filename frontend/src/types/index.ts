export interface Planilha {
  id: string;
  nomeArquivo: string;
  tipoArquivo: string;
  status: StatusPlanilha;
  clienteId: string;
  dataUpload: string;
  dataProcessamento?: string;
}

export enum StatusPlanilha {
  UPLOADED = 'UPLOADED',
  PROCESSANDO = 'PROCESSANDO',
  PROCESSADA = 'PROCESSADA',
  ERRO = 'ERRO',
  CONCLUIDA = 'CONCLUIDA',
}

export interface DashboardEstatisticas {
  totalPlanilhas: number;
  planilhasProcessadas: number;
  planilhasEmProcessamento: number;
  taxaSucesso: number;
}

export interface Cliente {
  id: string;
  nome: string;
  cnpj: string;
  email: string;
  telefone: string;
}
