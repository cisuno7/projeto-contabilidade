import { useState, useCallback, useRef } from 'react';
import { planilhaService } from '../../services/api';
import { Button } from '../../components/ui/Button/Button';
import { Input } from '../../components/ui/Input/Input';
import type { Planilha } from '../../types';
import './Upload.css';

export default function Upload() {
  const [arquivo, setArquivo] = useState<File | null>(null);
  const [clienteId, setClienteId] = useState('');
  const [loading, setLoading] = useState(false);
  const [mensagem, setMensagem] = useState<{ texto: string; tipo: 'sucesso' | 'erro' } | null>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setArquivo(e.target.files[0]);
      setMensagem(null);
    }
  }, []);

  const handleSubmit = useCallback(async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!arquivo || !clienteId.trim()) {
      setMensagem({ texto: 'Por favor, selecione um arquivo e informe o ID do cliente', tipo: 'erro' });
      return;
    }

    setLoading(true);
    setMensagem(null);

    try {
      const planilha: Planilha = await planilhaService.upload(arquivo, clienteId.trim());
      setMensagem({ texto: `Planilha "${planilha.nomeArquivo}" enviada com sucesso!`, tipo: 'sucesso' });
      setArquivo(null);
      setClienteId('');
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    } catch (error) {
      console.error('Erro ao fazer upload:', error);
      setMensagem({ texto: 'Erro ao fazer upload da planilha. Tente novamente.', tipo: 'erro' });
    } finally {
      setLoading(false);
    }
  }, [arquivo, clienteId]);

  return (
    <div className="upload-container">
      <div className="upload-header">
        <div>
          <h1 className="upload-title">Upload de Planilha</h1>
          <p className="upload-subtitle">Envie planilhas para processamento automático</p>
        </div>
      </div>
      
      <form onSubmit={handleSubmit} className="upload-form">
        <Input
          label="ID do Cliente"
          id="clienteId"
          type="text"
          value={clienteId}
          onChange={(e) => setClienteId(e.target.value)}
          placeholder="Digite o ID do cliente"
          required
          fullWidth
          disabled={loading}
        />

        <div className="input-group">
          <label htmlFor="arquivo" className="input-label">Selecione o arquivo</label>
          <div className="file-input-wrapper">
            <input
              ref={fileInputRef}
              id="arquivo"
              type="file"
              accept=".xlsx,.xls,.csv"
              onChange={handleFileChange}
              required
              disabled={loading}
              className="file-input"
              aria-describedby={arquivo ? 'file-info' : undefined}
            />
            {arquivo && (
              <div id="file-info" className="file-info">
                <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M4 4C4 2.89543 4.89543 2 6 2H8.58579C8.851 2 9.10536 2.10536 9.29289 2.29289L12.7071 5.70711C12.8946 5.89464 13 6.149 13 6.41421V12C13 13.1046 12.1046 14 11 14H6C4.89543 14 4 13.1046 4 12V4Z" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                  <path d="M8 2V6H12" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                </svg>
                <span>{arquivo.name}</span>
                <button
                  type="button"
                  onClick={() => {
                    setArquivo(null);
                    if (fileInputRef.current) {
                      fileInputRef.current.value = '';
                    }
                  }}
                  className="file-remove"
                  aria-label="Remover arquivo"
                >
                  <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 4L4 12M4 4L12 12" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                  </svg>
                </button>
              </div>
            )}
          </div>
          <span className="input-helper">Formatos aceitos: .xlsx, .xls, .csv</span>
        </div>

        {mensagem && (
          <div className={`upload-mensagem ${mensagem.tipo === 'sucesso' ? 'upload-mensagem--sucesso' : 'upload-mensagem--erro'}`} role="alert">
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              {mensagem.tipo === 'sucesso' ? (
                <path d="M16 4L7.5 12.5L4 9" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              ) : (
                <>
                  <circle cx="10" cy="10" r="9" stroke="currentColor" strokeWidth="2"/>
                  <path d="M10 6V10" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
                  <circle cx="10" cy="13" r="1" fill="currentColor"/>
                </>
              )}
            </svg>
            <span>{mensagem.texto}</span>
          </div>
        )}

        <Button 
          type="submit" 
          disabled={loading} 
          isLoading={loading}
          fullWidth
          size="large"
        >
          Enviar Planilha
        </Button>
      </form>
    </div>
  );
}
