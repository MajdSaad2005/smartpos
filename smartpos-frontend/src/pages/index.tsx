'use client';

import React, { useEffect, useState } from 'react';
import { Layout, Header, Content } from '@/components/layout/Layout';
import { Card, LoadingSpinner } from '@/components/common/FormElements';
import { ResponsiveContainer, PieChart, Pie, Cell, Tooltip } from 'recharts';
import { closeCashAPI, ticketAPI, dashboardAPI } from '@/lib/api';
import { CloseCash, Ticket } from '@/types/api';
import { formatCurrency } from '@/lib/utils';

export default function Dashboard() {
  const [loading, setLoading] = useState(true);
  const [closeCash, setCloseCash] = useState<CloseCash | null>(null);
  const [recentTickets, setRecentTickets] = useState<Ticket[]>([]);
  const [stats, setStats] = useState({
    totalSales: 0,
    totalReturns: 0,
    netAmount: 0,
    netProfit: 0,
    transactionCount: 0,
  });

  const fetchData = async (skipLoading = false) => {
    if (!skipLoading) setLoading(true);
    try {
      // Get open cash session
      const { data: closeCashData } = await closeCashAPI.getPending();
      const openSession = closeCashData.find(s => s.closedAt === null);
      setCloseCash(openSession || null);

      // Get recent tickets for the table display
      const { data: allTicketsData } = await ticketAPI.getRecent(50);
      setRecentTickets(allTicketsData);

      // Fetch aggregated stats from the SQL view (v_dashboard_stats)
      // This calculates all metrics in MySQL instead of JavaScript
      const { data: dashboardStats } = await dashboardAPI.getStats(7);
      
      setStats({
        totalSales: dashboardStats.totalSales || 0,
        totalReturns: dashboardStats.totalReturns || 0,
        netAmount: dashboardStats.netAmount || 0,
        netProfit: dashboardStats.netProfit || 0,
        transactionCount: dashboardStats.transactionCount || 0,
      });
    } catch (error) {
      console.error('Failed to fetch dashboard data:', error);
    } finally {
      if (!skipLoading) setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
    
    // Auto-refresh every 5 seconds (skip loading spinner)
    const interval = setInterval(() => fetchData(true), 5000);
    return () => clearInterval(interval);
  }, []);

  if (loading) {
    return (
      <Layout>
        <Header title="Dashboard" />
        <Content>
          <div className="flex justify-center items-center h-64">
            <LoadingSpinner />
          </div>
        </Content>
      </Layout>
    );
  }

  const salesByType = [
    { name: 'Sales', value: stats.totalSales },
    { name: 'Returns', value: stats.totalReturns },
  ];

  return (
    <Layout>
      <Header title="Dashboard" subtitle="Welcome back! Here's your sales overview." />
      <Content>
        <div className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
            <Card className="group overflow-hidden bg-gradient-to-br from-blue-50 to-white">
              <div className="text-sm font-medium text-gray-600 flex items-center gap-2">Total Sales
                <span className="inline-block h-2 w-2 rounded-full bg-blue-500 animate-pulse" />
              </div>
              <div className="text-3xl font-extrabold text-gray-900 mt-2 transition-transform duration-300 group-hover:scale-105">
                {formatCurrency(stats.totalSales)}
              </div>
              <div className="mt-3 h-1 w-full bg-blue-100">
                <div className="h-full bg-blue-500 transition-all duration-700" style={{ width: `${Math.min(100, (stats.totalSales || 0) / Math.max(1, (stats.totalSales + stats.totalReturns)) * 100)}%` }} />
              </div>
            </Card>
            <Card className="group overflow-hidden bg-gradient-to-br from-red-50 to-white">
              <div className="text-sm font-medium text-gray-600 flex items-center gap-2">Total Returns
                <span className="inline-block h-2 w-2 rounded-full bg-red-500 animate-pulse" />
              </div>
              <div className="text-3xl font-extrabold text-red-600 mt-2 transition-transform duration-300 group-hover:scale-105">
                {formatCurrency(stats.totalReturns)}
              </div>
              <div className="mt-3 h-1 w-full bg-red-100">
                <div className="h-full bg-red-500 transition-all duration-700" style={{ width: `${Math.min(100, (stats.totalReturns || 0) / Math.max(1, (stats.totalSales + stats.totalReturns)) * 100)}%` }} />
              </div>
            </Card>
            <Card className="group overflow-hidden bg-gradient-to-br from-green-50 to-white">
              <div className="text-sm font-medium text-gray-600">Net Amount</div>
              <div className="text-3xl font-extrabold text-green-600 mt-2 transition-transform duration-300 group-hover:scale-105">
                {formatCurrency(stats.netAmount)}
              </div>
              <div className="mt-3 text-xs text-gray-500">Sales − Returns</div>
            </Card>
            <Card className="group overflow-hidden bg-gradient-to-br from-purple-50 to-white">
              <div className="text-sm font-medium text-gray-600">Net Profit</div>
              <div className="text-3xl font-extrabold text-purple-700 mt-2 transition-transform duration-300 group-hover:scale-105">
                {formatCurrency(stats.netProfit)}
              </div>
              <div className="mt-3 text-xs text-gray-500">Margin × Quantity</div>
            </Card>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card title="Sales Distribution" className="overflow-hidden">
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie data={salesByType} cx="50%" cy="50%" labelLine={false} label={({ name, value }) => `${name}: ${formatCurrency(value)}`} outerRadius={80} fill="#8884d8" dataKey="value">
                    <Cell fill="#3b82f6" />
                    <Cell fill="#ef4444" />
                  </Pie>
                  <Tooltip formatter={(value) => formatCurrency(value as number)} />
                </PieChart>
              </ResponsiveContainer>
            </Card>

            <Card title="Cash Register Status" className="relative">
              {closeCash ? (
                <div className="space-y-3">
                  <div className="flex justify-between">
                    <span className="text-gray-600">Status</span>
                    <span className="font-semibold">{closeCash.closedAt ? 'Closed' : 'Open'}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600">Opened</span>
                    <span className="font-semibold">{new Date(closeCash.openedAt).toLocaleTimeString()}</span>
                  </div>
                  {closeCash.closedAt && (
                    <div className="flex justify-between">
                      <span className="text-gray-600">Closed</span>
                      <span className="font-semibold">{new Date(closeCash.closedAt).toLocaleTimeString()}</span>
                    </div>
                  )}
                  <div className="flex justify-between pt-3 border-t">
                    <span className="text-gray-600 font-semibold">Net Amount</span>
                    <span className="font-semibold text-lg text-green-600">{formatCurrency(closeCash.netAmount)}</span>
                  </div>
                </div>
              ) : (
                <div className="flex items-center gap-3 text-gray-500">
                  <span className="inline-block h-2 w-2 rounded-full bg-gray-400 animate-ping" />
                  <p>No active cash session</p>
                </div>
              )}
            </Card>
          </div>

          <Card title="Recent Transactions">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b bg-gray-50">
                    <th className="text-left py-2 px-4">Ticket #</th>
                    <th className="text-left py-2 px-4">Type</th>
                    <th className="text-left py-2 px-4">Amount</th>
                    <th className="text-left py-2 px-4">Status</th>
                    <th className="text-left py-2 px-4">Time</th>
                  </tr>
                </thead>
                <tbody>
                  {recentTickets.slice(0, 5).map((ticket, idx) => (
                    <tr key={ticket.id} className={`border-b transition-colors ${idx % 2 === 0 ? 'bg-white' : 'bg-gray-50'} hover:bg-blue-50`}>
                      <td className="py-2 px-4 font-semibold">{ticket.number}</td>
                      <td className="py-2 px-4">
                        <span className={`px-2 py-1 rounded text-xs font-semibold ${ticket.type === 'SALE' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'}`}>
                          {ticket.type}
                        </span>
                      </td>
                      <td className="py-2 px-4 font-semibold">{formatCurrency(ticket.total)}</td>
                      <td className="py-2 px-4">
                        <span className={`px-2 py-1 rounded text-xs font-semibold ${ticket.status === 'COMPLETED' ? 'bg-blue-100 text-blue-800' : 'bg-yellow-100 text-yellow-800'}`}>
                          {ticket.status}
                        </span>
                      </td>
                      <td className="py-2 px-4 text-sm text-gray-600">{new Date(ticket.createdAt).toLocaleTimeString()}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </Card>
        </div>
      </Content>
    </Layout>
  );
}
