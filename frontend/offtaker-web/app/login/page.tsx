'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
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
        localStorage.setItem('access_token', response.data.data.accessToken);
        localStorage.setItem('refresh_token', response.data.data.refreshToken);
        toast.success('Login successful!');
        router.push('/dashboard');
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-r from-teal-500 to-cyan-600">
      <div className="bg-white p-8 rounded-lg shadow-xl w-96">
        <div className="text-center mb-8">
          <h1 className="text-3xl font-bold text-gray-800">Agri-Yield</h1>
          <p className="text-gray-500 mt-2">Off-Taker Portal</p>
        </div>
        <form onSubmit={handleLogin}>
          <div className="mb-4">
            <label className="block text-gray-700 text-sm font-bold mb-2">Phone Number</label>
            <input
              type="tel"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-teal-500"
              placeholder="+251912345678"
              required
            />
          </div>
          <div className="mb-6">
            <label className="block text-gray-700 text-sm font-bold mb-2">Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-teal-500"
              required
            />
          </div>
          <button
            type="submit"
            disabled={loading}
            className="w-full bg-teal-600 text-white py-2 rounded-lg hover:bg-teal-700"
          >
            {loading ? 'Logging in...' : 'Login'}
          </button>
        </form>
        <div className="mt-6 text-center">
          <Link href="/register" className="text-teal-600 hover:text-teal-700 text-sm">
            Don't have an account? Register here
          </Link>
        </div>
      </div>
    </div>
  );
}
