import React, { forwardRef, useId } from 'react';
import './Input.css';

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
  icon?: React.ReactNode;
  fullWidth?: boolean;
}

export const Input = React.memo(forwardRef<HTMLInputElement, InputProps>(({ 
  label,
  error,
  helperText,
  icon,
  fullWidth = false,
  className = '',
  id,
  ...props
}, ref) => {
  const generatedId = useId();
  const inputId = id || generatedId;
  const hasError = !!error;
  const inputClasses = `
    input
    ${icon ? 'input--with-icon' : ''}
    ${hasError ? 'input--error' : ''}
    ${fullWidth ? 'input--full-width' : ''}
    ${className}
  `.trim();

  return (
    <div className={`input-group ${fullWidth ? 'input-group--full-width' : ''}`}>
      {label && (
        <label htmlFor={inputId} className="input-label">
          {label}
        </label>
      )}
      <div className="input-wrapper">
        {icon && <div className="input-icon">{icon}</div>}
        <input
          ref={ref}
          id={inputId}
          className={inputClasses}
          aria-invalid={hasError}
          aria-describedby={error ? `${inputId}-error` : helperText ? `${inputId}-helper` : undefined}
          {...props}
        />
      </div>
      {error && (
        <span id={`${inputId}-error`} className="input-error" role="alert">
          {error}
        </span>
      )}
      {helperText && !error && (
        <span id={`${inputId}-helper`} className="input-helper">
          {helperText}
        </span>
      )}
    </div>
  );
}));

Input.displayName = 'Input';
