'use client';

import { useEffect, useState, useRef } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import StatusBadge from '@/components/StatusBadge';
import FundingProgress from '@/components/FundingProgress';
import {
  LayoutDashboard,
  Sprout,
  Wallet,
  FileText,
  History,
  User as UserIcon,
  CloudSun,
  LogOut,
  Menu,
  X,
  Search,
  SlidersHorizontal,
  MapPin,
  Percent,
  Award,
  Calendar,
  ShieldCheck,
  Activity,
  ChevronLeft,
  ChevronRight,
  Info
} from 'lucide-react';
import { FarmListing, User } from '@/lib/types';

const CROP_TYPES = ['WHEAT', 'TEFF', 'BARLEY', 'MAIZE', 'SORGHUM', 'COFFEE', 'BEANS', 'MILLET'];
const REGIONS    = ['Oromia', 'Amhara', 'SNNPR', 'Tigray', 'Somali', 'Afar'];
const PAGE_SIZE  = 9;

function ndviLabel(v: number | null): { label: string; color: string; bg: string } {
  if (v == null) return { label: 'No Satellite Data', color: 'text-slate-400', bg: 'bg-slate-100 border-slate-200/60' };
  if (v >= 0.6)  return { label: 'Excellent Crop Health', color: 'text-emerald-700', bg: 'bg-emerald-50 border-emerald-100' };
  if (v >= 0.4)  return { label: 'Good Crop Health',      color: 'text-lime-700',   bg: 'bg-lime-50 border-lime-100'    };
  if (v >= 0.2)  return { label: 'Fair Crop Health',      color: 'text-amber-700',  bg: 'bg-amber-50 border-amber-100'  };
  return                { label: 'Poor Crop Health',      color: 'text-rose-700',   bg: 'bg-rose-50 border-rose-100'    };
}

function daysRemaining(deadline: string | null | undefined): string {
  if (!deadline) return '—';
  const diff = Math.ceil((new Date(deadline).getTime() - Date.now()) / 86400000);
  if (diff <= 0) return 'Ended';
  return `${diff} days left`;
}

interface Filters {
  cropType: string;
  region: string;
  minApr: string;
  maxApr: string;
  satelliteVerified: boolean;
  minAgriScore: string;
}

const ndviCache = new Map<string, number | null>();

