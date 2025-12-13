'use client';

import React, { useEffect, useState } from 'react';
import { Layout, Header, Content } from '@/components/layout/Layout';
import { Card, Button, Input, Select, LoadingSpinner } from '@/components/common/FormElements';
import { Product, Customer, Ticket, CreateTicketRequest } from '@/types/api';
import { productAPI, customerAPI, ticketAPI } from '@/lib/api';
import toast from 'react-hot-toast';
import { formatCurrency } from '@/lib/utils';
import { FiTrash2, FiPlus } from 'react-icons/fi';

export default function Sales() {
  const [products, setProducts] = useState<Product[]>([]);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [cartItems, setCartItems] = useState<any[]>([]);
  const [selectedCustomer, setSelectedCustomer] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [selectedProductId, setSelectedProductId] = useState<number | null>(null);
  const [quantity, setQuantity] = useState(1);

  useEffect(() => {
    fetchProducts();
    fetchCustomers();
  }, []);

  const fetchProducts = async () => {
    try {
      const { data } = await productAPI.getAll();
      setProducts(data);
    } catch (error) {
      toast.error('Failed to fetch products');
    }
  };

  const fetchCustomers = async () => {
    try {
      const { data } = await customerAPI.getAll();
      setCustomers(data);
    } catch (error) {
      toast.error('Failed to fetch customers');
    }
  };

  const addToCart = () => {
    if (!selectedProductId || quantity <= 0) {
      toast.error('Please select a product and quantity');
      return;
    }

    const product = products.find((p) => p.id === selectedProductId);
    if (!product) return;

    if (product.currentStock < quantity) {
      toast.error('Insufficient stock');
      return;
    }

    const existingItem = cartItems.find((item) => item.productId === selectedProductId);
    if (existingItem) {
      setCartItems(
        cartItems.map((item) =>
          item.productId === selectedProductId
            ? { ...item, quantity: item.quantity + quantity }
            : item
        )
      );
    } else {
      setCartItems([
        ...cartItems,
        {
          productId: selectedProductId,
          productName: product.name,
          quantity,
          unitPrice: product.salePrice,
          taxPercentage: product.taxPercentage || 0,
        },
      ]);
    }

    setSelectedProductId(null);
    setQuantity(1);
    toast.success('Item added to cart');
  };

  const removeFromCart = (productId: number) => {
    setCartItems(cartItems.filter((item) => item.productId !== productId));
  };

  const calculateTotals = () => {
    let subtotal = 0;
    let taxAmount = 0;

    cartItems.forEach((item) => {
      const lineSubtotal = item.unitPrice * item.quantity;
      const lineTax = (lineSubtotal * item.taxPercentage) / 100;
      subtotal += lineSubtotal;
      taxAmount += lineTax;
    });

    return {
      subtotal,
      taxAmount,
      total: subtotal + taxAmount,
    };
  };

  const handleCheckout = async (type: 'SALE' | 'RETURN') => {
    if (cartItems.length === 0) {
      toast.error('Cart is empty');
      return;
    }

    try {
      setLoading(true);
      const request: CreateTicketRequest = {
        type,
        customerId: selectedCustomer,
        lines: cartItems.map((item) => ({
          productId: item.productId,
          quantity: item.quantity,
        })),
      };

      const { data } = await ticketAPI.create(request);
      toast.success(`${type === 'SALE' ? 'Sale' : 'Return'} completed successfully`);
      
      // Reset cart
      setCartItems([]);
      setSelectedCustomer(null);
      
      // Refresh products to update stock
      fetchProducts();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to complete transaction');
    } finally {
      setLoading(false);
    }
  };

  const { subtotal, taxAmount, total } = calculateTotals();

  return (
    <Layout>
      <Header title="Point of Sale" subtitle="Process sales and returns" />
      <Content>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Product Selection */}
          <div className="lg:col-span-2 space-y-6">
            <Card title="Add Products">
              <div className="space-y-4">
                <Select
                  label="Select Product"
                  value={selectedProductId || ''}
                  onChange={(e) => setSelectedProductId(parseInt(e.target.value) || null)}
                  options={[{ value: '', label: 'Choose a product...' }, ...products.map((p) => ({ value: p.id, label: `${p.name} (Stock: ${p.currentStock})` }))]}
                />
                <Input
                  label="Quantity"
                  type="number"
                  min="1"
                  value={quantity}
                  onChange={(e) => setQuantity(parseInt(e.target.value) || 1)}
                />
                <Button onClick={addToCart} variant="primary" className="w-full">
                  <FiPlus className="inline mr-2" /> Add to Cart
                </Button>
              </div>
            </Card>

            <Card title="Customer (Optional)">
              <Select
                label="Select Customer"
                value={selectedCustomer || ''}
                onChange={(e) => setSelectedCustomer(parseInt(e.target.value) || null)}
                options={[{ value: '', label: 'No customer selected...' }, ...customers.map((c) => ({ value: c.id, label: `${c.firstName} ${c.lastName}` }))]}
              />
            </Card>
          </div>

          {/* Cart Summary */}
          <div className="space-y-6">
            <Card title="Shopping Cart">
              <div className="space-y-3">
                {cartItems.length === 0 ? (
                  <p className="text-gray-500 text-center py-4">Cart is empty</p>
                ) : (
                  <>
                    {cartItems.map((item) => (
                      <div key={item.productId} className="flex justify-between items-center p-3 bg-gray-50 rounded">
                        <div className="flex-1">
                          <p className="font-semibold text-sm">{item.productName}</p>
                          <p className="text-xs text-gray-500">
                            {item.quantity} x {formatCurrency(item.unitPrice)}
                          </p>
                        </div>
                        <button onClick={() => removeFromCart(item.productId)} className="text-red-500 hover:text-red-700">
                          <FiTrash2 />
                        </button>
                      </div>
                    ))}
                  </>
                )}
              </div>
            </Card>

            <Card title="Order Summary">
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span className="text-gray-600">Subtotal</span>
                  <span className="font-semibold">{formatCurrency(subtotal)}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-gray-600">Tax</span>
                  <span className="font-semibold">{formatCurrency(taxAmount)}</span>
                </div>
                <div className="flex justify-between pt-2 border-t text-lg font-bold">
                  <span>Total</span>
                  <span className="text-green-600">{formatCurrency(total)}</span>
                </div>
              </div>

              <div className="space-y-2 mt-4">
                <Button onClick={() => handleCheckout('SALE')} variant="primary" className="w-full" disabled={cartItems.length === 0 || loading}>
                  {loading ? 'Processing...' : 'Complete Sale'}
                </Button>
                <Button onClick={() => handleCheckout('RETURN')} variant="danger" className="w-full" disabled={cartItems.length === 0 || loading}>
                  {loading ? 'Processing...' : 'Process Return'}
                </Button>
              </div>
            </Card>
          </div>
        </div>
      </Content>
    </Layout>
  );
}
