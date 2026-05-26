'use client';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { register, verifyOtp } from '@/lib/api';

export default function RegisterPage() {
  const router = useRouter();
  const [step, setStep] = useState<'form' | 'otp'>('form');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [fullName, setFullName] = useState('');
  const [phone, setPhone] = useState('');
  const [faydaId, setFaydaId] = useState('');
  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [otp, setOtp] = useState('');

  async function handleRegister(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    if (password !== confirm) { setError('Passwords do not match'); return; }
    setLoading(true);
    try {
      await register({ phone, faydaId, password, fullName, role: 'OFF_TAKER' });
      setStep('otp');
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Registration failed');
    } finally { setLoading(false); }
  }

  async function handleOtp(e: React.FormEvent) {
    e.preventDefault();
    setLoading(true);
    try {
      await verifyOtp({ phone, otpCode: otp, purpose: 'REGISTRATION' });
      router.push('/login');
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'OTP verification failed');
    } finally { setLoading(false); }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-cyan-100 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg p-8">
        <div className="text-center mb-8">
          <div className="w-16 h-16 bg-blue-700 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <span className="text-white text-2xl">🚛</span>
          </div>
          <h1 className="text-2xl font-bold text-gray-900">
            {step === 'form' ? 'Create Off-Taker Account' : 'Verify Your Phone'}
          </h1>
          <p className="text-gray-500 mt-1">
            {step === 'form' ? 'Register to procure harvests from Ethiopian farmers' : `OTP sent to ${phone}`}
          </p>
        </div>
        {error && <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6 text-sm">{error}</div>}

        {step === 'form' ? (
          <form onSubmit={handleRegister} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Full Name / Company Name</label>
              <input type="text" value={fullName} onChange={e => setFullName(e.target.value)} required
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Phone Number</label>
              <input type="tel" value={phone} onChange={e => setPhone(e.target.value)} placeholder="+251912345678" required
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Fayda National ID</label>
              <input type="text" value={faydaId} onChange={e => setFaydaId(e.target.value)} required
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
                <input type="password" value={password} onChange={e => setPassword(e.target.value)} required
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Confirm Password</label>
                <input type="password" value={confirm} onChange={e => setConfirm(e.target.value)} required
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none" />
              </div>
            </div>
            <button type="submit" disabled={loading}
              className="w-full bg-blue-700 text-white py-3 rounded-lg font-semibold hover:bg-blue-800 disabled:opacity-50 transition-colors">
              {loading ? 'Creating account...' : 'Create Account'}
            </button>
          </form>
        ) : (
          <form onSubmit={handleOtp} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">6-Digit OTP</label>
              <input type="text" value={otp} onChange={e => setOtp(e.target.value)}
                maxLength={6} placeholder="000000" required
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none text-center text-2xl tracking-widest font-mono" />
            </div>
            <button type="submit" disabled={loading}
              className="w-full bg-blue-700 text-white py-3 rounded-lg font-semibold hover:bg-blue-800 disabled:opacity-50">
              {loading ? 'Verifying...' : 'Verify OTP'}
            </button>
            <button type="button" onClick={() => setStep('form')} className="w-full text-gray-500 text-sm">← Back</button>
          </form>
        )}
        <p className="text-center text-sm text-gray-500 mt-6">
          Already have an account? <Link href="/login" className="text-blue-700 font-semibold hover:underline">Sign in</Link>
        </p>
      </div>
    </div>
  );
}
