'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import StatusBadge from '@/components/StatusBadge';
import { User, Investment, FarmListing, PortfolioStats } from '@/lib/types';

export default function DashboardPage() {
  const router = useRouter();
  const [profile, setProfile] = useState<User | null>(null);
  const [recentInvestments, setRecentInvestments] = useState<Investment[]>([]);
  const [featuredListings, setFeaturedListings] = useState<FarmListing[]>([]);
  const [stats, setStats] = useState<PortfolioStats>({
    totalInvested: 0,
    totalReturned: 0,
    activeInvestments: 0,
    completedInvestments: 0,
    cancelledInvestments: 0,
    averageApr: 0,
  });
  const [loading, setLoading] = useState(true);
  const [investmentServiceUp, setInvestmentServiceUp] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    // 1. Profile — always works
    try {
      const res = await api.get('/users/me');
      if (res.data.success) setProfile(res.data.data);
    } catch {
      toast.error('Failed to load profile');
      setLoading(false);
      return;
    }

    // 2. Portfolio — investment service
    try {
      const res = await api.get('/portfolio');
      if (res.data.success) {
        const investments: Investment[] = res.data.data || [];
        setRecentInvestments(investments.slice(0, 3));
        const totalInvested = investments.reduce((s, i) => s + i.amountEtb, 0);
        const active = investments.filter(i => ['ACTIVE', 'ESCROW_LOCKED', 'PENDING'].includes(i.status)).length;
        const completed = investments.filter(i => i.status === 'COMPLETED').length;
        const cancelled = investments.filter(i => i.status === 'CANCELLED').length;
        const avgApr = investments.length > 0
          ? investments.reduce((s, i) => s + i.expectedReturnPct, 0) / investments.length : 0;
        setStats({ totalInvested, totalReturned: 0, activeInvestments: active, completedInvestments: completed, cancelledInvestments: cancelled, averageApr: avgApr });
        setInvestmentServiceUp(true);
      }
    } catch {
      setInvestmentServiceUp(false);
    }

    // 3. Listings — no query params to avoid type issues
    try {
      const res = await api.get('/listings');
      if (res.data.success) {
        setFeaturedListings((res.data.data || []).slice(0, 3));
        setInvestmentServiceUp(true);
      }
    } catch {
      // already handled above
    }

    setLoading(false);
  };

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-600 mx-auto mb-4" />
        <p className="text-gray-500 text-sm">Loading your dashboard...</p>
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="container mx-auto px-6 py-8">

        {/* Welcome Banner */}
        <div className="bg-gradient-to-r from-green-600 to-green-800 rounded-xl p-6 mb-8 text-white">
          <h2 className="text-2xl font-bold">Welcome back! 👋</h2>
          <p className="text-green-100 mt-1">
            {profile?.phone} &nbsp;·&nbsp;
            {profile?.kycStatus === 'VERIFIED' ? '✅ KYC Verified' : '⏳ KYC Pending'}
          </p>
        </div>

        {/* Stats Cards */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
          {[
            { label: 'Total Invested', value: `${stats.totalInvested.toLocaleString()} ETB`, color: 'text-green-600', icon: '💰' },
            { label: 'Active', value: stats.activeInvestments, color: 'text-blue-600', icon: '📈' },
            { label: 'Completed', value: stats.completedInvestments, color: 'text-gray-600', icon: '✅' },
            { label: 'Avg APR', value: `${stats.averageApr.toFixed(1)}%`, color: 'text-purple-600', icon: '📊' },
          ].map((stat) => (
            <div key={stat.label} className="bg-white rounded-xl shadow p-5">
              <div className="text-2xl mb-1">{stat.icon}</div>
              <p className="text-gray-500 text-sm">{stat.label}</p>
              <p className={`text-2xl font-bold ${stat.color}`}>{stat.value}</p>
            </div>
          ))}
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">

          {/* Recent Investments */}
          <div className="bg-white rounded-xl shadow p-6">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-semibold">Recent Investments</h3>
              <Link href="/portfolio" className="text-green-600 text-sm hover:underline">View all →</Link>
            </div>
            {recentInvestments.length === 0 ? (
              <div className="text-center py-8">
                <p className="text-4xl mb-3">🌱</p>
                <p className="text-gray-500 text-sm">No investments yet</p>
                <Link href="/listings"
                  className="mt-4 inline-block bg-green-600 text-white px-5 py-2 rounded-lg text-sm hover:bg-green-700">
                  Browse Farms
                </Link>
              </div>
            ) : (
              <div className="space-y-3">
                {recentInvestments.map((inv) => (
                  <Link key={inv.id} href={`/portfolio/${inv.id}`}
                    className="block border rounded-lg p-3 hover:bg-gray-50 transition">
                    <div className="flex justify-between items-start">
                      <div>
                        <p className="font-medium text-sm">{inv.cropType} — {inv.region}</p>
                        <p className="text-gray-500 text-xs">{inv.seasonName}</p>
                      </div>
                      <StatusBadge status={inv.status} />
                    </div>
                    <div className="flex justify-between mt-2 text-sm">
                      <span className="text-gray-700 font-medium">{inv.amountEtb.toLocaleString()} ETB</span>
                      <span className="text-green-600 font-medium">{inv.expectedReturnPct}% APR</span>
                    </div>
                  </Link>
                ))}
              </div>
            )}
          </div>

          {/* Featured Listings */}
          <div className="bg-white rounded-xl shadow p-6">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-semibold">Available Farm Listings</h3>
              <Link href="/listings" className="text-green-600 text-sm hover:underline">View all →</Link>
            </div>
            {featuredListings.length === 0 ? (
              <div className="text-center py-8">
                <p className="text-4xl mb-3">🚜</p>
                <p className="text-gray-500 text-sm">No listings available yet</p>
                <p className="text-gray-400 text-xs mt-1">
                  Listings appear when farmers submit input needs
                </p>
              </div>
            ) : (
              <div className="space-y-3">
                {featuredListings.map((listing) => (
                  <Link key={listing.id} href={`/listings/${listing.id}`}
                    className="block border rounded-lg p-3 hover:bg-gray-50 hover:border-green-300 transition">
                    <div className="flex justify-between items-start">
                      <div>
                        <p className="font-medium text-sm">{listing.cropType} — {listing.region}</p>
                        <p className="text-gray-500 text-xs">{listing.seasonName}</p>
                      </div>
                      <span className="text-green-600 font-bold text-sm">{listing.currentApr}% APR</span>
                    </div>
                    <div className="mt-2">
                      <div className="w-full bg-gray-200 rounded-full h-1.5">
                        <div className="bg-green-500 h-1.5 rounded-full"
                          style={{ width: `${Math.min(listing.fundingPct, 100)}%` }} />
                      </div>
                      <div className="flex justify-between mt-1">
                        <p className="text-xs text-gray-500">{listing.fundingPct.toFixed(1)}% funded</p>
                        <p className="text-xs text-gray-500">{listing.totalAmountEtb.toLocaleString()} ETB</p>
                      </div>
                    </div>
                  </Link>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Quick Actions */}
        <div className="mt-6 bg-white rounded-xl shadow p-6">
          <h3 className="text-lg font-semibold mb-4">Quick Actions</h3>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            {[
              { label: 'Browse Farms', href: '/listings', icon: '🌾', desc: 'Find farms to invest in' },
              { label: 'My Portfolio', href: '/portfolio', icon: '📈', desc: 'Track investments' },
              { label: 'Payout History', href: '/payouts', icon: '💵', desc: 'View earnings' },
              { label: 'Profile', href: '/profile', icon: '👤', desc: 'Account settings' },
            ].map((action) => (
              <Link key={action.href} href={action.href}
                className="border rounded-xl p-4 text-center hover:border-green-400 hover:bg-green-50 transition group">
                <div className="text-3xl mb-2">{action.icon}</div>
                <p className="font-medium text-sm group-hover:text-green-700">{action.label}</p>
                <p className="text-gray-500 text-xs mt-1">{action.desc}</p>
              </Link>
            ))}
          </div>
        </div>

      </div>
    </div>
  );
}
