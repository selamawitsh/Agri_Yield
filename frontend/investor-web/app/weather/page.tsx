'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { motion, AnimatePresence } from 'framer-motion';
import {
  CloudSun,
  Sprout,
  ChevronRight,
  MapPin,
  Layers
} from 'lucide-react';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import Sidebar from '@/components/DashboardSidebar';
import WeatherPanel from '@/components/WeatherPanel';

interface Portfolio {
  farmId: string;
  farmName?: string;
  cropType?: string;
  region?: string;
}

const pageFadeIn = {
  hidden: { opacity: 0, y: 15 },
  show: { opacity: 1, y: 0, transition: { duration: 0.4, ease: 'easeOut', staggerChildren: 0.05 } }
};

const blockVariants = {
  hidden: { opacity: 0, y: 15 },
  show: { opacity: 1, y: 0, transition: { type: 'spring', stiffness: 100, damping: 15 } }
};

export default function WeatherPage() {
  const router = useRouter();
  const [portfolioFarms, setPortfolioFarms] = useState<Portfolio[]>([]);
  const [selectedFarmId, setSelectedFarmId] = useState<string>('');
  const [loading, setLoading] = useState(true);
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    loadPortfolio();
  }, []);

  async function loadPortfolio() {
    try {
      const res = await api.get('/portfolio');
      if (res.data.success) {
        const investments = res.data.data || [];
        const farms: Portfolio[] = investments.map((inv: any) => ({
          farmId: inv.farmId,
          farmName: inv.farmName,
          cropType: inv.cropType,
          region: inv.region,
        }));

        // Deduplicate allocations by farmId
        const unique = farms.filter((f, i, arr) =>
            arr.findIndex(x => x.farmId === f.farmId) === i);

        setPortfolioFarms(unique);
        if (unique.length > 0) setSelectedFarmId(unique[0].farmId);
      }
    } catch {
      /* Error handled cleanly or falls back to empty state layout */
    } finally {
      setLoading(false);
    }
  }

  const selectedFarm = portfolioFarms.find(f => f.farmId === selectedFarmId);

  if (loading) return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-gradient-to-br from-[#f8fafc] to-[#f1f5f9] space-y-4">
        <motion.div
            animate={{ rotate: 360 }}
            transition={{ repeat: Infinity, duration: 1, ease: "linear" }}
            className="rounded-full h-12 w-12 border-4 border-slate-200 border-t-emerald-600"
        />
        <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: [0.4, 1, 0.4] }}
            transition={{ repeat: Infinity, duration: 1.5 }}
            className="text-slate-500 text-xs font-bold tracking-widest uppercase font-sans"
        >
          Accessing microclimate systems...
        </motion.p>
      </div>
  );

  return (
      <div className="min-h-screen bg-gradient-to-br from-[#f8fafc] to-[#f1f5f9] text-slate-800 font-sans antialiased flex flex-col">
        <Navbar onMenuClick={() => setIsSidebarOpen(!isSidebarOpen)} />

        <div className="flex flex-1 relative">
          <Sidebar isOpen={isSidebarOpen} setIsOpen={setIsSidebarOpen} />

          <main className="flex-1 min-w-0 overflow-y-auto px-4 sm:px-8 py-8 lg:max-w-6xl xl:max-w-7xl mx-auto w-full">
            <motion.div
                variants={pageFadeIn}
                initial="hidden"
                animate="show"
                className="space-y-6"
            >
              {/* Top Operational Header */}
              <div className="mb-2">
                <h1 className="text-2xl sm:text-3xl font-black tracking-tight text-slate-900">Weather & Climate</h1>
                <p className="text-slate-500 text-xs sm:text-sm font-medium">Monitor real-time atmospheric updates and vegetative indicators across allocations</p>
              </div>

              <AnimatePresence mode="wait">
                {portfolioFarms.length === 0 ? (
                    <motion.div
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        className="bg-white rounded-3xl shadow-xs border border-slate-200/60 p-12 text-center flex flex-col items-center justify-center space-y-3"
                    >
                      <div className="w-12 h-12 bg-slate-50 rounded-2xl flex items-center justify-center text-slate-400 border border-slate-100">
                        <CloudSun className="w-5 h-5" />
                      </div>
                      <div className="space-y-1">
                        <p className="text-slate-400 text-xs font-bold uppercase tracking-wider">No active portfolio coordinates registered</p>
                        <p className="text-slate-400 text-xs max-w-sm mx-auto">Microclimate telemetry drops online dynamically as soon as an initial cultivation allocation node is successfully initialized on the ledger.</p>
                      </div>
                      <Link
                          href="/listings"
                          className="mt-2 inline-block bg-slate-900 text-white px-5 py-2.5 rounded-xl text-xs font-bold uppercase tracking-wider hover:bg-slate-800 transition shadow-2xs"
                      >
                        Browse Primary Listings
                      </Link>
                    </motion.div>
                ) : (
                    <div className="space-y-6">
                      {/* Farm selector buttons panel */}
                      {portfolioFarms.length > 1 && (
                          <motion.div variants={blockVariants} className="bg-white rounded-3xl shadow-xs border border-slate-200/60 p-5">
                            <label className="block text-[10px] font-bold uppercase text-slate-400 tracking-wider mb-3">
                              Select Target Coordinates
                            </label>
                            <div className="flex flex-wrap gap-2">
                              {portfolioFarms.map(f => {
                                const isActive = selectedFarmId === f.farmId;
                                return (
                                    <button
                                        key={f.farmId}
                                        onClick={() => setSelectedFarmId(f.farmId)}
                                        className={`px-4 py-2 rounded-xl text-xs font-bold uppercase tracking-wide transition-all border ${
                                            isActive
                                                ? 'bg-slate-900 text-white border-slate-900 shadow-2xs'
                                                : 'bg-slate-50 text-slate-600 border-slate-200/70 hover:bg-slate-100 hover:text-slate-900'
                                        }`}
                                    >
                                      {f.farmName || f.cropType || `Node: ${f.farmId.slice(0, 8)}`}
                                    </button>
                                );
                              })}
                            </div>
                          </motion.div>
                      )}

                      {/* Target Node Profile Bar */}
                      {selectedFarm && (
                          <motion.div
                              variants={blockVariants}
                              className="bg-white rounded-2xl border border-slate-200/60 p-4 flex items-center justify-between gap-4 border-l-4 border-l-emerald-600 shadow-2xs"
                          >
                            <div className="flex items-center gap-3.5">
                              <div className="w-10 h-10 rounded-xl bg-emerald-50 text-emerald-700 border border-emerald-100/50 flex items-center justify-center shrink-0">
                                <Sprout className="w-4 h-4" />
                              </div>
                              <div className="space-y-0.5">
                                <p className="font-black text-slate-900 tracking-tight text-sm sm:text-base">
                                  {selectedFarm.farmName || selectedFarm.cropType || 'Monitored Asset Node'}
                                </p>
                                <div className="flex flex-wrap items-center gap-x-2 gap-y-0.5 text-xs text-slate-400 font-medium">
                            <span className="flex items-center gap-0.5">
                              <MapPin className="w-3.5 h-3.5" /> {selectedFarm.region || 'Ethiopia Region'}
                            </span>
                                  <span>&middot;</span>
                                  <span className="font-mono text-[11px] bg-slate-50 border border-slate-100 px-1.5 py-0.2 rounded">
                              ID: {selectedFarmId}
                            </span>
                                </div>
                              </div>
                            </div>
                            <div className="hidden sm:flex items-center gap-1 text-[10px] bg-slate-50 text-slate-400 px-2.5 py-1 rounded-md border border-slate-100 font-bold uppercase tracking-wider">
                              Telemetry Linked <ChevronRight className="w-3 h-3 text-slate-300" />
                            </div>
                          </motion.div>
                      )}

                      {/* Comprehensive Weather Panel Display Container */}
                      <motion.div variants={blockVariants} className="w-full">
                        {selectedFarmId && <WeatherPanel farmId={selectedFarmId} compact={false} />}
                      </motion.div>
                    </div>
                )}
              </AnimatePresence>
            </motion.div>
          </main>
        </div>
      </div>
  );
}