'use client';

import React, { useEffect, useState } from 'react';
import { Layout, Header, Content } from '@/components/layout/Layout';
import { Card, LoadingSpinner, Button } from '@/components/common/FormElements';
import { closeCashAPI, ticketAPI } from '@/lib/api';
import { CloseCash, Ticket } from '@/types/api';
import { formatCurrency } from '@/lib/utils';
import { FiDollarSign, FiLock, FiUnlock, FiCheckCircle } from 'react-icons/fi';

export default function CashPage() {
  const [loading, setLoading] = useState(true);
  const [activeSessions, setActiveSessions] = useState<CloseCash[]>([]);
  const [activeSession, setActiveSession] = useState<CloseCash | null>(null);
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [sessionTotals, setSessionTotals] = useState({
    totalSales: 0,
    totalReturns: 0,
    netAmount: 0
  });

  const calculateSessionTotals = (sessionTickets: Ticket[]) => {
    const totals = {
      totalSales: 0,
      totalReturns: 0,
      netAmount: 0
    };

    sessionTickets.forEach(ticket => {
      if (ticket.type === 'SALE') {
        totals.totalSales += ticket.total || 0;
      } else if (ticket.type === 'RETURN') {
        totals.totalReturns += Math.abs(ticket.total || 0);
      }
    });

    totals.netAmount = totals.totalSales - totals.totalReturns;
    return totals;
  };

  const loadData = async (skipLoading = false) => {
    if (!skipLoading) setLoading(true);
    try {
      const { data: sessions } = await closeCashAPI.getPending();
      setActiveSessions(sessions);
      
      const openSession = sessions.find(s => s.closedAt === null);
      setActiveSession(openSession || null);

      // Query recent tickets (not necessarily linked to this session yet, but for reference)
      const { data: recentTickets } = await ticketAPI.getRecent(50);
      setTickets(recentTickets);

      // Calculate totals from tickets that belong to the active session
      if (openSession && openSession.id) {
        const sessionTickets = recentTickets.filter(t => t.closeCashId === openSession.id);
        const totals = calculateSessionTotals(sessionTickets);
        setSessionTotals(totals);
      } else {
        setSessionTotals({ totalSales: 0, totalReturns: 0, netAmount: 0 });
      }
    } catch (error) {
      console.error('Failed to load cash data:', error);
    } finally {
      if (!skipLoading) setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
    
    // Auto-refresh every 5 seconds (skip loading spinner)
    const interval = setInterval(() => loadData(true), 5000);
    return () => clearInterval(interval);
  }, []);

  const handleOpenCash = async () => {
    try {
      await closeCashAPI.open();
      await loadData(true);
    } catch (error: any) {
      alert(`Failed to open cash: ${error.response?.data?.message || error.message}`);
    }
  };

  const handleCloseCash = async () => {
    if (!activeSession) return;
    if (!confirm('Are you sure you want to close this cash session?')) return;

    try {
      await closeCashAPI.close(activeSession.id);
      await loadData(true);
    } catch (error: any) {
      alert(`Failed to close cash: ${error.response?.data?.message || error.message}`);
    }
  };

  const handleReconcile = async (id: number) => {
    if (!confirm('Mark this session as reconciled?')) return;

    try {
      await closeCashAPI.reconcile(id);
      await loadData(true);
    } catch (error: any) {
      alert(`Failed to reconcile: ${error.response?.data?.message || error.message}`);
    }
  };

  if (loading) {
    return (
      <Layout>
        <Header title="Cash Management" />
        <Content>
          <div className="flex justify-center items-center h-64">
            <LoadingSpinner />
          </div>
        </Content>
      </Layout>
    );
  }

  return (
    <Layout>
      <Header title="Cash Management" subtitle="Manage cash register sessions" />
      <Content>
        <div className="space-y-6">
          {/* Current Session Card */}
          <Card className="relative overflow-hidden">
            <div className="flex justify-between items-start mb-4">
              <div>
                <h2 className="text-xl font-bold flex items-center gap-2">
                  <FiDollarSign className="text-green-500" />
                  Current Cash Session
                </h2>
                <p className="text-sm text-gray-600 mt-1">
                  {activeSession ? 'Session is open and accepting transactions' : 'No active session'}
                </p>
              </div>
              <div className="flex gap-2">
                {activeSession ? (
                  <Button onClick={handleCloseCash} variant="danger" className="flex items-center gap-2">
                    <FiLock /> Close Cash
                  </Button>
                ) : (
                  <Button onClick={handleOpenCash} variant="primary" className="flex items-center gap-2">
                    <FiUnlock /> Open Cash
                  </Button>
                )}
              </div>
            </div>

            {activeSession ? (
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-6">
                <div className="bg-blue-50 p-4 rounded-lg">
                  <div className="text-sm text-gray-600">Opened At</div>
                  <div className="text-lg font-bold text-gray-900">
                    {new Date(activeSession.openedAt).toLocaleString()}
                  </div>
                </div>
                <div className="bg-green-50 p-4 rounded-lg">
                  <div className="text-sm text-gray-600">Total Sales</div>
                  <div className="text-lg font-bold text-green-600">
                    {formatCurrency(sessionTotals.totalSales)}
                  </div>
                </div>
                <div className="bg-red-50 p-4 rounded-lg">
                  <div className="text-sm text-gray-600">Total Returns</div>
                  <div className="text-lg font-bold text-red-600">
                    {formatCurrency(sessionTotals.totalReturns)}
                  </div>
                </div>
                <div className="bg-purple-50 p-4 rounded-lg md:col-span-3">
                  <div className="text-sm text-gray-600">Net Amount (Expected Cash)</div>
                  <div className="text-2xl font-extrabold text-purple-700">
                    {formatCurrency(sessionTotals.netAmount)}
                  </div>
                </div>
                <div className="md:col-span-3">
                  <div className="text-sm text-gray-600 mb-2">Transaction Count: {tickets.length}</div>
                  <div className="text-xs text-gray-500">
                    {tickets.filter(t => t.type === 'SALE').length} sales, {tickets.filter(t => t.type === 'RETURN').length} returns
                  </div>
                </div>
              </div>
            ) : (
              <div className="text-center py-12 text-gray-500">
                <FiDollarSign className="mx-auto mb-4" size={48} />
                <p>No cash session is currently open.</p>
                <p className="text-sm mt-2">Click "Open Cash" to start a new session.</p>
              </div>
            )}
          </Card>

          {/* Previous Sessions */}
          <Card title="Pending Sessions (Not Reconciled)">
            {activeSessions.length === 0 ? (
              <p className="text-gray-500 text-center py-8">No sessions to display</p>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full">
                  <thead>
                    <tr className="border-b bg-gray-50">
                      <th className="text-left py-2 px-4">Session ID</th>
                      <th className="text-left py-2 px-4">Opened</th>
                      <th className="text-left py-2 px-4">Closed</th>
                      <th className="text-left py-2 px-4">Total Sales</th>
                      <th className="text-left py-2 px-4">Total Returns</th>
                      <th className="text-left py-2 px-4">Net Amount</th>
                      <th className="text-left py-2 px-4">Status</th>
                      <th className="text-left py-2 px-4">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {activeSessions.map((session, idx) => (
                      <tr key={session.id} className={`border-b transition-colors ${idx % 2 === 0 ? 'bg-white' : 'bg-gray-50'} hover:bg-blue-50`}>
                        <td className="py-2 px-4 font-semibold">#{session.id}</td>
                        <td className="py-2 px-4 text-sm">{new Date(session.openedAt).toLocaleString()}</td>
                        <td className="py-2 px-4 text-sm">
                          {session.closedAt ? new Date(session.closedAt).toLocaleString() : '—'}
                        </td>
                        <td className="py-2 px-4 font-semibold text-green-600">
                          {formatCurrency(session.totalSales)}
                        </td>
                        <td className="py-2 px-4 font-semibold text-red-600">
                          {formatCurrency(session.totalReturns)}
                        </td>
                        <td className="py-2 px-4 font-bold text-purple-700">
                          {formatCurrency(session.netAmount)}
                        </td>
                        <td className="py-2 px-4">
                          <span className={`px-2 py-1 rounded text-xs font-semibold ${session.closedAt ? 'bg-yellow-100 text-yellow-800' : 'bg-green-100 text-green-800'}`}>
                            {session.closedAt ? 'Closed' : 'Open'}
                          </span>
                        </td>
                        <td className="py-2 px-4">
                          {session.closedAt && !session.reconciled && (
                            <Button onClick={() => handleReconcile(session.id)} variant="primary" size="sm" className="flex items-center gap-1">
                              <FiCheckCircle size={14} /> Reconcile
                            </Button>
                          )}
                          {session.reconciled && (
                            <span className="text-xs text-gray-500">Reconciled</span>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </Card>
        </div>
      </Content>
    </Layout>
  );
}
