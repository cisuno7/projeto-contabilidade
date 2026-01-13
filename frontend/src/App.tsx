import { BrowserRouter as Router, Routes, Route, Link, useLocation, Navigate, useNavigate } from 'react-router-dom';
import { lazy, Suspense } from 'react';
import { authService } from './services/auth';
import { LoadingSpinner } from './components/ui/LoadingSpinner/LoadingSpinner';
import { ErrorBoundary } from './components/ErrorBoundary/ErrorBoundary';
import './App.css';

// Lazy loading das páginas
const Dashboard = lazy(() => import('./pages/Dashboard/Dashboard'));
const Upload = lazy(() => import('./pages/Upload/Upload'));
const Historico = lazy(() => import('./pages/Historico/Historico'));
const Login = lazy(() => import('./pages/Login/Login'));

function Navigation() {
  const location = useLocation();
  const navigate = useNavigate();

  const handleLogout = () => {
    authService.logout();
    navigate('/login', { replace: true });
  };

  return (
    <nav className="navigation">
      <div className="nav-container">
        <h2 className="nav-title">Sistema Contábil</h2>
        <ul className="nav-links">
          <li>
            <Link
              to="/"
              className={location.pathname === '/' ? 'active' : ''}
            >
              Dashboard
            </Link>
          </li>
          <li>
            <Link
              to="/upload"
              className={location.pathname === '/upload' ? 'active' : ''}
            >
              Upload
            </Link>
          </li>
          <li>
            <Link
              to="/historico"
              className={location.pathname === '/historico' ? 'active' : ''}
            >
              Histórico
            </Link>
          </li>
          <li>
            <button onClick={handleLogout} className="logout-button">
              Sair
            </button>
          </li>
        </ul>
      </div>
    </nav>
  );
}

function ProtectedRoute({ children }: { children: React.ReactElement }) {
  const isAuthenticated = authService.isAuthenticated();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return (
    <>
      <Navigation />
      <main className="main-content">
        {children}
      </main>
    </>
  );
}

function App() {
  return (
    <ErrorBoundary>
    <Router>
      <div className="app">
          <Suspense fallback={
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
              <LoadingSpinner size="large" />
            </div>
          }>
          <Routes>
              <Route path="/login" element={<Login />} />
              <Route
                path="/"
                element={
                  <ProtectedRoute>
                    <Dashboard />
                  </ProtectedRoute>
                }
              />
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
              <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
          </Suspense>
      </div>
    </Router>
    </ErrorBoundary>
  );
}

export default App;
