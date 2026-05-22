'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';

export default function HomePage() {
  const router = useRouter();

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (token) {
      router.push('/dashboard');
    } else {
      router.push('/login');
    }
  }, [router]);

  return (
      <div className="min-h-screen flex items-center justify-center bg-[#16352b]">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#caa07a] mx-auto mb-4"></div>
          <p className="text-slate-400 font-mono text-sm tracking-widest uppercase">Routing Connection...</p>
        </div>
      </div>
  );
}