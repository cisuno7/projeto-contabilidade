import React from 'react';
import './Button.css';

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost';
  size?: 'small' | 'medium' | 'large';
  isLoading?: boolean;
  fullWidth?: boolean;
  children: React.ReactNode;
}

export const Button = React.memo<ButtonProps>(({ 
  variant = 'primary',
  size = 'medium',
  isLoading = false,
  fullWidth = false,
  className = '',
  disabled,
  children,
  ...props
}) => {
  const baseClass = 'button';
  const variantClass = `button--${variant}`;
  const sizeClass = `button--${size}`;
  const widthClass = fullWidth ? 'button--full-width' : '';
  const loadingClass = isLoading ? 'button--loading' : '';
  
  const classes = `${baseClass} ${variantClass} ${sizeClass} ${widthClass} ${loadingClass} ${className}`.trim();

  return (
    <button
      className={classes}
      disabled={disabled || isLoading}
      {...props}
    >
      {isLoading && (
        <svg className="button-spinner" width="16" height="16" viewBox="0 0 16 16" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="8" cy="8" r="6" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeDasharray="31.416" strokeDashoffset="31.416">
            <animate attributeName="stroke-dasharray" dur="2s" values="0 31.416;15.708 15.708;0 31.416;0 31.416" repeatCount="indefinite"/>
            <animate attributeName="stroke-dashoffset" dur="2s" values="0;-15.708;-31.416;-31.416" repeatCount="indefinite"/>
          </circle>
        </svg>
      )}
      {children}
    </button>
  );
});

Button.displayName = 'Button';
