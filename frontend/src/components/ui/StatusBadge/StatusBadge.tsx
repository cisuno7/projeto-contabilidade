import React from 'react';
import { StatusPlanilha } from '../../../types';
import './StatusBadge.css';

interface StatusBadgeProps {
  status: StatusPlanilha;
  className?: string;
}

export const StatusBadge = React.memo<StatusBadgeProps>(({ status, className = '' }) => {
  const getStatusConfig = (status: StatusPlanilha) => {
    switch (status) {
      case StatusPlanilha.CONCLUIDA:
        return {
          label: 'Concluída',
          class: 'status-badge--success',
          icon: (
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M11.375 3.5L5.25 9.625L2.625 7" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
            </svg>
          ),
        };
      case StatusPlanilha.PROCESSADA:
        return {
          label: 'Processada',
          class: 'status-badge--info',
          icon: (
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M7 3.5V7L9.33333 9.33333" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
              <circle cx="7" cy="7" r="6" stroke="currentColor" strokeWidth="2"/>
            </svg>
          ),
        };
      case StatusPlanilha.PROCESSANDO:
        return {
          label: 'Processando',
          class: 'status-badge--warning',
          icon: (
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="7" cy="7" r="6" stroke="currentColor" strokeWidth="2"/>
              <path d="M7 3.5V7L9.33333 9.33333" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            </svg>
          ),
        };
      case StatusPlanilha.ERRO:
        return {
          label: 'Erro',
          class: 'status-badge--error',
          icon: (
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M7 3.5V7M7 10.5H7.00833" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
              <circle cx="7" cy="7" r="6" stroke="currentColor" strokeWidth="2"/>
            </svg>
          ),
        };
      case StatusPlanilha.UPLOADED:
      default:
        return {
          label: 'Enviada',
          class: 'status-badge--default',
          icon: (
            <svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M7 9.33333V3.5M7 3.5L4.66667 5.83333M7 3.5L9.33333 5.83333" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"/>
              <path d="M3.5 9.33333H10.5" stroke="currentColor" strokeWidth="2" strokeLinecap="round"/>
            </svg>
          ),
        };
    }
  };

  const config = getStatusConfig(status);

  return (
    <span className={`status-badge ${config.class} ${className}`}>
      {config.icon}
      <span>{config.label}</span>
    </span>
  );
});

StatusBadge.displayName = 'StatusBadge';
