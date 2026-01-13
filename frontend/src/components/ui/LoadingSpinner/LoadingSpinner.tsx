import React from 'react';
import './LoadingSpinner.css';

interface LoadingSpinnerProps {
  size?: 'small' | 'medium' | 'large';
  message?: string;
}

export const LoadingSpinner = React.memo<LoadingSpinnerProps>(({ 
  size = 'medium',
  message = 'Carregando...' 
}) => {
  return (
    <div className={`loading-spinner-container loading-spinner--${size}`}>
      <div className="loading-spinner">
        <div className="spinner-ring"></div>
        <div className="spinner-ring"></div>
        <div className="spinner-ring"></div>
      </div>
      {message && <p className="loading-spinner-message">{message}</p>}
    </div>
  );
});

LoadingSpinner.displayName = 'LoadingSpinner';
