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
  const [stats, setStats] = useState<PortfolioStats | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      const [profileRes, portfolioRes, listingsRes] = await Promise.allSettled([
        api.get('/users/me'),
        api.get('/portfolio'),
        api.get('/listings?minApr=0'),
      ]);

      if (profileRes.status === 'fulfilled' && profileRes.value.data.success) {
        setProfile(profileRes.value.data.data);
      }

      if (portfolioRes.status === 'fulfilled' && portfolioRes.value.data.success) {
        const investments: Investment[] = portfolioRes.value.data.data;
        setRecentInvestments(investments.slice(0, 3));

        const totalInvested = investments.reduce((s, i) => s + i.amountEtb, 0);
        const active = investments.filter(i => i.status === 'ACTIVE' || i.status === 'ESCROW_LOCKED').length;
        const completed = investments.filter(i => i.status === 'COMPLETED').length;
        const cancelled = investments.filter(i => i.status === 'CANCELLED').length;
        const avgApr = investments.length > 0
            ? investments.reduce((s, i) => s + i.expectedReturnPct, 0) / investments.length : 0;

        setStats({ totalInvested, totalReturned: 0, activeInvestments: active, completedInvestments: completed, cancelledInvestments: cancelled, averageApr: avgApr });
      }

      if (listingsRes.status === 'fulfilled' && listingsRes.value.data.success) {
        setFeaturedListings(listingsRes.value.data.data.slice(0, 3));
      }
    } catch (error) {
      toast.error('Failed to load dashboard');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-900" />
      </div>
  );

  return (
      <div className="min-h-screen bg-gray-50 pb-12">
        <Navbar />
        <div className="container mx-auto px-4 sm:px-6 py-6 max-w-6xl">

          {/* Welcome Banner (Image/Dark Card Style) */}
          <div className="relative bg-emerald-950 overflow-hidden rounded-[2rem] p-8 mb-8 text-white shadow-sm">
            {/* Decorative background overlay to mimic image hero */}
            <div className="absolute inset-0 opacity-40 bg-[url('https://images.unsplash.com/photo-1592982537447-6f296fb00fd8?auto=format&fit=crop&q=80')] bg-cover bg-center mix-blend-overlay"></div>
            <div className="relative z-10">
              <h2 className="text-3xl font-bold tracking-tight">Hi, Good Morning... 👋</h2>
              <p className="text-emerald-100/80 mt-2 font-medium">Here's your investment overview</p>
            </div>
          </div>

          {/* Stats Cards */}
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
            {[
              { label: 'Total Invested', value: `${(stats?.totalInvested || 0).toLocaleString()} ETB`, color: 'text-emerald-950', icon: '💰' },
              { label: 'Active', value: stats?.activeInvestments || 0, color: 'text-emerald-950', icon: '🌱' },
              { label: 'Completed', value: stats?.completedInvestments || 0, color: 'text-gray-500', icon: '✅' },
              { label: 'Avg APR', value: `${(stats?.averageApr || 0).toFixed(1)}%`, color: 'text-lime-600', icon: '📈' },
            ].map((stat) => (
                <div key={stat.label} className="bg-white rounded-3xl shadow-sm border border-gray-100 p-5 flex flex-col justify-between">
                  <div className="flex items-center gap-2 mb-3">
                    <div className="bg-gray-50 p-2 rounded-full text-lg">{stat.icon}</div>
                  </div>
                  <div>
                    <p className={`text-2xl font-bold tracking-tight ${stat.color}`}>{stat.value}</p>
                    <p className="text-gray-500 text-sm font-medium mt-1">{stat.label}</p>
                  </div>
                </div>
            ))}
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">

            {/* Recent Investments */}
            <div className="bg-white rounded-[2rem] shadow-sm border border-gray-100 p-6">
              <div className="flex justify-between items-center mb-6">
                <h3 className="text-xl font-bold text-emerald-950">Recent Activity</h3>
                <Link href="/portfolio" className="text-emerald-700 text-sm font-semibold hover:text-emerald-900 transition">View all</Link>
              </div>

              {recentInvestments.length === 0 ? (
                  <div className="text-center py-10 bg-gray-50 rounded-3xl">
                    <p className="text-gray-400 text-4xl mb-3">📭</p>
                    <p className="text-gray-500 font-medium">No investments yet</p>
                    <Link href="/listings" className="mt-4 inline-block bg-emerald-950 text-white px-6 py-2.5 rounded-full text-sm font-semibold hover:bg-emerald-900 transition">
                      Browse Farms
                    </Link>
                  </div>
              ) : (
                  <div className="space-y-4">
                    {recentInvestments.map((inv) => (
                        <div key={inv.id} className="flex flex-col p-4 rounded-3xl bg-gray-50 border border-gray-100 hover:bg-emerald-50/50 transition">
                          <div className="flex justify-between items-start mb-2">
                            <div className="flex items-center gap-3">
                              <div className="w-10 h-10 bg-white rounded-full flex items-center justify-center shadow-sm text-lg">🌾</div>
                              <div>
                                <p className="font-bold text-emerald-950 text-sm">{inv.cropType} • {inv.region}</p>
                                <p className="text-gray-500 text-xs font-medium">{inv.seasonName}</p>
                              </div>
                            </div>
                            <StatusBadge status={inv.status} />
                          </div>
                          <div className="flex justify-between items-center mt-2 pl-13 text-sm font-medium">
                            <span className="text-emerald-900 bg-emerald-100/50 px-3 py-1 rounded-full">{inv.amountEtb.toLocaleString()} ETB</span>
                            <span className="text-lime-700">{inv.expectedReturnPct}% APR</span>
                          </div>
                        </div>
                    ))}
                  </div>
              )}
            </div>

            {/* Featured Listings */}
            <div className="bg-white rounded-[2rem] shadow-sm border border-gray-100 p-6">
              <div className="flex justify-between items-center mb-6">
                <h3 className="text-xl font-bold text-emerald-950">Available Fields</h3>
                <Link href="/listings" className="text-emerald-700 text-sm font-semibold hover:text-emerald-900 transition">View all</Link>
              </div>

              {featuredListings.length === 0 ? (
                  <div className="text-center py-10 bg-gray-50 rounded-3xl">
                    <p className="text-gray-400 text-4xl mb-3">🌱</p>
                    <p className="text-gray-500 font-medium">No listings available yet</p>
                  </div>
              ) : (
                  <div className="space-y-4">
                    {featuredListings.map((listing) => (
                        <Link key={listing.id} href={`/listings/${listing.id}`} className="block rounded-3xl border border-gray-100 overflow-hidden hover:shadow-md transition bg-white">
                          <div className="p-4">
                            <div className="flex justify-between items-start mb-3">
                              <div className="flex items-center gap-3">
                                <div className="w-12 h-12 bg-lime-100 rounded-2xl flex items-center justify-center text-xl">🚜</div>
                                <div>
                                  <p className="font-bold text-emerald-950">{listing.cropType} Field</p>
                                  <p className="text-gray-500 text-xs font-medium">{listing.region} • {listing.seasonName}</p>
                                </div>
                              </div>
                              <span className="bg-lime-200 text-emerald-950 font-bold text-xs px-3 py-1.5 rounded-full">{listing.currentApr}% APR</span>
                            </div>
                            <div className="mt-4 bg-gray-50 p-3 rounded-2xl">
                              <div className="flex justify-between text-xs font-medium text-emerald-900 mb-2">
                                <span>Progress</span>
                                <span>{listing.fundingPct.toFixed(1)}% funded</span>
                              </div>
                              <div className="w-full bg-gray-200 rounded-full h-2">
                                <div className="bg-emerald-950 h-2 rounded-full" style={{ width: `${Math.min(listing.fundingPct, 100)}%` }} />
                              </div>
                            </div>
                          </div>
                        </Link>
                    ))}
                  </div>
              )}
            </div>
          </div>

          {/* Quick Actions (Bottom Pill style) */}
          <div className="mt-8 bg-emerald-950 rounded-[2.5rem] p-2 max-w-2xl mx-auto shadow-lg flex justify-between items-center px-4">
            {[
              { label: 'Home', href: '/dashboard', icon: '🏠', active: true },
              { label: 'Fields', href: '/listings', icon: '🗺️', active: false },
              { label: 'Portfolio', href: '/portfolio', icon: '💼', active: false },
              { label: 'Profile', href: '/profile', icon: '👤', active: false },
            ].map((action) => (
                <Link key={action.href} href={action.href}
                      className={`flex items-center gap-2 px-6 py-3 rounded-full transition ${action.active ? 'bg-white text-emerald-950' : 'text-white hover:bg-emerald-900'}`}>
                  <div className="text-lg">{action.icon}</div>
                  {action.active && <span className="font-bold text-sm">{action.label}</span>}
                </Link>
            ))}
          </div>

        </div>
      </div>
  );
}