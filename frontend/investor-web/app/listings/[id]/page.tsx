'use client';

import { useEffect, useState } from 'react';
import { useRouter, useParams } from 'next/navigation';
import dynamic from 'next/dynamic';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import Navbar from '@/components/Navbar';
import StatusBadge from '@/components/StatusBadge';
import FundingProgress from '@/components/FundingProgress';
import NdviChart from '@/components/NdviChart';
import YieldPredictionBar from '@/components/YieldPredictionBar';
import {
  ChevronLeft,
  MapPin,
  ShieldCheck,
  Calendar,
  Info,
  TrendingUp,
  Activity,
  Layers,
  Star,
  CheckCircle2,
  AlertCircle,
  Tag
} from 'lucide-react';
import { FarmListing, NdviReading, YieldPrediction } from '@/lib/types';

const FarmMap = dynamic(() => import('@/components/FarmMap'), { ssr: false });

interface AgronomistReport {
  reportId: string;
  visitDate: string;
  diagnosis: string;
  treatment: string;
  rating: number;
}

interface Bid {
  id: string;
  offtakerId: string;
  quantityQuintals: number;
  pricePerQuintalEtb: number;
  totalValueEtb: number;
  status: string;
  expiresAt: string;
}

interface VoucherEvent {
  sequenceOrder: number;
  productCategory: string;
  productDescription: string;
  amountEtb: number;
  status: string;
  redeemedAt: string | null;
}

