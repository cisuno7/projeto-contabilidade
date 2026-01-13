import React from 'react';
import './EmptyState.css';

interface EmptyStateProps {
  title: string;
  message?: string;
  icon?: React.ReactNode;
  action?: React.ReactNode;
}

export const EmptyState = React.memo<EmptyStateProps>(({ 
  title, 
  message, 
  icon,
  action 
}) => {
  const defaultIcon = (
    <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
      <path d="M32 16V32L42.6667 42.6667" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
      <circle cx="32" cy="32" r="28" stroke="currentColor" strokeWidth="2"/>
    </svg>
  );

  return (
    <div className="empty-state">
      <div className="empty-state-icon">
        {icon || defaultIcon}
      </div>
      <h3 className="empty-state-title">{title}</h3>
      {message && <p className="empty-state-message">{message}</p>}
      {action && <div className="empty-state-action">{action}</div>}
    </div>
  );
});

EmptyState.displayName = 'EmptyState';
