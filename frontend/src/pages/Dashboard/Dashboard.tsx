import { useDashboardStats } from '../../hooks/useDashboardStats';
import { StatCard } from '../../components/ui/StatCard/StatCard';
import { LoadingSpinner } from '../../components/ui/LoadingSpinner/LoadingSpinner';
import { Button } from '../../components/ui/Button/Button';
import './Dashboard.css';

export default function Dashboard() {
  const { estatisticas, loading, error, refetch } = useDashboardStats();

  if (loading) {
    return (
      <div className="dashboard-container">
        <LoadingSpinner size="large" message="Carregando estatísticas..." />
      </div>
    );
  }

  if (error) {
    return (
      <div className="dashboard-container">
        <div className="dashboard-error">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <circle cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="2"/>
            <path d="M12 8V12" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            <circle cx="12" cy="16" r="1" fill="currentColor"/>
          </svg>
          <div>
            <h3>Erro ao carregar estatísticas</h3>
            <p>{error}</p>
            <Button onClick={refetch}>
              Tentar novamente
            </Button>
          </div>
        </div>
      </div>
    );
  }

  if (!estatisticas) {
    return null;
  }

  const taxaSucessoPercent = (estatisticas.taxaSucesso * 100).toFixed(1);

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <div>
          <h1 className="dashboard-title">Dashboard</h1>
          <p className="dashboard-subtitle">Visão geral do sistema contábil</p>
        </div>
        <button onClick={refetch} className="refresh-button" aria-label="Atualizar dados">
          <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M10 3V1M10 19V17M17 10H19M1 10H3M15.657 4.343L17.071 2.929M2.929 17.071L4.343 15.657M15.657 15.657L17.071 17.071M2.929 2.929L4.343 4.343" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            <path d="M10 5C7.239 5 5 7.239 5 10C5 12.761 7.239 15 10 15C12.761 15 15 12.761 15 10C15 7.239 12.761 5 10 5Z" stroke="currentColor" strokeWidth="2"/>
          </svg>
        </button>
      </div>

      <div className="dashboard-stats">
        <StatCard
          title="Total de Planilhas"
          value={estatisticas.totalPlanilhas}
          color="primary"
          icon={
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M4 4C4 2.89543 4.89543 2 6 2H10.5858C10.851 2 11.1054 2.10536 11.2929 2.29289L15.7071 6.70711C15.8946 6.89464 16 7.149 16 7.41421V16C16 17.1046 15.1046 18 14 18H6C4.89543 18 4 17.1046 4 16V4Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              <path d="M10 2V6H14" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          }
        />

        <StatCard
          title="Processadas"
          value={estatisticas.planilhasProcessadas}
          color="success"
          icon={
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M16 4L7.5 12.5L4 9" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          }
        />

        <StatCard
          title="Em Processamento"
          value={estatisticas.planilhasEmProcessamento}
          color="warning"
          icon={
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="10" cy="10" r="7" stroke="currentColor" strokeWidth="2"/>
              <path d="M10 6V10L13 13" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            </svg>
          }
        />

        <StatCard
          title="Taxa de Sucesso"
          value={`${taxaSucessoPercent}%`}
          color="info"
          icon={
            <svg width="20" height="20" viewBox="0 0 20 20" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M10 2L12.09 7.26L18 8.27L14 12.14L14.91 18.02L10 15.77L5.09 18.02L6 12.14L2 8.27L7.91 7.26L10 2Z" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          }
        />
      </div>
    </div>
  );
}
