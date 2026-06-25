"use client";

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import Navbar from '@/components/Navbar';
import Icon from '@/components/Icons';
import StatusBadge from '@/components/StatusBadge';
import { getMyProfile, getMyBids } from '@/lib/api';
import type { UserProfile, Bid } from '@/lib/types';

export default function DashboardPage() {
  const router = useRouter();
  const [user,    setUser]    = useState<UserProfile | null>(null);
  const [bids,    setBids]    = useState<Bid[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!localStorage.getItem('access_token')) { router.push('/login'); return; }
    load();
  }, []);

  const load = async () => {
    try {
      const [profileRes, bidsRes] = await Promise.allSettled([
        getMyProfile(),
        getMyBids(),
      ]);
      if (profileRes.status === 'fulfilled') setUser(profileRes.value.data.data);
      if (bidsRes.status === 'fulfilled')    setBids(bidsRes.value.data.data || []);
    } catch { toast.error('Failed to load dashboard'); }
    finally  { setLoading(false); }
  };

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-teal-600" />
    </div>
  );

  const activeBids    = bids.filter(b => ['PENDING','ACCEPTED','CONTRACT_SIGNED'].includes(b.status));
  const completedBids = bids.filter(b => b.status === 'COMPLETED');
  const totalSpend    = completedBids.reduce((s, b) => s + b.totalValueEtb, 0);
  const totalQuintals = completedBids.reduce((s, b) => s + b.quantityQuintals, 0);

  return (
    <div className="min-h-screen bg-gray-50 pb-20 md:pb-0">
      <Navbar />
      <div className="container mx-auto px-4 sm:px-6 py-6 max-w-6xl">

        {/* Welcome */}
        <div className="bg-gradient-to-r from-teal-700 to-cyan-700 rounded-2xl p-6 mb-6 text-white">
          <div className="flex justify-between items-start">
            <div>
              <h2 className="text-2xl font-bold">Welcome back!</h2>
              <p className="text-teal-100 mt-1 text-sm">
                {user?.phone} &nbsp;·&nbsp;
                {user?.kycStatus === 'VERIFIED' ? 'KYC Verified' : 'KYC Pending'}
              </p>
            </div>
            <Link href="/farms"
              className="bg-white/20 hover:bg-white/30 transition text-white px-4 py-2 rounded-xl text-sm font-semibold">
              + Find Farms
            </Link>
          </div>
        </div>

        {/* Stats */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
            {[
            { label: 'Active Bids',       value: activeBids.length,                         icon: 'money', color: 'text-teal-600',   bg: 'bg-teal-50'   },
            { label: 'Completed Deals',   value: completedBids.length,                       icon: 'check', color: 'text-green-600',  bg: 'bg-green-50'  },
            { label: 'Total Procured',    value: `${totalQuintals.toFixed(1)} qt`,            icon: 'farm', color: 'text-orange-600', bg: 'bg-orange-50' },
            { label: 'Total Spend',       value: `${totalSpend.toLocaleString()} ETB`,        icon: 'analytics', color: 'text-blue-600',   bg: 'bg-blue-50'   },
          ].map(s => (
            <div key={s.label} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-4">
              <div className={`w-9 h-9 ${s.bg} rounded-xl flex items-center justify-center text-xl mb-2`}>
                <Icon name={s.icon} className="h-5 w-5 text-current" />
              </div>
              <p className="text-gray-400 text-xs font-medium uppercase tracking-wide">{s.label}</p>
              <p className={`text-xl font-bold ${s.color} mt-0.5`}>{s.value}</p>
            </div>
          ))}
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

          {/* Recent bids */}
          <div className="lg:col-span-2 bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
            <div className="flex justify-between items-center mb-4">
              <h3 className="font-bold text-gray-800">Recent Bids</h3>
              <Link href="/bids" className="text-teal-600 text-sm font-medium hover:underline">View all →</Link>
            </div>
            {bids.length === 0 ? (
              <div className="text-center py-10">
                <p className="text-3xl mb-2"></p>
                <p className="text-gray-400 text-sm">No bids yet</p>
                <Link href="/farms" className="mt-3 inline-block bg-teal-600 text-white px-4 py-2 rounded-xl text-sm font-semibold hover:bg-teal-700">
                  Browse Farms
                </Link>
              </div>
            ) : (
              <div className="space-y-3">
                {bids.slice(0, 5).map(bid => (
                  <div key={bid.id} className="flex items-center justify-between border border-gray-100 rounded-xl p-3">
                    <div>
                      <p className="text-sm font-semibold text-gray-800">
                        {bid.quantityQuintals} qt @ {bid.pricePerQuintalEtb.toLocaleString()} ETB/qt
                      </p>
                      <p className="text-xs text-gray-400 mt-0.5">
                        Total: {bid.totalValueEtb.toLocaleString()} ETB · {new Date(bid.createdAt).toLocaleDateString()}
                      </p>
                    </div>
                    <StatusBadge status={bid.status} />
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Quick actions */}
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
            <h3 className="font-bold text-gray-800 mb-4">Quick Actions</h3>
            <div className="space-y-3">
                {[
                { href: '/farms',     icon: '', label: 'Browse farms',       desc: 'Find harvests to buy' },
                { href: '/bids',      icon: '', label: 'My bids',            desc: 'Track all bids' },
                { href: '/logistics', icon: '', label: 'Logistics',          desc: 'Manage dispatches' },
                { href: '/analytics', icon: '', label: 'Analytics',          desc: 'Procurement insights' },
                { href: '/profile',   icon: '', label: 'Profile',            desc: 'Account settings' },
              ].map(a => (
                <Link key={a.href} href={a.href}
                  className="flex items-center gap-3 p-3 border border-gray-100 rounded-xl hover:border-teal-300 hover:bg-teal-50 transition group">
                  <span className="text-xl">{a.icon}</span>
                  <div>
                    <p className="text-sm font-semibold text-gray-700 group-hover:text-teal-700">{a.label}</p>
                    <p className="text-xs text-gray-400">{a.desc}</p>
                  </div>
                </Link>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
