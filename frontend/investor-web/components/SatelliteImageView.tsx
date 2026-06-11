'use client';

import { useEffect, useState, useRef } from 'react';
import dynamic from 'next/dynamic';

const FarmMap = dynamic(() => import('./FarmMap'), { ssr: false });

interface Props {
  farmId: string;
  lat?: number;
  lng?: number;
  ndvi?: number;
  areaHectares?: number;
  label?: string;
}

export default function SatelliteImageView({ farmId, lat, lng, ndvi, areaHectares, label }: Props) {
  const [imgSrc, setImgSrc]       = useState<string | null>(null);
  const [loading, setLoading]     = useState(true);
  const [useFallback, setFallback] = useState(false);
  const blobUrlRef                = useRef<string | null>(null);
  const cancelledRef              = useRef(false);

  useEffect(() => {
    if (!farmId) return;
    cancelledRef.current = false;
    setLoading(true);
    setFallback(false);
    setImgSrc(null);

    const token = localStorage.getItem('access_token');
    const base  = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api/v1';
    const url   = `${base}/geospatial/farms/${farmId}/satellite-image?width=800&height=500`;

    fetch(url, { headers: { Authorization: `Bearer ${token}` } })
      .then(r => {
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        return r.blob();
      })
      .then(blob => {
        if (cancelledRef.current) return;
        if (blobUrlRef.current) URL.revokeObjectURL(blobUrlRef.current);
        const objectUrl = URL.createObjectURL(blob);
        blobUrlRef.current = objectUrl;
        setImgSrc(objectUrl);
      })
      .catch(() => {
        if (!cancelledRef.current) setFallback(true);
      })
      .finally(() => {
        if (!cancelledRef.current) setLoading(false);
      });

    return () => {
      cancelledRef.current = true;
      if (blobUrlRef.current) { URL.revokeObjectURL(blobUrlRef.current); blobUrlRef.current = null; }
    };
  }, [farmId]);

  if (loading) return (
    <div className="h-56 rounded-2xl bg-gray-100 flex items-center justify-center animate-pulse">
      <div className="text-center">
        <div className="text-3xl mb-2">🛰️</div>
        <p className="text-gray-400 text-sm">Fetching Sentinel-2 image…</p>
      </div>
    </div>
  );

  // Real satellite PNG loaded successfully
  if (imgSrc) return (
    <div className="relative">
      <img
        src={imgSrc}
        alt="Farm — Copernicus Sentinel-2 true-colour"
        className="w-full h-56 object-cover rounded-2xl"
      />
      <span className="absolute top-2 right-2 text-xs bg-black/50 text-white px-2 py-0.5 rounded-full">
        🛰️ Sentinel-2
      </span>
    </div>
  );

  // Fallback: show Leaflet satellite map when no Sentinel-2 scene available
  if (useFallback && lat && lng) return (
    <div className="relative">
      <FarmMap lat={lat} lng={lng} label={label} height={224} ndvi={ndvi} areaHectares={areaHectares} />
      <div className="absolute bottom-2 left-2 right-2 bg-black/60 text-white text-xs px-3 py-1.5 rounded-xl text-center">
        🗺️ Map view — Sentinel-2 scene not yet available for this farm
      </div>
    </div>
  );

  // No GPS coords either — minimal placeholder
  return (
    <div className="h-56 bg-gradient-to-br from-emerald-900 to-green-800 rounded-2xl flex flex-col items-center justify-center text-white/70">
      <span className="text-4xl mb-2">🛰️</span>
      <p className="text-sm font-medium">Satellite image pending</p>
      <p className="text-xs mt-1 opacity-60">Farm monitoring starts after NDVI sync</p>
    </div>
  );
}
