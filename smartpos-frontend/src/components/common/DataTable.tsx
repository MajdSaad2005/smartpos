'use client';

import React from 'react';
import { FiSearch, FiPlus } from 'react-icons/fi';
import { Button, Input } from '@/components/common/FormElements';

interface TableColumn<T> {
  header: string;
  accessor: keyof T | ((row: T) => React.ReactNode);
  width?: string;
}

interface DataTableProps<T> {
  columns: TableColumn<T>[];
  data: T[];
  onRowClick?: (row: T) => void;
  onAdd?: () => void;
  searchPlaceholder?: string;
  onSearch?: (term: string) => void;
  loading?: boolean;
}

export function DataTable<T extends { id: number }>({
  columns,
  data,
  onRowClick,
  onAdd,
  searchPlaceholder = 'Search...',
  onSearch,
  loading,
}: DataTableProps<T>) {
  const [searchTerm, setSearchTerm] = React.useState('');

  const handleSearch = (term: string) => {
    setSearchTerm(term);
    onSearch?.(term);
  };

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center flex-col sm:flex-row gap-4">
        <div className="w-full sm:w-64">
          <Input
            placeholder={searchPlaceholder}
            value={searchTerm}
            onChange={(e) => handleSearch(e.target.value)}
            className="pl-10"
            prefix={<FiSearch className="absolute left-3 top-3 text-gray-400" />}
          />
        </div>
        {onAdd && (
          <Button onClick={onAdd} variant="primary" size="md">
            <FiPlus className="inline mr-2" /> Add New
          </Button>
        )}
      </div>

      <div className="overflow-x-auto">
        <table className="w-full bg-white rounded-lg shadow-sm">
          <thead>
            <tr className="border-b border-gray-200 bg-gray-50">
              {columns.map((col, idx) => (
                <th key={idx} className="px-6 py-3 text-left text-sm font-semibold text-gray-700">
                  {col.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={columns.length} className="px-6 py-8 text-center text-gray-500">
                  Loading...
                </td>
              </tr>
            ) : data.length === 0 ? (
              <tr>
                <td colSpan={columns.length} className="px-6 py-8 text-center text-gray-500">
                  No data found
                </td>
              </tr>
            ) : (
              data.map((row) => (
                <tr
                  key={row.id}
                  className="border-b border-gray-200 hover:bg-gray-50 transition-colors cursor-pointer"
                  onClick={() => onRowClick?.(row)}
                >
                  {columns.map((col, idx) => (
                    <td key={idx} className="px-6 py-4 text-sm text-gray-700">
                      {typeof col.accessor === 'function' ? col.accessor(row) : String(row[col.accessor])}
                    </td>
                  ))}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}
