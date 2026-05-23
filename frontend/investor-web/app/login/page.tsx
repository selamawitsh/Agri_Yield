'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import toast from 'react-hot-toast';
import api from '@/lib/api';

export default function LoginPage() {
  const router = useRouter();
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('access_token');
    if (token && token.includes('.') && token.split('.').length === 3) {
      router.push('/dashboard');
    }
  }, []);

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await api.post('/auth/login', { phone, password });

      if (response.data.success) {
        const accessToken = response.data.data.accessToken;
        const refreshToken = response.data.data.refreshToken;

        if (accessToken && accessToken.includes('.') && accessToken.split('.').length === 3) {
          localStorage.setItem('access_token', accessToken);
          localStorage.setItem('refresh_token', refreshToken);
          toast.success('Login successful!');
          router.push('/dashboard');
        } else {
          toast.error('Invalid token received');
        }
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
      <div className="min-h-screen flex items-center justify-center bg-emerald-950 relative overflow-hidden">
        {/* Background Image overlay to match app's organic feel */}
        <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1586771107445-d3ca888129ff?auto=format&fit=crop&q=80')] bg-cover bg-center opacity-30 mix-blend-overlay"></div>

        <div className="bg-white p-10 rounded-[2.5rem] shadow-2xl w-full max-w-md relative z-10 m-4">
          <div className="text-center mb-10">
            <div className="w-16 h-16 bg-lime-200 rounded-[1.25rem] mx-auto flex items-center justify-center text-3xl mb-4 shadow-sm">
              🌱
            </div>
            <h1 className="text-3xl font-black text-emerald-950 tracking-tight">Agri-Yield</h1>
            <p className="text-gray-500 font-medium mt-2">Welcome back, Investor.</p>
          </div>

          <form onSubmit={handleLogin}>
            <div className="mb-5">
              <label className="block text-emerald-950 text-sm font-bold mb-2 ml-2">
                Phone Number
              </label>
              <input
                  type="tel"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                  className="w-full px-5 py-4 bg-gray-50 border border-gray-200 rounded-full focus:outline-none focus:ring-2 focus:ring-emerald-900 focus:bg-white transition text-emerald-950 font-semibold placeholder-gray-400"
                  placeholder="+251 912 345 678"
                  required
              />
            </div>

            <div className="mb-8">
              <label className="block text-emerald-950 text-sm font-bold mb-2 ml-2">
                Password
              </label>
              <input
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full px-5 py-4 bg-gray-50 border border-gray-200 rounded-full focus:outline-none focus:ring-2 focus:ring-emerald-900 focus:bg-white transition text-emerald-950 font-semibold placeholder-gray-400"
                  placeholder="••••••••"
                  required
              />
            </div>

            <button
                type="submit"
                disabled={loading}
                className="w-full bg-emerald-950 text-white py-4 rounded-full text-lg font-bold hover:bg-emerald-900 transition duration-200 shadow-lg hover:-translate-y-0.5 disabled:opacity-50 disabled:hover:translate-y-0"
            >
              {loading ? 'Authenticating...' : 'Sign In'}
            </button>
          </form>

          <div className="mt-8 text-center">
            <Link href="/register" className="text-emerald-700 hover:text-emerald-900 font-bold text-sm transition">
              Create an account
            </Link>
          </div>

          {/* Demo credentials */}
          <div className="mt-8 p-4 bg-lime-50 rounded-3xl text-center border border-lime-100">
            <p className="text-xs font-bold text-emerald-900 uppercase tracking-wide mb-2">Demo Account</p>
            <div className="flex justify-center gap-4 text-xs font-medium text-emerald-800">
              <span>+251900000001</span>
              <span className="w-1 h-1 bg-emerald-300 rounded-full my-auto"></span>
              <span>Debug@1234</span>
            </div>
          </div>
        </div>
      </div>
  );
}