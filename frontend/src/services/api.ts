import axios from 'axios';
import type { Planilha, DashboardEstatisticas } from '../types';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export const planilhaService = {
  upload: async (arquivo: File, clienteId: string, nomeArquivo?: string): Promise<Planilha> => {
    const formData = new FormData();
    formData.append('arquivo', arquivo);
    formData.append('clienteId', clienteId);
    if (nomeArquivo) {
      formData.append('nomeArquivo', nomeArquivo);
    }

    const response = await api.post<Planilha>('/planilhas/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
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

export const dashboardService = {
  obterEstatisticas: async (): Promise<DashboardEstatisticas> => {
    const response = await api.get<DashboardEstatisticas>('/dashboard/estatisticas');
    return response.data;
  },
};

export default api;
