'use client';

import React, { useEffect, useState } from 'react';
import { Layout, Header, Content } from '@/components/layout/Layout';
import { Card, Button, Input, Select, LoadingSpinner } from '@/components/common/FormElements';
import { Product, Customer, Ticket, CreateTicketRequest, Discount, DiscountType, ApplicableOn } from '@/types/api';
import { productAPI, customerAPI, ticketAPI, couponAPI, discountAPI } from '@/lib/api';
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
  const [couponCode, setCouponCode] = useState('');
  const [appliedCoupon, setAppliedCoupon] = useState<{code: string, discount: number} | null>(null);
  const [availableDiscounts, setAvailableDiscounts] = useState<Discount[]>([]);
  const [selectedDiscountId, setSelectedDiscountId] = useState<number | null>(null);
  const [recentTickets, setRecentTickets] = useState<Ticket[]>([]);

  useEffect(() => {
    fetchProducts();
    fetchCustomers();
    fetchActiveDiscounts();
    fetchRecentTickets();
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

  const fetchActiveDiscounts = async () => {
    try {
      const { data } = await discountAPI.getActive();
      setAvailableDiscounts(data);
    } catch (error) {
      console.error('Failed to fetch discounts');
    }
  };

  const fetchRecentTickets = async () => {
    try {
      const { data } = await ticketAPI.getRecent(5);
      setRecentTickets(data);
    } catch (error) {
      console.error('Failed to fetch recent tickets');
    }
  };

  const recalculateTicket = async (ticketId: number) => {
    try {
      const { data } = await ticketAPI.recalculate(ticketId);
      toast.success('Ticket recalculated using explicit SQL transaction (BEGIN/COMMIT)');
      fetchRecentTickets();
    } catch (error: any) {
      toast.error(error.response?.data || 'Failed to recalculate ticket');
    }
  };

  const applyCoupon = async () => {
    if (!couponCode.trim()) {
      toast.error('Please enter a coupon code');
      return;
    }

    const { subtotal } = calculateSubtotalAndTax();
    
    try {
      const { data } = await couponAPI.validate(couponCode, subtotal);
      setAppliedCoupon({ code: couponCode, discount: data.discount });
      toast.success(`Coupon applied! Discount: ${formatCurrency(data.discount)}`);
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Invalid coupon code');
      setAppliedCoupon(null);
    }
  };

  const removeCoupon = () => {
    setAppliedCoupon(null);
    setCouponCode('');
    toast.success('Coupon removed');
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
          isDefective: false,
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

  const calculateSubtotalAndTax = () => {
    let subtotal = 0;
    let taxAmount = 0;

    cartItems.forEach((item) => {
      const lineSubtotal = item.unitPrice * item.quantity;
      const lineTax = (lineSubtotal * item.taxPercentage) / 100;
      subtotal += lineSubtotal;
      taxAmount += lineTax;
    });

    return { subtotal, taxAmount };
  };

  const calculateTotals = () => {
    const { subtotal, taxAmount } = calculateSubtotalAndTax();
    let totalBeforeDiscounts = subtotal + taxAmount;
    let totalDiscount = 0;

    // Apply coupon discount
    if (appliedCoupon) {
      totalDiscount += appliedCoupon.discount;
    }

    // Apply selected discount
    if (selectedDiscountId) {
      const discount = availableDiscounts.find(d => d.id === selectedDiscountId);
      if (discount) {
        if (discount.applicableOn === ApplicableOn.TOTAL) {
          const discountAmount = discount.discountType === DiscountType.PERCENTAGE
            ? (subtotal * discount.discountValue) / 100
            : discount.discountValue;
          
          let finalDiscountAmount = discountAmount;
          if (discount.maximumDiscountAmount && discountAmount > discount.maximumDiscountAmount) {
            finalDiscountAmount = discount.maximumDiscountAmount;
          }
          totalDiscount += finalDiscountAmount;
        }
      }
    }

    return {
      subtotal,
      taxAmount,
      totalDiscount,
      total: Math.max(0, totalBeforeDiscounts - totalDiscount),
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
          isDefective: type === 'RETURN' ? item.isDefective : undefined,
        })),
        couponCode: appliedCoupon?.code,
        discountId: selectedDiscountId,
      };

      const { data } = await ticketAPI.create(request);
      toast.success(`${type === 'SALE' ? 'Sale' : 'Return'} completed successfully`);
      
      // Reset cart
      setCartItems([]);
      setSelectedCustomer(null);
      setAppliedCoupon(null);
      setCouponCode('');
      setSelectedDiscountId(null);
      
      // Refresh products to update stock
      fetchProducts();
      fetchRecentTickets();
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Failed to complete transaction');
    } finally {
      setLoading(false);
    }
  };

  const { subtotal, taxAmount, totalDiscount, total } = calculateTotals();

  const totalDiscounts = availableDiscounts.filter(d => {
    if (!d.active) return false;
    if (d.requiresCustomer && !selectedCustomer) return false;
    if (d.minimumPurchaseAmount && subtotal < d.minimumPurchaseAmount) return false;
    return d.applicableOn === ApplicableOn.TOTAL;
  });

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
                      <div key={item.productId} className="p-3 bg-gray-50 rounded">
                        <div className="flex justify-between items-start">
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
                        <div className="mt-2">
                          <label className="flex items-center text-xs text-gray-600">
                            <input
                              type="checkbox"
                              checked={item.isDefective || false}
                              onChange={(e) => {
                                setCartItems(cartItems.map(ci => 
                                  ci.productId === item.productId 
                                    ? { ...ci, isDefective: e.target.checked }
                                    : ci
                                ));
                              }}
                              className="mr-2"
                            />
                            Mark as defective (won't be added back to stock)
                          </label>
                        </div>
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
                {totalDiscount > 0 && (
                  <div className="flex justify-between text-green-600">
                    <span>Discount</span>
                    <span className="font-semibold">-{formatCurrency(totalDiscount)}</span>
                  </div>
                )}
                <div className="flex justify-between pt-2 border-t text-lg font-bold">
                  <span>Total</span>
                  <span className="text-green-600">{formatCurrency(total)}</span>
                </div>
              </div>

              {/* Coupon Input */}
              <div className="mt-4 pt-4 border-t">
                <p className="text-sm font-medium mb-2">Apply Coupon</p>
                {appliedCoupon ? (
                  <div className="flex items-center justify-between p-2 bg-green-50 rounded">
                    <span className="text-sm font-semibold text-green-700">{appliedCoupon.code} (-{formatCurrency(appliedCoupon.discount)})</span>
                    <button onClick={removeCoupon} className="text-red-500 text-xs hover:underline">Remove</button>
                  </div>
                ) : (
                  <div className="flex gap-2">
                    <input
                      type="text"
                      placeholder="Enter coupon code"
                      value={couponCode}
                      onChange={(e) => setCouponCode(e.target.value.toUpperCase())}
                      className="flex-1 border rounded px-3 py-2 text-sm"
                    />
                    <button onClick={applyCoupon} className="px-3 py-2 bg-blue-500 text-white rounded text-sm hover:bg-blue-600">
                      Apply
                    </button>
                  </div>
                )}
              </div>

              {/* Discount Selection */}
              {totalDiscounts.length > 0 && (
                <div className="mt-4 pt-4 border-t">
                  <p className="text-sm font-medium mb-2">Available Discounts</p>
                  <select
                    value={selectedDiscountId || ''}
                    onChange={(e) => setSelectedDiscountId(e.target.value ? parseInt(e.target.value) : null)}
                    className="w-full border rounded px-3 py-2 text-sm"
                  >
                    <option value="">No discount selected</option>
                    {totalDiscounts.map(d => (
                      <option key={d.id} value={d.id}>
                        {d.name} - {d.discountType === DiscountType.PERCENTAGE ? `${d.discountValue}%` : formatCurrency(d.discountValue)}
                      </option>
                    ))}
                  </select>
                </div>
              )}

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

        {/* Recent Transactions with Recalculate Feature */}
        {recentTickets.length > 0 && (
          <div className="mt-6">
            <Card title="Recent Transactions (Last 5)">
              <div className="space-y-2">
                {recentTickets.map((ticket) => (
                  <div key={ticket.id} className="flex items-center justify-between p-3 bg-gray-50 rounded border">
                    <div className="flex-1">
                      <div className="flex items-center gap-4">
                        <span className="font-medium">#{ticket.number}</span>
                        <span className={`px-2 py-1 text-xs rounded ${ticket.type === 'SALE' ? 'bg-green-100 text-green-800' : 'bg-blue-100 text-blue-800'}`}>
                          {ticket.type}
                        </span>
                        <span className="text-sm text-gray-600">
                          {new Date(ticket.createdAt).toLocaleString()}
                        </span>
                      </div>
                      <div className="text-sm text-gray-700 mt-1">
                        Total: <span className="font-semibold">{formatCurrency(ticket.total)}</span>
                      </div>
                    </div>
                    <Button
                      onClick={() => recalculateTicket(ticket.id)}
                      variant="secondary"
                      className="text-xs"
                    >
                      🔄 Recalculate (SQL)
                    </Button>
                  </div>
                ))}
                <div className="text-xs text-gray-500 mt-2 p-2 bg-blue-50 rounded border border-blue-200">
                  💡 <strong>Recalculate</strong> uses explicit SQL transaction control (BEGIN/COMMIT/ROLLBACK) to demonstrate raw SQL transaction management
                </div>
              </div>
            </Card>
          </div>
        )}
      </Content>
    </Layout>
  );
}
