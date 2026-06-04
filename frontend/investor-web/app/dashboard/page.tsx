'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import StatusBadge from '@/components/StatusBadge';
import RoiChart from '@/components/RoiChart';
import { User, Investment, FarmListing, PortfolioStats, WeatherAlert } from '@/lib/types';

export default function DashboardPage() {
  const router = useRouter();
  const [profile, setProfile] = useState<User | null>(null);
  const [recentInvestments, setRecentInvestments] = useState<Investment[]>([]);
  const [featuredListings, setFeaturedListings] = useState<FarmListing[]>([]);
  const [alerts, setAlerts] = useState<WeatherAlert[]>([]);
  const [stats, setStats] = useState<PortfolioStats>({
    totalInvested: 0, totalReturned: 0, activeInvestments: 0,
    completedInvestments: 0, cancelledInvestments: 0, averageApr: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    loadDashboard();
  }, []);

  // 60-second polling for live updates (SRS 6.3.1)
  useEffect(() => {
    const interval = setInterval(() => {
      api.get('/portfolio').then(r => {
        if (r.data.success) setRecentInvestments((r.data.data || []).slice(0, 3));
      }).catch(() => {});
    }, 60000);
    return () => clearInterval(interval);
  }, []);

  const loadDashboard = async () => {
    try {
      const res = await api.get('/users/me');
      if (res.data.success) setProfile(res.data.data);
    } catch { toast.error('Failed to load profile'); setLoading(false); return; }

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
      }
    } catch {}

    try {
      const res = await api.get('/listings');
      if (res.data.success) setFeaturedListings((res.data.data || []).slice(0, 3));
    } catch {}

    // Try to load weather alerts from any invested farm
    try {
      const portfolio = await api.get('/portfolio');
      if (portfolio.data.success && portfolio.data.data?.length > 0) {
        const farmId = portfolio.data.data[0].farmId;
        const alertRes = await api.get(`/weather/alerts/${farmId}`);
        if (alertRes.data.success) setAlerts((alertRes.data.data || []).slice(0, 3));
      }
    } catch {}

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

  const severityColor: Record<string, string> = {
    HIGH: 'bg-red-50 border-red-200 text-red-700',
    MEDIUM: 'bg-yellow-50 border-yellow-200 text-yellow-700',
    LOW: 'bg-blue-50 border-blue-200 text-blue-700',
    CRITICAL: 'bg-red-100 border-red-300 text-red-800',
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
      <div className="container mx-auto px-6 py-8 max-w-7xl">

        {/* Welcome Banner */}
        <div className="bg-gradient-to-r from-green-700 to-emerald-900 rounded-2xl p-6 mb-6 text-white">
          <div className="flex justify-between items-start">
            <div>
              <h2 className="text-2xl font-bold">Welcome back! 👋</h2>
              <p className="text-green-100 mt-1">
                {profile?.phone} &nbsp;·&nbsp;
                {profile?.kycStatus === 'VERIFIED' ? '✅ KYC Verified' : '⏳ KYC Pending'}
              </p>
            </div>
            <Link href="/listings"
              className="bg-white/20 hover:bg-white/30 transition text-white px-4 py-2 rounded-xl text-sm font-semibold">
              + Invest Now
            </Link>
          </div>
        </div>

        {/* Alert Strip — SRS requirement */}
        {alerts.length > 0 && (
          <div className="space-y-2 mb-6">
            {alerts.map(alert => (
              <div key={alert.id} className={`flex items-start gap-3 border rounded-xl px-4 py-3 text-sm ${severityColor[alert.severity] || 'bg-gray-50 border-gray-200'}`}>
                <span className="text-lg">⚠️</span>
                <div className="flex-1">
                  <span className="font-bold">{alert.alertType.replace(/_/g, ' ')}</span>
                  {' — '}{alert.messageEn}
                </div>
                <span className="text-xs opacity-60">{new Date(alert.createdAt).toLocaleDateString()}</span>
              </div>
            ))}
          </div>
        )}

        {/* Stats Cards */}
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6">
          {[
            { label: 'Total Invested', value: `${stats.totalInvested.toLocaleString()} ETB`, color: 'text-green-600', icon: '💰', bg: 'bg-green-50' },
            { label: 'Active Investments', value: stats.activeInvestments, color: 'text-blue-600', icon: '📈', bg: 'bg-blue-50' },
            { label: 'Completed', value: stats.completedInvestments, color: 'text-gray-700', icon: '✅', bg: 'bg-gray-50' },
            { label: 'Avg APR', value: `${stats.averageApr.toFixed(1)}%`, color: 'text-purple-600', icon: '📊', bg: 'bg-purple-50' },
          ].map((stat) => (
            <div key={stat.label} className="bg-white rounded-2xl shadow-sm border border-gray-100 p-5">
              <div className={`w-10 h-10 ${stat.bg} rounded-xl flex items-center justify-center text-xl mb-3`}>{stat.icon}</div>
              <p className="text-gray-500 text-xs font-medium uppercase tracking-wide">{stat.label}</p>
              <p className={`text-2xl font-bold ${stat.color} mt-1`}>{stat.value}</p>
            </div>
          ))}
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-6">

          {/* Recent Investments with NDVI trend (SRS) */}
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 lg:col-span-1">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-base font-bold text-gray-800">My Portfolio</h3>
              <Link href="/portfolio" className="text-green-600 text-sm font-medium hover:underline">View all →</Link>
            </div>
            {recentInvestments.length === 0 ? (
              <div className="text-center py-8">
                <p className="text-4xl mb-3">🌱</p>
                <p className="text-gray-500 text-sm">No investments yet</p>
                <Link href="/listings" className="mt-4 inline-block bg-green-600 text-white px-5 py-2 rounded-xl text-sm font-semibold hover:bg-green-700">Browse Farms</Link>
              </div>
            ) : (
              <div className="space-y-3">
                {recentInvestments.map((inv) => (
                  <Link key={inv.id} href={`/portfolio/${inv.id}`}
                    className="block border border-gray-100 rounded-xl p-3 hover:bg-gray-50 transition">
                    <div className="flex justify-between items-start">
                      <div>
                        <p className="font-semibold text-sm text-gray-800">{inv.cropType} — {inv.region}</p>
                        <p className="text-gray-400 text-xs">{inv.seasonName}</p>
                      </div>
                      <StatusBadge status={inv.status} />
                    </div>
                    <div className="flex justify-between mt-2 text-sm">
                      <span className="text-gray-700 font-semibold">{inv.amountEtb.toLocaleString()} ETB</span>
                      <span className="text-green-600 font-semibold">{inv.expectedReturnPct}% APR</span>
                    </div>
                  </Link>
                ))}
              </div>
            )}
          </div>

          {/* ROI vs Inflation Chart — SRS requirement */}
          <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 lg:col-span-2">
            <div className="flex justify-between items-center mb-4">
              <div>
                <h3 className="text-base font-bold text-gray-800">ROI vs Inflation</h3>
                <p className="text-xs text-gray-400 mt-0.5">Your Agri-Yield returns vs Ethiopia inflation rate</p>
              </div>
            </div>
            <RoiChart averageApr={stats.averageApr || 18} />
            <p className="text-xs text-gray-400 mt-2 text-center">
              Avg APR: <span className="text-green-600 font-bold">{stats.averageApr.toFixed(1)}%</span>
              &nbsp;vs Inflation: <span className="text-orange-500 font-bold">~22%</span>
            </p>
          </div>
        </div>

        {/* Farm Listings — Discover Section (SRS) */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-6">
          <div className="flex justify-between items-center mb-4">
            <h3 className="text-base font-bold text-gray-800">Discover Farm Listings</h3>
            <Link href="/listings" className="text-green-600 text-sm font-medium hover:underline">View all →</Link>
          </div>
          {featuredListings.length === 0 ? (
            <div className="text-center py-8">
              <p className="text-4xl mb-3">🚜</p>
              <p className="text-gray-500 text-sm">No listings available yet</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              {featuredListings.map((listing) => (
                <Link key={listing.id} href={`/listings/${listing.id}`}
                  className="block border border-gray-100 rounded-xl p-4 hover:border-green-300 hover:bg-green-50/50 transition">
                  <div className="flex justify-between items-start mb-3">
                    <div>
                      <p className="font-bold text-sm text-gray-800">{listing.cropType} — {listing.region}</p>
                      <p className="text-gray-400 text-xs">{listing.seasonName}</p>
                    </div>
                    <span className="bg-green-100 text-green-700 font-bold text-xs px-2 py-1 rounded-full">{listing.currentApr}% APR</span>
                  </div>
                  {/* NDVI badge — SRS requirement */}
                  <div className="flex gap-2 mb-3">
                    <span className="bg-emerald-50 text-emerald-700 text-xs px-2 py-1 rounded-full font-medium">
                      Agri-Score: {listing.agriScore}/900
                    </span>
                    <StatusBadge status={listing.status} />
                  </div>
                  <div className="w-full bg-gray-200 rounded-full h-1.5">
                    <div className="bg-green-500 h-1.5 rounded-full transition-all"
                      style={{ width: `${Math.min(listing.fundingPct, 100)}%` }} />
                  </div>
                  <div className="flex justify-between mt-1">
                    <p className="text-xs text-gray-400">{listing.fundingPct?.toFixed(1)}% funded</p>
                    <p className="text-xs text-gray-500 font-medium">{listing.totalAmountEtb?.toLocaleString()} ETB</p>
                  </div>
                </Link>
              ))}
            </div>
          )}
        </div>

        {/* Quick Actions */}
        <div className="bg-white rounded-2xl shadow-sm border border-gray-100 p-6">
          <h3 className="text-base font-bold text-gray-800 mb-4">Quick Actions</h3>
          <div className="grid grid-cols-2 md:grid-cols-5 gap-3">
            {[
              { label: 'Browse Farms', href: '/listings', icon: '🌾', desc: 'Find farms to invest in' },
              { label: 'My Portfolio', href: '/portfolio', icon: '📈', desc: 'Track investments' },
              { label: 'Payout History', href: '/payouts', icon: '💵', desc: 'View earnings' },
              { label: 'Statements', href: '/statements', icon: '📄', desc: 'Download PDF reports' },
              { label: 'Profile', href: '/profile', icon: '👤', desc: 'Account settings' },
            ].map((action) => (
              <Link key={action.href} href={action.href}
                className="border border-gray-100 rounded-xl p-4 text-center hover:border-green-400 hover:bg-green-50 transition group">
                <div className="text-2xl mb-2">{action.icon}</div>
                <p className="font-semibold text-sm text-gray-700 group-hover:text-green-700">{action.label}</p>
                <p className="text-gray-400 text-xs mt-1">{action.desc}</p>
              </Link>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}
