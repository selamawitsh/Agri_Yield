'use client';
import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { register, verifyOtp } from '@/lib/api';

const RISK_OPTIONS = ['LOW', 'MODERATE', 'HIGH'];
const GOAL_OPTIONS = [
  'Agricultural impact investing',
  'Diversified portfolio returns',
  'Supporting Ethiopian farmers',
  'Inflation hedge',
  'Long-term wealth building',
];

export default function RegisterPage() {
  const router = useRouter();
  const [step, setStep] = useState<'form' | 'otp'>('form');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Form fields
  const [fullName, setFullName] = useState('');
  const [phone, setPhone] = useState('');
  const [faydaId, setFaydaId] = useState('');
  const [password, setPassword] = useState('');
  const [confirm, setConfirm] = useState('');
  const [riskTolerance, setRiskTolerance] = useState('MODERATE');
  const [investmentGoal, setInvestmentGoal] = useState(GOAL_OPTIONS[0]);

  // OTP
  const [otp, setOtp] = useState('');

  async function handleRegister(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    if (password !== confirm) { setError('Passwords do not match'); return; }
    if (password.length < 8) { setError('Password must be at least 8 characters'); return; }
    setLoading(true);
    try {
      await register({ phone, faydaId, password, fullName, role: 'INVESTOR' });
      setStep('otp');
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Registration failed');
    } finally {
      setLoading(false);
    }
  }

  async function handleOtp(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await verifyOtp({ phone, otpCode: otp, purpose: 'REGISTRATION' });
      router.push('/login?registered=true');
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'OTP verification failed');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-50 to-emerald-100 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg p-8">
        <div className="text-center mb-8">
          <div className="w-16 h-16 bg-green-600 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <span className="text-white text-2xl">🌾</span>
          </div>
          <h1 className="text-2xl font-bold text-gray-900">
            {step === 'form' ? 'Create Investor Account' : 'Verify Your Phone'}
          </h1>
          <p className="text-gray-500 mt-1">
            {step === 'form'
              ? 'Join Agri-Yield and invest in Ethiopian agriculture'
              : `Enter the 6-digit OTP sent to ${phone}`}
          </p>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6 text-sm">
            {error}
          </div>
        )}

        {step === 'form' ? (
          <form onSubmit={handleRegister} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="col-span-2">
                <label className="block text-sm font-medium text-gray-700 mb-1">Full Name</label>
                <input type="text" value={fullName} onChange={e => setFullName(e.target.value)} required
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none" />
              </div>
              <div className="col-span-2">
                <label className="block text-sm font-medium text-gray-700 mb-1">Phone Number</label>
                <input type="tel" value={phone} onChange={e => setPhone(e.target.value)} placeholder="+251912345678" required
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none" />
              </div>
              <div className="col-span-2">
                <label className="block text-sm font-medium text-gray-700 mb-1">Fayda National ID</label>
                <input type="text" value={faydaId} onChange={e => setFaydaId(e.target.value)} required
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
                <input type="password" value={password} onChange={e => setPassword(e.target.value)} required
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none" />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Confirm Password</label>
                <input type="password" value={confirm} onChange={e => setConfirm(e.target.value)} required
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none" />
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Risk Tolerance</label>
              <div className="grid grid-cols-3 gap-2">
                {RISK_OPTIONS.map(r => (
                  <button key={r} type="button" onClick={() => setRiskTolerance(r)}
                    className={`py-2 rounded-lg text-sm font-semibold border transition-colors ${
                      riskTolerance === r
                        ? 'bg-green-600 text-white border-green-600'
                        : 'bg-white text-gray-600 border-gray-300 hover:border-green-400'
                    }`}>
                    {r}
                  </button>
                ))}
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Investment Goal</label>
              <select value={investmentGoal} onChange={e => setInvestmentGoal(e.target.value)}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none">
                {GOAL_OPTIONS.map(g => <option key={g}>{g}</option>)}
              </select>
            </div>

            <button type="submit" disabled={loading}
              className="w-full bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 disabled:opacity-50 transition-colors">
              {loading ? 'Creating account...' : 'Create Account'}
            </button>
          </form>
        ) : (
          <form onSubmit={handleOtp} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">6-Digit OTP</label>
              <input type="text" value={otp} onChange={e => setOtp(e.target.value)}
                maxLength={6} placeholder="000000" required
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none text-center text-2xl tracking-widest font-mono" />
            </div>
            <button type="submit" disabled={loading}
              className="w-full bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 disabled:opacity-50 transition-colors">
              {loading ? 'Verifying...' : 'Verify OTP'}
            </button>
            <button type="button" onClick={() => setStep('form')}
              className="w-full text-gray-500 text-sm hover:text-gray-700">
              ← Back to registration
            </button>
          </form>
        )}

        <p className="text-center text-sm text-gray-500 mt-6">
          Already have an account?{' '}
          <Link href="/login" className="text-green-600 font-semibold hover:underline">Sign in</Link>
        </p>
      </div>
    </div>
  );
}
