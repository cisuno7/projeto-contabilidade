import { useState, useCallback, useEffect, useRef } from 'react';
import axios from 'axios';
import { planilhaService, clienteService } from '../../services/api';
import { Button } from '../../components/ui/Button/Button';
import type { Planilha, Cliente } from '../../types';
import './Upload.css';

/** UFs (mesma convenção que Clientes): valor em duas letras para filtrar produto/NCM/CEST no Supabase. */
const UF_OPCOES: { valor: string; label: string }[] = [
  { valor: 'AC', label: 'Acre' },
  { valor: 'AL', label: 'Alagoas' },
  { valor: 'AP', label: 'Amapá' },
  { valor: 'AM', label: 'Amazonas' },
  { valor: 'BA', label: 'Bahia' },
  { valor: 'CE', label: 'Ceará' },
  { valor: 'DF', label: 'Distrito Federal' },
  { valor: 'ES', label: 'Espírito Santo' },
  { valor: 'GO', label: 'Goiás' },
  { valor: 'MA', label: 'Maranhão' },
  { valor: 'MT', label: 'Mato Grosso' },
  { valor: 'MS', label: 'Mato Grosso do Sul' },
  { valor: 'MG', label: 'Minas Gerais' },
  { valor: 'PA', label: 'Pará' },
  { valor: 'PB', label: 'Paraíba' },
  { valor: 'PR', label: 'Paraná' },
  { valor: 'PE', label: 'Pernambuco' },
  { valor: 'PI', label: 'Piauí' },
  { valor: 'RJ', label: 'Rio de Janeiro' },
  { valor: 'RN', label: 'Rio Grande do Norte' },
  { valor: 'RS', label: 'Rio Grande do Sul' },
  { valor: 'RO', label: 'Rondônia' },
  { valor: 'RR', label: 'Roraima' },
  { valor: 'SC', label: 'Santa Catarina' },
  { valor: 'SP', label: 'São Paulo' },
  { valor: 'SE', label: 'Sergipe' },
  { valor: 'TO', label: 'Tocantins' },
];

type RelatorioCorrelacaoJson = {
  totalLinhas?: number;
  linhasComAlteracao?: number;
  linhasPendentes?: number;
  nota?: string;
  ufConferencia?: string;
  pendentes?: Array<Record<string, unknown>>;
};

type MetadataPlanilhaJson = {
  resumoTexto?: string;
  relatorioCorrelacao?: RelatorioCorrelacaoJson;
  caminhoArquivoCorrigidoGerado?: string;
};

function renderAiMetadata(meta: string | undefined) {
  if (!meta) return null;
  const trimmed = meta.trim();
  if (trimmed.startsWith('{')) {
    try {
      const parsed = JSON.parse(meta) as MetadataPlanilhaJson;
      const rel = parsed.relatorioCorrelacao;
      const pendentes = rel?.pendentes;
      return (
        <div className="upload-metadata-json">
          {parsed.resumoTexto && (
            <pre className="upload-mensagem-alteracoes">{parsed.resumoTexto}</pre>
          )}
          {rel && pendentes && pendentes.length > 0 && (
            <div className="upload-relatorio-correlacao">
              <h4 className="upload-relatorio-titulo">Relatório de correlação (conferência da base)</h4>
              {rel.nota && <p className="upload-relatorio-nota">{rel.nota}</p>}
              <p className="upload-relatorio-stats">
                {typeof rel.ufConferencia === 'string' && rel.ufConferencia
                  ? `UF da conferência no banco: ${rel.ufConferencia}. `
                  : null}
                Total de linhas: {rel.totalLinhas ?? '—'} · Com alteração automática:{' '}
                {rel.linhasComAlteracao ?? '—'} · Pendentes:{' '}
                {rel.linhasPendentes ?? pendentes.length}
              </p>
              <ul className="upload-relatorio-lista">
                {pendentes.map((p, i) => {
                  const cands = p.candidatos_produto;
                  const list =
                    Array.isArray(cands) && cands.length > 0
                      ? (cands as Array<Record<string, unknown>>)
                      : [];
                  return (
                    <li key={i} className="upload-relatorio-item">
                      <div className="upload-relatorio-linha">
                        <strong>Linha {String(p.linha ?? '')}</strong>
                        {' — '}
                        <span>{String(p.nome ?? '')}</span>
                      </div>
                      <div className="upload-relatorio-tags">
                        {Boolean(p.falta_ncm) && (
                          <span className="upload-tag">falta NCM</span>
                        )}
                        {Boolean(p.falta_cest) && (
                          <span className="upload-tag">falta CEST</span>
                        )}
                        {typeof p.ia_prioridade === 'string' && p.ia_prioridade && (
                          <span className="upload-tag upload-tag--prioridade">
                            prioridade: {p.ia_prioridade}
                          </span>
                        )}
                      </div>
                      {typeof p.ia_insight === 'string' && p.ia_insight && (
                        <p className="upload-relatorio-insight">{p.ia_insight}</p>
                      )}
                      {list.length > 0 && (
                        <details className="upload-relatorio-details">
                          <summary>Candidatos na tabela produto (correlação)</summary>
                          <ul className="upload-relatorio-sublista">
                            {list.map((c, j) => (
                              <li key={j}>
                                {String(c.nome ?? '')}
                                {typeof c.uf === 'string' && c.uf ? ` · UF ${c.uf}` : ''} (similaridade{' '}
                                {String(c.score_similaridade ?? '—')}) — NCM {String(c.codigo_ncm ?? '—')} · CEST{' '}
                                {String(c.codigo_cest ?? '—')}
                              </li>
                            ))}
                          </ul>
                        </details>
                      )}
                    </li>
                  );
                })}
              </ul>
            </div>
          )}
        </div>
      );
    } catch {
      return <pre className="upload-mensagem-alteracoes">{meta}</pre>;
    }
  }
  return <pre className="upload-mensagem-alteracoes">{meta}</pre>;
}

