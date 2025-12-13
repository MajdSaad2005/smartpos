'use client';

import React, { useState, useEffect } from 'react';
import { useForm } from 'react-hook-form';
import { Layout, Header, Content } from '@/components/layout/Layout';
import { DataTable } from '@/components/common/DataTable';
import { Card, Button, Input, Select } from '@/components/common/FormElements';
import { Product, CreateTicketLineRequest } from '@/types/api';
import { productAPI } from '@/lib/api';
import toast from 'react-hot-toast';
import { ticketAPI } from '@/lib/api';

interface StockAdjustmentForm {
  productId: string;
  quantity: number;
  notes: string;
}

export default function Inventory() {
  const [products, setProducts] = useState<Product[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(false);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<StockAdjustmentForm>();

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      const response = await productAPI.getAll();
      setProducts(response.data);
    } catch (error) {
      toast.error('Failed to load products');
    }
  };

  const onSubmit = async (data: StockAdjustmentForm) => {
    if (!data.productId || data.quantity <= 0) {
      toast.error('Please select a product and enter a valid quantity');
      return;
    }

    setLoading(true);
    try {
      const product = products.find(p => p.id === parseInt(data.productId));
      if (!product) {
        toast.error('Product not found');
        return;
      }

      // Create a RETURN ticket to increase stock
      const ticketData = {
        type: 'RETURN',
        customerId: null,
        lines: [
          {
            productId: parseInt(data.productId),
            quantity: data.quantity,
          },
        ] as CreateTicketLineRequest[],
      };

      await ticketAPI.create(ticketData as any);
      toast.success(`Added ${data.quantity} units to ${product.name}`);
      setShowModal(false);
      reset();
      fetchProducts();
    } catch (error: any) {
      console.error('Stock adjustment error:', error);
      toast.error(error.response?.data?.message || 'Failed to adjust stock');
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    { header: 'Code', accessor: (row: Product) => row.code },
    { header: 'Name', accessor: (row: Product) => row.name },
    { header: 'Current Stock', accessor: (row: Product) => row.currentStock },
    { header: 'Sale Price', accessor: (row: Product) => `$${row.salePrice.toFixed(2)}` },
  ];

  return (
    <Layout>
      <Header title="Inventory" subtitle="Manage stock levels" actions={<Button onClick={() => setShowModal(true)}>Add Stock</Button>} />
      <Content>
        {showModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <Card className="w-full max-w-md">
              <h3 className="text-lg font-semibold mb-4">Add Stock</h3>
              <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                <Select
                  label="Product"
                  {...register('productId', { required: 'Product is required' })}
                  error={errors.productId?.message as string}
                  options={[
                    { value: '', label: 'Select a product...' },
                    ...products.map(p => ({
                      value: p.id.toString(),
                      label: `${p.name} (Stock: ${p.currentStock})`,
                    })),
                  ]}
                />

                <Input
                  label="Quantity to Add"
                  type="number"
                  {...register('quantity', {
                    required: 'Quantity is required',
                    min: { value: 1, message: 'Quantity must be at least 1' },
                  })}
                  error={errors.quantity?.message as string}
                />

                <Input
                  label="Notes (Optional)"
                  {...register('notes')}
                  placeholder="Reason for stock adjustment"
                />

                <div className="flex gap-2 justify-end">
                  <Button type="button" variant="ghost" onClick={() => { setShowModal(false); reset(); }}>
                    Cancel
                  </Button>
                  <Button type="submit" disabled={loading} variant="primary">
                    {loading ? 'Adding...' : 'Add Stock'}
                  </Button>
                </div>
              </form>
            </Card>
          </div>
        )}

        <Card>
          <DataTable columns={columns} data={products} searchPlaceholder="Search products..." />
        </Card>
      </Content>
    </Layout>
  );
}
