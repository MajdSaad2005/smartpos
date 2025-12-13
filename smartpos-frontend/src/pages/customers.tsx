'use client';

import React, { useEffect, useState } from 'react';
import { Layout, Header, Content } from '@/components/layout/Layout';
import { DataTable } from '@/components/common/DataTable';
import { Card, Button, Input, LoadingSpinner } from '@/components/common/FormElements';
import { Customer } from '@/types/api';
import { customerAPI } from '@/lib/api';
import { useForm } from 'react-hook-form';
import toast from 'react-hot-toast';

export default function Customers() {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const { register, handleSubmit, reset, formState: { errors } } = useForm();

  useEffect(() => {
    fetchCustomers();
  }, []);

  const fetchCustomers = async () => {
    try {
      setLoading(true);
      const { data } = await customerAPI.getAll();
      setCustomers(data);
    } catch (error) {
      toast.error('Failed to fetch customers');
    } finally {
      setLoading(false);
    }
  };

  const onSubmit = async (data: any) => {
    console.log('Form submitted with data:', data);
    try {
      // Remove active field - backend doesn't expect it
      const { active, ...customerData } = data;
      if (editingId) {
        await customerAPI.update(editingId, customerData);
        toast.success('Customer updated successfully');
      } else {
        await customerAPI.create(customerData);
        toast.success('Customer created successfully');
      }
      setShowModal(false);
      reset();
      setEditingId(null);
      fetchCustomers();
    } catch (error: any) {
      console.error('Error saving customer:', error);
      toast.error(error.response?.data?.message || 'Failed to save customer');
    }
  };

  const handleDelete = async (id: number) => {
    if (confirm('Are you sure you want to delete this customer?')) {
      try {
        await customerAPI.delete(id);
        toast.success('Customer deleted successfully');
        fetchCustomers();
      } catch (error) {
        toast.error('Failed to delete customer');
      }
    }
  };

  const handleEdit = (customer: Customer) => {
    reset(customer);
    setEditingId(customer.id);
    setShowModal(true);
  };

  const columns = [
    { header: 'Code', accessor: 'code' as const },
    { header: 'Name', accessor: (row: Customer) => `${row.firstName} ${row.lastName}` },
    { header: 'Email', accessor: 'email' as const },
    { header: 'Phone', accessor: 'phone' as const },
    { header: 'Status', accessor: (row: Customer) => row.active ? '✓ Active' : '✗ Inactive' },
  ];

  return (
    <Layout>
      <Header title="Customers" subtitle="Manage your customer database" actions={<Button onClick={() => setShowModal(true)}>Add Customer</Button>} />
      <Content>
        {showModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <Card className="w-full max-w-md">
              <h3 className="text-lg font-semibold mb-4">{editingId ? 'Edit Customer' : 'Add Customer'}</h3>
              <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                <Input label="Code" {...register('code', { required: true })} />
                <Input label="First Name" {...register('firstName', { required: true })} />
                <Input label="Last Name" {...register('lastName', { required: true })} />
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
              data={customers}
              onRowClick={(customer) => handleEdit(customer)}
              searchPlaceholder="Search customers..."
              loading={loading}
            />
          </Card>
        )}
      </Content>
    </Layout>
  );
}
