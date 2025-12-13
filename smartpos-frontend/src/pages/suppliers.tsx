'use client';

import React, { useEffect, useState } from 'react';
import { Layout, Header, Content } from '@/components/layout/Layout';
import { DataTable } from '@/components/common/DataTable';
import { Card, Button, Input, LoadingSpinner } from '@/components/common/FormElements';
import { Supplier } from '@/types/api';
import { supplierAPI } from '@/lib/api';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';

export default function Suppliers() {
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const { register, handleSubmit, reset, formState: { errors } } = useForm();

  useEffect(() => {
    fetchSuppliers();
  }, []);

  const fetchSuppliers = async () => {
    try {
      setLoading(true);
      const { data } = await supplierAPI.getAll();
      setSuppliers(data);
    } catch (error) {
      toast.error('Failed to fetch suppliers');
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = async (data: any) => {
    console.log('Form submitted with data:', data);
    try {
      // Remove active field - backend doesn't expect it in CreateSupplierRequest
      const { active, ...supplierData } = data;
      if (editingId) {
        await supplierAPI.update(editingId, supplierData);
        toast.success('Supplier updated successfully');
      } else {
        await supplierAPI.create(supplierData);
        toast.success('Supplier created successfully');
      }
      setShowModal(false);
      reset();
      setEditingId(null);
      fetchSuppliers();
    } catch (error: any) {
      console.error('Error saving supplier:', error);
      toast.error(error.response?.data?.message || 'Failed to save supplier');
    }
  };

  const handleDelete = async (id: number) => {
    if (confirm('Are you sure you want to delete this supplier?')) {
      try {
        await supplierAPI.delete(id);
        toast.success('Supplier deleted successfully');
        fetchSuppliers();
      } catch (error) {
        toast.error('Failed to delete supplier');
      }
    }
  };

  const handleEdit = (supplier: Supplier) => {
    reset(supplier);
    setEditingId(supplier.id);
    setShowModal(true);
  };

  const openNewModal = () => {
    reset({ code: '', name: '', email: '', phone: '', address: '', taxId: '' });
    setEditingId(null);
    setShowModal(true);
  };

  const columns = [
    { header: 'Code', accessor: 'code' as const },
    { header: 'Name', accessor: 'name' as const },
    { header: 'Email', accessor: 'email' as const },
    { header: 'Phone', accessor: 'phone' as const },
    { header: 'Status', accessor: (row: Supplier) => row.active ? '✓ Active' : '✗ Inactive' },
  ];

  return (
    <Layout>
      <Header title="Suppliers" subtitle="Manage your supplier information" actions={<Button onClick={openNewModal}>Add Supplier</Button>} />
      <Content>
        {showModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <Card className="w-full max-w-md">
              <h3 className="text-lg font-semibold mb-4">{editingId ? 'Edit Supplier' : 'Add Supplier'}</h3>
              <form onSubmit={handleSubmit(onSubmit, (errors) => console.log('Validation errors:', errors))} className="space-y-4">
                <Input label="Code" {...register('code', { required: 'Code is required' })} error={errors.code?.message as string} />
                <Input label="Name" {...register('name', { required: 'Name is required' })} error={errors.name?.message as string} />
                <Input label="Email" type="email" {...register('email')} />
                <Input label="Phone" {...register('phone')} />
                <Input label="Address" {...register('address')} />
                <Input label="Tax ID" {...register('taxId')} />
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
              data={suppliers}
              onRowClick={(supplier) => handleEdit(supplier)}
              searchPlaceholder="Search suppliers..."
              loading={loading}
            />
          </Card>
        )}
      </Content>
    </Layout>
  );
}
