export interface Planilha {
  id: string;
  nomeArquivo: string;
  tipoArquivo: string;
  status: StatusPlanilha;
  clienteId: string;
  dataUpload: string;
  dataProcessamento?: string;
  podeBaixar?: boolean;
  aiMetadata?: string;
}

export const StatusPlanilha = {
  UPLOADED: 'UPLOADED',
  PROCESSANDO: 'PROCESSANDO',
  PROCESSADA: 'PROCESSADA',
  ERRO: 'ERRO',
  CONCLUIDA: 'CONCLUIDA',
} as const;

export type StatusPlanilha = typeof StatusPlanilha[keyof typeof StatusPlanilha];



export interface Cliente {
  id: string;
  nome: string;
  cnpj: string;
  email: string;
  telefone: string;
}
