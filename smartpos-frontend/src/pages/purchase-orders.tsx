'use client';

import React, { useEffect, useState } from 'react';
import { Layout, Header, Content } from '@/components/layout/Layout';
import { Card, LoadingSpinner, Select } from '@/components/common/FormElements';
import { PurchaseOrder, Supplier } from '@/types/api';
import { purchaseOrderAPI, supplierAPI } from '@/lib/api';
import toast from 'react-hot-toast';
import { formatCurrency } from '@/lib/utils';
import { FiPackage } from 'react-icons/fi';

export default function PurchaseOrders() {
  const [purchaseOrders, setPurchaseOrders] = useState<PurchaseOrder[]>([]);
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [loading, setLoading] = useState(false);
  const [filterSupplier, setFilterSupplier] = useState<string>('');
  const [filterStatus, setFilterStatus] = useState<string>('');

  useEffect(() => {
    fetchPurchaseOrders();
    fetchSuppliers();
  }, []);

  const fetchPurchaseOrders = async () => {
    try {
      setLoading(true);
      const { data } = await purchaseOrderAPI.getAll();
      setPurchaseOrders(data.sort((a, b) => new Date(b.orderDate).getTime() - new Date(a.orderDate).getTime()));
    } catch (error) {
      toast.error('Failed to fetch purchase orders');
    } finally {
      setLoading(false);
    }
  };

  const fetchSuppliers = async () => {
    try {
      const { data } = await supplierAPI.getAll();
      setSuppliers(data);
    } catch (error) {
      toast.error('Failed to fetch suppliers');
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PENDING': return 'bg-yellow-100 text-yellow-800';
      case 'ORDERED': return 'bg-blue-100 text-blue-800';
      case 'RECEIVED': return 'bg-green-100 text-green-800';
      case 'CANCELLED': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const filteredOrders = purchaseOrders.filter(po => {
    if (filterSupplier && po.supplierId.toString() !== filterSupplier) return false;
    if (filterStatus && po.status !== filterStatus) return false;
    return true;
  });

  if (loading && purchaseOrders.length === 0) {
    return (
      <Layout>
        <Header title="Purchase Order History" />
        <Content>
          <div className="flex justify-center items-center h-64">
            <LoadingSpinner />
          </div>
        </Content>
      </Layout>
    );
  }

  return (
    <Layout>
      <Header
        title="Purchase Order History"
        subtitle="View all inventory purchases from suppliers"
      />
      <Content>
        <Card className="mb-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <Select
              label="Filter by Supplier"
              value={filterSupplier}
              onChange={(e) => setFilterSupplier(e.target.value)}
              options={[
                { value: '', label: 'All Suppliers' },
                ...suppliers.map(s => ({ value: s.id.toString(), label: s.name }))
              ]}
            />
            <Select
              label="Filter by Status"
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
              options={[
                { value: '', label: 'All Statuses' },
                { value: 'PENDING', label: 'Pending' },
                { value: 'ORDERED', label: 'Ordered' },
                { value: 'RECEIVED', label: 'Received' },
                { value: 'CANCELLED', label: 'Cancelled' },
              ]}
            />
          </div>
        </Card>

        <div className="space-y-4">
          {filteredOrders.map(po => (
            <Card key={po.id}>
              <div className="flex justify-between items-start mb-4">
                <div>
                  <h3 className="text-lg font-semibold flex items-center gap-2">
                    <FiPackage />
                    {po.orderNumber}
                  </h3>
                  <p className="text-sm text-gray-600">{po.supplierName}</p>
                </div>
                <span className={`px-3 py-1 rounded-full text-xs font-semibold ${getStatusColor(po.status)}`}>
                  {po.status}
                </span>
              </div>

              <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4 text-sm">
                <div>
                  <p className="text-gray-600">Order Date</p>
                  <p className="font-semibold">{new Date(po.orderDate).toLocaleDateString()}</p>
                </div>
                {po.expectedDeliveryDate && (
                  <div>
                    <p className="text-gray-600">Expected</p>
                    <p className="font-semibold">{new Date(po.expectedDeliveryDate).toLocaleDateString()}</p>
                  </div>
                )}
                {po.receivedDate && (
                  <div>
                    <p className="text-gray-600">Received</p>
                    <p className="font-semibold text-green-600">{new Date(po.receivedDate).toLocaleDateString()}</p>
                  </div>
                )}
                <div>
                  <p className="text-gray-600">Total</p>
                  <p className="font-semibold text-green-600">{formatCurrency(po.total)}</p>
                </div>
              </div>

              <div className="border-t pt-3 mb-3">
                <p className="text-sm font-semibold mb-2">Items Purchased:</p>
                <div className="space-y-1">
                  {po.lines.map(line => (
                    <div key={line.id} className="text-sm flex justify-between items-center p-2 bg-gray-50 rounded">
                      <div>
                        <span className="font-medium">{line.productName}</span>
                        <span className="text-gray-600 ml-2">({line.productCode})</span>
                      </div>
                      <div className="text-right">
                        <div className="font-semibold">{line.quantityOrdered} units × {formatCurrency(line.unitCost)}</div>
                        <div className="text-gray-600 text-xs">= {formatCurrency(line.lineTotal)}</div>
                      </div>
                    </div>
                  ))}
                </div>
                <div className="mt-2 pt-2 border-t flex justify-between items-center">
                  <span className="font-semibold">Subtotal:</span>
                  <span>{formatCurrency(po.subtotal)}</span>
                </div>
                {po.taxAmount > 0 && (
                  <div className="flex justify-between items-center text-sm text-gray-600">
                    <span>Tax:</span>
                    <span>{formatCurrency(po.taxAmount)}</span>
                  </div>
                )}
                <div className="flex justify-between items-center font-bold text-lg">
                  <span>Total:</span>
                  <span className="text-green-600">{formatCurrency(po.total)}</span>
                </div>
              </div>

              {po.notes && (
                <div className="text-sm text-gray-600 bg-blue-50 p-2 rounded">
                  <span className="font-semibold">Notes:</span> {po.notes}
                </div>
              )}
            </Card>
          ))}

          {filteredOrders.length === 0 && !loading && (
            <div className="text-center py-12 text-gray-500">
              <FiPackage className="mx-auto text-4xl mb-2" />
              <p>No purchase orders found.</p>
              <p className="text-sm mt-1">Add stock from the Inventory page to create purchase orders.</p>
            </div>
          )}
        </div>
      </Content>
    </Layout>
  );
}
