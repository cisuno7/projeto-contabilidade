import { useCallback, useState } from 'react';
import { usePlanilhas } from '../../hooks/usePlanilhas';
import { planilhaService } from '../../services/api';
import { StatusBadge } from '../../components/ui/StatusBadge/StatusBadge';
import { EmptyState } from '../../components/ui/EmptyState/EmptyState';
import { LoadingSpinner } from '../../components/ui/LoadingSpinner/LoadingSpinner';
import { Button } from '../../components/ui/Button/Button';
import type { StatusPlanilha } from '../../types';
import './Historico.css';

export default function Historico() {
  const { planilhas, loading, error, refetch } = usePlanilhas();
  const [downloadingId, setDownloadingId] = useState<string | null>(null);

  const handleBaixar = useCallback(async (planilhaId: string, nomeArquivo: string) => {
    try {
      setDownloadingId(planilhaId);
      const blob = await planilhaService.baixar(planilhaId);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = nomeArquivo;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (err) {
      console.error('Erro ao baixar planilha:', err);
      alert('Erro ao baixar a planilha. Tente novamente.');
    } finally {
      setDownloadingId(null);
    }
  }, []);

  const formatarData = useCallback((data: string): string => {
    return new Date(data).toLocaleString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }, []);

  if (loading) {
    return (
      <div className="historico-container">
        <LoadingSpinner size="large" message="Carregando histórico..." />
      </div>
    );
  }

  if (error) {
    return (
      <div className="historico-container">
        <div className="historico-error">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
            <path d="M12 8V12" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            <circle cx="12" cy="16" r="1" fill="currentColor"/>
          </svg>
          <div>
            <h3>Erro ao carregar histórico</h3>
            <p>{error}</p>
            <Button onClick={refetch}>
              Tentar novamente
            </Button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="historico-container">
      <div className="historico-header">
        <div>
          <h1 className="historico-title">Histórico de Planilhas</h1>
          <p className="historico-subtitle">Visualize todas as planilhas processadas no sistema</p>
        </div>
        <button onClick={refetch} className="refresh-button" aria-label="Atualizar histórico">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M10 3V1M10 19V17M17 10H19M1 10H3M15.657 4.343L17.071 2.929M2.929 17.071L4.343 15.657M15.657 15.657L17.071 17.071M2.929 2.929L4.343 4.343" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            <path d="M10 5C7.239 5 5 7.239 5 10C5 12.761 7.239 15 10 15C12.761 15 15 12.761 15 10C15 7.239 12.761 5 10 5Z" stroke="currentColor" strokeWidth="2"/>
          </svg>
        </button>
      </div>

      {planilhas.length === 0 ? (
        <EmptyState
          title="Nenhuma planilha encontrada"
          message="Ainda não há planilhas no histórico. Faça upload de uma planilha para começar."
          icon={
            <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M32 16V32L42.6667 42.6667" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
              <circle cx="32" cy="32" r="28" stroke="currentColor" strokeWidth="2"/>
            </svg>
          }
        />
      ) : (
        <div className="historico-table-wrapper">
          <table className="historico-table">
            <thead>
              <tr>
                <th>Nome do Arquivo</th>
                <th>Status</th>
                <th>Data de Upload</th>
                <th>Data de Processamento</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {planilhas.map((planilha) => (
                <tr key={planilha.id}>
                  <td>
                    <div className="table-cell-filename">
                      <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                        <path d="M4 4C4 2.89543 4.89543 2 6 2H8.58579C8.851 2 9.10536 2.10536 9.29289 2.29289L12.7071 5.70711C12.8946 5.89464 13 6.149 13 6.41421V12C13 13.1046 12.1046 14 11 14H6C4.89543 14 4 13.1046 4 12V4Z" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                        <path d="M8 2V6H12" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                      </svg>
                      <span>{planilha.nomeArquivo}</span>
                    </div>
                  </td>
                  <td>
                    <StatusBadge status={planilha.status} />
                  </td>
                  <td>
                    <span className="table-cell-date">{formatarData(planilha.dataUpload)}</span>
                  </td>
                  <td>
                    <span className="table-cell-date">
                      {planilha.dataProcessamento ? formatarData(planilha.dataProcessamento) : '-'}
                    </span>
                  </td>
                  <td>
                    <Button
                      onClick={() => handleBaixar(planilha.id, planilha.nomeArquivo)}
                      disabled={planilha.status !== StatusPlanilha.CONCLUIDA || downloadingId === planilha.id}
                      isLoading={downloadingId === planilha.id}
                      size="small"
                      aria-label={`Baixar ${planilha.nomeArquivo}`}
                    >
                      {downloadingId !== planilha.id && (
                        <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
                          <path d="M8 11.3333V2.66667M8 2.66667L5.33333 5.33333M8 2.66667L10.6667 5.33333" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"/>
                          <path d="M2.66667 11.3333H13.3333" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round"/>
                        </svg>
                      )}
                      Baixar
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
