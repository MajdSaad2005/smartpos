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
  cashierName?: string;
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
  isDefective?: boolean; // For returns - mark as defective
  discountAmount?: number; // Discount for this line
  couponCode?: string; // Coupon code if applicable
}

export interface CreateTicketRequest {
  type: 'SALE' | 'RETURN';
  customerId: number | null;
  notes?: string;
  lines: CreateTicketLineRequest[];
  couponCode?: string;
  discountId?: number;
}

export enum DiscountType {
  PERCENTAGE = 'PERCENTAGE',
  FIXED_AMOUNT = 'FIXED_AMOUNT'
}

export enum ApplicableOn {
  TOTAL = 'TOTAL',
  PRODUCT_CATEGORY = 'PRODUCT_CATEGORY',
  SPECIFIC_PRODUCT = 'SPECIFIC_PRODUCT'
}

export interface Coupon {
  id: number;
  code: string;
  description: string;
  discountType: DiscountType;
  discountValue: number;
  minimumPurchaseAmount?: number;
  maximumDiscountAmount?: number;
  validFrom: string;
  validUntil: string;
  active: boolean;
  maxUsageCount?: number;
  currentUsageCount: number;
  createdAt: string;
}

export interface Discount {
  id: number;
  name: string;
  description?: string;
  discountType: DiscountType;
  discountValue: number;
  applicableOn: ApplicableOn;
  applicableProductId?: number;
  minimumPurchaseAmount?: number;
  maximumDiscountAmount?: number;
  validFrom: string;
  validUntil: string;
  active: boolean;
  requiresCustomer: boolean;
  createdAt: string;
}

export interface CreateCouponRequest {
  code: string;
  description: string;
  discountType: DiscountType;
  discountValue: number;
  minimumPurchaseAmount?: number;
  maximumDiscountAmount?: number;
  validFrom: string;
  validUntil: string;
  active: boolean;
  maxUsageCount?: number;
}

export interface CreateDiscountRequest {
  name: string;
  description?: string;
  discountType: DiscountType;
  discountValue: number;
  applicableOn: ApplicableOn;
  applicableProductId?: number;
  minimumPurchaseAmount?: number;
  maximumDiscountAmount?: number;
  validFrom: string;
  validUntil: string;
  active: boolean;
  requiresCustomer: boolean;
}

// Reporting Types
export interface SalesReportDTO {
  ticketId: number;
  ticketNumber: string;
  createdAt: string;
  customerName: string;
  total: number;
  cashierName: string;
  itemCount: number;
}

export interface ProductSalesStatsDTO {
  productId: number;
  productCode: string;
  productName: string;
  totalQuantitySold: number;
  totalRevenue: number;
  averagePrice: number;
  transactionCount: number;
}

export interface CustomerPurchaseSummaryDTO {
  customerId: number;
  customerCode: string;
  customerName: string;
  totalPurchases: number;
  totalSpent: number;
  averageTransactionValue: number;
}

// Purchase Order Types
export interface PurchaseOrder {
  id: number;
  orderNumber: string;
  supplierId: number;
  supplierName: string;
  status: 'PENDING' | 'ORDERED' | 'RECEIVED' | 'CANCELLED';
  orderDate: string;
  expectedDeliveryDate?: string;
  receivedDate?: string;
  subtotal: number;
  taxAmount: number;
  total: number;
  notes?: string;
  lines: PurchaseOrderLine[];
  createdAt: string;
  updatedAt?: string;
}

export interface PurchaseOrderLine {
  id: number;
  productId: number;
  productName: string;
  productCode: string;
  quantityOrdered: number;
  quantityReceived: number;
  unitCost: number;
  lineTotal: number;
  taxPercentage?: number;
  taxAmount?: number;
}

export interface CreatePurchaseOrderRequest {
  supplierId: number;
  expectedDeliveryDate?: string;
  notes?: string;
  lines: CreatePurchaseOrderLineRequest[];
}

export interface CreatePurchaseOrderLineRequest {
  productId: number;
  quantity: number;
}
