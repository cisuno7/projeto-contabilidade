import { useEffect, useState } from 'react';
import { dashboardService } from '../services/api';
import type { DashboardEstatisticas } from '../types';

interface UseDashboardStatsReturn {
  estatisticas: DashboardEstatisticas | null;
  loading: boolean;
  error: string | null;
  refetch: () => Promise<void>;
}

export function useDashboardStats(): UseDashboardStatsReturn {
  const [estatisticas, setEstatisticas] = useState<DashboardEstatisticas | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchEstatisticas = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await dashboardService.obterEstatisticas();
      setEstatisticas(data);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Erro ao carregar estatísticas';
      setError(errorMessage);
      console.error('Erro ao carregar estatísticas:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEstatisticas();
  }, []);

  return {
    estatisticas,
    loading,
    error,
    refetch: fetchEstatisticas,
  };
}
