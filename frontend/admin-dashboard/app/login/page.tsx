'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';
import api from '@/lib/api';

export default function LoginPage() {
  const router = useRouter();
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await api.post('/auth/login', { phone, password });

      if (response.data.success) {
        localStorage.setItem('access_token', response.data.data.access_token);
        localStorage.setItem('refresh_token', response.data.data.refresh_token);
        toast.success('System connection verified');
        router.push('/dashboard');
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  return (
      <div className="min-h-screen flex items-center justify-center px-4 relative overflow-hidden">
        {/* Decorative ambient blurred vector fields to match the app screenshot backgrounds */}
        <div className="absolute -top-24 -left-24 w-96 h-96 bg-[#D4EAD9] rounded-full blur-[100px] opacity-70 pointer-events-none" />
        <div className="absolute -bottom-24 -right-24 w-96 h-96 bg-[#E8F3D6] rounded-full blur-[100px] opacity-70 pointer-events-none" />

        <div className="w-full max-w-md bg-white border border-[#E2ECE6] rounded-[28px] p-8 shadow-[0_20px_50px_-12px_rgba(28,60,42,0.08)] z-10 relative">
          <div className="flex flex-col items-center mb-8 text-center">
            <div className="w-14 h-14 rounded-2xl bg-gradient-to-br from-[#1B4332] to-[#2D6A4F] flex items-center justify-center mb-4 shadow-lg shadow-emerald-900/10 text-white">
              <svg className="w-6 h-6" fill="none" stroke="currentColor" strokeWidth="2.5" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 3v18M3 12h18M12 3l4 4M12 3L8 7m4 14l4-4m-4 4l-4-4" />
              </svg>
            </div>
            <h1 className="text-2xl font-bold tracking-tight text-[#0F291B]">Agri-Yield Hub</h1>
            <p className="text-slate-500 text-sm mt-1">Field Operation & Management Portal</p>
          </div>

          <form onSubmit={handleLogin} className="space-y-5">
            <div>
              <label className="block text-[#1C3C2A] text-xs font-semibold tracking-wide mb-2">Registered Phone Number</label>
              <input
                  type="tel"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                  className="w-full bg-[#F6F9F7] text-[#0F291B] border border-[#E2ECE6] rounded-xl px-4 py-3.5 text-sm font-medium focus:outline-none focus:border-[#2D6A4F] focus:bg-white transition-all placeholder-slate-400"
                  placeholder="+251911111111"
                  required
              />
            </div>

            <div>
              <label className="block text-[#1C3C2A] text-xs font-semibold tracking-wide mb-2">Security Access Key</label>
              <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full bg-[#F6F9F7] text-[#0F291B] border border-[#E2ECE6] rounded-xl px-4 py-3.5 text-sm font-medium focus:outline-none focus:border-[#2D6A4F] focus:bg-white transition-all placeholder-slate-400"
                  placeholder="••••••••"
                  required
              />
            </div>

            <button
                type="submit"
                disabled={loading}
                className="w-full bg-[#1B4332] hover:bg-[#143225] text-white font-semibold text-sm py-3.5 rounded-xl transition-all active:scale-[0.98] disabled:opacity-50 mt-2 shadow-md shadow-emerald-900/10"
            >
              {loading ? 'Verifying Coordinates...' : 'Access Dashboard'}
            </button>
          </form>

          <div className="mt-8 pt-6 border-t border-[#F0F5F2] text-center font-medium text-xs text-slate-500 bg-[#F8FAF9] rounded-xl p-3 border">
            <span className="text-[#2D6A4F] font-semibold block mb-1">Sandbox Node Mode</span>
            <p className="font-mono text-slate-600">Tel: +251911111111 · Key: admin123</p>
          </div>
        </div>
      </div>
  );
}