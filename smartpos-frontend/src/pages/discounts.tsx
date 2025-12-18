'use client';

import { useState, useEffect } from 'react';
import { discountAPI, productAPI } from '@/lib/api';
import { Discount, CreateDiscountRequest, DiscountType, ApplicableOn, Product } from '@/types/api';

export default function DiscountsPage() {
  const [discounts, setDiscounts] = useState<Discount[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [showDialog, setShowDialog] = useState(false);
  const [editingDiscount, setEditingDiscount] = useState<Discount | null>(null);
  const [formData, setFormData] = useState<CreateDiscountRequest>({
    name: '',
    description: '',
    discountType: DiscountType.PERCENTAGE,
    discountValue: 0,
    applicableOn: ApplicableOn.TOTAL,
    validFrom: new Date().toISOString().slice(0, 16),
    validUntil: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().slice(0, 16),
    active: true,
    requiresCustomer: false,
  });

  useEffect(() => {
    loadDiscounts();
    loadProducts();
  }, []);

  const loadDiscounts = async () => {
    try {
      const response = await discountAPI.getAll();
      setDiscounts(response.data);
    } catch (error) {
      console.error('Error loading discounts:', error);
      alert('Failed to load discounts');
    } finally {
      setLoading(false);
    }
  };

  const loadProducts = async () => {
    try {
      const response = await productAPI.getAll();
      setProducts(response.data);
    } catch (error) {
      console.error('Error loading products:', error);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingDiscount) {
        await discountAPI.update(editingDiscount.id, formData);
      } else {
        await discountAPI.create(formData);
      }
      setShowDialog(false);
      resetForm();
      loadDiscounts();
    } catch (error: any) {
      console.error('Error saving discount:', error);
      alert(error.response?.data?.message || 'Failed to save discount');
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Are you sure you want to delete this discount?')) return;
    try {
      await discountAPI.delete(id);
      loadDiscounts();
    } catch (error) {
      console.error('Error deleting discount:', error);
      alert('Failed to delete discount');
    }
  };

  const handleEdit = (discount: Discount) => {
    setEditingDiscount(discount);
    setFormData({
      name: discount.name,
      description: discount.description,
      discountType: discount.discountType,
      discountValue: discount.discountValue,
      applicableOn: discount.applicableOn,
      applicableProductId: discount.applicableProductId,
      minimumPurchaseAmount: discount.minimumPurchaseAmount,
      maximumDiscountAmount: discount.maximumDiscountAmount,
      validFrom: new Date(discount.validFrom).toISOString().slice(0, 16),
      validUntil: new Date(discount.validUntil).toISOString().slice(0, 16),
      active: discount.active,
      requiresCustomer: discount.requiresCustomer,
    });
    setShowDialog(true);
  };

  const resetForm = () => {
    setEditingDiscount(null);
    setFormData({
      name: '',
      description: '',
      discountType: DiscountType.PERCENTAGE,
      discountValue: 0,
      applicableOn: ApplicableOn.TOTAL,
      validFrom: new Date().toISOString().slice(0, 16),
      validUntil: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().slice(0, 16),
      active: true,
      requiresCustomer: false,
    });
  };

  const getApplicableOnLabel = (applicableOn: ApplicableOn, productId?: number) => {
    switch (applicableOn) {
      case ApplicableOn.TOTAL:
        return 'Cart Total';
      case ApplicableOn.SPECIFIC_PRODUCT:
        const product = products.find(p => p.id === productId);
        return `Product: ${product?.name || productId}`;
      case ApplicableOn.PRODUCT_CATEGORY:
        return 'Product Category';
      default:
        return applicableOn;
    }
  };

  if (loading) {
    return <div className="p-4">Loading...</div>;
  }

  return (
    <div className="p-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Discounts</h1>
        <button
          onClick={() => { resetForm(); setShowDialog(true); }}
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
        >
          + New Discount
        </button>
      </div>

      <div className="bg-white rounded-lg shadow overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Name</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Discount</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Applies To</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Valid Period</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Requires Customer</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Status</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Actions</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {discounts.map((discount) => (
              <tr key={discount.id}>
                <td className="px-6 py-4 font-semibold">{discount.name}</td>
                <td className="px-6 py-4 whitespace-nowrap">
                  {discount.discountType === DiscountType.PERCENTAGE
                    ? `${discount.discountValue}%`
                    : `$${discount.discountValue}`}
                </td>
                <td className="px-6 py-4">
                  {getApplicableOnLabel(discount.applicableOn, discount.applicableProductId)}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm">
                  {new Date(discount.validFrom).toLocaleDateString()} - {new Date(discount.validUntil).toLocaleDateString()}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-center">
                  {discount.requiresCustomer ? '✓' : '—'}
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 py-1 text-xs rounded ${discount.active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                    {discount.active ? 'Active' : 'Inactive'}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm">
                  <button onClick={() => handleEdit(discount)} className="text-blue-600 hover:underline mr-3">Edit</button>
                  <button onClick={() => handleDelete(discount.id)} className="text-red-600 hover:underline">Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {showDialog && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg p-6 w-full max-w-2xl max-h-[90vh] overflow-y-auto">
            <h2 className="text-xl font-bold mb-4">{editingDiscount ? 'Edit Discount' : 'New Discount'}</h2>
            <form onSubmit={handleSubmit}>
              <div className="grid grid-cols-2 gap-4 mb-4">
                <div>
                  <label className="block text-sm font-medium mb-1">Name *</label>
                  <input
                    type="text"
                    required
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    className="w-full border rounded px-3 py-2"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Status</label>
                  <select
                    value={formData.active ? 'true' : 'false'}
                    onChange={(e) => setFormData({ ...formData, active: e.target.value === 'true' })}
                    className="w-full border rounded px-3 py-2"
                  >
                    <option value="true">Active</option>
                    <option value="false">Inactive</option>
                  </select>
                </div>
              </div>

              <div className="mb-4">
                <label className="block text-sm font-medium mb-1">Description</label>
                <textarea
                  value={formData.description || ''}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="w-full border rounded px-3 py-2"
                  rows={2}
                />
              </div>

              <div className="grid grid-cols-2 gap-4 mb-4">
                <div>
                  <label className="block text-sm font-medium mb-1">Discount Type *</label>
                  <select
                    value={formData.discountType}
                    onChange={(e) => setFormData({ ...formData, discountType: e.target.value as DiscountType })}
                    className="w-full border rounded px-3 py-2"
                  >
                    <option value={DiscountType.PERCENTAGE}>Percentage</option>
                    <option value={DiscountType.FIXED_AMOUNT}>Fixed Amount</option>
                  </select>
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Discount Value *</label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    value={formData.discountValue}
                    onChange={(e) => setFormData({ ...formData, discountValue: parseFloat(e.target.value) })}
                    className="w-full border rounded px-3 py-2"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4 mb-4">
                <div>
                  <label className="block text-sm font-medium mb-1">Applicable On *</label>
                  <select
                    value={formData.applicableOn}
                    onChange={(e) => setFormData({ ...formData, applicableOn: e.target.value as ApplicableOn, applicableProductId: undefined })}
                    className="w-full border rounded px-3 py-2"
                  >
                    <option value={ApplicableOn.TOTAL}>Cart Total</option>
                    <option value={ApplicableOn.SPECIFIC_PRODUCT}>Specific Product</option>
                    <option value={ApplicableOn.PRODUCT_CATEGORY}>Product Category</option>
                  </select>
                </div>
                {formData.applicableOn === ApplicableOn.SPECIFIC_PRODUCT && (
                  <div>
                    <label className="block text-sm font-medium mb-1">Product *</label>
                    <select
                      required
                      value={formData.applicableProductId || ''}
                      onChange={(e) => setFormData({ ...formData, applicableProductId: parseInt(e.target.value) })}
                      className="w-full border rounded px-3 py-2"
                    >
                      <option value="">Select product</option>
                      {products.map(p => (
                        <option key={p.id} value={p.id}>{p.name}</option>
                      ))}
                    </select>
                  </div>
                )}
              </div>

              <div className="grid grid-cols-2 gap-4 mb-4">
                <div>
                  <label className="block text-sm font-medium mb-1">Min Purchase Amount</label>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.minimumPurchaseAmount || ''}
                    onChange={(e) => setFormData({ ...formData, minimumPurchaseAmount: e.target.value ? parseFloat(e.target.value) : undefined })}
                    className="w-full border rounded px-3 py-2"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Max Discount Amount</label>
                  <input
                    type="number"
                    step="0.01"
                    value={formData.maximumDiscountAmount || ''}
                    onChange={(e) => setFormData({ ...formData, maximumDiscountAmount: e.target.value ? parseFloat(e.target.value) : undefined })}
                    className="w-full border rounded px-3 py-2"
                  />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4 mb-4">
                <div>
                  <label className="block text-sm font-medium mb-1">Valid From *</label>
                  <input
                    type="datetime-local"
                    required
                    value={formData.validFrom}
                    onChange={(e) => setFormData({ ...formData, validFrom: e.target.value })}
                    className="w-full border rounded px-3 py-2"
                  />
                </div>
                <div>
                  <label className="block text-sm font-medium mb-1">Valid Until *</label>
                  <input
                    type="datetime-local"
                    required
                    value={formData.validUntil}
                    onChange={(e) => setFormData({ ...formData, validUntil: e.target.value })}
                    className="w-full border rounded px-3 py-2"
                  />
                </div>
              </div>

              <div className="mb-4">
                <label className="flex items-center">
                  <input
                    type="checkbox"
                    checked={formData.requiresCustomer}
                    onChange={(e) => setFormData({ ...formData, requiresCustomer: e.target.checked })}
                    className="mr-2"
                  />
                  <span className="text-sm font-medium">Requires Customer</span>
                </label>
              </div>

              <div className="flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => { setShowDialog(false); resetForm(); }}
                  className="px-4 py-2 border rounded hover:bg-gray-100"
                >
                  Cancel
                </button>
                <button type="submit" className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
                  {editingDiscount ? 'Update' : 'Create'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
