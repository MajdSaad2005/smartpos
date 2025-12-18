import { apiClient } from '@/lib/axios';
import {
  Product,
  Supplier,
  Customer,
  Ticket,
  CloseCash,
  StockLevel,
  Coupon,
  Discount,
  PurchaseOrder,
  CreateProductRequest,
  CreateSupplierRequest,
  CreateCustomerRequest,
  CreateTicketRequest,
  CreateCouponRequest,
  CreateDiscountRequest,
  CreatePurchaseOrderRequest,
  SalesReportDTO,
  ProductSalesStatsDTO,
  CustomerPurchaseSummaryDTO,
} from '@/types/api';

// Product APIs
export const productAPI = {
  getAll: () => apiClient.get<Product[]>('/v1/products'),
  getById: (id: number) => apiClient.get<Product>(`/v1/products/${id}`),
  getByCode: (code: string) => apiClient.get<Product>(`/v1/products/code/${code}`),
  search: (searchTerm: string) => apiClient.get<Product[]>('/v1/products/search', { params: { searchTerm } }),
  create: (data: CreateProductRequest) => apiClient.post<Product>('/v1/products', data),
  update: (id: number, data: CreateProductRequest) => apiClient.put<Product>(`/v1/products/${id}`, data),
  delete: (id: number) => apiClient.delete(`/v1/products/${id}`),
};

// Supplier APIs
export const supplierAPI = {
  getAll: () => apiClient.get<Supplier[]>('/v1/suppliers'),
  getActive: () => apiClient.get<Supplier[]>('/v1/suppliers'),
  getById: (id: number) => apiClient.get<Supplier>(`/v1/suppliers/${id}`),
  create: (data: CreateSupplierRequest) => apiClient.post<Supplier>('/v1/suppliers', data),
  update: (id: number, data: CreateSupplierRequest) => apiClient.put<Supplier>(`/v1/suppliers/${id}`, data),
  delete: (id: number) => apiClient.delete(`/v1/suppliers/${id}`),
};

// Customer APIs
export const customerAPI = {
  getAll: () => apiClient.get<Customer[]>('/v1/customers'),
  getById: (id: number) => apiClient.get<Customer>(`/v1/customers/${id}`),
  search: (searchTerm: string) => apiClient.get<Customer[]>('/v1/customers/search', { params: { searchTerm } }),
  create: (data: CreateCustomerRequest) => apiClient.post<Customer>('/v1/customers', data),
  update: (id: number, data: CreateCustomerRequest) => apiClient.put<Customer>(`/v1/customers/${id}`, data),
  delete: (id: number) => apiClient.delete(`/v1/customers/${id}`),
};

// Ticket APIs
export const ticketAPI = {
  getById: (id: number) => apiClient.get<Ticket>(`/v1/tickets/${id}`),
  getByNumber: (number: string) => apiClient.get<Ticket>(`/v1/tickets/number/${number}`),
  getByCustomerId: (customerId: number) => apiClient.get<Ticket[]>(`/v1/tickets/customer/${customerId}`),
  getByDateRange: (startDate: string, endDate: string) =>
    apiClient.get<Ticket[]>('/v1/tickets/date-range', { params: { startDate, endDate } }),
  getRecent: (limit: number = 10) => apiClient.get<Ticket[]>('/v1/tickets/recent', { params: { limit } }),
  create: (data: CreateTicketRequest) => apiClient.post<Ticket>('/v1/tickets', data),
  cancel: (id: number) => apiClient.delete(`/v1/tickets/${id}`),
  recalculate: (id: number) => apiClient.post<string>(`/v1/tickets/${id}/recalculate`),
};

// Close Cash APIs
export const closeCashAPI = {
  open: (cashierName?: string) => apiClient.post<CloseCash>('/v1/close-cash/open', null, {
    params: cashierName ? { cashierName } : undefined
  }),
  close: (id: number) => apiClient.post<CloseCash>(`/v1/close-cash/${id}/close`),
  getById: (id: number) => apiClient.get<CloseCash>(`/v1/close-cash/${id}`),
  getPending: () => apiClient.get<CloseCash[]>('/v1/close-cash/pending'),
  reconcile: (id: number) => apiClient.post(`/v1/close-cash/${id}/reconcile`),
};

