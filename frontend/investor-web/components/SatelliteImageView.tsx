'use client';

import { useEffect, useState } from 'react';

// ── Module-level cache outside React — survives StrictMode double-invoke ──────
const imageCache = new Map<string, string>(); // farmId → blob URL
const pendingFetch = new Map<string, Promise<string | null>>(); // farmId → in-flight promise

async function fetchSatelliteImage(farmId: string): Promise<string | null> {
  // Return cached blob URL immediately
  if (imageCache.has(farmId)) return imageCache.get(farmId)!;

  // Return in-flight promise if already fetching
  if (pendingFetch.has(farmId)) return pendingFetch.get(farmId)!;

  const token = localStorage.getItem('access_token');
  const base  = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';
  const url   = `${base}/geospatial/farms/${farmId}/satellite-image?width=800&height=500`;

  const attempt = async (tries: number): Promise<string | null> => {
    try {
      const r = await fetch(url, { headers: { Authorization: `Bearer ${token}` } });
      if (!r.ok) {
        if (r.status === 404 && tries < 4) {
          await new Promise(res => setTimeout(res, 5000)); // wait 5s then retry
          return attempt(tries + 1);
        }
        return null;
      }
      const blob = await r.blob();
      const objectUrl = URL.createObjectURL(blob);
      imageCache.set(farmId, objectUrl);
      return objectUrl;
    } catch {
      return null;
    }
  };

  const promise = attempt(1).finally(() => pendingFetch.delete(farmId));
  pendingFetch.set(farmId, promise);
  return promise;
}

interface Props {
  farmId: string;
  ndvi?: number;
  healthStatus?: string;
  cloudCoverage?: number;
  recordedDate?: string;
}

export default function SatelliteImageView({
  farmId, ndvi, healthStatus, cloudCoverage, recordedDate
}: Props) {
  const [imgSrc, setImgSrc]   = useState<string | null>(() => imageCache.get(farmId) ?? null);
  const [loading, setLoading] = useState(!imageCache.has(farmId));
  const [failed, setFailed]   = useState(false);

  useEffect(() => {
    if (!farmId) return;
    // Already cached — nothing to do
    if (imageCache.has(farmId)) {
      setImgSrc(imageCache.get(farmId)!);
      setLoading(false);
      return;
    }

    setLoading(true);
    setFailed(false);

    fetchSatelliteImage(farmId).then(url => {
      if (url) {
        setImgSrc(url);
        setLoading(false);
      } else {
        setFailed(true);
        setLoading(false);
      }
    });
    // No cleanup needed — fetch lives outside React lifecycle
  }, [farmId]);

  const ndviColor =
    ndvi == null ? '#6b7280' :
    ndvi >= 0.6  ? '#16a34a' :
    ndvi >= 0.4  ? '#65a30d' :
    ndvi >= 0.2  ? '#ca8a04' : '#dc2626';

  if (loading) return (
    <div className="h-64 rounded-2xl bg-gray-100 flex items-center justify-center">
      <div className="text-center">
        <div className="w-10 h-10 border-4 border-green-600 border-t-transparent rounded-full animate-spin mx-auto mb-3" />
        <p className="text-gray-500 text-sm font-medium">Fetching Sentinel-2 image…</p>
        <p className="text-gray-400 text-xs mt-1">Band 4/3/2 · 10m resolution</p>
      </div>
    </div>
  );

  if (failed) return (
    <div className="h-64 rounded-2xl border-2 border-dashed border-gray-200 bg-gray-50 flex flex-col items-center justify-center gap-3 px-6">
      <div className="w-14 h-14 bg-gray-100 rounded-2xl flex items-center justify-center text-2xl">🛰️</div>
      <div className="text-center">
        <p className="text-gray-700 font-semibold text-sm">Satellite image unavailable</p>
        <p className="text-gray-400 text-xs mt-1.5 leading-relaxed max-w-xs">
          No clear Sentinel-2 scene in the last 45 days. Cloud cover was too high during satellite passes.
        </p>
      </div>
      <div className="flex gap-2 flex-wrap justify-center">
        <span className="text-xs bg-gray-100 text-gray-500 px-3 py-1 rounded-full">☁️ High cloud cover</span>
        <span className="text-xs bg-gray-100 text-gray-500 px-3 py-1 rounded-full">🔄 Sentinel-2 passes every 5 days</span>
      </div>
    </div>
  );

  if (imgSrc) return (
    <div className="relative rounded-2xl overflow-hidden">
      <img
        src={imgSrc}
        alt="Farm — Copernicus Sentinel-2 true-colour"
        className="w-full h-64 object-cover"
      />
      <div className="absolute top-0 left-0 right-0 flex justify-between items-start p-3">
        <span className="bg-black/50 backdrop-blur-sm text-white text-xs px-2.5 py-1 rounded-full font-medium">
          🛰️ Sentinel-2 · Band 4/3/2
        </span>
        {recordedDate && (
          <span className="bg-black/50 backdrop-blur-sm text-white text-xs px-2.5 py-1 rounded-full">
            📅 {new Date(recordedDate).toLocaleDateString('en-ET', { day:'numeric', month:'short', year:'numeric' })}
          </span>
        )}
      </div>
      <div className="absolute bottom-0 left-0 right-0 bg-gradient-to-t from-black/75 to-transparent px-4 pt-8 pb-3">
        <div className="flex items-center justify-between">
          <p className="text-white/60 text-xs">Copernicus · 10m resolution · True-colour</p>
          {ndvi != null && (
            <div className="text-xs font-bold px-2.5 py-1 rounded-full border"
              style={{ background: ndviColor+'33', color:'#fff', borderColor: ndviColor+'88' }}>
              🌿 NDVI {ndvi.toFixed(3)}{healthStatus && ` — ${healthStatus}`}
            </div>
          )}
        </div>
        {cloudCoverage != null && (
          <p className="text-white/40 text-xs mt-0.5">☁️ Cloud cover: {cloudCoverage.toFixed(1)}%</p>
        )}
      </div>
    </div>
  );

  return null;
}
