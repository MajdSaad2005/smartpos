'use client';

import { useState, useEffect } from 'react';
import { reportingAPI } from '@/lib/api';
import { SalesReportDTO, ProductSalesStatsDTO, CustomerPurchaseSummaryDTO } from '@/types/api';

export default function ReportsPage() {
  const [activeTab, setActiveTab] = useState<'sales' | 'products' | 'customers' | 'other'>('sales');
  const [loading, setLoading] = useState(false);
  const [startDate, setStartDate] = useState(
    new Date(new Date().setMonth(new Date().getMonth() - 1)).toISOString().slice(0, 16)
  );
  const [endDate, setEndDate] = useState(new Date().toISOString().slice(0, 16));

  // Sales Report
  const [salesReport, setSalesReport] = useState<SalesReportDTO[]>([]);
  const [productStats, setProductStats] = useState<ProductSalesStatsDTO[]>([]);
  const [productsBelowAvg, setProductsBelowAvg] = useState<ProductSalesStatsDTO[]>([]);
  const [customerSummary, setCustomerSummary] = useState<CustomerPurchaseSummaryDTO[]>([]);
  const [highValueCustomers, setHighValueCustomers] = useState<CustomerPurchaseSummaryDTO[]>([]);
  const [activeEntities, setActiveEntities] = useState<string[]>([]);
  const [lowStockProducts, setLowStockProducts] = useState<string[]>([]);
  const [threshold, setThreshold] = useState(1000);

  const loadSalesReport = async () => {
    setLoading(true);
    try {
      const response = await reportingAPI.getSalesReport(startDate, endDate);
      setSalesReport(response.data);
    } catch (error) {
      console.error('Error loading sales report:', error);
      alert('Failed to load sales report');
    } finally {
      setLoading(false);
    }
  };

  const loadProductStats = async () => {
    setLoading(true);
    try {
      const response = await reportingAPI.getProductStats(startDate, endDate);
      setProductStats(response.data);
    } catch (error) {
      console.error('Error loading product stats:', error);
      alert('Failed to load product statistics');
    } finally {
      setLoading(false);
    }
  };

  const loadProductsBelowAverage = async () => {
    setLoading(true);
    try {
      const response = await reportingAPI.getProductsBelowAverage(startDate, endDate);
      setProductsBelowAvg(response.data);
    } catch (error) {
      console.error('Error loading below average products:', error);
      alert('Failed to load below average products');
    } finally {
      setLoading(false);
    }
  };

  const loadCustomerSummary = async () => {
    setLoading(true);
    try {
      const response = await reportingAPI.getCustomerSummary(startDate, endDate);
      setCustomerSummary(response.data);
    } catch (error) {
      console.error('Error loading customer summary:', error);
      alert('Failed to load customer summary');
    } finally {
      setLoading(false);
    }
  };

  const loadHighValueCustomers = async () => {
    setLoading(true);
    try {
      const response = await reportingAPI.getHighValueCustomers(threshold);
      setHighValueCustomers(response.data);
    } catch (error) {
      console.error('Error loading high value customers:', error);
      alert('Failed to load high value customers');
    } finally {
      setLoading(false);
    }
  };

  const loadActiveEntities = async () => {
    setLoading(true);
    try {
      const response = await reportingAPI.getActiveEntities();
      setActiveEntities(response.data);
    } catch (error) {
      console.error('Error loading active entities:', error);
      alert('Failed to load active entities');
    } finally {
      setLoading(false);
    }
  };

  const loadLowStockProducts = async () => {
    setLoading(true);
    try {
      const response = await reportingAPI.getLowStockProducts();
      setLowStockProducts(response.data);
    } catch (error) {
      console.error('Error loading low stock products:', error);
      alert('Failed to load low stock products');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (activeTab === 'sales') {
      loadSalesReport();
    } else if (activeTab === 'products') {
      loadProductStats();
      loadProductsBelowAverage();
    } else if (activeTab === 'customers') {
      loadCustomerSummary();
    }
  }, [activeTab, startDate, endDate]);

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  return (
    <div className="p-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold mb-2">Reports & Analytics</h1>
        <p className="text-gray-600">Advanced database queries and business intelligence</p>
      </div>

      {/* Date Range Filter */}
      <div className="bg-white rounded-lg shadow p-4 mb-6">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div>
            <label className="block text-sm font-medium mb-1">Start Date</label>
            <input
              type="datetime-local"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              className="w-full border rounded px-3 py-2"
            />
          </div>
          <div>
            <label className="block text-sm font-medium mb-1">End Date</label>
            <input
              type="datetime-local"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              className="w-full border rounded px-3 py-2"
            />
          </div>
          <div className="flex items-end">
            <button
              onClick={() => {
                if (activeTab === 'sales') loadSalesReport();
                else if (activeTab === 'products') { loadProductStats(); loadProductsBelowAverage(); }
                else if (activeTab === 'customers') loadCustomerSummary();
              }}
              className="w-full bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
            >
              Refresh Report
            </button>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div className="mb-6 border-b">
        <div className="flex gap-4">
          <button
            onClick={() => setActiveTab('sales')}
            className={`px-4 py-2 font-medium ${activeTab === 'sales' ? 'border-b-2 border-blue-500 text-blue-600' : 'text-gray-600'}`}
          >
            Sales Report
          </button>
          <button
            onClick={() => setActiveTab('products')}
            className={`px-4 py-2 font-medium ${activeTab === 'products' ? 'border-b-2 border-blue-500 text-blue-600' : 'text-gray-600'}`}
          >
            Product Analytics
          </button>
          <button
            onClick={() => setActiveTab('customers')}
            className={`px-4 py-2 font-medium ${activeTab === 'customers' ? 'border-b-2 border-blue-500 text-blue-600' : 'text-gray-600'}`}
          >
            Customer Analytics
          </button>
          <button
            onClick={() => setActiveTab('other')}
            className={`px-4 py-2 font-medium ${activeTab === 'other' ? 'border-b-2 border-blue-500 text-blue-600' : 'text-gray-600'}`}
          >
            Other Reports
          </button>
        </div>
      </div>

      {loading && (
        <div className="text-center py-8">
          <div className="inline-block animate-spin rounded-full h-8 w-8 border-b-2 border-blue-500"></div>
          <p className="mt-2 text-gray-600">Loading...</p>
        </div>
      )}

      {/* Sales Report Tab */}
      {activeTab === 'sales' && !loading && (
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <div className="p-4 bg-gray-50 border-b">
            <h2 className="text-lg font-semibold">Sales Report (Multi-Table JOIN)</h2>
            <p className="text-sm text-gray-600">Joins: tickets → customers → close_cash → ticket_lines</p>
          </div>
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Ticket #</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Customer</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Cashier</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Items</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Total</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {salesReport.map((sale) => (
                  <tr key={sale.ticketId}>
                    <td className="px-6 py-4 whitespace-nowrap font-mono text-sm">{sale.ticketNumber}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm">{formatDate(sale.createdAt)}</td>
                    <td className="px-6 py-4 whitespace-nowrap">{sale.customerName}</td>
                    <td className="px-6 py-4 whitespace-nowrap">{sale.cashierName || 'N/A'}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-center">{sale.itemCount}</td>
                    <td className="px-6 py-4 whitespace-nowrap font-semibold">{formatCurrency(sale.total)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            {salesReport.length === 0 && (
              <div className="text-center py-8 text-gray-500">No sales data for selected period</div>
            )}
          </div>
        </div>
      )}

      {/* Product Analytics Tab */}
      {activeTab === 'products' && !loading && (
        <div className="space-y-6">
          {/* Product Stats */}
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="p-4 bg-gray-50 border-b">
              <h2 className="text-lg font-semibold">Product Sales Statistics (Aggregation with GROUP BY)</h2>
              <p className="text-sm text-gray-600">Uses: SUM, AVG, COUNT, GROUP BY, HAVING</p>
            </div>
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Code</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Product</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Qty Sold</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Revenue</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Avg Price</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Transactions</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {productStats.map((stat) => (
                    <tr key={stat.productId}>
                      <td className="px-6 py-4 whitespace-nowrap font-mono text-sm">{stat.productCode}</td>
                      <td className="px-6 py-4">{stat.productName}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-center">{stat.totalQuantitySold}</td>
                      <td className="px-6 py-4 whitespace-nowrap font-semibold">{formatCurrency(stat.totalRevenue)}</td>
                      <td className="px-6 py-4 whitespace-nowrap">{formatCurrency(stat.averagePrice)}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-center">{stat.transactionCount}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {productStats.length === 0 && (
                <div className="text-center py-8 text-gray-500">No product data for selected period</div>
              )}
            </div>
          </div>

          {/* Below Average Products */}
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <div className="p-4 bg-gray-50 border-b">
              <h2 className="text-lg font-semibold">Products Below Average Sales (Nested Query)</h2>
              <p className="text-sm text-gray-600">Uses: Subquery in HAVING clause to find underperformers</p>
            </div>
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Product</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Revenue</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Qty Sold</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {productsBelowAvg.map((stat) => (
                    <tr key={stat.productId} className="bg-yellow-50">
                      <td className="px-6 py-4">{stat.productName}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-red-600 font-semibold">{formatCurrency(stat.totalRevenue)}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-center">{stat.totalQuantitySold}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
              {productsBelowAvg.length === 0 && (
                <div className="text-center py-8 text-gray-500">All products performing above average!</div>
              )}
            </div>
          </div>
        </div>
      )}

      {/* Customer Analytics Tab */}
      {activeTab === 'customers' && !loading && (
        <div className="bg-white rounded-lg shadow overflow-hidden">
          <div className="p-4 bg-gray-50 border-b">
            <h2 className="text-lg font-semibold">Customer Purchase Summary (Multi-Table JOIN + GROUP BY)</h2>
            <p className="text-sm text-gray-600">Aggregates: COUNT, SUM, AVG with LEFT JOIN</p>
          </div>
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Code</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Customer</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Purchases</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Total Spent</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Avg Transaction</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {customerSummary.map((customer) => (
                  <tr key={customer.customerId}>
                    <td className="px-6 py-4 whitespace-nowrap font-mono text-sm">{customer.customerCode}</td>
                    <td className="px-6 py-4">{customer.customerName}</td>
                    <td className="px-6 py-4 whitespace-nowrap text-center">{customer.totalPurchases}</td>
                    <td className="px-6 py-4 whitespace-nowrap font-semibold text-green-600">{formatCurrency(customer.totalSpent)}</td>
                    <td className="px-6 py-4 whitespace-nowrap">{formatCurrency(customer.averageTransactionValue)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            {customerSummary.length === 0 && (
              <div className="text-center py-8 text-gray-500">No customer data for selected period</div>
            )}
          </div>
        </div>
      )}

      {/* Other Reports Tab */}
      {activeTab === 'other' && (
        <div className="space-y-6">
          {/* High Value Customers */}
          <div className="bg-white rounded-lg shadow p-4">
            <div className="mb-4">
              <h2 className="text-lg font-semibold mb-2">High-Value Customers (Nested Query with EXISTS)</h2>
              <p className="text-sm text-gray-600 mb-4">Correlated subquery to find customers with transactions above threshold</p>
              <div className="flex gap-2">
                <input
                  type="number"
                  value={threshold}
                  onChange={(e) => setThreshold(parseFloat(e.target.value))}
                  className="border rounded px-3 py-2 w-40"
                  placeholder="Threshold"
                />
                <button
                  onClick={loadHighValueCustomers}
                  className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
                >
                  Load
                </button>
              </div>
            </div>
            <div className="space-y-2">
              {highValueCustomers.map((customer) => (
                <div key={customer.customerId} className="p-3 bg-green-50 rounded border border-green-200">
                  <div className="flex justify-between items-center">
                    <span className="font-semibold">{customer.customerName}</span>
                    <span className="text-green-700 font-bold">{formatCurrency(customer.totalSpent)}</span>
                  </div>
                  <div className="text-sm text-gray-600">
                    {customer.totalPurchases} purchases • Avg: {formatCurrency(customer.averageTransactionValue)}
                  </div>
                </div>
              ))}
              {highValueCustomers.length === 0 && (
                <div className="text-center py-4 text-gray-500">No high-value customers found</div>
              )}
            </div>
          </div>

          {/* Active Entities (UNION) */}
          <div className="bg-white rounded-lg shadow p-4">
            <div className="mb-4">
              <h2 className="text-lg font-semibold mb-2">All Active Entities (UNION)</h2>
              <p className="text-sm text-gray-600 mb-4">Combines customers, suppliers, and products using UNION</p>
              <button
                onClick={loadActiveEntities}
                className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
              >
                Load Active Entities
              </button>
            </div>
            <div className="max-h-96 overflow-y-auto space-y-1">
              {activeEntities.map((entity, index) => (
                <div key={index} className="p-2 bg-gray-50 rounded text-sm font-mono">
                  {entity}
                </div>
              ))}
              {activeEntities.length === 0 && (
                <div className="text-center py-4 text-gray-500">Click Load to view active entities</div>
              )}
            </div>
          </div>

          {/* Low Stock Products */}
          <div className="bg-white rounded-lg shadow p-4">
            <div className="mb-4">
              <h2 className="text-lg font-semibold mb-2">Low Stock Alert (Multi-Table JOIN)</h2>
              <p className="text-sm text-gray-600 mb-4">Products below minimum stock level</p>
              <button
                onClick={loadLowStockProducts}
                className="bg-orange-500 text-white px-4 py-2 rounded hover:bg-orange-600"
              >
                Check Low Stock
              </button>
            </div>
            <div className="space-y-2">
              {lowStockProducts.map((product, index) => (
                <div key={index} className="p-3 bg-red-50 rounded border border-red-200">
                  <span className="text-red-700 font-mono text-sm">⚠️ {product}</span>
                </div>
              ))}
              {lowStockProducts.length === 0 && (
                <div className="text-center py-4 text-green-600">✓ All products are adequately stocked</div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
