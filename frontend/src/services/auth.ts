import axios from 'axios';

const API_BASE = import.meta.env.VITE_API_URL
  || (import.meta.env.PROD ? 'https://projeto-contabilidade.onrender.com/api' : '/api');

const api = axios.create({
  baseURL: API_BASE,
  headers: {
    'Content-Type': 'application/json',
  },
});

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface RegisterRequest {
  username: string; // Backend espera "username"
  email: string;
  password: string; // Backend espera "password"
  cnpj?: string;
  telefone?: string;
}

export interface LoginResponse {
  userId: string;
  username: string;
  email: string;
  role: string;
  token: string;
  tokenType: string;
  // Mantém compatibilidade com código existente
  usuario?: {
    id: string;
    email: string;
    nome: string;
  };
}

export const authService = {
  register: async (userData: RegisterRequest): Promise<void> => {
    try {
      await api.post('/auth/register', userData);
    } catch (error) {
      throw error;
    }
  },

  login: async (email: string, senha: string): Promise<LoginResponse> => {
    try {
      const response = await api.post<LoginResponse>('/auth/login', {
        usernameOrEmail: email, // Backend espera "usernameOrEmail"
        password: senha, // Backend espera "password"
      });

      // Armazenar token no localStorage
      if (response.data.token) {
        localStorage.setItem('token', response.data.token);
        // Compatibilidade com código existente
        const usuarioCompatibilidade = {
          id: response.data.userId,
          email: response.data.email,
          nome: response.data.username
        };
        localStorage.setItem('usuario', JSON.stringify(usuarioCompatibilidade));

        // Configurar token para próximas requisições
        api.defaults.headers.common['Authorization'] = `${response.data.tokenType || 'Bearer'} ${response.data.token}`;
      }

      return response.data;
    } catch (error) {
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
