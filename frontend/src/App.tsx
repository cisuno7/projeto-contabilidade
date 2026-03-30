import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { lazy, Suspense } from 'react';
import { authService } from './services/auth';
import { LoadingSpinner } from './components/ui/LoadingSpinner/LoadingSpinner';
import { ErrorBoundary } from './components/ErrorBoundary/ErrorBoundary';
import Navbar from './components/layout/Navbar/Navbar';
import './App.css';

// Lazy loading das páginas
const Upload = lazy(() => import('./pages/Upload/Upload'));
const Historico = lazy(() => import('./pages/Historico/Historico'));
const Clientes = lazy(() => import('./pages/Clientes/Clientes'));
const Login = lazy(() => import('./pages/Login/Login'));
const Register = lazy(() => import('./pages/Register/Register'));

function routerBasename(): string | undefined {
  const b = import.meta.env.BASE_URL;
  if (b === '/' || b === '') return undefined;
  return b.endsWith('/') ? b.slice(0, -1) : b;
}

function ProtectedRoute({ children }: { children: React.ReactElement }) {
  const isAuthenticated = authService.isAuthenticated();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return (
    <>
      <Navbar />
      <main className="main-content">
        {children}
      </main>
    </>
  );
}

function App() {
  return (
    <ErrorBoundary>
      <Router basename={routerBasename()}>
        <div className="app">
          <Suspense
            fallback={
              <div
                style={{
                  display: 'flex',
                  justifyContent: 'center',
                  alignItems: 'center',
                  minHeight: '100vh',
                }}
              >
                <LoadingSpinner size="large" />
              </div>
            }
          >
            <Routes>

              {/* Página pública */}
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />

              {/* Rota principal agora vai para Upload */}
              <Route
                path="/"
                element={<Navigate to="/upload" replace />}
              />

              {/* Rotas protegidas */}
              <Route
                path="/upload"
                element={
                  <ProtectedRoute>
                    <Upload />
                  </ProtectedRoute>
                }
              />

              <Route
                path="/historico"
                element={
                  <ProtectedRoute>
                    <Historico />
                  </ProtectedRoute>
                }
              />

              <Route
                path="/clientes"
                element={
                  <ProtectedRoute>
                    <Clientes />
                  </ProtectedRoute>
                }
              />

              {/* Qualquer rota inválida */}
              <Route path="*" element={<Navigate to="/upload" replace />} />

            </Routes>
          </Suspense>
        </div>
      </Router>
    </ErrorBoundary>
  );
}

export default App;