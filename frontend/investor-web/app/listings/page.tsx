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

const CROP_TYPES = ['', 'WHEAT', 'TEFF', 'BARLEY', 'MAIZE', 'SORGHUM', 'COFFEE', 'BEANS', 'MILLET'];
const REGIONS = ['', 'Oromia', 'Amhara', 'SNNPR', 'Tigray', 'Somali', 'Afar'];

export default function ListingsPage() {
  const router = useRouter();
  const [listings, setListings] = useState<FarmListing[]>([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({ cropType: '', region: '', minApr: '', maxApr: '' });

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    fetchListings();
  }, []);

  const fetchListings = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams();
      if (filters.cropType) params.append('cropType', filters.cropType);
      if (filters.region) params.append('region', filters.region);
      if (filters.minApr) params.append('minApr', filters.minApr);
      if (filters.maxApr) params.append('maxApr', filters.maxApr);

      const response = await api.get(`/listings?${params.toString()}`);
      if (response.data.success) {
        setListings(response.data.data);
      }
    } catch (error) {
      toast.error('Failed to load listings');
    } finally {
      setLoading(false);
    }
  };

  const handleFilterSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchListings();
  };

  const handleFilterReset = () => {
    setFilters({ cropType: '', region: '', minApr: '', maxApr: '' });
    setTimeout(fetchListings, 0);
  };

  return (
      <div className="min-h-screen bg-gray-50 pb-12">
        <Navbar />
        <div className="container mx-auto px-4 sm:px-6 py-6 max-w-6xl">

          <div className="mb-8 flex justify-between items-end">
            <div>
              <h1 className="text-3xl font-bold text-emerald-950 tracking-tight">Map Field</h1>
              <p className="text-gray-500 mt-2 font-medium">Browse and discover active premium plots</p>
            </div>
          </div>

          {/* Filters Panel */}
          <div className="bg-white rounded-[2.5rem] shadow-sm border border-gray-100 p-6 mb-8">
            <form onSubmit={handleFilterSubmit}>
              <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                {/* Filter inputs styled as rounded pills like the map search bar */}
                <div>
                  <select value={filters.cropType}
                          onChange={(e) => setFilters({ ...filters, cropType: e.target.value })}
                          className="w-full px-5 py-3.5 bg-gray-50 border-none rounded-full text-sm font-semibold text-emerald-950 focus:ring-2 focus:ring-emerald-900 appearance-none">
                    <option value="">All Crops</option>
                    {CROP_TYPES.filter(c=>c).map(c => <option key={c} value={c}>{c}</option>)}
                  </select>
                </div>
                <div>
                  <select value={filters.region}
                          onChange={(e) => setFilters({ ...filters, region: e.target.value })}
                          className="w-full px-5 py-3.5 bg-gray-50 border-none rounded-full text-sm font-semibold text-emerald-950 focus:ring-2 focus:ring-emerald-900 appearance-none">
                    <option value="">All Regions</option>
                    {REGIONS.filter(r=>r).map(r => <option key={r} value={r}>{r}</option>)}
                  </select>
                </div>
                <div>
                  <input type="number" step="0.1" value={filters.minApr}
                         onChange={(e) => setFilters({ ...filters, minApr: e.target.value })}
                         placeholder="Min APR %"
                         className="w-full px-5 py-3.5 bg-gray-50 border-none rounded-full text-sm font-semibold text-emerald-950 focus:ring-2 focus:ring-emerald-900 placeholder-gray-400" />
                </div>
                <div>
                  <input type="number" step="0.1" value={filters.maxApr}
                         onChange={(e) => setFilters({ ...filters, maxApr: e.target.value })}
                         placeholder="Max APR %"
                         className="w-full px-5 py-3.5 bg-gray-50 border-none rounded-full text-sm font-semibold text-emerald-950 focus:ring-2 focus:ring-emerald-900 placeholder-gray-400" />
                </div>
              </div>
              <div className="flex justify-end gap-3 mt-6">
                <button type="button" onClick={handleFilterReset}
                        className="bg-gray-100 text-emerald-950 px-6 py-3 rounded-full text-sm font-bold hover:bg-gray-200 transition">
                  Clear
                </button>
                <button type="submit"
                        className="bg-emerald-950 text-white px-8 py-3 rounded-full text-sm font-bold shadow-md hover:bg-emerald-900 transition">
                  Search Fields
                </button>
              </div>
            </form>
          </div>

          {/* Results */}
          {loading ? (
              <div className="flex justify-center py-24">
                <div className="animate-spin rounded-full h-12 w-12 border-b-4 border-emerald-950" />
              </div>
          ) : listings.length === 0 ? (
              <div className="bg-white rounded-[2.5rem] shadow-sm border border-gray-100 p-16 text-center">
                <p className="text-6xl mb-4">🔍</p>
                <p className="text-emerald-950 font-bold text-xl">No fields found</p>
                <p className="text-gray-400 font-medium mt-2">Adjust your filters to see more results</p>
              </div>
          ) : (
              <>
                <p className="text-sm font-semibold text-gray-500 mb-6 pl-2">{listings.length} plot{listings.length !== 1 ? 's' : ''} available right now</p>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {listings.map((listing) => (
                      <div key={listing.id} className="bg-white rounded-[2rem] shadow-sm border border-gray-100 hover:shadow-md transition overflow-hidden group">
                        {/* Card Header (Image representation) */}
                        <div className="relative h-32 bg-emerald-900 overflow-hidden">
                          <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1625246333195-78d9c38ad449?auto=format&fit=crop&q=80')] bg-cover bg-center opacity-60 group-hover:scale-105 transition duration-500"></div>
                          <div className="absolute top-4 right-4">
                       <span className="bg-lime-200 text-emerald-950 font-bold text-xs px-3 py-1.5 rounded-full shadow-sm">
                        {listing.currentApr}% APR
                      </span>
                          </div>
                        </div>

                        {/* Body */}
                        <div className="p-6 relative">
                          {/* Floating Icon */}
                          <div className="absolute -top-8 left-6 w-14 h-14 bg-white rounded-2xl flex items-center justify-center shadow-sm text-2xl border border-gray-50">
                            🌾
                          </div>

                          <div className="mt-6 mb-4">
                            <h3 className="font-bold text-lg text-emerald-950">{listing.cropType} Field</h3>
                            <p className="text-gray-500 text-sm font-medium mt-1">📍 {listing.region} • {listing.kebeleCode}</p>
                          </div>

                          <div className="flex justify-between items-center mb-5 bg-gray-50 p-2 rounded-full border border-gray-100">
                            <StatusBadge status={listing.status} />
                            <span className="text-xs font-bold text-gray-500 px-2">{listing.seasonName}</span>
                          </div>

                          <div className="mb-5">
                            <FundingProgress
                                funded={listing.fundedAmountEtb}
                                total={listing.totalAmountEtb}
                                pct={listing.fundingPct}
                            />
                          </div>

                          <div className="grid grid-cols-2 gap-4 mt-2 text-sm border-t border-gray-100 pt-5">
                            <div>
                              <p className="text-gray-400 text-xs font-semibold uppercase">Capacity</p>
                              <p className="font-bold text-emerald-950 mt-1">{listing.totalAmountEtb.toLocaleString()} ETB</p>
                            </div>
                            <div>
                              <p className="text-gray-400 text-xs font-semibold uppercase">Agri-Score</p>
                              <p className="font-bold text-lime-700 mt-1">{listing.agriScore} / 900</p>
                            </div>
                          </div>

                          <Link href={`/listings/${listing.id}`}
                                className="mt-6 block w-full text-center bg-emerald-950 text-white py-3.5 rounded-full text-sm font-bold hover:bg-emerald-900 transition shadow-sm">
                            View Details
                          </Link>
                        </div>
                      </div>
                  ))}
                </div>
              </>
          )}
        </div>
      </div>
  );
}