import axios from 'axios';
import type { Planilha } from '../types';

const api = axios.create({
  baseURL: '/api',
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
  ): Promise<Planilha> => {
    const formData = new FormData();
    formData.append('arquivo', arquivo);
    formData.append('clienteId', clienteId);
    if (nomeArquivo) {
      formData.append('nomeArquivo', nomeArquivo);
    }
    formData.append('corrigirComIA', String(corrigirComIA));

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

export default api;