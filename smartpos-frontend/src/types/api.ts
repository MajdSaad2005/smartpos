// API Types
export interface Product {
  id: number;
  code: string;
  name: string;
  description: string;
  purchasePrice: number;
  salePrice: number;
  active: boolean;
  taxPercentage: number;
  supplierId: number;
  supplierName: string;
  currentStock: number;
}

export interface Supplier {
  id: number;
  code: string;
  name: string;
  address: string;
  email: string;
  phone: string;
  taxId: string;
  active: boolean;
}

export interface Customer {
  id: number;
  code: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  address: string;
  taxId: string;
  active: boolean;
}

export interface TicketLine {
  id: number;
  productId: number;
  productName: string;
  quantity: number;
  unitPrice: number;
  lineTotal: number;
  taxPercentage: number;
  taxAmount: number;
}

export interface Ticket {
  id: number;
  number: string;
  type: 'SALE' | 'RETURN';
  createdAt: string;
  subtotal: number;
  taxAmount: number;
  total: number;
  status: 'PENDING' | 'COMPLETED' | 'CANCELLED';
  customerId: number | null;
  customerName: string | null;
  closeCashId: number | null;
  lines: TicketLine[];
}

export interface CloseCash {
  id: number;
  openedAt: string;
  closedAt: string | null;
  totalSales: number;
  totalReturns: number;
  netAmount: number;
  reconciled: boolean;
}

export interface StockLevel {
  id: number;
  productId: number;
  productName: string;
  minimumLevel: number;
  maximumLevel: number;
}

// Request Types
export interface CreateProductRequest {
  code: string;
  name: string;
  description: string;
  purchasePrice: number;
  salePrice: number;
  taxPercentage: number;
  supplierId: number;
}

export interface CreateSupplierRequest {
  code: string;
  name: string;
  address: string;
  email: string;
  phone: string;
  taxId: string;
}

export interface CreateCustomerRequest {
  code: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  address: string;
  taxId: string;
}

export interface CreateTicketLineRequest {
  productId: number;
  quantity: number;
}

export interface CreateTicketRequest {
  type: 'SALE' | 'RETURN';
  customerId: number | null;
  notes?: string;
  lines: CreateTicketLineRequest[];
}
