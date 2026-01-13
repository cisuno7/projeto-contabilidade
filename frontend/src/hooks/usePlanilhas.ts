import { useEffect, useState } from 'react';
import { planilhaService } from '../services/api';
import type { Planilha } from '../types';

interface UsePlanilhasReturn {
  planilhas: Planilha[];
  loading: boolean;
  error: string | null;
  refetch: () => Promise<void>;
}

export function usePlanilhas(): UsePlanilhasReturn {
  const [planilhas, setPlanilhas] = useState<Planilha[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchPlanilhas = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await planilhaService.listar();
      setPlanilhas(data);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Erro ao carregar planilhas';
      setError(errorMessage);
      console.error('Erro ao carregar planilhas:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPlanilhas();
  }, []);

  return {
    planilhas,
    loading,
    error,
    refetch: fetchPlanilhas,
  };
}
