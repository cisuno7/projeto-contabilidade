import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { lazy, Suspense } from 'react';
import { authService } from './services/auth';
import { LoadingSpinner } from './components/ui/LoadingSpinner/LoadingSpinner';
import { ErrorBoundary } from './components/ErrorBoundary/ErrorBoundary';
import Navbar from './components/layout/Navbar/Navbar';
import './App.css';

// Lazy loading das páginas
const Dashboard = lazy(() => import('./pages/Dashboard/Dashboard'));
const Upload = lazy(() => import('./pages/Upload/Upload'));
const Historico = lazy(() => import('./pages/Historico/Historico'));
const Login = lazy(() => import('./pages/Login/Login'));
const Register = lazy(() => import('./pages/Register/Register'));

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
    <Router>
      <div className="app">
          <Suspense fallback={
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
              <LoadingSpinner size="large" />
            </div>
          }>
          <Routes>
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
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
