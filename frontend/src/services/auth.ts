import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface LoginResponse {
  token: string;
  usuario: {
    id: string;
    email: string;
    nome: string;
  };
}

export const authService = {
  login: async (email: string, senha: string): Promise<LoginResponse> => {
    try {
      const response = await api.post<LoginResponse>('/auth/login', {
        email,
        senha,
      });

      // Armazenar token no localStorage
      if (response.data.token) {
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('usuario', JSON.stringify(response.data.usuario));
        
        // Configurar token para próximas requisições
        api.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`;
      }

      return response.data;
    } catch (error) {
      // Se o endpoint não existir ainda, simular autenticação para desenvolvimento
      if (axios.isAxiosError(error) && error.response?.status === 404) {
        console.warn('Endpoint de autenticação não encontrado. Usando autenticação mockada para desenvolvimento.');
        // Mock para desenvolvimento - remover quando backend estiver implementado
        const mockResponse: LoginResponse = {
          token: 'mock-token-' + Date.now(),
          usuario: {
            id: '1',
            email,
            nome: 'Usuário de Teste',
          },
        };
        localStorage.setItem('token', mockResponse.token);
        localStorage.setItem('usuario', JSON.stringify(mockResponse.usuario));
        return mockResponse;
      }
      throw error;
    }
  },

  logout: (): void => {
    localStorage.removeItem('token');
    localStorage.removeItem('usuario');
    delete api.defaults.headers.common['Authorization'];
  },

  isAuthenticated: (): boolean => {
    return !!localStorage.getItem('token');
  },

  getToken: (): string | null => {
    return localStorage.getItem('token');
  },

  getUsuario: () => {
    const usuarioStr = localStorage.getItem('usuario');
    if (usuarioStr) {
      try {
        return JSON.parse(usuarioStr);
      } catch {
        return null;
      }
    }
    return null;
  },
};

// Configurar interceptor para adicionar token automaticamente
api.interceptors.request.use(
  (config) => {
    const token = authService.getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para tratar erros de autenticação
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      authService.logout();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
