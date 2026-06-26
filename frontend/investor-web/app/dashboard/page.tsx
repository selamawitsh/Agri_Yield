'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import StatusBadge from '@/components/StatusBadge';
import RoiChart from '@/components/RoiChart';
import {
  LayoutDashboard,
  Sprout,
  Wallet,
  FileText,
  BarChart3,
  AlertTriangle,
  TrendingUp,
  CheckCircle2,
  Search,
  History,
  User as UserIcon,
  CircleDollarSign,
  Menu,
  X,
  LogOut,
  Bell,
  CloudSun
} from 'lucide-react';
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
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [activeTab, setActiveTab] = useState('dashboard');

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    loadDashboard();
  }, []);

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

  const handleLogout = () => {
    localStorage.removeItem('access_token');
    router.push('/login');
  };

  if (loading) return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50 font-sans">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-emerald-600 mx-auto mb-4" />
          <p className="text-slate-500 text-sm font-medium tracking-wide">Loading environment...</p>
        </div>
      </div>
  );

  const severityColor: Record<string, string> = {
    HIGH: 'bg-rose-50 border-rose-200 text-rose-700',
    MEDIUM: 'bg-amber-50 border-amber-200 text-amber-700',
    LOW: 'bg-sky-50 border-sky-200 text-sky-700',
    CRITICAL: 'bg-red-100 border-red-300 text-red-800',
  };

  const navLinks = [
    { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard, href: '#' },
    { id: 'farms', label: 'Farms Listing', icon: Sprout, href: '/listings' },
    { id: 'portfolio', label: 'My Portfolio', icon: Wallet, href: '/portfolio' },
    { id: 'payouts', label: 'Payout History', icon: History, href: '/payouts' },
    { id: 'statements', label: 'Statements', icon: FileText, href: '/statements' },
    { id: 'weather', label: 'Weather Alerts', icon: CloudSun, href: '#weather-section' },
    { id: 'profile', label: 'Profile Settings', icon: UserIcon, href: '/profile' },
  ];

  return (
      <div className="h-screen bg-[#f8fafc] text-slate-800 font-sans antialiased flex overflow-hidden">

        {/* 1. PERMANENT SIDEBAR CONTAINER */}
        <aside className={`
        fixed inset-y-0 left-0 z-50 w-72 bg-white border-r border-slate-100 p-6 flex flex-col justify-between 
        transition-transform duration-300 ease-in-out transform lg:translate-x-0 lg:static lg:flex-shrink-0
        ${isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}
      `}>
          <div className="space-y-8 overflow-y-auto pr-1 flex-1">
            {/* Dashboard Application Logo */}
            <div className="flex items-center gap-3 px-2">
              <div className="w-9 h-9 rounded-xl bg-emerald-800 flex items-center justify-center text-white font-black text-base shadow-sm shadow-emerald-800/20">
                Y
              </div>
              <div className="flex flex-col">
                <span className="font-extrabold text-sm text-slate-900 tracking-tight leading-none">Agri Yield</span>
                <span className="text-[10px] font-semibold text-emerald-600 tracking-wider uppercase mt-1">Platform</span>
              </div>
            </div>

            {/* User Profile Info */}
            <div className="flex items-center gap-3.5 p-3 bg-slate-50 rounded-xl border border-slate-100/60">
              <div className="w-10 h-10 rounded-lg bg-gradient-to-br from-emerald-600 to-teal-700 flex items-center justify-center text-white font-bold text-xs shadow-xs">
                {profile?.phone ? profile.phone.slice(-2) : 'FI'}
              </div>
              <div className="overflow-hidden flex-1">
                <p className="text-xs font-bold text-slate-900 truncate tracking-tight">{profile?.phone || 'Investor Account'}</p>
                <span className={`inline-block text-[9px] font-extrabold uppercase tracking-widest mt-0.5 ${profile?.kycStatus === 'VERIFIED' ? 'text-emerald-600' : 'text-amber-600'}`}>
                {profile?.kycStatus === 'VERIFIED' ? 'Verified Profile' : 'Pending Verification'}
              </span>
              </div>
            </div>

            {/* Navigation Items */}
            <nav className="space-y-1">
              <p className="text-[10px] font-bold tracking-widest text-slate-400 uppercase mb-3 px-3">Main Menu</p>
              {navLinks.map((link) => {
                const Icon = link.icon;
                const isActive = activeTab === link.id;
                return (
                    <Link
                        key={link.id}
                        href={link.href}
                        onClick={() => {
                          setActiveTab(link.id);
                          setIsMobileMenuOpen(false);
                        }}
                        className={`flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all duration-150 ${
                            isActive
                                ? 'bg-slate-900 text-white shadow-sm font-semibold'
                                : 'text-slate-500 hover:bg-slate-50 hover:text-slate-900'
                        }`}
                    >
                      <Icon className={`w-4 h-4 ${isActive ? 'text-emerald-400' : 'text-slate-400'}`} />
                      {link.label}
                    </Link>
                );
              })}
            </nav>
          </div>

          {/* Sidebar Footer */}
          <div className="space-y-4 pt-4 border-t border-slate-100 flex-shrink-0">
            <button
                onClick={handleLogout}
                className="flex items-center gap-3 w-full px-4 py-3 text-slate-500 hover:bg-rose-50 hover:text-rose-600 rounded-xl text-sm font-medium transition-colors group"
            >
              <LogOut className="w-4 h-4 text-slate-400 group-hover:text-rose-500" />
              Sign Out
            </button>
            <div className="text-[10px] text-slate-400 font-semibold px-4 tracking-wide uppercase">
              System v2.6.0
            </div>
          </div>
        </aside>

        {/* Mobile drawer click backdrop overlay */}
        {isMobileMenuOpen && (
            <div
                onClick={() => setIsMobileMenuOpen(false)}
                className="fixed inset-0 bg-slate-900/30 backdrop-blur-xs z-40 lg:hidden"
            />
        )}

        {/* 2. APP CENTRAL WORKSPACE CONTAINER */}
        <div className="flex-1 flex flex-col min-w-0 overflow-hidden">

          {/* Mobile View Toggle Strip */}
          <div className="lg:hidden bg-white border-b border-slate-200/80 px-4 py-3.5 flex items-center justify-between flex-shrink-0">
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 rounded-lg bg-emerald-800 flex items-center justify-center text-white font-bold text-xs">
                Y
              </div>
              <span className="font-bold text-sm text-slate-900 tracking-tight">Agri Yield</span>
            </div>
            <button
                onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                className="p-2 text-slate-600 hover:bg-slate-50 rounded-xl transition-colors border border-slate-100"
            >
              {isMobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
            </button>
          </div>

          {/* Scrollable Workspace Window Panel */}
          <main className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8 space-y-6">

            {/* Clean Dashboard Identity Header Module */}
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-2">
              <div>
                <h1 className="text-2xl font-black tracking-tight text-slate-900 sm:text-3xl">Overview Dashboard</h1>
                <p className="text-slate-400 text-xs mt-1 font-medium">Real-time agriculture yield performance analytics and farm tracking indices.</p>
              </div>
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 bg-white border border-slate-200/60 rounded-xl hidden sm:flex items-center justify-center text-slate-400 relative">
                  <Bell className="w-4 h-4" />
                  <span className="absolute top-2.5 right-2.5 w-1.5 h-1.5 bg-emerald-500 rounded-full" />
                </div>
                <Link href="/listings" className="bg-emerald-600 text-white hover:bg-emerald-500 transition-all px-5 py-2.5 rounded-xl text-xs font-bold tracking-wider uppercase shadow-md shadow-emerald-600/10 text-center">
                  + Invest Now
                </Link>
              </div>
            </div>

            {/* Weather Alert Engine Stream UI */}
            {alerts.length > 0 && (
                <div id="weather-section" className="space-y-2 scroll-mt-6">
                  {alerts.map(alert => (
                      <div key={alert.id} className={`flex items-start gap-3 border rounded-xl px-4 py-3 text-sm shadow-2xs ${severityColor[alert.severity] || 'bg-slate-50 border-slate-200'}`}>
                        <AlertTriangle className="w-4 h-4 mt-0.5 flex-shrink-0" />
                        <div className="flex-1 leading-relaxed">
                          <span className="font-extrabold uppercase tracking-wider text-xs mr-1.5">{alert.alertType.replace(/_/g, ' ')}:</span>
                          {alert.messageEn}
                        </div>
                        <span className="text-xs opacity-60 font-mono whitespace-nowrap">{new Date(alert.createdAt).toLocaleDateString()}</span>
                      </div>
                  ))}
                </div>
            )}

            {/* Metrics Data Grid Structure */}
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              {[
                { label: 'Total Invested', value: `${stats.totalInvested.toLocaleString()} ETB`, color: 'text-emerald-700', icon: CircleDollarSign, bg: 'bg-emerald-50 text-emerald-600' },
                { label: 'Active Fields', value: stats.activeInvestments, color: 'text-indigo-700', icon: TrendingUp, bg: 'bg-indigo-50 text-indigo-600' },
                { label: 'Completed Crops', value: stats.completedInvestments, color: 'text-slate-700', icon: CheckCircle2, bg: 'bg-slate-100 text-slate-600' },
                { label: 'Avg Portfolio APR', value: `${stats.averageApr.toFixed(1)}%`, color: 'text-violet-700', icon: BarChart3, bg: 'bg-violet-50 text-violet-600' },
              ].map((stat) => (
                  <div key={stat.label} className="bg-white rounded-2xl shadow-2xs border border-slate-100 p-5 flex items-center justify-between hover:shadow-xs transition-all duration-200">
                    <div className="space-y-1">
                      <p className="text-slate-400 text-[10px] font-bold uppercase tracking-wider">{stat.label}</p>
                      <p className={`text-2xl font-black tracking-tight ${stat.color}`}>{stat.value}</p>
                    </div>
                    <div className={`w-12 h-12 ${stat.bg} rounded-xl flex items-center justify-center flex-shrink-0`}>
                      <stat.icon className="w-5 h-5" />
                    </div>
                  </div>
              ))}
            </div>

            {/* Dual Panel Data Analytics Visualizer */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

              {/* Left Portfolio Mini Dashboard Stream */}
              <div className="bg-white rounded-2xl shadow-2xs border border-slate-100 p-6 flex flex-col justify-between">
                <div>
                  <div className="flex justify-between items-center mb-5">
                    <h3 className="text-xs font-bold text-slate-900 tracking-wider uppercase">My Active Portfolio</h3>
                    <Link href="/portfolio" className="text-emerald-600 text-xs font-semibold hover:underline">Full Log</Link>
                  </div>

                  {recentInvestments.length === 0 ? (
                      <div className="text-center py-10">
                        <div className="w-12 h-12 bg-slate-50 text-slate-400 rounded-full flex items-center justify-center mx-auto mb-3">
                          <Sprout className="w-5 h-5" />
                        </div>
                        <p className="text-slate-400 text-xs font-medium">No live investment profiles tracked</p>
                      </div>
                  ) : (
                      <div className="space-y-3">
                        {recentInvestments.map((inv) => (
                            <Link key={inv.id} href={`/portfolio/${inv.id}`} className="block border border-slate-100 rounded-xl p-3.5 hover:bg-slate-50/80 hover:border-slate-200 transition-all shadow-2xs">
                              <div className="flex justify-between items-start gap-2">
                                <div>
                                  <p className="font-bold text-sm text-slate-900 truncate max-w-[140px]">{inv.cropType} — {inv.region}</p>
                                  <p className="text-slate-400 text-[11px] mt-0.5">{inv.seasonName}</p>
                                </div>
                                <StatusBadge status={inv.status} />
                              </div>
                              <div className="flex justify-between mt-3 text-xs border-t border-slate-100 pt-2.5">
                                <span className="text-slate-900 font-extrabold">{inv.amountEtb.toLocaleString()} ETB</span>
                                <span className="text-emerald-600 font-extrabold">{inv.expectedReturnPct}% APR</span>
                              </div>
                            </Link>
                        ))}
                      </div>
                  )}
                </div>
              </div>

              {/* Performance Graphical Analysis Metrics */}
              <div className="bg-white rounded-2xl shadow-2xs border border-slate-100 p-6 lg:col-span-2">
                <div className="mb-4">
                  <h3 className="text-xs font-bold text-slate-900 tracking-wider uppercase">ROI Tracking Matrix</h3>
                  <p className="text-xs text-slate-400 mt-0.5">Aggregate investment yield returns measured relative to local inflation rate benchmarks.</p>
                </div>
                <div className="pt-2">
                  <RoiChart averageApr={stats.averageApr || 18} />
                </div>
                <div className="text-xs text-slate-400 mt-5 border-t border-slate-100 pt-3 flex justify-center gap-6 font-medium">
                  <div>Avg Portfolio APR: <span className="text-emerald-600 font-bold">{stats.averageApr.toFixed(1)}%</span></div>
                  <div>Benchmark Inflation: <span className="text-amber-600 font-bold">~22%</span></div>
                </div>
              </div>
            </div>

            {/* Primary Available Listings Group Grid */}
            <div className="bg-white rounded-2xl shadow-2xs border border-slate-100 p-6">
              <div className="flex justify-between items-center mb-5">
                <h3 className="text-xs font-bold text-slate-900 tracking-wider uppercase">Discover Farm Fields</h3>
                <Link href="/listings" className="text-emerald-600 text-xs font-semibold hover:underline">Explore All</Link>
              </div>

              {featuredListings.length === 0 ? (
                  <div className="text-center py-10">
                    <p className="text-slate-400 text-xs font-medium">No live field parameters matched seeking capital allocation portfolios.</p>
                  </div>
              ) : (
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                    {featuredListings.map((listing) => (
                        <Link key={listing.id} href={`/listings/${listing.id}`} className="block border border-slate-100 rounded-xl p-4.5 hover:border-emerald-300 hover:bg-emerald-50/5 transition-all shadow-2xs flex flex-col justify-between">
                          <div>
                            <div className="flex justify-between items-start mb-3 gap-2">
                              <div>
                                <p className="font-bold text-sm text-slate-900 tracking-tight">{listing.cropType} — {listing.region}</p>
                                <p className="text-slate-400 text-[11px] mt-0.5">{listing.seasonName}</p>
                              </div>
                              <span className="bg-emerald-50 text-emerald-800 font-extrabold text-xs px-2.5 py-1 rounded-lg border border-emerald-100 whitespace-nowrap">
                          {listing.currentApr}% APR
                        </span>
                            </div>

                            <div className="flex gap-2 mb-4 items-center flex-wrap">
                        <span className="bg-slate-50 border border-slate-200/80 text-slate-600 text-[10px] px-2 py-0.5 rounded-md font-bold tracking-wide uppercase">
                          Agri-Score: {listing.agriScore}/900
                        </span>
                              <StatusBadge status={listing.status} />
                            </div>
                          </div>

                          <div className="space-y-2 mt-2">
                            <div className="w-full bg-slate-100 rounded-full h-1.5">
                              <div className="bg-emerald-600 h-1.5 rounded-full transition-all" style={{ width: `${Math.min(listing.fundingPct, 100)}%` }} />
                            </div>
                            <div className="flex justify-between items-center text-[11px] font-medium">
                              <p className="text-slate-400">{listing.fundingPct?.toFixed(1)}% funded</p>
                              <p className="text-slate-900 font-bold">{listing.totalAmountEtb?.toLocaleString()} ETB</p>
                            </div>
                          </div>
                        </Link>
                    ))}
                  </div>
              )}
            </div>

            {/* Navigation Action Shortcuts */}
            <div className="bg-white rounded-2xl shadow-2xs border border-slate-100 p-6">
              <h3 className="text-xs font-bold text-slate-900 tracking-wider uppercase mb-4">Quick Shortcuts</h3>
              <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-3">
                {[
                  { label: 'Browse Farms', href: '/listings', icon: Search, desc: 'Find active crop profiles' },
                  { label: 'My Portfolio', href: '/portfolio', icon: Wallet, desc: 'Track your returns' },
                  { label: 'Payout History', href: '/payouts', icon: History, desc: 'View disbursement log' },
                  { label: 'Statements', href: '/statements', icon: FileText, desc: 'Download documents' },
                  { label: 'Profile Settings', href: '/profile', icon: UserIcon, desc: 'Manage account access' },
                ].map((action) => (
                    <Link key={action.href} href={action.href} className="border border-slate-100 rounded-xl p-4 text-center hover:border-emerald-300 hover:bg-emerald-50/10 transition-all group flex flex-col items-center justify-center shadow-2xs">
                      <div className="w-10 h-10 rounded-xl bg-slate-50 group-hover:bg-emerald-50 text-slate-400 group-hover:text-emerald-700 flex items-center justify-center mb-2.5 transition-colors">
                        <action.icon className="w-5 h-5" />
                      </div>
                      <p className="font-bold text-xs text-slate-800 group-hover:text-emerald-950 tracking-tight">{action.label}</p>
                      <p className="text-slate-400 text-[10px] mt-1 leading-snug font-medium max-w-[110px] mx-auto">{action.desc}</p>
                    </Link>
                ))}
              </div>
            </div>

          </main>
        </div>
      </div>
  );
}