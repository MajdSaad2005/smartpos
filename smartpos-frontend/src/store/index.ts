import { create } from 'zustand';
import { Product, Supplier, Customer, Ticket, CloseCash } from '@/types/api';

interface StoreState {
  // Products
  products: Product[];
  setProducts: (products: Product[]) => void;
  addProduct: (product: Product) => void;
  updateProduct: (product: Product) => void;

  // Suppliers
  suppliers: Supplier[];
  setSuppliers: (suppliers: Supplier[]) => void;

  // Customers
  customers: Customer[];
  setCustomers: (customers: Customer[]) => void;

  // Tickets
  tickets: Ticket[];
  currentTicket: Ticket | null;
  setTickets: (tickets: Ticket[]) => void;
  setCurrentTicket: (ticket: Ticket | null) => void;

  // Close Cash
  closeCash: CloseCash | null;
  setCloseCash: (closeCash: CloseCash | null) => void;

  // UI State
  loading: boolean;
  setLoading: (loading: boolean) => void;
}

export const useStore = create<StoreState>((set) => ({
  // Products
  products: [],
  setProducts: (products) => set({ products }),
  addProduct: (product) => set((state) => ({ products: [...state.products, product] })),
  updateProduct: (product) =>
    set((state) => ({
      products: state.products.map((p) => (p.id === product.id ? product : p)),
    })),

  // Suppliers
  suppliers: [],
  setSuppliers: (suppliers) => set({ suppliers }),

  // Customers
  customers: [],
  setCustomers: (customers) => set({ customers }),

  // Tickets
  tickets: [],
  currentTicket: null,
  setTickets: (tickets) => set({ tickets }),
  setCurrentTicket: (ticket) => set({ currentTicket: ticket }),

  // Close Cash
  closeCash: null,
  setCloseCash: (closeCash) => set({ closeCash }),

  // UI State
  loading: false,
  setLoading: (loading) => set({ loading }),
}));
