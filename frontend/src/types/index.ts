export interface Planilha {
  id: string;
  nomeArquivo: string;
  tipoArquivo: string;
  status: StatusPlanilha;
  clienteId: string;
  clienteNome?: string;
  clienteCnpj?: string;
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
  name: string;
  documentNumber: string;
  estado?: string;
  regime?: string;
  active?: boolean;
}
