import { apiClient } from '@/lib/axios';
import {
  Product,
  Supplier,
  Customer,
  Ticket,
  CloseCash,
  StockLevel,
  CreateProductRequest,
  CreateSupplierRequest,
  CreateCustomerRequest,
  CreateTicketRequest,
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
};

// Close Cash APIs
export const closeCashAPI = {
  open: () => apiClient.post<CloseCash>('/v1/close-cash/open'),
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
