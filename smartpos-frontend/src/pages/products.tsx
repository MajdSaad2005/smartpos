'use client';

import React, { useEffect, useState } from 'react';
import { Layout, Header, Content } from '@/components/layout/Layout';
import { DataTable } from '@/components/common/DataTable';
import { Card, Button, Input, Select, LoadingSpinner, Alert } from '@/components/common/FormElements';
import { Product, Supplier } from '@/types/api';
import { productAPI, supplierAPI } from '@/lib/api';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';
import { formatCurrency } from '@/lib/utils';

export default function Products() {
  const [products, setProducts] = useState<Product[]>([]);
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const { register, handleSubmit, reset, watch, formState: { errors } } = useForm();

  useEffect(() => {
    fetchProducts();
    fetchSuppliers();
  }, []);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      const { data } = await productAPI.getAll();
      setProducts(data);
    } catch (error) {
      toast.error('Failed to fetch products');
    } finally {
      setLoading(false);
    }
  };

  const fetchSuppliers = async () => {
    try {
      const { data } = await supplierAPI.getActive();
      setSuppliers(data);
    } catch (error) {
      toast.error('Failed to fetch suppliers');
    }
  };

  const onSubmit = async (data: any) => {
    console.log('Form submitted with data:', data);
    try {
      // Remove active field - backend doesn't expect it
      const { active, ...productData } = data;
      if (editingId) {
        await productAPI.update(editingId, productData);
        toast.success('Product updated successfully');
      } else {
        await productAPI.create(productData);
        toast.success('Product created successfully');
      }
      setShowModal(false);
      reset();
      setEditingId(null);
      fetchProducts();
    } catch (error: any) {
      console.error('Error saving product:', error);
      toast.error(error.response?.data?.message || 'Failed to save product');
    }
  };

  const handleDelete = async (id: number) => {
    if (confirm('Are you sure you want to delete this product?')) {
      try {
        await productAPI.delete(id);
        toast.success('Product deleted successfully');
        fetchProducts();
      } catch (error) {
        toast.error('Failed to delete product');
      }
    }
  };

  const handleEdit = (product: Product) => {
    reset(product);
    setEditingId(product.id);
    setShowModal(true);
  };

  const columns = [
    { header: 'Code', accessor: 'code' as const },
    { header: 'Name', accessor: 'name' as const },
    { header: 'Supplier', accessor: 'supplierName' as const },
    { header: 'Sale Price', accessor: (row: Product) => formatCurrency(row.salePrice) },
    { header: 'Stock', accessor: 'currentStock' as const },
    { header: 'Status', accessor: (row: Product) => row.active ? '✓ Active' : '✗ Inactive' },
  ];

  return (
    <Layout>
      <Header title="Products" subtitle="Manage your product inventory" actions={<Button onClick={() => setShowModal(true)}>Add Product</Button>} />
      <Content>
        {showModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <Card className="w-full max-w-md">
              <h3 className="text-lg font-semibold mb-4">{editingId ? 'Edit Product' : 'Add Product'}</h3>
              <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                <Input label="Code" {...register('code', { required: true })} />
                <Input label="Name" {...register('name', { required: true })} />
                <Input label="Description" {...register('description')} />
                <Input label="Purchase Price" type="number" step="0.01" {...register('purchasePrice', { required: true })} />
                <Input label="Sale Price" type="number" step="0.01" {...register('salePrice', { required: true })} />
                <Input label="Tax %" type="number" step="0.01" {...register('taxPercentage')} />
                <Select
                  label="Supplier"
                  {...register('supplierId', { required: true })}
                  options={suppliers.map((s) => ({ value: s.id, label: s.name }))}
                />
                <div className="flex gap-2 justify-end">
                  <Button type="button" variant="ghost" onClick={() => { setShowModal(false); setEditingId(null); reset(); }}>
                    Cancel
                  </Button>
                  <Button type="submit" variant="primary">
                    Save
                  </Button>
                </div>
              </form>
            </Card>
          </div>
        )}

        {loading ? (
          <div className="flex justify-center items-center h-64">
            <LoadingSpinner />
          </div>
        ) : (
          <Card>
            <DataTable
              columns={columns}
              data={products}
              onRowClick={(product) => handleEdit(product)}
              searchPlaceholder="Search products..."
              loading={loading}
            />
          </Card>
        )}
      </Content>
    </Layout>
  );
}
