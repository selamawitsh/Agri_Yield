'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import StatusBadge from '@/components/StatusBadge';
import FundingProgress from '@/components/FundingProgress';
import { FarmListing } from '@/lib/types';

const CROP_TYPES = ['WHEAT', 'TEFF', 'BARLEY', 'MAIZE', 'SORGHUM', 'COFFEE', 'BEANS', 'MILLET'];
const REGIONS    = ['Oromia', 'Amhara', 'SNNPR', 'Tigray', 'Somali', 'Afar'];
const PAGE_SIZE  = 9;

function daysRemaining(deadline: string | null): string {
  if (!deadline) return '—';
  const diff = Math.ceil((new Date(deadline).getTime() - Date.now()) / 86400000);
  if (diff <= 0) return 'Expired';
  return `${diff}d left`;
}

interface Filters {
  cropType: string;
  region: string;
  minApr: string;
  maxApr: string;
  satelliteVerified: boolean;
  minAgriScore: string;
}

export default function ListingsPage() {
  const router = useRouter();
  const [listings, setListings]   = useState<FarmListing[]>([]);
  const [loading, setLoading]     = useState(true);
  const [page, setPage]           = useState(0);
  const [filters, setFilters]     = useState<Filters>({
    cropType: '', region: '', minApr: '', maxApr: '',
    satelliteVerified: false,
    minAgriScore: '',
  });

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    fetchListings();
  }, []);

  const fetchListings = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (filters.cropType)         params.append('cropType',          filters.cropType);
      if (filters.region)           params.append('region',            filters.region);
      if (filters.minApr)           params.append('minApr',            filters.minApr);
      if (filters.maxApr)           params.append('maxApr',            filters.maxApr);
      if (filters.satelliteVerified) params.append('satelliteVerified', 'true');

      const res = await api.get(`/listings?${params}`);
      if (res.data.success) {
        let data: FarmListing[] = res.data.data || [];
        // client-side agri-score filter until backend adds the param
        if (filters.minAgriScore)
          data = data.filter(l => l.agriScore >= parseInt(filters.minAgriScore));
        setListings(data);
        setPage(0);
      }
    } catch {
      toast.error('Failed to load listings');
    } finally { setLoading(false); }
  };

  const setFilter = (key: keyof Filters, value: string | boolean) =>
    setFilters(prev => ({ ...prev, [key]: value }));

  const handleReset = () => {
    setFilters({ cropType: '', region: '', minApr: '', maxApr: '', satelliteVerified: false, minAgriScore: '' });
    setTimeout(fetchListings, 0);
  };

  const paginated   = listings.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE);
  const totalPages  = Math.ceil(listings.length / PAGE_SIZE);

  return (
    <div className="min-h-screen bg-gray-50 pb-12">
      <Navbar />
      <div className="container mx-auto px-4 sm:px-6 py-6 max-w-6xl">

        <div className="mb-6">
          <h1 className="text-3xl font-bold text-gray-900">Farm Listings</h1>
          <p className="text-gray-500 mt-1">Browse and invest in active agricultural plots</p>
        </div>

        {/* ── Filters — SRS §6.3.2 ── */}
        <div className="bg-white rounded-3xl shadow-sm border border-gray-100 p-6 mb-8">
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-3">

            {/* Crop type */}
            <select value={filters.cropType} onChange={e => setFilter('cropType', e.target.value)}
              className="px-4 py-3 bg-gray-50 rounded-xl text-sm font-medium text-gray-700 border border-gray-200 focus:outline-none focus:ring-2 focus:ring-green-500">
              <option value="">All Crops</option>
              {CROP_TYPES.map(c => <option key={c} value={c}>{c}</option>)}
            </select>

            {/* Region */}
            <select value={filters.region} onChange={e => setFilter('region', e.target.value)}
              className="px-4 py-3 bg-gray-50 rounded-xl text-sm font-medium text-gray-700 border border-gray-200 focus:outline-none focus:ring-2 focus:ring-green-500">
              <option value="">All Regions</option>
              {REGIONS.map(r => <option key={r} value={r}>{r}</option>)}
            </select>

            {/* Min APR */}
            <input type="number" step="0.1" value={filters.minApr}
              onChange={e => setFilter('minApr', e.target.value)}
              placeholder="Min APR %"
              className="px-4 py-3 bg-gray-50 rounded-xl text-sm border border-gray-200 focus:outline-none focus:ring-2 focus:ring-green-500" />

            {/* Max APR */}
            <input type="number" step="0.1" value={filters.maxApr}
              onChange={e => setFilter('maxApr', e.target.value)}
              placeholder="Max APR %"
              className="px-4 py-3 bg-gray-50 rounded-xl text-sm border border-gray-200 focus:outline-none focus:ring-2 focus:ring-green-500" />

            {/* Min Agri-Score — SRS §6.3.2 */}
            <input type="number" min="0" max="900" value={filters.minAgriScore}
              onChange={e => setFilter('minAgriScore', e.target.value)}
              placeholder="Min Agri-Score (0-900)"
              className="px-4 py-3 bg-gray-50 rounded-xl text-sm border border-gray-200 focus:outline-none focus:ring-2 focus:ring-green-500" />

            {/* Satellite verified toggle — SRS §6.3.2 */}
            <label className={`flex items-center gap-3 px-4 py-3 rounded-xl border cursor-pointer transition ${
              filters.satelliteVerified
                ? 'bg-green-50 border-green-400 text-green-700'
                : 'bg-gray-50 border-gray-200 text-gray-600'
            }`}>
              <input
                type="checkbox"
                checked={filters.satelliteVerified}
                onChange={e => setFilter('satelliteVerified', e.target.checked)}
                className="w-4 h-4 accent-green-600"
              />
              <span className="text-sm font-medium">🛰 Satellite verified</span>
            </label>

            {/* Actions */}
            <div className="flex gap-2 col-span-2 md:col-span-1">
              <button onClick={handleReset}
                className="flex-1 bg-gray-100 text-gray-600 px-3 py-3 rounded-xl text-sm font-semibold hover:bg-gray-200 transition">
                Clear
              </button>
              <button onClick={fetchListings}
                className="flex-1 bg-green-700 text-white px-3 py-3 rounded-xl text-sm font-semibold hover:bg-green-600 transition">
                Search
              </button>
            </div>
          </div>
        </div>

        {/* ── Results ── */}
        {loading ? (
          <div className="flex justify-center py-24">
            <div className="animate-spin rounded-full h-12 w-12 border-b-4 border-green-700" />
          </div>
        ) : listings.length === 0 ? (
          <div className="bg-white rounded-3xl p-16 text-center border border-gray-100">
            <p className="text-5xl mb-4">🔍</p>
            <p className="text-gray-700 font-bold text-xl">No listings found</p>
            <p className="text-gray-400 mt-2">Try adjusting your filters</p>
            <button onClick={handleReset} className="mt-4 text-green-600 text-sm font-semibold hover:underline">
              Clear filters
            </button>
          </div>
        ) : (
          <>
            <p className="text-sm text-gray-400 font-semibold mb-5 pl-1">
              {listings.length} listing{listings.length !== 1 ? 's' : ''} · Page {page + 1} of {totalPages}
            </p>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-5">
              {paginated.map(listing => (
                <div key={listing.id}
                  className="bg-white rounded-3xl shadow-sm border border-gray-100 hover:shadow-md transition overflow-hidden group">

                  {/* Card header image */}
                  <div className="relative h-28 bg-emerald-900 overflow-hidden">
                    <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1625246333195-78d9c38ad449?auto=format&fit=crop&q=80')] bg-cover bg-center opacity-50 group-hover:scale-105 transition duration-500" />
                    <div className="absolute top-3 right-3">
                      <span className="bg-lime-300 text-emerald-950 font-bold text-xs px-2.5 py-1 rounded-full shadow">
                        {listing.currentApr}% APR
                      </span>
                    </div>
                    {/* Days remaining — SRS */}
                    <div className="absolute top-3 left-3">
                      <span className="bg-black/40 text-white text-xs px-2.5 py-1 rounded-full font-medium">
                        ⏱ {daysRemaining(listing.listingExpiresAt ?? listing.fundingDeadline ?? null)}
                      </span>
                    </div>
                    {/* Satellite verified badge */}
                    {listing.satelliteVerified && (
                      <div className="absolute bottom-3 left-3">
                        <span className="bg-blue-600/80 text-white text-xs px-2 py-0.5 rounded-full font-medium">
                          🛰 Verified
                        </span>
                      </div>
                    )}
                  </div>

                  <div className="p-5">
                    <div className="flex justify-between items-start mb-3">
                      <div>
                        <h3 className="font-bold text-gray-900">{listing.cropType} — {listing.region}</h3>
                        <p className="text-gray-400 text-xs mt-0.5">{listing.kebeleCode} · {listing.seasonName}</p>
                      </div>
                    </div>

                    <div className="flex flex-wrap gap-1.5 mb-4">
                      <StatusBadge status={listing.status} />
                      {/* Agri-Score badge — SRS */}
                      <span className={`text-xs font-bold px-2 py-0.5 rounded-full ${
                        listing.agriScore >= 700 ? 'bg-green-100 text-green-700' :
                        listing.agriScore >= 500 ? 'bg-yellow-100 text-yellow-700' :
                        'bg-red-100 text-red-700'
                      }`}>
                        ★ {listing.agriScore}/900
                      </span>
                    </div>

                    <div className="mb-4">
                      <FundingProgress
                        funded={listing.fundedAmountEtb}
                        total={listing.totalAmountEtb}
                        pct={listing.fundingPct}
                      />
                    </div>

                    <div className="grid grid-cols-2 gap-3 text-xs border-t border-gray-100 pt-4 mb-4">
                      <div>
                        <p className="text-gray-400 uppercase tracking-wide font-semibold">Target</p>
                        <p className="font-bold text-gray-800 mt-0.5">{listing.totalAmountEtb?.toLocaleString()} ETB</p>
                      </div>
                      <div>
                        <p className="text-gray-400 uppercase tracking-wide font-semibold">Base APR</p>
                        <p className="font-bold text-gray-800 mt-0.5">{listing.baseApr}%</p>
                      </div>
                    </div>

                    <Link href={`/listings/${listing.id}`}
                      className="block w-full text-center bg-green-700 text-white py-3 rounded-full text-sm font-bold hover:bg-green-600 transition shadow-sm">
                      View Details
                    </Link>
                  </div>
                </div>
              ))}
            </div>

            {/* Pagination — SRS §6.3.2 */}
            {totalPages > 1 && (
              <div className="flex justify-center gap-2 mt-8 flex-wrap">
                <button onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0}
                  className="px-4 py-2 rounded-xl bg-white border border-gray-200 text-sm font-semibold disabled:opacity-40 hover:border-green-400 transition">
                  ← Prev
                </button>
                {Array.from({ length: totalPages }, (_, i) => (
                  <button key={i} onClick={() => setPage(i)}
                    className={`w-9 h-9 rounded-xl text-sm font-bold transition ${
                      page === i
                        ? 'bg-green-700 text-white'
                        : 'bg-white border border-gray-200 text-gray-600 hover:border-green-400'
                    }`}>
                    {i + 1}
                  </button>
                ))}
                <button onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))} disabled={page === totalPages - 1}
                  className="px-4 py-2 rounded-xl bg-white border border-gray-200 text-sm font-semibold disabled:opacity-40 hover:border-green-400 transition">
                  Next →
                </button>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}
