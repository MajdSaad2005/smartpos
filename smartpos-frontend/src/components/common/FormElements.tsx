'use client';

import React from 'react';
import { FiAlertCircle, FiCheckCircle, FiInfo, FiX } from 'react-icons/fi';
import clsx from 'clsx';

interface AlertProps {
  type: 'success' | 'error' | 'warning' | 'info';
  title?: string;
  message: string;
  onClose?: () => void;
  dismissible?: boolean;
}

export const Alert: React.FC<AlertProps> = ({
  type,
  title,
  message,
  onClose,
  dismissible = true,
}) => {
  const baseClasses = 'p-4 rounded-lg flex items-start gap-3';
  const typeClasses = {
    success: 'bg-green-50 border border-green-200',
    error: 'bg-red-50 border border-red-200',
    warning: 'bg-yellow-50 border border-yellow-200',
    info: 'bg-blue-50 border border-blue-200',
  };

  const textClasses = {
    success: 'text-green-800',
    error: 'text-red-800',
    warning: 'text-yellow-800',
    info: 'text-blue-800',
  };

  const iconColor = {
    success: 'text-green-500',
    error: 'text-red-500',
    warning: 'text-yellow-500',
    info: 'text-blue-500',
  };

  const icons = {
    success: <FiCheckCircle className={`w-5 h-5 ${iconColor.success} flex-shrink-0`} />,
    error: <FiAlertCircle className={`w-5 h-5 ${iconColor.error} flex-shrink-0`} />,
    warning: <FiAlertCircle className={`w-5 h-5 ${iconColor.warning} flex-shrink-0`} />,
    info: <FiInfo className={`w-5 h-5 ${iconColor.info} flex-shrink-0`} />,
  };

  return (
    <div className={clsx(baseClasses, typeClasses[type])}>
      {icons[type]}
      <div className="flex-1">
        {title && <h3 className={clsx('font-semibold mb-1', textClasses[type])}>{title}</h3>}
        <p className={textClasses[type]}>{message}</p>
      </div>
      {dismissible && onClose && (
        <button onClick={onClose} className={clsx('flex-shrink-0', textClasses[type])}>
          <FiX className="w-5 h-5" />
        </button>
      )}
    </div>
  );
};

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
  loading?: boolean;
}

export const Button: React.FC<ButtonProps> = ({
  variant = 'primary',
  size = 'md',
  loading = false,
  children,
  className,
  ...props
}) => {
  const baseClasses = 'font-medium rounded-lg transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed';

  const variantClasses = {
    primary: 'bg-blue-600 text-white hover:bg-blue-700 focus:ring-blue-500',
    secondary: 'bg-gray-200 text-gray-900 hover:bg-gray-300 focus:ring-gray-500',
    danger: 'bg-red-600 text-white hover:bg-red-700 focus:ring-red-500',
    ghost: 'bg-transparent text-gray-700 hover:bg-gray-100 focus:ring-gray-500',
  };

  const sizeClasses = {
    sm: 'px-3 py-1.5 text-sm',
    md: 'px-4 py-2 text-base',
    lg: 'px-6 py-3 text-lg',
  };

  return (
    <button
      className={clsx(baseClasses, variantClasses[variant], sizeClasses[size], className)}
      disabled={loading || props.disabled}
      {...props}
    >
      {loading ? '...' : children}
    </button>
  );
};

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  helperText?: string;
}

export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, helperText, className, ...props }, ref) => {
    return (
      <div className="w-full">
        {label && <label className="block text-sm font-medium text-gray-700 mb-2">{label}</label>}
        <input
          ref={ref}
          className={clsx(
            'w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100 disabled:cursor-not-allowed',
            error ? 'border-red-500 focus:ring-red-500' : 'border-gray-300',
            className
          )}
          {...props}
        />
        {error && <p className="text-sm text-red-500 mt-1">{error}</p>}
        {helperText && <p className="text-sm text-gray-500 mt-1">{helperText}</p>}
      </div>
    );
  }
);

interface SelectProps extends React.SelectHTMLAttributes<HTMLSelectElement> {
  label?: string;
  error?: string;
  options: { value: string | number; label: string }[];
}

export const Select = React.forwardRef<HTMLSelectElement, SelectProps>(
  ({ label, error, options, className, ...props }, ref) => {
    return (
      <div className="w-full">
        {label && <label className="block text-sm font-medium text-gray-700 mb-2">{label}</label>}
        <select
          ref={ref}
          className={clsx(
            'w-full px-4 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:bg-gray-100 disabled:cursor-not-allowed',
            error ? 'border-red-500 focus:ring-red-500' : 'border-gray-300',
            className
          )}
          {...props}
        >
          {options.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
        {error && <p className="text-sm text-red-500 mt-1">{error}</p>}
      </div>
    );
  }
);

interface CardProps {
  children: React.ReactNode;
  className?: string;
  title?: string;
}

export const Card: React.FC<CardProps> = ({ children, className, title }) => {
  return (
    <div className={clsx('bg-white rounded-lg shadow-md p-6', className)}>
      {title && <h2 className="text-xl font-semibold mb-4 text-gray-900">{title}</h2>}
      {children}
    </div>
  );
};

interface BadgeProps {
  variant?: 'primary' | 'success' | 'warning' | 'danger' | 'gray';
  children: React.ReactNode;
}

export const Badge: React.FC<BadgeProps> = ({ variant = 'primary', children }) => {
  const variantClasses = {
    primary: 'bg-blue-100 text-blue-800',
    success: 'bg-green-100 text-green-800',
    warning: 'bg-yellow-100 text-yellow-800',
    danger: 'bg-red-100 text-red-800',
    gray: 'bg-gray-100 text-gray-800',
  };

  return <span className={clsx('px-2.5 py-0.5 rounded-full text-xs font-medium', variantClasses[variant])}>{children}</span>;
};

interface LoadingSpinnerProps {
  size?: 'sm' | 'md' | 'lg';
}

export const LoadingSpinner: React.FC<LoadingSpinnerProps> = ({ size = 'md' }) => {
  const sizeClasses = {
    sm: 'w-4 h-4',
    md: 'w-8 h-8',
    lg: 'w-12 h-12',
  };

  return (
    <div className="flex items-center justify-center">
      <div className={clsx('animate-spin rounded-full border-b-2 border-blue-500', sizeClasses[size])}></div>
    </div>
  );
};
