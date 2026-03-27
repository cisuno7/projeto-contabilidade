import axios from 'axios';
import type { Planilha, Cliente } from '../types';

const API_BASE = import.meta.env.VITE_API_URL
  || (import.meta.env.PROD ? 'https://projeto-contabilidade.onrender.com/api' : '/api');

const api = axios.create({
  baseURL: API_BASE,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers = config.headers || {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const planilhaService = {
  upload: async (
    arquivo: File,
    clienteId: string,
    nomeArquivo?: string,
    corrigirComIA: boolean = true,
    arquivoReferencia?: File | null,
  ): Promise<Planilha> => {
    const formData = new FormData();
    formData.append('arquivo', arquivo);
    formData.append('clienteId', clienteId);
    if (nomeArquivo) {
      formData.append('nomeArquivo', nomeArquivo);
    }
    formData.append('corrigirComIA', String(corrigirComIA));
    if (arquivoReferencia) {
      formData.append('arquivoReferencia', arquivoReferencia);
    }

    const response = await api.post<Planilha>('/planilhas/upload', formData);
    return response.data;
  },

  processar: async (planilhaId: string, usarIA: boolean = true): Promise<Planilha> => {
    const response = await api.post<Planilha>('/planilhas/processar', {
      planilhaId,
      usarIA,
    });
    return response.data;
  },

  baixar: async (planilhaId: string): Promise<Blob> => {
    const response = await api.get(`/planilhas/${planilhaId}/download`, {
      responseType: 'blob',
    });
    return response.data;
  },

  listar: async (): Promise<Planilha[]> => {
    const response = await api.get<Planilha[]>('/planilhas');
    return response.data;
  },
};

export const clienteService = {
  listar: async (nome?: string): Promise<Cliente[]> => {
    const params = nome ? { nome } : {};
    const response = await api.get<Cliente[]>('/clientes', { params });
    return response.data;
  },

  criar: async (dados: { name: string; documentNumber: string; estado?: string; regime?: string }): Promise<Cliente> => {
    const response = await api.post<Cliente>('/clientes', dados);
    return response.data;
  },
};

export default api;