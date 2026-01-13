import React, { Component, ErrorInfo, ReactNode } from 'react';
import './ErrorBoundary.css';

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
  errorInfo: ErrorInfo | null;
}

export class ErrorBoundary extends Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = {
      hasError: false,
      error: null,
      errorInfo: null,
    };
  }

  static getDerivedStateFromError(error: Error): Partial<State> {
    return { hasError: true, error };
  }

  componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('ErrorBoundary capturou um erro:', error, errorInfo);
    this.setState({
      error,
      errorInfo,
    });
  }

  handleReset = () => {
    this.setState({
      hasError: false,
      error: null,
      errorInfo: null,
    });
  };

  render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback;
      }

      return (
        <div className="error-boundary">
          <div className="error-boundary-content">
            <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="32" cy="32" r="30" stroke="currentColor" strokeWidth="2"/>
              <path d="M32 16V32M32 40H32.0167" stroke="currentColor" strokeWidth="3" strokeLinecap="round"/>
            </svg>
            <h2 className="error-boundary-title">Ops! Algo deu errado</h2>
            <p className="error-boundary-message">
              Ocorreu um erro inesperado. Por favor, tente recarregar a página.
            </p>
            {process.env.NODE_ENV === 'development' && this.state.error && (
              <details className="error-boundary-details">
                <summary>Detalhes do erro (desenvolvimento)</summary>
                <pre className="error-boundary-stack">
                  {this.state.error.toString()}
                  {this.state.errorInfo?.componentStack}
                </pre>
              </details>
            )}
            <div className="error-boundary-actions">
              <button onClick={this.handleReset} className="error-boundary-button error-boundary-button--primary">
                Tentar novamente
              </button>
              <button 
                onClick={() => window.location.reload()} 
                className="error-boundary-button error-boundary-button--secondary"
              >
                Recarregar página
              </button>
            </div>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
