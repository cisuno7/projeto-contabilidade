import React from 'react';
import './StatCard.css';

interface StatCardProps {
  title: string;
  value: string | number;
  icon?: React.ReactNode;
  trend?: {
    value: number;
    isPositive: boolean;
  };
  color?: 'primary' | 'success' | 'warning' | 'info';
}

export const StatCard = React.memo<StatCardProps>(({ 
  title, 
  value, 
  icon, 
  trend,
  color = 'primary' 
}) => {
  return (
    <div className={`stat-card stat-card--${color}`}>
      <div className="stat-card-header">
        <div className="stat-card-icon">{icon}</div>
        <h3 className="stat-card-title">{title}</h3>
      </div>
      <div className="stat-card-body">
        <p className="stat-card-value">{value}</p>
        {trend && (
          <div className={`stat-card-trend ${trend.isPositive ? 'trend-positive' : 'trend-negative'}`}>
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
              {trend.isPositive ? (
                <path d="M8 4L12 8H10V12H6V8H4L8 4Z" fill="currentColor"/>
              ) : (
                <path d="M8 12L4 8H6V4H10V8H12L8 12Z" fill="currentColor"/>
              )}
            </svg>
            <span>{Math.abs(trend.value)}%</span>
          </div>
        )}
      </div>
    </div>
  );
});

StatCard.displayName = 'StatCard';