export default function ListingsPage() {
  const router = useRouter();
  const [profile, setProfile] = useState<User | null>(null);
  const [listings, setListings]       = useState<FarmListing[]>([]);
  const [loading,  setLoading]        = useState(true);
  const [page,     setPage]           = useState(0);
  const [ndviMap,  setNdviMap]        = useState<Map<string, number | null>>(new Map());
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const [activeTab, setActiveTab]     = useState('farms');
  const [filters,  setFilters]        = useState<Filters>({
    cropType: '', region: '', minApr: '', maxApr: '',
    satelliteVerified: false, minAgriScore: '',
  });

  const fetchingNdvi = useRef(new Set<string>());

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    loadUserAndListings();
  }, []);

  useEffect(() => {
    const paginated = listings.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE);
    paginated.forEach(l => {
      const fId = l.farmId;
      if (ndviCache.has(fId) || fetchingNdvi.current.has(fId)) return;
      fetchingNdvi.current.add(fId);
      api.get(`/geospatial/farms/${fId}/ndvi`)
          .then(r => {
            const val: number | null = r.data.success && r.data.data
                ? (r.data.data.ndviValue ?? null)
                : null;
            ndviCache.set(fId, val);
            setNdviMap(prev => new Map(prev).set(fId, val));
          })
          .catch(() => {
            ndviCache.set(fId, null);
            setNdviMap(prev => new Map(prev).set(fId, null));
          })
          .finally(() => fetchingNdvi.current.delete(fId));
    });
  }, [listings, page]);

  const loadUserAndListings = async () => {
    setLoading(true);
    try {
      const userRes = await api.get('/users/me');
      if (userRes.data.success) setProfile(userRes.data.data);
    } catch {
      toast.error('Could not load your profile');
    }
    await fetchListings(false);
  };

  const fetchListings = async (displayLoading = true) => {
    if (displayLoading) setLoading(true);
    try {
      const params = new URLSearchParams();
      if (filters.cropType)          params.append('cropType',         filters.cropType);
      if (filters.region)            params.append('region',           filters.region);
      if (filters.minApr)            params.append('minApr',           filters.minApr);
      if (filters.maxApr)            params.append('maxApr',           filters.maxApr);
      if (filters.satelliteVerified) params.append('satelliteVerified','true');
      if (filters.minAgriScore)      params.append('minAgriScore',     filters.minAgriScore);

      const res = await api.get(`/listings?${params}`);
      if (res.data.success) {
        setListings(res.data.data || []);
        setPage(0);
      }
    } catch {
      toast.error('Could not load farm listings');
    } finally {
      setLoading(false);
    }
  };

  const setFilter = (key: keyof Filters, value: string | boolean) =>
      setFilters(prev => ({ ...prev, [key]: value }));

  const handleReset = () => {
    setFilters({ cropType:'', region:'', minApr:'', maxApr:'', satelliteVerified: false, minAgriScore:'' });
    setPage(0);
    setTimeout(() => {
      setLoading(true);
      api.get('/listings').then(res => {
        if (res.data.success) setListings(res.data.data || []);
      }).finally(() => setLoading(false));
    }, 0);
  };

  const handleLogout = () => {
    localStorage.removeItem('access_token');
    router.push('/login');
  };

  const paginated  = listings.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE);
  const totalPages = Math.ceil(listings.length / PAGE_SIZE);

  const navLinks = [
    { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard, href: '/dashboard' },
    { id: 'farms', label: 'Browse Farms', icon: Sprout, href: '#' },
    { id: 'portfolio', label: 'My Investments', icon: Wallet, href: '/portfolio' },
    { id: 'payouts', label: 'Earnings History', icon: History, href: '/payouts' },
    { id: 'statements', label: 'Documents', icon: FileText, href: '/statements' },
    { id: 'weather', label: 'Weather Alerts', icon: CloudSun, href: '/dashboard#weather-section' },
    { id: 'profile', label: 'Profile Settings', icon: UserIcon, href: '/profile' },
  ];

  return (
      <div className="h-screen bg-[#f8fafc] text-slate-800 font-sans antialiased flex overflow-hidden">

        {/* 1. SIDEBAR NAVIGATION */}
        <aside className={`
        fixed inset-y-0 left-0 z-50 w-72 bg-white border-r border-slate-100 p-6 flex flex-col justify-between 
        transition-transform duration-300 ease-in-out transform lg:translate-x-0 lg:static lg:flex-shrink-0
        ${isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}
      `}>
          <div className="space-y-8 overflow-y-auto pr-1 flex-1">
            {/* Logo */}
            <div className="flex items-center gap-3 px-2">
              <div className="w-9 h-9 rounded-xl bg-emerald-800 flex items-center justify-center text-white font-black text-base shadow-sm shadow-emerald-800/20">
                Y
              </div>
              <div className="flex flex-col">
                <span className="font-extrabold text-sm text-slate-900 tracking-tight leading-none">Agri Yield</span>
                <span className="text-[10px] font-semibold text-emerald-600 tracking-wider uppercase mt-1">Platform</span>
              </div>
            </div>

            {/* User Account Info */}
            <div className="flex items-center gap-3.5 p-3 bg-slate-50 rounded-xl border border-slate-100/60">
              <div className="w-10 h-10 rounded-lg bg-gradient-to-br from-emerald-600 to-teal-700 flex items-center justify-center text-white font-bold text-xs shadow-xs">
                {profile?.phone ? profile.phone.slice(-2) : 'US'}
              </div>
              <div className="overflow-hidden flex-1">
                <p className="text-xs font-bold text-slate-900 truncate tracking-tight">{profile?.phone || 'Investor'}</p>
                <span className={`inline-block text-[9px] font-extrabold uppercase tracking-widest mt-0.5 ${profile?.kycStatus === 'VERIFIED' ? 'text-emerald-600' : 'text-amber-600'}`}>
                {profile?.kycStatus === 'VERIFIED' ? 'Verified' : 'Pending Verification'}
              </span>
              </div>
            </div>

            {/* Navigation Links */}
            <nav className="space-y-1">
              <p className="text-[10px] font-bold tracking-widest text-slate-400 uppercase mb-3 px-3">Menu</p>
              {navLinks.map((link) => {
                const Icon = link.icon;
                const isActive = activeTab === link.id;
                return (
                    <Link
                        key={link.id}
                        href={link.href}
                        onClick={() => {
                          if (link.id === 'farms') return;
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
              Log Out
            </button>
            <div className="text-[10px] text-slate-400 font-semibold px-4 tracking-wide uppercase">
              Version 2.6.0
            </div>
          </div>
        </aside>

        {/* Mobile Backdrop */}
        {isMobileMenuOpen && (
            <div onClick={() => setIsMobileMenuOpen(false)} className="fixed inset-0 bg-slate-900/30 backdrop-blur-xs z-40 lg:hidden" />
        )}

        {/* 2. MAIN CONTENT AREA */}
        <div className="flex-1 flex flex-col min-w-0 overflow-hidden">

          {/* Mobile Header Bar */}
          <div className="lg:hidden bg-white border-b border-slate-200/80 px-4 py-3.5 flex items-center justify-between flex-shrink-0">
            <div className="flex items-center gap-2">
              <div className="w-8 h-8 rounded-lg bg-emerald-800 flex items-center justify-center text-white font-bold text-xs">Y</div>
              <span className="font-bold text-sm text-slate-900 tracking-tight">Agri Yield</span>
            </div>
            <button
                onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                className="p-2 text-slate-600 hover:bg-slate-50 rounded-xl transition-colors border border-slate-100"
            >
              {isMobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
            </button>
          </div>

          {/* Scrollable Farm Listings Container */}
          <main className="flex-1 overflow-y-auto p-4 sm:p-6 lg:p-8 space-y-6">

            {/* Page Title */}
            <div className="pb-2">
              <h1 className="text-2xl font-black tracking-tight text-slate-900 sm:text-3xl">Available Farms</h1>
              <p className="text-slate-400 text-xs mt-1 font-medium">Explore verified farm listings and find new investment opportunities.</p>
            </div>

            {/* Search & Filters */}
            <div className="bg-white rounded-2xl shadow-2xs border border-slate-100 p-5">
              <div className="flex items-center gap-2 mb-4 text-slate-900">
                <SlidersHorizontal className="w-4 h-4 text-emerald-600" />
                <span className="text-xs font-bold tracking-wider uppercase">Find Your Ideal Farm</span>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">
                <select
                    value={filters.cropType}
                    onChange={e => setFilter('cropType', e.target.value)}
                    className="px-3.5 py-2.5 bg-slate-50 text-slate-700 border border-slate-200/70 rounded-xl text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-slate-900 focus:bg-white transition"
                >
                  <option value="">All Crop Types</option>
                  {CROP_TYPES.map(c => <option key={c} value={c}>{c}</option>)}
                </select>

                <select
                    value={filters.region}
                    onChange={e => setFilter('region', e.target.value)}
                    className="px-3.5 py-2.5 bg-slate-50 text-slate-700 border border-slate-200/70 rounded-xl text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-slate-900 focus:bg-white transition"
                >
                  <option value="">All Regions</option>
                  {REGIONS.map(r => <option key={r} value={r}>{r}</option>)}
                </select>

                <div className="relative flex items-center">
                  <Percent className="w-3.5 h-3.5 absolute left-3.5 text-slate-400" />
                  <input
                      type="number" step="0.1" value={filters.minApr}
                      onChange={e => setFilter('minApr', e.target.value)}
                      placeholder="Min Return (APR %)"
                      className="w-full pl-9 pr-3.5 py-2.5 bg-slate-50 text-xs border border-slate-200/70 rounded-xl focus:outline-none focus:ring-2 focus:ring-slate-900 focus:bg-white transition font-medium"
                  />
                </div>

                <div className="relative flex items-center">
                  <Percent className="w-3.5 h-3.5 absolute left-3.5 text-slate-400" />
                  <input
                      type="number" step="0.1" value={filters.maxApr}
                      onChange={e => setFilter('maxApr', e.target.value)}
                      placeholder="Max Return (APR %)"
                      className="w-full pl-9 pr-3.5 py-2.5 bg-slate-50 text-xs border border-slate-200/70 rounded-xl focus:outline-none focus:ring-2 focus:ring-slate-900 focus:bg-white transition font-medium"
                  />
                </div>

                <div className="relative flex items-center">
                  <Award className="w-3.5 h-3.5 absolute left-3.5 text-slate-400" />
                  <input
                      type="number" min="0" max="900" value={filters.minAgriScore}
                      onChange={e => setFilter('minAgriScore', e.target.value)}
                      placeholder="Min Farm Score (0–900)"
                      className="w-full pl-9 pr-3.5 py-2.5 bg-slate-50 text-xs border border-slate-200/70 rounded-xl focus:outline-none focus:ring-2 focus:ring-slate-900 focus:bg-white transition font-medium"
                  />
                </div>

                <button
                    type="button"
                    onClick={() => setFilter('satelliteVerified', !filters.satelliteVerified)}
                    className={`flex items-center gap-2.5 px-3.5 py-2.5 rounded-xl border text-xs font-semibold transition text-left ${
                        filters.satelliteVerified
                            ? 'bg-emerald-50 border-emerald-300 text-emerald-800'
                            : 'bg-slate-50 border-slate-200/70 text-slate-600 hover:bg-slate-100/60'
                    }`}
                >
                  <ShieldCheck className={`w-4 h-4 ${filters.satelliteVerified ? 'text-emerald-600' : 'text-slate-400'}`} />
                  <span>Satellite Verified Only</span>
                </button>

                <div className="flex gap-2 sm:col-span-2 md:col-span-1 lg:col-span-2">
                  <button
                      onClick={handleReset}
                      className="flex-1 bg-slate-100 text-slate-600 px-4 py-2.5 rounded-xl text-xs font-bold tracking-wide uppercase hover:bg-slate-200 transition"
                  >
                    Reset Filters
                  </button>
                  <button
                      onClick={() => fetchListings(true)}
                      className="flex-1 bg-slate-900 text-white px-4 py-2.5 rounded-xl text-xs font-bold tracking-wide uppercase hover:bg-slate-800 transition shadow-xs flex items-center justify-center gap-2"
                  >
                    <Search className="w-3.5 h-3.5" />
                    <span>Search Farms</span>
                  </button>
                </div>
              </div>
            </div>

            {/* Loading State */}
            {loading ? (
                <div className="flex flex-col items-center justify-center py-24 space-y-4">
                  <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-slate-900" />
                  <p className="text-slate-400 text-xs font-semibold tracking-wide uppercase">Loading farms...</p>
                </div>
            ) : listings.length === 0 ? (
                /* Empty State */
                <div className="bg-white rounded-2xl p-16 text-center border border-slate-100 max-w-xl mx-auto shadow-2xs">
                  <div className="w-12 h-12 rounded-full bg-slate-50 flex items-center justify-center text-slate-400 mx-auto mb-4">
                    <Info className="w-5 h-5" />
                  </div>
                  <p className="text-slate-900 font-bold text-sm tracking-tight">No Farms Found</p>
                  <p className="text-slate-400 text-xs mt-1.5 font-medium leading-relaxed">We couldn't find any farms matching your filters. Try changing your search options.</p>
                  <button
                      onClick={handleReset}
                      className="mt-4 bg-slate-900 text-white font-bold text-xs px-4 py-2 rounded-xl uppercase tracking-wider shadow-xs hover:bg-slate-800 transition"
                  >
                    Clear Search
                  </button>
                </div>
            ) : (
                <>
                  {/* Pagination Sub-title info */}
                  <div className="flex items-center justify-between pl-1">
                    <p className="text-[11px] text-slate-400 font-bold uppercase tracking-wider">
                      Showing {listings.length} farm{listings.length !== 1 ? 's' : ''} &middot; Page {page + 1} of {totalPages}
                    </p>
                  </div>

                  {/* Farm Cards Grid */}
                  <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
                    {paginated.map(listing => {
                      const ndvi    = ndviMap.get(listing.farmId) ?? ndviCache.get(listing.farmId) ?? null;
                      const health  = ndviLabel(ndvi);
                      const deadline = listing.listingExpiresAt ?? listing.fundingDeadline;

                      return (
                          <div
                              key={listing.id}
                              className="bg-white rounded-2xl shadow-2xs border border-slate-100 hover:shadow-xs transition duration-200 overflow-hidden flex flex-col justify-between"
                          >
                            {/* Card Image Banner Header */}
                            <div className="relative h-28 bg-slate-900 overflow-hidden">
                              <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1625246333195-78d9c38ad449?auto=format&fit=crop&q=80')] bg-cover bg-center opacity-40 transition duration-300" />

                              {/* Upper Badges */}
                              <div className="absolute top-3 right-3">
                          <span className="bg-white text-slate-900 font-black text-xs px-2.5 py-1 rounded-lg shadow-xs border border-slate-100">
                            {listing.currentApr}% APR
                          </span>
                              </div>
                              <div className="absolute top-3 left-3 flex items-center gap-1.5 bg-slate-900/60 backdrop-blur-md text-white text-[10px] font-bold tracking-wide uppercase px-2.5 py-1 rounded-lg">
                                <Calendar className="w-3 h-3 text-slate-300" />
                                <span>{daysRemaining(deadline)}</span>
                              </div>

                              {/* Lower Badges */}
                              <div className="absolute bottom-3 left-3 flex flex-wrap gap-1.5">
                                {listing.satelliteVerified && (
                                    <div className="flex items-center gap-1 bg-emerald-800 text-white text-[9px] font-extrabold tracking-wider uppercase px-2 py-0.5 rounded-md border border-emerald-700/50 shadow-2xs">
                                      <ShieldCheck className="w-2.5 h-2.5 text-emerald-400" />
                                      <span>Verified Orbit</span>
                                    </div>
                                )}
                                <div className={`flex items-center gap-1 text-[9px] font-extrabold tracking-wider uppercase px-2 py-0.5 rounded-md border ${health.bg} ${health.color}`}>
                                  <Activity className="w-2.5 h-2.5" />
                                  <span>{ndvi != null ? `NDVI ${ndvi.toFixed(2)}` : 'No Orbit Scan'}</span>
                                </div>
                              </div>
                            </div>

                            {/* Card Body */}
                            <div className="p-5 flex-1 flex flex-col justify-between space-y-4">
                              <div>
                                <div className="flex justify-between items-start">
                                  <div>
                                    <h3 className="font-extrabold text-sm text-slate-900 tracking-tight">{listing.cropType} &mdash; {listing.region}</h3>
                                    <div className="flex items-center gap-1.5 text-slate-400 text-[11px] font-semibold mt-1">
                                      <MapPin className="w-3 h-3 text-slate-300" />
                                      <span>Kebele {listing.kebeleCode}</span>
                                      <span className="text-slate-200">&middot;</span>
                                      <span>{listing.seasonName}</span>
                                    </div>
                                  </div>
                                </div>

                                <div className="flex flex-wrap gap-1.5 mt-3">
                                  <StatusBadge status={listing.status} />
                                  <span className={`text-[10px] font-bold px-2 py-0.5 rounded-md border tracking-wide uppercase ${
                                      listing.agriScore >= 700 ? 'bg-emerald-50 border-emerald-100 text-emerald-700' :
                                          listing.agriScore >= 500 ? 'bg-amber-50 border-amber-100 text-amber-700' :
                                              'bg-rose-50 border-rose-100 text-rose-700'
                                  }`}>
                              Farm Score: {listing.agriScore}/900
                            </span>
                                </div>
                              </div>

                              {/* Progress Bar Container */}
                              <div className="bg-slate-50 border border-slate-100/80 p-3 rounded-xl space-y-1">
                                <FundingProgress
                                    funded={listing.fundedAmountEtb}
                                    total={listing.totalAmountEtb}
                                    pct={listing.fundingPct}
                                />
                              </div>

                              {/* Financial Target Metrics Row */}
                              <div className="grid grid-cols-2 gap-4 text-xs border-t border-slate-100 pt-3.5">
                                <div>
                                  <p className="text-slate-400 text-[10px] font-bold uppercase tracking-wider">Funding Target</p>
                                  <p className="font-black text-slate-900 mt-0.5">{listing.totalAmountEtb?.toLocaleString()} ETB</p>
                                </div>
                                <div>
                                  <p className="text-slate-400 text-[10px] font-bold uppercase tracking-wider">Base Return Rate</p>
                                  <p className="font-black text-slate-900 mt-0.5">{listing.baseApr}% APR</p>
                                </div>
                              </div>

                              {/* Action CTA Button */}
                              <Link
                                  href={`/listings/${listing.id}`}
                                  className="block w-full text-center bg-slate-900 text-white py-2.5 rounded-xl text-xs font-bold tracking-wider uppercase hover:bg-slate-800 transition shadow-xs"
                              >
                                View Farm Details
                              </Link>
                            </div>
                          </div>
                      );
                    })}
                  </div>

                  {/* Bottom Pagination Interface */}
                  {totalPages > 1 && (
                      <div className="flex justify-center items-center gap-1.5 mt-8 flex-wrap">
                        <button
                            onClick={() => setPage(p => Math.max(0, p - 1))}
                            disabled={page === 0}
                            className="p-2 rounded-xl bg-white border border-slate-200 text-slate-600 disabled:opacity-40 disabled:hover:bg-white hover:bg-slate-50 hover:text-slate-900 transition shadow-2xs"
                        >
                          <ChevronLeft className="w-4 h-4" />
                        </button>

                        {Array.from({ length: totalPages }, (_, i) => (
                            <button
                                key={i}
                                onClick={() => setPage(i)}
                                className={`w-9 h-9 rounded-xl text-xs font-bold transition shadow-2xs ${
                                    page === i
                                        ? 'bg-slate-900 text-white'
                                        : 'bg-white border border-slate-200 text-slate-600 hover:bg-slate-50 hover:text-slate-900'
                                }`}
                            >
                              {i + 1}
                            </button>
                        ))}

                        <button
                            onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                            disabled={page === totalPages - 1}
                            className="p-2 rounded-xl bg-white border border-slate-200 text-slate-600 disabled:opacity-40 disabled:hover:bg-white hover:bg-slate-50 hover:text-slate-900 transition shadow-2xs"
                        >
                          <ChevronRight className="w-4 h-4" />
                        </button>
                      </div>
                  )}
                </>
            )}
          </main>
        </div>
      </div>
  );
}