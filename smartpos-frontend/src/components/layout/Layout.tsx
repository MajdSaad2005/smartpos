'use client';

import React, { useState } from 'react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { FiMenu, FiX, FiDollarSign, FiBox, FiUsers, FiShoppingCart, FiBarChart2, FiSettings, FiPackage, FiTrendingUp, FiTag, FiShoppingBag } from 'react-icons/fi';
import clsx from 'clsx';

const menuItems = [
  { href: '/', label: 'Dashboard', icon: FiBarChart2 },
  { href: '/cash', label: 'Cash', icon: FiDollarSign },
  { href: '/sales', label: 'Sales', icon: FiShoppingCart },
  { href: '/products', label: 'Products', icon: FiBox },
  { href: '/inventory', label: 'Inventory', icon: FiPackage },
  { href: '/purchase-orders', label: 'Purchase Orders', icon: FiShoppingBag },
  { href: '/customers', label: 'Customers', icon: FiUsers },
  { href: '/suppliers', label: 'Suppliers', icon: FiUsers },
  { href: '/coupons', label: 'Coupons', icon: FiTag },
  { href: '/discounts', label: 'Discounts', icon: FiTag },
  { href: '/reports', label: 'Reports', icon: FiTrendingUp },
  { href: '/settings', label: 'Settings', icon: FiSettings },
];

export const Sidebar: React.FC = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [isMobile, setIsMobile] = useState(false);
  const pathname = usePathname();

  React.useEffect(() => {
    const checkMobile = () => {
      setIsMobile(window.innerWidth < 768);
    };
    
    checkMobile();
    window.addEventListener('resize', checkMobile);
    
    return () => window.removeEventListener('resize', checkMobile);
  }, []);

  const handleLinkClick = () => {
    // Only close sidebar on mobile screens
    if (isMobile) {
      setIsOpen(false);
    }
  };

  return (
    <>
      {/* Mobile Menu Button */}
      <button
        className="fixed top-4 left-4 z-50 p-2 bg-blue-600 text-white rounded-lg md:hidden"
        onClick={() => setIsOpen(!isOpen)}
      >
        {isOpen ? <FiX size={24} /> : <FiMenu size={24} />}
      </button>

      {/* Sidebar */}
      <div
        className={clsx(
          'fixed left-0 top-0 h-screen w-64 bg-gradient-to-b from-gray-900 via-gray-800 to-gray-900 text-white p-6 transform transition-transform duration-300 z-40 md:translate-x-0 shadow-2xl',
          isOpen ? 'translate-x-0' : '-translate-x-full'
        )}
      >
        <div className="mb-8">
          <h1 className="text-2xl font-bold flex items-center gap-2">
            <FiDollarSign className="text-green-400" /> SmartPOS
          </h1>
        </div>

        <nav className="space-y-2">
          {menuItems.map((item) => {
            const Icon = item.icon;
            const isActive = pathname === item.href;
            return (
              <Link
                key={item.href}
                href={item.href}
                className={clsx(
                  'flex items-center gap-3 px-4 py-3 rounded-lg transition-all duration-200',
                  isActive ? 'bg-blue-600 text-white shadow-lg scale-[1.02]' : 'text-gray-300 hover:bg-gray-800 hover:translate-x-1'
                )}
                onClick={handleLinkClick}
              >
                <Icon size={20} className={clsx(isActive ? 'animate-pulse' : '')} />
                <span className="font-medium tracking-wide">{item.label}</span>
              </Link>
            );
          })}
        </nav>
      </div>

      {/* Backdrop */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-black bg-opacity-50 z-30 md:hidden"
          onClick={() => setIsOpen(false)}
        />
      )}
    </>
  );
};

interface HeaderProps {
  title: string;
  subtitle?: string;
  actions?: React.ReactNode;
}

export const Header: React.FC<HeaderProps> = ({ title, subtitle, actions }) => {
  return (
    <div className="bg-white shadow-sm border-b border-gray-200 px-6 py-4">
      <div className="flex justify-between items-start">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">{title}</h1>
          {subtitle && <p className="text-gray-600 mt-1">{subtitle}</p>}
        </div>
        {actions && <div className="flex gap-2">{actions}</div>}
      </div>
    </div>
  );
};

interface ContentProps {
  children: React.ReactNode;
}

export const Content: React.FC<ContentProps> = ({ children }) => {
  return <main className="ml-0 md:ml-64 p-4 md:p-6">{children}</main>;
};

interface LayoutProps {
  children: React.ReactNode;
}

export const Layout: React.FC<LayoutProps> = ({ children }) => {
  return (
    <div className="min-h-screen bg-gray-50">
      <Sidebar />
      {children}
    </div>
  );
};
