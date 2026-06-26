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
        // Backend returns accessToken (camelCase)
        localStorage.setItem('access_token', response.data.data.accessToken);
        localStorage.setItem('refresh_token', response.data.data.refreshToken);
        toast.success('Access granted');
        router.push('/dashboard');
      }
    } catch (error: unknown) {
      const msg = error instanceof Error ? error.message : 'Authentication failed';
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center px-4 relative overflow-hidden">
      <div className="absolute -top-24 -left-24 w-96 h-96 bg-[#D4EAD9] rounded-full blur-[100px] opacity-70 pointer-events-none" />
      <div className="absolute -bottom-24 -right-24 w-96 h-96 bg-[#E8F3D6] rounded-full blur-[100px] opacity-70 pointer-events-none" />

      <div className="w-full max-w-md bg-white border border-[#E2ECE6] rounded-[28px] p-8 shadow-[0_20px_50px_-12px_rgba(28,60,42,0.08)] z-10">
        <div className="flex flex-col items-center mb-8 text-center">
            <div className="w-14 h-14 rounded-2xl bg-gradient-to-br from-[#1B4332] to-[#2D6A4F] flex items-center justify-center mb-4 shadow-lg text-white text-2xl">
          </div>
          <h1 className="text-2xl font-bold tracking-tight text-[#0F291B]">Agri-Yield Admin</h1>
          <p className="text-slate-500 text-sm mt-1">User Management Portal</p>
        </div>

        <form onSubmit={handleLogin} className="space-y-5">
          <div>
            <label className="block text-[#1C3C2A] text-xs font-semibold tracking-wide mb-2">Phone Number</label>
            <input type="tel" value={phone} onChange={e => setPhone(e.target.value)}
              className="w-full bg-[#F6F9F7] text-[#0F291B] border border-[#E2ECE6] rounded-xl px-4 py-3.5 text-sm font-medium focus:outline-none focus:border-[#2D6A4F] focus:bg-white transition-all placeholder-slate-400"
              placeholder="+251911111111" required />
          </div>
          <div>
            <label className="block text-[#1C3C2A] text-xs font-semibold tracking-wide mb-2">Password</label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)}
              className="w-full bg-[#F6F9F7] text-[#0F291B] border border-[#E2ECE6] rounded-xl px-4 py-3.5 text-sm font-medium focus:outline-none focus:border-[#2D6A4F] focus:bg-white transition-all"
              required />
          </div>
          <button type="submit" disabled={loading}
            className="w-full bg-[#1B4332] hover:bg-[#143225] text-white font-semibold text-sm py-3.5 rounded-xl transition-all disabled:opacity-50 shadow-md">
            {loading ? 'Signing in...' : 'Access Dashboard'}
          </button>
        </form>
      </div>
    </div>
  );
}