// Stock Level APIs
export const stockLevelAPI = {
  getByProductId: (productId: number) => apiClient.get<StockLevel[]>(`/v1/stock-levels/product/${productId}`),
  getById: (id: number) => apiClient.get<StockLevel>(`/v1/stock-levels/${id}`),
  create: (productId: number, minimumLevel: number, maximumLevel: number) =>
    apiClient.post<StockLevel>('/v1/stock-levels', null, {
      params: { productId, minimumLevel, maximumLevel },
    }),
  update: (id: number, minimumLevel: number, maximumLevel: number) =>
    apiClient.put<StockLevel>(`/v1/stock-levels/${id}`, null, {
      params: { minimumLevel, maximumLevel },
    }),
  delete: (id: number) => apiClient.delete(`/v1/stock-levels/${id}`),
};

// Coupon APIs
export const couponAPI = {
  getAll: () => apiClient.get<Coupon[]>('/v1/coupons'),
  getActive: () => apiClient.get<Coupon[]>('/v1/coupons/active'),
  getById: (id: number) => apiClient.get<Coupon>(`/v1/coupons/${id}`),
  getByCode: (code: string) => apiClient.get<Coupon>(`/v1/coupons/code/${code}`),
  create: (data: CreateCouponRequest) => apiClient.post<Coupon>('/v1/coupons', data),
  update: (id: number, data: CreateCouponRequest) => apiClient.put<Coupon>(`/v1/coupons/${id}`, data),
  delete: (id: number) => apiClient.delete(`/v1/coupons/${id}`),
  validate: (code: string, amount: number) => 
    apiClient.post<{ discount: number }>('/v1/coupons/validate', { code, amount }),
};

// Discount APIs
export const discountAPI = {
  getAll: () => apiClient.get<Discount[]>('/v1/discounts'),
  getActive: () => apiClient.get<Discount[]>('/v1/discounts/active'),
  getById: (id: number) => apiClient.get<Discount>(`/v1/discounts/${id}`),
  getForProduct: (productId: number) => apiClient.get<Discount[]>(`/v1/discounts/product/${productId}`),
  getTotalDiscounts: () => apiClient.get<Discount[]>('/v1/discounts/total'),
  create: (data: CreateDiscountRequest) => apiClient.post<Discount>('/v1/discounts', data),
  update: (id: number, data: CreateDiscountRequest) => apiClient.put<Discount>(`/v1/discounts/${id}`, data),
  delete: (id: number) => apiClient.delete(`/v1/discounts/${id}`),
};

// Reporting APIs
export const reportingAPI = {
  getSalesReport: (startDate: string, endDate: string) => 
    apiClient.get<SalesReportDTO[]>('/v1/reports/sales', { params: { startDate, endDate } }),
  getProductStats: (startDate: string, endDate: string) =>
    apiClient.get<ProductSalesStatsDTO[]>('/v1/reports/product-stats', { params: { startDate, endDate } }),
  getProductsBelowAverage: (startDate: string, endDate: string) =>
    apiClient.get<ProductSalesStatsDTO[]>('/v1/reports/products-below-average', { params: { startDate, endDate } }),
  getActiveEntities: () => apiClient.get<string[]>('/v1/reports/active-entities'),
  getCustomerSummary: (startDate: string, endDate: string) =>
    apiClient.get<CustomerPurchaseSummaryDTO[]>('/v1/reports/customer-summary', { params: { startDate, endDate } }),
  getHighValueCustomers: (threshold: number) =>
    apiClient.get<CustomerPurchaseSummaryDTO[]>('/v1/reports/high-value-customers', { params: { threshold } }),
  getLowStockProducts: () => apiClient.get<string[]>('/v1/reports/low-stock'),
};

export const purchaseOrderAPI = {
  create: (request: CreatePurchaseOrderRequest) => apiClient.post<PurchaseOrder>('/v1/purchase-orders', request),
  getAll: () => apiClient.get<PurchaseOrder[]>('/v1/purchase-orders'),
  getById: (id: number) => apiClient.get<PurchaseOrder>(`/v1/purchase-orders/${id}`),
  getBySupplier: (supplierId: number) => apiClient.get<PurchaseOrder[]>(`/v1/purchase-orders/supplier/${supplierId}`),
  getByStatus: (status: string) => apiClient.get<PurchaseOrder[]>(`/v1/purchase-orders/status/${status}`),
  receive: (id: number) => apiClient.put<PurchaseOrder>(`/v1/purchase-orders/${id}/receive`),
  cancel: (id: number) => apiClient.put<void>(`/v1/purchase-orders/${id}/cancel`),
};
