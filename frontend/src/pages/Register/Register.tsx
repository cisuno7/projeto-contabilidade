import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import { authService } from '../../services/auth';
import './Register.css';

export default function Register() {
  const [formData, setFormData] = useState({
    nome: '',
    email: '',
    senha: '',
    confirmarSenha: '',
    cnpj: '',
    telefone: ''
  });
  const [loading, setLoading] = useState(false);
  const [erro, setErro] = useState<string | null>(null);
  const [mostrarSenha, setMostrarSenha] = useState(false);
  const [aceitarTermos, setAceitarTermos] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const validarFormulario = () => {
    if (!formData.nome.trim()) {
      setErro('Nome é obrigatório');
      return false;
    }

    if (!formData.email.trim()) {
      setErro('Email é obrigatório');
      return false;
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      setErro('Email inválido');
      return false;
    }

    if (formData.senha.length < 6) {
      setErro('A senha deve ter pelo menos 6 caracteres');
      return false;
    }

    if (formData.senha !== formData.confirmarSenha) {
      setErro('As senhas não coincidem');
      return false;
    }

    if (!aceitarTermos) {
      setErro('Você deve aceitar os termos de uso');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErro(null);

    if (!validarFormulario()) {
      return;
    }

    setLoading(true);

    try {
      await authService.register({
        username: formData.nome, // Backend espera "username"
        email: formData.email,
        password: formData.senha, // Backend espera "password"
        cnpj: formData.cnpj,
        telefone: formData.telefone
      });

      navigate('/login', {
        state: {
          message: 'Conta criada com sucesso! Faça o login para continuar.',
          success: true
        }
      });
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.data?.message) {
        setErro(error.response.data.message);
      } else {
        setErro('Erro ao criar conta. Tente novamente.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-container">
      <div className="register-background">
        <div className="register-shapes">
          <div className="shape shape-1"></div>
          <div className="shape shape-2"></div>
          <div className="shape shape-3"></div>
        </div>
      </div>

      <div className="register-card">
        <div className="register-header">
          <div className="register-logo">
            <svg width="48" height="48" viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect width="48" height="48" rx="12" fill="url(#register-gradient)"/>
              <path d="M24 14L32 22H28V30H20V22H16L24 14Z" fill="white" fillOpacity="0.95"/>
              <defs>
                <linearGradient id="register-gradient" x1="0" y1="0" x2="48" y2="48" gradientUnits="userSpaceOnUse">
                  <stop stopColor="#6366f1"/>
                  <stop offset="1" stopColor="#4f46e5"/>
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h1 className="register-title">Criar Conta</h1>
          <p className="register-subtitle">Sistema de Automação Contábil</p>
        </div>

        <form className="register-form" onSubmit={handleSubmit}>
          {erro && (
            <div className="register-error">
              <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="10" cy="10" r="9" stroke="currentColor" strokeWidth="2"/>
                <path d="M10 6V10" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                <circle cx="10" cy="13" r="1" fill="currentColor"/>
              </svg>
              <span>{erro}</span>
            </div>
          )}

          <div className="form-grid">
            <div className="input-group">
              <label htmlFor="nome">Nome Completo *</label>
              <div className="input-wrapper">
                <svg className="input-icon" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M10 10C12.2091 10 14 8.20914 14 6C14 3.79086 12.2091 2 10 2C7.79086 2 6 3.79086 6 6C6 8.20914 7.79086 10 10 10Z" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                  <path d="M2 18C2 14.6863 4.68629 12 8 12H12C15.3137 12 18 14.6863 18 18" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
                <input
                  id="nome"
                  name="nome"
                  type="text"
                  placeholder="Seu nome completo"
                  value={formData.nome}
                  onChange={handleChange}
                  required
                  autoComplete="name"
                />
              </div>
            </div>

            <div className="input-group">
              <label htmlFor="email">Email *</label>
              <div className="input-wrapper">
                <svg className="input-icon" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M3 4L10 10L17 4M3 16H17V6L10 12L3 6V16Z" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
                <input
                  id="email"
                  name="email"
                  type="email"
                  placeholder="seu@email.com"
                  value={formData.email}
                  onChange={handleChange}
                  required
                  autoComplete="email"
                />
              </div>
            </div>

            <div className="input-group">
              <label htmlFor="cnpj">CNPJ</label>
              <div className="input-wrapper">
                <svg className="input-icon" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <rect x="3" y="3" width="14" height="14" rx="2" stroke="currentColor" strokeWidth="1.5"/>
                  <path d="M7 7H9M7 10H13M7 13H11" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
                </svg>
                <input
                  id="cnpj"
                  name="cnpj"
                  type="text"
                  placeholder="00.000.000/0000-00"
                  value={formData.cnpj}
                  onChange={handleChange}
                  autoComplete="organization"
                />
              </div>
            </div>

            <div className="input-group">
              <label htmlFor="telefone">Telefone</label>
              <div className="input-wrapper">
                <svg className="input-icon" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M3 5C3 4.44772 3.44772 4 4 4H6.27924C6.70967 4 7.09181 4.27543 7.22792 4.68377L8.72574 9.17721C8.88311 9.64932 8.66938 10.1653 8.19727 10.3227L6.32923 11.006C5.73661 11.2222 5.29755 11.7637 5.19101 12.3846C4.99474 13.5985 5.77954 14.7839 6.99348 14.9802C7.61437 15.0867 8.15587 14.6476 8.37208 14.055L9.05589 12.1869C9.21326 11.7148 9.72924 11.5011 10.2014 11.6584L14.6948 13.1562C15.1032 13.2923 15.3786 13.6745 15.3786 14.1049V16.4C15.3786 16.9523 14.9309 17.4 14.3786 17.4H12.4C7.86577 17.4 4.2 13.7342 4.2 9.2V5Z" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
                <input
                  id="telefone"
                  name="telefone"
                  type="tel"
                  placeholder="(11) 99999-9999"
                  value={formData.telefone}
                  onChange={handleChange}
                  autoComplete="tel"
                />
              </div>
            </div>

            <div className="input-group">
              <label htmlFor="senha">Senha *</label>
              <div className="input-wrapper">
                <svg className="input-icon" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <rect x="3" y="8" width="14" height="9" rx="2" stroke="currentColor" strokeWidth="1.5"/>
                  <path d="M6 8V5C6 3.34315 7.34315 2 9 2H11C12.6569 2 14 3.34315 14 5V8" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
                </svg>
                <input
                  id="senha"
                  name="senha"
                  type={mostrarSenha ? 'text' : 'password'}
                  placeholder="••••••••"
                  value={formData.senha}
                  onChange={handleChange}
                  required
                  autoComplete="new-password"
                />
                <button
                  type="button"
                  className="password-toggle"
                  onClick={() => setMostrarSenha(!mostrarSenha)}
                  aria-label={mostrarSenha ? 'Ocultar senha' : 'Mostrar senha'}
                >
                  {mostrarSenha ? (
                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M10 4C6 4 3.27 6.11 2 9C3.27 11.89 6 14 10 14C14 14 16.73 11.89 18 9C16.73 6.11 14 4 10 4Z" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                      <path d="M10 11C11.1046 11 12 10.1046 12 9C12 7.89543 11.1046 7 10 7C8.89543 7 8 7.89543 8 9C8 10.1046 8.89543 11 10 11Z" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                      <path d="M2 2L18 18" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
                    </svg>
                  ) : (
                    <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M10 4C6 4 3.27 6.11 2 9C3.27 11.89 6 14 10 14C14 14 16.73 11.89 18 9C16.73 6.11 14 4 10 4Z" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                      <path d="M10 11C11.1046 11 12 10.1046 12 9C12 7.89543 11.1046 7 10 7C8.89543 7 8 7.89543 8 9C8 10.1046 8.89543 11 10 11Z" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                    </svg>
                  )}
                </button>
              </div>
            </div>

            <div className="input-group">
              <label htmlFor="confirmarSenha">Confirmar Senha *</label>
              <div className="input-wrapper">
                <svg className="input-icon" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <rect x="3" y="8" width="14" height="9" rx="2" stroke="currentColor" strokeWidth="1.5"/>
                  <path d="M6 8V5C6 3.34315 7.34315 2 9 2H11C12.6569 2 14 3.34315 14 5V8" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
                  <path d="M8 12L10 14L14 10" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
                <input
                  id="confirmarSenha"
                  name="confirmarSenha"
                  type={mostrarSenha ? 'text' : 'password'}
                  placeholder="••••••••"
                  value={formData.confirmarSenha}
                  onChange={handleChange}
                  required
                  autoComplete="new-password"
                />
              </div>
            </div>
          </div>

          <div className="register-options">
            <label className="terms-checkbox">
              <input
                type="checkbox"
                checked={aceitarTermos}
                onChange={(e) => setAceitarTermos(e.target.checked)}
                required
              />
              <span>Aceito os <a href="#" className="terms-link">Termos de Uso</a> e <a href="#" className="terms-link">Política de Privacidade</a></span>
            </label>
          </div>

          <button
            type="submit"
            className="register-button"
            disabled={loading}
          >
            {loading ? (
              <>
                <svg className="spinner" width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <circle cx="10" cy="10" r="8" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeDasharray="31.416" strokeDashoffset="31.416">
                    <animate attributeName="stroke-dasharray" dur="2s" values="0 31.416;15.708 15.708;0 31.416;0 31.416" repeatCount="indefinite"/>
                    <animate attributeName="stroke-dashoffset" dur="2s" values="0;-15.708;-31.416;-31.416" repeatCount="indefinite"/>
                  </circle>
                </svg>
                Criando conta...
              </>
            ) : (
              <>
                Criar Conta
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M7.5 15L12.5 10L7.5 5" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
              </>
            )}
          </button>

          <div className="register-footer">
            <p>Já tem uma conta? <Link to="/login" className="login-link">Fazer Login</Link></p>
          </div>
        </form>
      </div>
    </div>
  );
}