'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import Navbar from '@/components/Navbar';
import StatusBadge from '@/components/StatusBadge';
import { getMyBids } from '@/lib/api';
import type { Bid } from '@/lib/types';

type TabKey = 'ALL' | 'PENDING' | 'ACCEPTED' | 'CONTRACT_SIGNED' | 'COMPLETED' | 'EXPIRED';

export default function BidsPage() {
  const router = useRouter();
  const [bids,    setBids]    = useState<Bid[]>([]);
  const [loading, setLoading] = useState(true);
  const [tab,     setTab]     = useState<TabKey>('ALL');

  useEffect(() => {
    if (!localStorage.getItem('access_token')) { router.push('/login'); return; }
    load();
  }, []);

  const load = async () => {
    try {
      const res = await getMyBids();
      if (res.data.success) setBids(res.data.data || []);
    } catch {
      toast.error('Failed to load bids');
    } finally {
      setLoading(false);
    }
  };

  const TABS: { key: TabKey; label: string }[] = [
    { key: 'ALL',             label: 'All' },
    { key: 'PENDING',         label: 'Pending' },
    { key: 'ACCEPTED',        label: 'Accepted' },
    { key: 'CONTRACT_SIGNED', label: 'Signed' },
    { key: 'COMPLETED',       label: 'Completed' },
    { key: 'EXPIRED',         label: 'Expired' },
  ];

  const filtered = bids.filter(b => tab === 'ALL' || b.status === tab);

  const totalActive = bids
    .filter(b => ['PENDING', 'ACCEPTED', 'CONTRACT_SIGNED'].includes(b.status))
    .reduce((s, b) => s + b.totalValueEtb, 0);

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-teal-600" />
    </div>
  );

  return (
    <div className="min-h-screen bg-gray-50 pb-20 md:pb-0">
      <Navbar />
      <div className="container mx-auto px-4 sm:px-6 py-6 max-w-5xl">

        <div className="flex justify-between items-center mb-6">
          <div>
            <h1 className="text-2xl font-bold text-gray-900">My Bids</h1>
            <p className="text-gray-500 mt-1 text-sm">Track all your purchase bids and contracts</p>
          </div>
          <Link href="/farms"
            className="bg-teal-700 text-white px-4 py-2 rounded-xl text-sm font-semibold hover:bg-teal-600 transition">
            + New Bid
          </Link>
        </div>

        <div className="grid grid-cols-3 gap-4 mb-6">
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
            <p className="text-gray-400 text-xs uppercase tracking-wide font-medium">Total Bids</p>
            <p className="text-2xl font-bold text-teal-600 mt-1">{bids.length}</p>
          </div>
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
            <p className="text-gray-400 text-xs uppercase tracking-wide font-medium">Active Value</p>
            <p className="text-2xl font-bold text-orange-600 mt-1">{totalActive.toLocaleString()} ETB</p>
          </div>
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
            <p className="text-gray-400 text-xs uppercase tracking-wide font-medium">Completed</p>
            <p className="text-2xl font-bold text-green-600 mt-1">
              {bids.filter(b => b.status === 'COMPLETED').length}
            </p>
          </div>
        </div>

        <div className="flex gap-1 bg-white rounded-xl shadow-sm border border-gray-100 p-1 mb-5 flex-wrap">
          {TABS.map(t => (
            <button key={t.key} onClick={() => setTab(t.key)}
              className={`px-3 py-1.5 rounded-lg text-sm font-semibold transition ${
                tab === t.key ? 'bg-teal-600 text-white' : 'text-gray-500 hover:bg-gray-100'
              }`}>
              {t.label}
              <span className="ml-1 opacity-60 text-xs">
                ({t.key === 'ALL' ? bids.length : bids.filter(b => b.status === t.key).length})
              </span>
            </button>
          ))}
        </div>

        {filtered.length === 0 ? (
            <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-12 text-center">
            <p className="text-4xl mb-3"></p>
            <p className="text-gray-500 font-medium">No bids in this category</p>
            <Link href="/farms" className="mt-3 inline-block bg-teal-600 text-white px-4 py-2 rounded-xl text-sm font-semibold hover:bg-teal-700">
              Browse Farms
            </Link>
          </div>
        ) : (
          <div className="space-y-4">
            {filtered.map(bid => {
              const expiresIn = Math.ceil(
                (new Date(bid.expiresAt).getTime() - Date.now()) / 86400000);

              return (
                <div key={bid.id} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
                  <div className="flex justify-between items-start mb-3">
                    <div>
                      <p className="font-bold text-gray-800">
                        {bid.quantityQuintals} quintals @ {bid.pricePerQuintalEtb.toLocaleString()} ETB/qt
                      </p>
                      <p className="text-xs text-gray-400 font-mono mt-0.5">
                        Farm: {bid.farmId.slice(0, 12)}…
                      </p>
                    </div>
                    <div className="flex flex-col items-end gap-1">
                      <StatusBadge status={bid.status} />
                      {bid.status === 'PENDING' && expiresIn > 0 && (
                        <span className="text-xs text-orange-500 font-medium">⏱ {expiresIn}d left</span>
                      )}
                    </div>
                  </div>

                  <div className="grid grid-cols-2 md:grid-cols-4 gap-3 text-xs border-t border-gray-100 pt-3">
                    <div>
                      <p className="text-gray-400">Total Value</p>
                      <p className="font-bold text-teal-700">{bid.totalValueEtb.toLocaleString()} ETB</p>
                    </div>
                    <div>
                      <p className="text-gray-400">Deposit (10%)</p>
                      <p className="font-semibold text-gray-700">{bid.bidDepositEtb.toLocaleString()} ETB</p>
                    </div>
                    <div>
                      <p className="text-gray-400">Placed</p>
                      <p className="font-semibold">{new Date(bid.createdAt).toLocaleDateString()}</p>
                    </div>
                    <div>
                      <p className="text-gray-400">Expires</p>
                      <p className="font-semibold">{new Date(bid.expiresAt).toLocaleDateString()}</p>
                    </div>
                  </div>

                  {bid.status === 'ACCEPTED' && bid.agreementId && (
                    <div className="mt-3 bg-blue-50 border border-blue-100 rounded-xl p-3">
                      <p className="text-sm font-semibold text-blue-800 mb-2">Contract ready for signing</p>
                      <p className="text-xs text-blue-600 mb-3">
                        The farmer has accepted your bid. Sign the purchase agreement to proceed.
                      </p>
                      <Link href={`/agreements/${bid.agreementId}`}
                        className="inline-block bg-blue-700 text-white px-4 py-2 rounded-lg text-sm font-semibold hover:bg-blue-800 transition">
                        View &amp; Sign Agreement →
                      </Link>
                    </div>
                  )}

                  {bid.status === 'ACCEPTED' && !bid.agreementId && (
                    <div className="mt-3 bg-amber-50 border border-amber-100 rounded-xl p-3">
                        <p className="text-sm text-amber-700">Agreement being generated — refresh in a moment.</p>
                    </div>
                  )}

                  {bid.status === 'CONTRACT_SIGNED' && bid.agreementId && (
                    <div className="mt-3 bg-green-50 border border-green-100 rounded-xl p-3 flex justify-between items-center">
                      <p className="text-sm font-semibold text-green-800">Contract signed — schedule pickup</p>
                      <Link href={`/logistics?agreementId=${bid.agreementId}`}
                        className="bg-green-700 text-white px-3 py-1.5 rounded-lg text-xs font-semibold hover:bg-green-800 transition">
                        Schedule Dispatch →
                      </Link>
                    </div>
                  )}

                  <div className="flex gap-3 mt-3 pt-3 border-t border-gray-100">
                    <button onClick={() => router.push(`/farms?lookup=${bid.farmId}`)}
                      className="text-teal-600 text-sm font-semibold hover:underline">
                      View Farm →
                    </button>
                    <span className="text-gray-200">|</span>
                    <p className="text-xs text-gray-400 font-mono self-center">
                      Bid: {bid.id.slice(0, 12)}…
                    </p>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
