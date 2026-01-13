# Paleta de Cores - Sistema Contábil

## 🎨 Paleta Escolhida: Azul-Roxo Suave (Índigo)

Esta é a paleta de cores oficial do projeto, implementada em todas as interfaces.

---

## 🎯 Cores Principais

**Paleta Completa:**
```
Primária (Índigo):
- Índigo Principal: #6366f1
- Índigo Escuro: #4f46e5
- Índigo Claro: #818cf8
- Azul Muito Escuro: #1e293b

Neutros:
- Branco: #ffffff
- Cinza Muito Claro: #f1f5f9
- Cinza Médio: #64748b
- Cinza Escuro: #334155

Destaques:
- Verde Sucesso: #10b981 (para ações positivas)
- Vermelho Erro: #ef4444 (para erros)
- Amarelo Aviso: #f59e0b (opcional)
```

---

## 📋 Aplicação das Cores

### Degradês
- **Fundo Principal:** `linear-gradient(135deg, #6366f1 0%, #4f46e5 100%)`
- **Formas Animadas:** `linear-gradient(135deg, #1e293b 0%, #818cf8 100%)`
- **Botões:** `linear-gradient(135deg, #6366f1 0%, #4f46e5 100%)`

### Elementos Interativos
- **Botões Primários:** `#6366f1` com hover `#4f46e5`
- **Links:** `#6366f1` com hover `#4f46e5`
- **Bordas Focus:** `#6366f1` com shadow `rgba(99, 102, 241, 0.1)`

### Textos
- **Títulos:** `#1e293b` (azul muito escuro)
- **Subtítulos/Descrições:** `#64748b` (cinza médio)
- **Textos Secundários:** `#9ca3af` (cinza claro)

### Cards e Containers
- **Fundo:** `#ffffff` (branco)
- **Bordas:** `#f1f5f9` (cinza muito claro)
- **Sombras:** `rgba(99, 102, 241, 0.1)` a `rgba(99, 102, 241, 0.15)`

---

## ✅ Onde está Implementada

A paleta está aplicada em:
- ✅ Tela de Login (degradê de fundo, botões, elementos interativos)
- ✅ Dashboard (cards, ícones, botões)
- ✅ Componentes UI reutilizáveis:
  - `StatCard` (gradientes, bordas)
  - `LoadingSpinner` (cores do spinner)
- ✅ Navegação e elementos globais

---

## 🎨 Exemplos de Uso

### CSS - Botão Primário
```css
.button-primary {
  background: linear-gradient(135deg, #6366f1 0%, #4f46e5 100%);
  color: white;
  border: none;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
}

.button-primary:hover {
  box-shadow: 0 6px 20px rgba(99, 102, 241, 0.4);
}
```

### CSS - Card
```css
.card {
  background: white;
  border: 1px solid #f1f5f9;
  border-radius: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.card::before {
  background: linear-gradient(90deg, #6366f1 0%, #818cf8 100%);
}
```

### CSS - Input Focus
```css
.input:focus {
  border-color: #6366f1;
  box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
}
```

---

## 📝 Notas

- **Status:** ✅ Implementada e ativa no projeto
- **Responsividade:** Todas as cores mantêm bom contraste em diferentes dispositivos
- **Acessibilidade:** Contraste adequado para leitura (WCAG AA)
- **Consistência:** Use sempre essas cores para manter a identidade visual do sistema

---

**Última atualização:** 2024
