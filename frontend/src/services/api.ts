import axios from 'axios';
import type { Planilha, Cliente } from '../types';

/** API em produção: mesma origem quando o front é servido pelo Spring em /api; senão VITE_API_URL ou fallback. */
function resolveApiBase(): string {
  const explicit = import.meta.env.VITE_API_URL as string | undefined;
  if (explicit) return explicit;
  if (!import.meta.env.PROD) return '/api';
  const base = import.meta.env.BASE_URL;
  if (base && base !== '/') {
    return `${window.location.origin}/api`;
  }
  return 'https://projeto-contabilidade.onrender.com/api';
}

const API_BASE = resolveApiBase();

const api = axios.create({
  baseURL: API_BASE,
});

api.interceptors.request.use((config) => {
  // Só o GET /clientes (lista para o dropdown) é público. POST /clientes (cadastro) precisa do Bearer.
  const method = String(config.method ?? 'get').toLowerCase();
  const url = String(config.url ?? '');
  const isPublicClientesGet =
    method === 'get' &&
    (url === '/clientes' ||
      url.startsWith('/clientes?') ||
      url.startsWith('/clientes/') ||
      url === '/api/clientes' ||
      url.startsWith('/api/clientes?') ||
      url.startsWith('/api/clientes/'));

  if (!isPublicClientesGet) {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers = config.headers || {};
      config.headers.Authorization = `Bearer ${token}`;
    }
  }
  return config;
});

export const planilhaService = {
  upload: async (
    arquivo: File,
    clienteId: string,
    nomeArquivo?: string,
    corrigirComIA: boolean = true,
    ufConferencia?: string | null,
  ): Promise<Planilha> => {
    const formData = new FormData();
    formData.append('arquivo', arquivo);
    formData.append('clienteId', clienteId);
    if (nomeArquivo) {
      formData.append('nomeArquivo', nomeArquivo);
    }
    formData.append('corrigirComIA', String(corrigirComIA));
    if (corrigirComIA && ufConferencia && ufConferencia.trim()) {
      formData.append('ufConferencia', ufConferencia.trim().toUpperCase());
    }

    const response = await api.post<Planilha>('/planilhas/upload', formData);
    return response.data;
  },

  processar: async (
    planilhaId: string,
    usarIA: boolean = true,
    ufConferencia?: string | null,
  ): Promise<Planilha> => {
    const body: Record<string, unknown> = { planilhaId, usarIA };
    if (usarIA && ufConferencia && ufConferencia.trim()) {
      body.ufConferencia = ufConferencia.trim().toUpperCase();
    }
    const response = await api.post<Planilha>('/planilhas/processar', body);
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