export default function Upload() {
  const [arquivo, setArquivo] = useState<File | null>(null);
  const [clienteId, setClienteId] = useState('');
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [loadingClientes, setLoadingClientes] = useState(true);
  const [corrigirComIA, setCorrigirComIA] = useState(true);
  /** UF usada para buscar produtos / NCM / CEST no Supabase (padrão ES enquanto a base ES estiver carregada). */
  const [ufConferencia, setUfConferencia] = useState('ES');
  const [loading, setLoading] = useState(false);
  const [mensagem, setMensagem] = useState<{ texto: string; tipo: 'sucesso' | 'erro' } | null>(null);
  const [planilhaProcessada, setPlanilhaProcessada] = useState<Planilha | null>(null);
  const [downloading, setDownloading] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    clienteService
      .listar()
      .then((lista) => setClientes(lista))
      .catch((err: unknown) => {
        setClientes([]);
        const msg =
          axios.isAxiosError(err) && err.response?.data && typeof err.response.data === 'object' && 'message' in err.response.data
            ? String((err.response.data as { message?: unknown }).message ?? '')
            : 'Não foi possível carregar os clientes. Verifique se o backend está acessível e tente recarregar a página.';
        setMensagem({ texto: msg || 'Não foi possível carregar os clientes.', tipo: 'erro' });
      })
      .finally(() => setLoadingClientes(false));
  }, []);

  const handleBaixar = useCallback(async () => {
    if (!planilhaProcessada) return;
    try {
      setDownloading(true);
      const blob = await planilhaService.baixar(planilhaProcessada.id);
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `corrigido_${planilhaProcessada.nomeArquivo || 'planilha.xlsx'}`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    } catch (err) {
      console.error('Erro ao baixar planilha:', err);
      const msg = axios.isAxiosError(err) && err.response?.data?.message
        ? err.response.data.message
        : 'Erro ao baixar a planilha. Tente novamente.';
      setMensagem({ texto: msg, tipo: 'erro' });
    } finally {
      setDownloading(false);
    }
  }, [planilhaProcessada]);

  const handleFileChange = useCallback((e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setArquivo(e.target.files[0]);
      setMensagem(null);
    }
  }, []);

  const handleSubmit = useCallback(async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!arquivo || !clienteId.trim()) {
      setMensagem({ texto: 'Por favor, selecione um arquivo e um cliente', tipo: 'erro' });
      return;
    }
    if (corrigirComIA && !ufConferencia.trim()) {
      setMensagem({ texto: 'Selecione a UF para conferência no banco de dados.', tipo: 'erro' });
      return;
    }

    setLoading(true);
    setMensagem(null);

    try {
      const planilha: Planilha = await planilhaService.upload(
        arquivo,
        clienteId.trim(),
        undefined,
        corrigirComIA,
        corrigirComIA ? ufConferencia.trim() : null,
      );
      const processada = corrigirComIA && planilha.podeBaixar;
      setMensagem({ 
        texto: processada 
          ? `Planilha "${planilha.nomeArquivo}" processada com sucesso! NCM e CEST corrigidos.`
          : `Planilha "${planilha.nomeArquivo}" enviada com sucesso! Correção com IA: ${corrigirComIA ? 'ativada' : 'desativada'}.`, 
        tipo: 'sucesso' 
      });
      setPlanilhaProcessada(processada ? planilha : null);
      setArquivo(null);
      setClienteId('');
      setCorrigirComIA(true);
      setUfConferencia('ES');
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    } catch (error) {
      console.error('Erro ao fazer upload:', error);
      const msg = axios.isAxiosError(error) && error.response?.data?.message
        ? error.response.data.message
        : 'Erro ao fazer upload da planilha. Tente novamente.';
      setMensagem({ texto: msg, tipo: 'erro' });
    } finally {
      setLoading(false);
    }
  }, [arquivo, clienteId, corrigirComIA, ufConferencia]);

  return (
    <div className="upload-container">
      <div className="upload-header">
        <div>
          <h1 className="upload-title">Upload de Planilha</h1>
          <p className="upload-subtitle">Envie planilhas para processamento automático</p>
        </div>
      </div>
      
      <form onSubmit={handleSubmit} className="upload-form">
        <div className="input-group input-group--full-width">
          <label htmlFor="clienteId" className="input-label">
            Cliente
          </label>
          <select
            id="clienteId"
            value={clienteId}
            onChange={(e) => setClienteId(e.target.value)}
            disabled={loading || loadingClientes}
            className="upload-select"
            required
          >
            <option value="">— Selecione um cliente —</option>
            {clientes.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name} {c.documentNumber ? `(${c.documentNumber})` : ''}
              </option>
            ))}
          </select>
          {loadingClientes && (
            <span className="input-helper">Carregando clientes...</span>
          )}
        </div>

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

        {corrigirComIA && (
          <div className="upload-bloco-referencia" role="region" aria-labelledby="upload-uf-titulo">
            <h2 id="upload-uf-titulo" className="upload-bloco-referencia-titulo">
              UF da conferência no banco
            </h2>
            <p className="upload-bloco-referencia-desc">
              A correção usa a regra com base na{' '}
              UF escolhida (NCM/CEST vinculados também da mesma UF).
            </p>
            <div className="input-group input-group--sem-margem-extra">
              <label htmlFor="ufConferencia" className="input-label">
                Estado (UF)
              </label>
              <select
                id="ufConferencia"
                className="file-input"
                style={{ cursor: 'pointer', padding: '0.65rem 0.75rem' }}
                value={ufConferencia}
                onChange={(e) => setUfConferencia(e.target.value)}
                disabled={loading}
                aria-describedby="upload-uf-ajuda"
                required={corrigirComIA}
              >
                {UF_OPCOES.map((uf) => (
                  <option key={uf.valor} value={uf.valor}>
                    {uf.valor} — {uf.label}
                  </option>
                ))}
              </select>
              <span id="upload-uf-ajuda" className="input-helper">
                Padrão: ES.
              </span>
            </div>
          </div>
        )}

        <div className="input-group">
          <label className="input-label">Correção automática com IA</label>
          <div className="checkbox-row">
            <input
              id="corrigirComIA"
              type="checkbox"
              checked={corrigirComIA}
              onChange={(e) => setCorrigirComIA(e.target.checked)}
              disabled={loading}
            />
            <label htmlFor="corrigirComIA" className="checkbox-label">
              Corrigir planilha com IA (NCM e CEST conforme o banco na UF escolhida)
            </label>
          </div>
          <span className="input-helper">
            Quando ativado, o sistema cruza cada linha com produtos cadastrados no Supabase para aquela UF e
            preenche ou ajusta NCM e CEST na planilha enviada.
          </span>
        </div>

        {planilhaProcessada?.podeBaixar && (
          <div className="upload-download">
            <Button
              type="button"
              onClick={handleBaixar}
              disabled={downloading}
              isLoading={downloading}
              fullWidth
              size="large"
            >
              Baixar planilha corrigida
            </Button>
          </div>
        )}

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
            <div className="upload-mensagem-conteudo">
              <span>{mensagem.texto}</span>
              {mensagem.tipo === 'sucesso' && planilhaProcessada?.aiMetadata && (
                renderAiMetadata(planilhaProcessada.aiMetadata)
              )}
            </div>
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
