'use client';
import { useEffect, useRef } from 'react';

interface Props {
  lat: number;
  lng: number;
  label?: string;
  height?: number;
  ndvi?: number;
  areaHectares?: number;
}

export default function FarmMap({ lat, lng, label, height = 300, ndvi, areaHectares }: Props) {
  const ref = useRef<HTMLDivElement>(null);
  const mapRef = useRef<any>(null);

  useEffect(() => {
    if (!ref.current) return;

    if (mapRef.current) {
      mapRef.current.remove();
      mapRef.current = null;
    }

    import('leaflet').then(L => {
      if (!ref.current) return;

      if ((ref.current as any)._leaflet_id) {
        (ref.current as any)._leaflet_id = null;
      }

      delete (L.Icon.Default.prototype as any)._getIconUrl;
      L.Icon.Default.mergeOptions({
        iconRetinaUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon-2x.png',
        iconUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-icon.png',
        shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/images/marker-shadow.png',
      });

      const map = L.map(ref.current, {
        zoomControl: true,
        scrollWheelZoom: false,
        attributionControl: true,
      });
      mapRef.current = map;

      // Satellite basemap — ESRI World Imagery (free, no API key)
      L.tileLayer(
        'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}',
        { attribution: 'Tiles © Esri', maxZoom: 18 }
      ).addTo(map);

      // Labels on top
      L.tileLayer(
        'https://server.arcgisonline.com/ArcGIS/rest/services/Reference/World_Boundaries_and_Places/MapServer/tile/{z}/{y}/{x}',
        { maxZoom: 18, opacity: 0.7 }
      ).addTo(map);

      map.setView([lat, lng], 14);

      const ndviColor =
        ndvi == null ? '#6b7280' :
        ndvi >= 0.6  ? '#16a34a' :
        ndvi >= 0.4  ? '#65a30d' :
        ndvi >= 0.2  ? '#ca8a04' : '#dc2626';

      const radiusM = areaHectares
        ? Math.sqrt((areaHectares * 10000) / Math.PI)
        : 150;

      L.circle([lat, lng], {
        radius: radiusM,
        color: ndviColor,
        fillColor: ndviColor,
        fillOpacity: 0.25,
        weight: 2,
      }).addTo(map);

      const icon = L.divIcon({
        html: `<div style="background:${ndviColor};border:3px solid white;border-radius:50% 50% 50% 0;transform:rotate(-45deg);width:28px;height:28px;box-shadow:0 2px 8px rgba(0,0,0,0.4);display:flex;align-items:center;justify-content:center;"><span style="transform:rotate(45deg);font-size:13px;">🌾</span></div>`,
        iconSize: [28, 28],
        iconAnchor: [14, 28],
        className: '',
      });

      const marker = L.marker([lat, lng], { icon }).addTo(map);

      if (label) {
        const ndviText = ndvi != null ? `<br><span style="color:${ndviColor};font-weight:700">NDVI: ${ndvi.toFixed(3)}</span>` : '';
        const areaText = areaHectares ? `<br><span style="color:#6b7280;font-size:11px">${areaHectares.toFixed(1)} ha</span>` : '';
        marker.bindPopup(`<div style="font-family:sans-serif;min-width:140px"><strong style="color:#0f766e">${label}</strong>${ndviText}${areaText}</div>`, { maxWidth: 200 }).openPopup();
      }
    });

    return () => {
      if (mapRef.current) {
        mapRef.current.remove();
        mapRef.current = null;
      }
    };
  }, [lat, lng, label, ndvi, areaHectares]);

  return (
    <>
      <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.css" />
      <div ref={ref} style={{ height, borderRadius: 16, overflow: 'hidden', zIndex: 0 }} />
    </>
  );
}
