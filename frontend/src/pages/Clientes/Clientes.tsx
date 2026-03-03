import { useCallback, useState } from 'react';
import { clienteService } from '../../services/api';
import { Button } from '../../components/ui/Button/Button';
import { Input } from '../../components/ui/Input/Input';
import './Clientes.css';

// Estado: enviamos apenas a UF (iniciais), ex: SP, RJ, MG
const UF_ESTADOS = [
  { valor: '', label: 'Selecione o estado' },
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

const REGIMES = [
  { valor: '', label: 'Selecione o regime' },
  { valor: 'SIMPLES_NACIONAL', label: 'Simples Nacional' },
  { valor: 'LUCRO_REAL', label: 'Lucro Real' },
  { valor: 'LUCRO_PRESUMIDO', label: 'Lucro Presumido' },
];

export default function Clientes() {
  const [salvando, setSalvando] = useState(false);
  const [mensagem, setMensagem] = useState<{ texto: string; tipo: 'sucesso' | 'erro' } | null>(null);

  const [form, setForm] = useState({
    name: '',
    documentNumber: '',
    estado: '',
    regime: '',
  });

  const handleSubmit = useCallback(
    async (e: React.FormEvent) => {
      e.preventDefault();
      if (!form.name.trim() || !form.documentNumber.trim()) {
        setMensagem({ texto: 'Nome e CNPJ são obrigatórios.', tipo: 'erro' });
        return;
      }
      setSalvando(true);
      setMensagem(null);
      try {
        await clienteService.criar({
          name: form.name.trim(),
          documentNumber: form.documentNumber.trim(),
          estado: form.estado || undefined,  // UF: SP, RJ, MG, etc.
          regime: form.regime || undefined,
        });
        setMensagem({ texto: 'Cliente cadastrado com sucesso!', tipo: 'sucesso' });
        setForm({ name: '', documentNumber: '', estado: '', regime: '' });
      } catch (err: unknown) {
        const msg =
          err && typeof err === 'object' && 'response' in err && (err as { response?: { data?: { message?: string } } }).response?.data?.message
            ? (err as { response: { data: { message: string } } }).response.data.message
            : 'Erro ao cadastrar cliente. Tente novamente.';
        setMensagem({ texto: msg, tipo: 'erro' });
      } finally {
        setSalvando(false);
      }
    },
    [form]
  );

  return (
    <div className="clientes-container">
      <div className="clientes-header">
        <div>
          <h1 className="clientes-title">Cadastro de Clientes</h1>
          <p className="clientes-subtitle">
            Cadastre clientes para usar no upload de planilhas. O histórico de planilhas corrigidas fica em Histórico.
          </p>
        </div>
      </div>

      <section className="clientes-form-section">
          <form onSubmit={handleSubmit} className="clientes-form">
            <Input
              label="Nome"
              id="name"
              type="text"
              value={form.name}
              onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
              placeholder="Nome do cliente"
              required
              fullWidth
              disabled={salvando}
            />
            <Input
              label="CNPJ"
              id="documentNumber"
              type="text"
              value={form.documentNumber}
              onChange={(e) => setForm((f) => ({ ...f, documentNumber: e.target.value }))}
              placeholder="00.000.000/0000-00"
              required
              fullWidth
              disabled={salvando}
            />
            <div className="input-group input-group--full-width">
              <label htmlFor="estado" className="input-label">
                Estado
              </label>
              <select
                id="estado"
                value={form.estado}
                onChange={(e) => setForm((f) => ({ ...f, estado: e.target.value }))}
                disabled={salvando}
                className="clientes-select"
              >
                {UF_ESTADOS.map((uf) => (
                  <option key={uf.valor || 'empty'} value={uf.valor}>
                    {uf.label}
                  </option>
                ))}
              </select>
            </div>
            <div className="input-group input-group--full-width">
              <label htmlFor="regime" className="input-label">
                Regime tributário
              </label>
              <select
                id="regime"
                value={form.regime}
                onChange={(e) => setForm((f) => ({ ...f, regime: e.target.value }))}
                disabled={salvando}
                className="clientes-select"
              >
                {REGIMES.map((r) => (
                  <option key={r.valor || 'empty'} value={r.valor}>
                    {r.label}
                  </option>
                ))}
              </select>
            </div>
            {mensagem && (
              <div
                className={`clientes-mensagem clientes-mensagem--${mensagem.tipo}`}
                role="alert"
              >
                {mensagem.texto}
              </div>
            )}
            <Button type="submit" disabled={salvando} isLoading={salvando} fullWidth size="large">
              Cadastrar Cliente
            </Button>
          </form>
      </section>
    </div>
  );
}