export default function ListingDetailPage() {
  const router   = useRouter();
  const params   = useParams();
  const listingId = params.id as string;

  const [listing,        setListing]        = useState<FarmListing | null>(null);
  const [ndviHistory,    setNdviHistory]    = useState<NdviReading[]>([]);
  const [yieldPrediction,setYieldPrediction]= useState<YieldPrediction | null>(null);
  const [agronomistRpts, setAgronomistRpts] = useState<AgronomistReport[]>([]);
  const [bids,           setBids]           = useState<Bid[]>([]);
  const [voucherEvents,  setVoucherEvents]  = useState<VoucherEvent[]>([]);
  const [loading,        setLoading]        = useState(true);
  const [investing,      setInvesting]      = useState(false);
  const [showModal,      setShowModal]      = useState(false);
  const [investAmount,   setInvestAmount]   = useState('');
  const [investNotes,    setInvestNotes]    = useState('');

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (!token) { router.push('/login'); return; }
    fetchAll();
  }, [listingId]);

  const fetchAll = async () => {
    try {
      const [listingRes, ndviRes] = await Promise.allSettled([
        api.get(`/listings/${listingId}`),
        api.get(`/listings/${listingId}/ndvi-history`),
      ]);

      if (listingRes.status === 'fulfilled' && listingRes.value.data.success) {
        const l: FarmListing = listingRes.value.data.data;
        setListing(l);

        api.get(`/offtaker/bids/farm/${l.farmId}`).then(r => {
          if (r.data.success) setBids(r.data.data || []);
        }).catch(() => {});

        api.get(`/farms/${l.farmId}/digital-twin`).then(r => {
          if (r.data.success) {
            setAgronomistRpts(r.data.data?.agronomistReports || []);
          }
        }).catch(() => {});

        api.get(`/vouchers/farm/${l.farmId}`).then(r => {
          if (r.data.success) {
            const vouchers = r.data.data?.vouchers || [];
            setVoucherEvents(vouchers.map((v: any) => ({
              sequenceOrder:    v.sequenceOrder,
              productCategory:  v.productCategory,
              productDescription: v.productDescription,
              amountEtb:        v.amountEtb,
              status:           v.status,
              redeemedAt:       v.redeemedAt,
            })));
          }
        }).catch(() => {});

        api.get(`/geospatial/yield/${l.farmId}`).then(r => {
          if (r.data.success) setYieldPrediction(r.data.data);
        }).catch(() => {});

      } else {
        toast.error('Listing not found');
        router.push('/listings');
        return;
      }

      if (ndviRes.status === 'fulfilled' && ndviRes.value.data.success)
        setNdviHistory(ndviRes.value.data.data || []);

    } catch {
      toast.error('Failed to load listing');
      router.push('/listings');
    } finally { setLoading(false); }
  };

  const handleInvest = async (e: React.FormEvent) => {
    e.preventDefault();
    const amount = parseFloat(investAmount);
    if (isNaN(amount) || amount < 500) { toast.error('Minimum investment is 500 ETB'); return; }
    if (!listing) return;
    const remaining = listing.totalAmountEtb - listing.fundedAmountEtb;
    if (amount > remaining) { toast.error(`Max you can invest is ${remaining.toLocaleString()} ETB`); return; }
    setInvesting(true);
    try {
      const res = await api.post(`/listings/${listingId}/invest`, { amountEtb: amount, notes: investNotes });
      if (res.data.success) {
        toast.success('Investment placed! Funds are secured in escrow.');
        setShowModal(false);
        setInvestAmount('');
        fetchAll();
      }
    } catch (err: any) {
      toast.error(err.response?.data?.message || 'Investment failed');
    } finally { setInvesting(false); }
  };

  if (loading) return (
      <div className="h-screen flex flex-col items-center justify-center bg-[#f8fafc] space-y-4">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-slate-900" />
        <p className="text-slate-400 text-xs font-semibold tracking-wide uppercase">Loading farm details...</p>
      </div>
  );
  if (!listing) return null;

  const remaining   = listing.totalAmountEtb - listing.fundedAmountEtb;
  const canInvest   = listing.status === 'OPEN' || listing.status === 'PARTIALLY_FUNDED';
  const latestNdvi  = ndviHistory.length > 0 ? ndviHistory[ndviHistory.length - 1].ndviValue : null;
  const ndviLabel   = latestNdvi == null ? 'No Data'
      : latestNdvi >= 0.6 ? 'Excellent' : latestNdvi >= 0.4 ? 'Good'
          : latestNdvi >= 0.2 ? 'Moderate' : 'Poor';
  const ndviColor   = latestNdvi == null ? 'text-slate-400'
      : latestNdvi >= 0.6 ? 'text-emerald-700' : latestNdvi >= 0.4 ? 'text-lime-700'
          : latestNdvi >= 0.2 ? 'text-amber-700' : 'text-rose-700';

  const regionCoords: Record<string, [number, number]> = {
    'Oromia': [8.5, 39.3], 'Amhara': [11.5, 38.0], 'SNNPR': [6.8, 37.5],
    'Tigray': [14.0, 38.5], 'Somali': [7.5, 44.0],  'Afar':  [11.8, 41.5],
  };
  const [mapLat, mapLng] = regionCoords[listing.region] || [9.145, 40.489];
  const expiresAt = listing.listingExpiresAt ?? listing.fundingDeadline;

  return (
      <div className="min-h-screen bg-[#f8fafc] text-slate-800 font-sans antialiased pb-16">
        <Navbar />

        <div className="max-w-5xl mx-auto px-4 sm:px-6 py-8 space-y-6">

          {/* Back Button */}
          <button
              onClick={() => router.back()}
              className="flex items-center gap-2 text-slate-600 font-bold text-xs bg-white px-4 py-2.5 rounded-xl shadow-2xs border border-slate-100 hover:bg-slate-50 transition uppercase tracking-wide"
          >
            <ChevronLeft className="w-4 h-4" />
            Back to Listings
          </button>

          {/* Hero Card Banner */}
          <div className="bg-white rounded-2xl shadow-2xs border border-slate-100 overflow-hidden">
            <div className="relative h-48 bg-slate-900">
              <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1592982537447-6f296fb00fd8?auto=format&fit=crop&q=80')] bg-cover bg-center opacity-40" />

              <div className="absolute bottom-5 left-6 right-6 flex flex-col sm:flex-row justify-between items-start sm:items-end gap-4">
                <div className="bg-white/95 backdrop-blur-md p-4 rounded-xl border border-white/20 shadow-xs">
                  <h1 className="text-lg font-black tracking-tight text-slate-900">{listing.cropType} &mdash; {listing.region}</h1>
                  <div className="flex items-center gap-1.5 text-slate-400 text-xs font-semibold mt-1">
                    <MapPin className="w-3 h-3 text-slate-300" />
                    <span>Kebele {listing.kebeleCode}</span>
                    <span className="text-slate-200">&middot;</span>
                    <span>{listing.seasonName}</span>
                  </div>
                </div>

                <div className="bg-emerald-800 text-white px-5 py-3 rounded-xl text-center shadow-sm border border-emerald-700/50">
                  <p className="text-2xl font-black leading-none">{listing.currentApr}%</p>
                  <p className="text-[10px] font-bold uppercase tracking-wider mt-1.5 opacity-90">Current APR</p>
                </div>
              </div>
            </div>

            <div className="p-6 space-y-6">
              {/* Top Badges */}
              <div className="flex flex-wrap gap-2 items-center">
                <StatusBadge status={listing.status} />
                {listing.satelliteVerified && (
                    <span className="text-[10px] font-extrabold text-emerald-800 bg-emerald-50 border border-emerald-100 px-3 py-1 rounded-md tracking-wide uppercase flex items-center gap-1">
                  <ShieldCheck className="w-3 h-3 text-emerald-600" />
                  Verified Orbit
                </span>
                )}
                {expiresAt && (
                    <span className="text-[10px] font-extrabold text-slate-500 bg-slate-50 border border-slate-100 px-3 py-1 rounded-md tracking-wide uppercase flex items-center gap-1">
                  <Calendar className="w-3 h-3 text-slate-400" />
                  Deadline: {new Date(expiresAt).toLocaleDateString()}
                </span>
                )}
                <span className="text-[10px] font-extrabold text-blue-800 bg-blue-50 border border-blue-100 px-3 py-1 rounded-md tracking-wide uppercase">
                Parametric Insurance: Active
              </span>
              </div>

              {/* Funding Progress */}
              <div className="bg-slate-50 border border-slate-100/80 p-4 rounded-xl">
                <FundingProgress funded={listing.fundedAmountEtb} total={listing.totalAmountEtb} pct={listing.fundingPct} />
              </div>

              {/* Metrics Dashboard Grid */}
              <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                {[
                  { label: 'Total Needed',   value: `${listing.totalAmountEtb?.toLocaleString()} ETB` },
                  { label: 'Funded Amount',  value: `${listing.fundedAmountEtb?.toLocaleString()} ETB` },
                  { label: 'Remaining Target', value: `${remaining.toLocaleString()} ETB` },
                  { label: 'Farm Score',     value: `${listing.agriScore} / 900` },
                  { label: 'Base APR',       value: `${listing.baseApr}%` },
                  { label: 'Calculated APR', value: `${listing.currentApr}%` },
                  { label: 'NDVI Health',    value: ndviLabel, valueClass: ndviColor },
                  { label: 'Latest NDVI Scan', value: latestNdvi != null ? latestNdvi.toFixed(3) : 'N/A' },
                ].map(item => (
                    <div key={item.label} className="bg-slate-50 border border-slate-100/60 rounded-xl p-3.5">
                      <p className="text-slate-400 text-[10px] font-bold uppercase tracking-wider">{item.label}</p>
                      <p className={`font-black text-sm mt-1 ${(item as any).valueClass || 'text-slate-900'}`}>{item.value}</p>
                    </div>
                ))}
              </div>
            </div>
          </div>

          {/* NDVI Chart Section */}
          <div className="bg-white rounded-2xl shadow-2xs border border-slate-100 p-6">
            <div className="flex items-center gap-2 mb-1">
              <Activity className="w-4 h-4 text-emerald-600" />
              <h3 className="font-extrabold text-sm text-slate-900 tracking-tight">NDVI Plant Growth Trend</h3>
            </div>
            <p className="text-xs text-slate-400 mb-4 font-medium">Satellite health history across the current season. Higher levels mean a stronger crop canopy.</p>
            <NdviChart data={ndviHistory} height={220} />
          </div>

          {/* Yield Prediction Component */}
          {yieldPrediction && (
              <div className="bg-white rounded-2xl shadow-2xs border border-slate-100 p-1">
                <YieldPredictionBar
                    min={yieldPrediction.predictedYieldMin}
                    mean={yieldPrediction.predictedYieldMean}
                    max={yieldPrediction.predictedYieldMax}
                    confidencePct={yieldPrediction.confidencePct}
                    cropType={listing.cropType}
                />
              </div>
          )}

          {/* Farm Map Section */}
          <div className="bg-white rounded-2xl shadow-2xs border border-slate-100 p-6">
            <div className="flex items-center gap-2 mb-4">
              <Layers className="w-4 h-4 text-emerald-600" />
              <h3 className="font-extrabold text-sm text-slate-900 tracking-tight">Geospatial Farm Boundaries</h3>
            </div>
            <div className="rounded-xl overflow-hidden border border-slate-100">
              <FarmMap lat={mapLat} lng={mapLng} label={`${listing.cropType} — ${listing.region}`} height={260} />
            </div>
            <p className="text-[11px] text-slate-400 mt-3 text-center font-medium">
              Approximate region coordinates &mdash; {listing.region}, Kebele {listing.kebeleCode}
            </p>
          </div>

          {/* APR Calculation Warning Message */}
          <div className="bg-lime-50 border border-lime-100/80 rounded-2xl p-5 flex items-start gap-3.5">
            <TrendingUp className="w-5 h-5 text-emerald-700 mt-0.5 flex-shrink-0" />
            <div>
              <h3 className="font-extrabold text-xs text-emerald-950 tracking-wide uppercase">Interactive Return Breakdown</h3>
              <p className="text-emerald-900 text-xs font-medium mt-1 leading-relaxed">
                {listing.baseApr}% base rate + satellite health bonus + weather index bonus = <strong className="font-black text-emerald-950">{listing.currentApr}%</strong> total active APR.
              </p>
              <p className="text-emerald-700/60 text-[10px] font-semibold mt-1">Calculations are refreshed via remote orbit data feeds every 5 days.</p>
            </div>
          </div>

          {/* Off-Taker Commercial Bids */}
          <div className="bg-white rounded-2xl shadow-2xs border border-slate-100 p-6">
            <h3 className="font-extrabold text-sm text-slate-900 tracking-tight mb-4">Off-Taker Purchase Contracts</h3>
            {bids.length === 0 ? (
                <div className="bg-slate-50 border border-dashed border-slate-200 rounded-xl p-6 text-center text-slate-400 text-xs font-medium">
                  <AlertCircle className="w-5 h-5 mx-auto mb-2 text-slate-300" />
                  No commercial buy bids recorded. Purchasing entities can place binding volume commitments once funding is fully closed.
                </div>
            ) : (
                <div className="space-y-3">
                  {bids.map(bid => (
                      <div key={bid.id} className="flex items-center justify-between border border-slate-100 bg-slate-50/50 rounded-xl p-4">
                        <div>
                          <p className="text-xs font-bold text-slate-800">
                            {bid.quantityQuintals} quintals at {bid.pricePerQuintalEtb.toLocaleString()} ETB/qt
                          </p>
                          <p className="text-[11px] text-slate-400 font-medium mt-0.5">
                            Total Agreement: {bid.totalValueEtb.toLocaleString()} ETB &middot; Expires {new Date(bid.expiresAt).toLocaleDateString()}
                          </p>
                        </div>
                        <span className={`text-[10px] font-extrabold px-2.5 py-1 rounded-md tracking-wide uppercase ${
                            bid.status === 'ACCEPTED'       ? 'bg-emerald-50 border border-emerald-100 text-emerald-700' :
                                bid.status === 'CONTRACT_SIGNED' ? 'bg-blue-50 border border-blue-100 text-blue-700' :
                                    bid.status === 'PENDING'         ? 'bg-amber-50 border border-amber-100 text-amber-700' :
                                        'bg-slate-100 border border-slate-200 text-slate-500'
                        }`}>{bid.status}</span>
                      </div>
                  ))}
                </div>
            )}
          </div>

          {/* Voucher Redemptions */}
          <div className="bg-white rounded-2xl shadow-2xs border border-slate-100 p-6">
            <h3 className="font-extrabold text-sm text-slate-900 tracking-tight mb-4">Input Voucher Disbursements</h3>
            {voucherEvents.length === 0 ? (
                <div className="bg-slate-50 border border-dashed border-slate-200 rounded-xl p-6 text-center text-slate-400 text-xs font-medium">
                  <Tag className="w-5 h-5 mx-auto mb-2 text-slate-300" />
                  Secured track lines will compile immediately when funding parameters are finalized.
                </div>
            ) : (
                <div className="space-y-2.5">
                  {voucherEvents.sort((a, b) => a.sequenceOrder - b.sequenceOrder).map(v => (
                      <div key={v.sequenceOrder}
                           className={`flex items-center justify-between border rounded-xl p-3.5 ${
                               v.status === 'REDEEMED'  ? 'border-emerald-100 bg-emerald-50/30' :
                                   v.status === 'ACTIVE'    ? 'border-amber-100 bg-amber-50/30' :
                                       v.status === 'EXPIRED'   ? 'border-rose-100 bg-rose-50/30' :
                                           'border-slate-100 bg-slate-50/50'
                           }`}
                      >
                        <div>
                          <p className="text-xs font-bold text-slate-800">
                            Step {v.sequenceOrder} &mdash; {v.productDescription}
                          </p>
                          <p className="text-[11px] text-slate-400 font-medium mt-0.5">
                            Allocation: {v.amountEtb.toLocaleString()} ETB
                            {v.redeemedAt && ` &middot; Disbursed ${new Date(v.redeemedAt).toLocaleDateString()}`}
                          </p>
                        </div>
                        <span className={`text-[10px] font-extrabold px-2.5 py-1 rounded-md tracking-wide uppercase ${
                            v.status === 'REDEEMED'  ? 'bg-emerald-50 border border-emerald-100 text-emerald-700' :
                                v.status === 'ACTIVE'    ? 'bg-amber-50 border border-amber-100 text-amber-700' :
                                    v.status === 'EXPIRED'   ? 'bg-rose-50 border border-rose-100 text-rose-700' :
                                        'bg-slate-100 border border-slate-200 text-slate-500'
                        }`}>{v.status}</span>
                      </div>
                  ))}
                </div>
            )}
          </div>

          {/* Agronomist Reports */}
          <div className="bg-white rounded-2xl shadow-2xs border border-slate-100 p-6">
            <h3 className="font-extrabold text-sm text-slate-900 tracking-tight mb-4">Agronomist Field Inspection Logs</h3>
            {agronomistRpts.length === 0 ? (
                <div className="bg-slate-50 border border-dashed border-slate-200 rounded-xl p-6 text-center text-slate-400 text-xs font-medium">
                  <CheckCircle2 className="w-5 h-5 mx-auto mb-2 text-slate-300" />
                  No verified in-person field visits recorded during this crop cycle window.
                </div>
            ) : (
                <div className="space-y-4">
                  {agronomistRpts.map(r => (
                      <div key={r.reportId} className="border border-slate-100 bg-slate-50/30 rounded-xl p-4">
                        <div className="flex justify-between items-center mb-3">
                          <p className="text-xs font-bold text-slate-800">
                            Inspection Entry: {new Date(r.visitDate).toLocaleDateString()}
                          </p>
                          <div className="flex gap-0.5">
                            {Array.from({ length: 5 }, (_, i) => (
                                <Star key={i} className={`w-3.5 h-3.5 ${i < r.rating ? 'fill-amber-400 text-amber-400' : 'text-slate-200'}`} />
                            ))}
                          </div>
                        </div>
                        <div className="space-y-1.5 text-xs">
                          <p className="text-slate-700"><span className="font-bold text-slate-400 uppercase text-[10px] mr-1">Condition Profile:</span> {r.diagnosis}</p>
                          <p className="text-slate-700"><span className="font-bold text-slate-400 uppercase text-[10px] mr-1">Applied Strategy:</span> {r.treatment}</p>
                        </div>
                      </div>
                  ))}
                </div>
            )}
          </div>

          {/* Bottom Invest Call to Action */}
          {canInvest ? (
              <button
                  onClick={() => setShowModal(true)}
                  className="w-full bg-slate-900 text-white py-3.5 rounded-xl text-xs font-bold tracking-wider uppercase hover:bg-slate-800 transition shadow-xs"
              >
                Invest Now &mdash; {listing.currentApr}% APR
              </button>
          ) : (
              <div className="w-full bg-slate-100 text-slate-400 border border-slate-200/60 py-3.5 rounded-xl text-center text-xs font-bold tracking-wider uppercase">
                {listing.status === 'FULLY_FUNDED' ? 'Fully Funded' : listing.status}
              </div>
          )}
        </div>

        {/* Invest Input Form Modal */}
        {showModal && (
            <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-xs flex items-end md:items-center justify-center z-50 p-4">
              <div className="bg-white rounded-2xl p-6 w-full max-w-md shadow-xl border border-slate-100">
                <div className="flex items-center justify-between border-b border-slate-100 pb-3 mb-4">
                  <h2 className="text-sm font-black text-slate-900 uppercase tracking-wide">Secure Farm Investment</h2>
                  <button onClick={() => setShowModal(false)} className="text-slate-400 hover:text-slate-600 font-bold text-xs uppercase">Close</button>
                </div>

                <p className="text-slate-500 text-xs font-medium mb-4">
                  {listing.region} region &middot; <span className="text-emerald-700 font-bold">{listing.currentApr}% APR</span> &middot; Maximum available: {remaining.toLocaleString()} ETB
                </p>

                <form onSubmit={handleInvest} className="space-y-4">
                  <div>
                    <label className="block text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-2">Funding Amount (ETB) &middot; Minimum 500</label>
                    <input
                        type="number" min="500" max={remaining} step="100" required
                        value={investAmount} onChange={e => setInvestAmount(e.target.value)}
                        className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-sm font-bold text-slate-800 focus:outline-none focus:ring-2 focus:ring-slate-900 focus:bg-white transition"
                        placeholder="500"
                    />
                    {investAmount && parseFloat(investAmount) >= 500 && (
                        <p className="text-emerald-700 text-[11px] font-bold mt-2 pl-1 flex items-center gap-1">
                          <Info className="w-3 h-3" />
                          Estimated Annual Yield Return: {(parseFloat(investAmount) * listing.currentApr / 100).toFixed(2)} ETB
                        </p>
                    )}
                  </div>

                  <div>
                    <label className="block text-[11px] font-bold text-slate-400 uppercase tracking-wider mb-2">Optional Memo Notes</label>
                    <textarea
                        value={investNotes} onChange={e => setInvestNotes(e.target.value)}
                        className="w-full px-4 py-3 bg-slate-50 border border-slate-200 rounded-xl text-xs text-slate-700 resize-none focus:outline-none focus:ring-2 focus:ring-slate-900 focus:bg-white transition"
                        rows={2} placeholder="Add specific notes about this action item..."
                    />
                  </div>

                  <div className="flex gap-2 pt-2">
                    <button
                        type="button"
                        onClick={() => setShowModal(false)}
                        className="flex-1 bg-slate-100 text-slate-600 py-3 rounded-xl text-xs font-bold uppercase tracking-wider hover:bg-slate-200 transition"
                    >
                      Cancel
                    </button>
                    <button
                        type="submit"
                        disabled={investing}
                        className="flex-1 bg-slate-900 text-white py-3 rounded-xl text-xs font-bold uppercase tracking-wider hover:bg-slate-800 transition disabled:opacity-50"
                    >
                      {investing ? 'Processing...' : 'Confirm Allocation'}
                    </button>
                  </div>
                </form>
              </div>
            </div>
        )}
      </div>
  );
}