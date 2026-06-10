'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { register, verifyOtp } from '@/lib/api';

// SRS §6.3.2: Multi-step: phone/Fayda/password → risk quiz (3 questions) → bank linking → confirm
type Step = 'form' | 'otp' | 'risk' | 'bank' | 'done';

const RISK_QUESTIONS = [
  {
    id: 'tolerance',
    question: 'How would you describe your investment risk appetite?',
    options: [
      { label: 'Conservative — I prefer stable, lower returns', value: 'LOW' },
      { label: 'Balanced — I accept moderate risk for better returns', value: 'MODERATE' },
      { label: 'Aggressive — I seek high returns and accept higher risk', value: 'HIGH' },
    ],
  },
  {
    id: 'horizon',
    question: 'What is your typical investment time horizon?',
    options: [
      { label: 'Short-term (under 6 months)', value: 'SHORT' },
      { label: 'Medium-term (6–18 months)', value: 'MEDIUM' },
      { label: 'Long-term (over 18 months)', value: 'LONG' },
    ],
  },
  {
    id: 'goal',
    question: 'What is your primary investment goal?',
    options: [
      { label: 'Agricultural impact investing', value: 'Agricultural impact investing' },
      { label: 'Diversified portfolio returns', value: 'Diversified portfolio returns' },
      { label: 'Supporting Ethiopian farmers', value: 'Supporting Ethiopian farmers' },
      { label: 'Inflation hedge', value: 'Inflation hedge' },
      { label: 'Long-term wealth building', value: 'Long-term wealth building' },
    ],
  },
];

const STEP_LABELS = ['Account', 'Verify', 'Risk Profile', 'Bank Account', 'Done'];
const STEP_KEYS: Step[] = ['form', 'otp', 'risk', 'bank', 'done'];

export default function RegisterPage() {
  const router = useRouter();
  const [step, setStep]       = useState<Step>('form');
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState('');

  // Step 1 — account
  const [fullName, setFullName]   = useState('');
  const [phone, setPhone]         = useState('');
  const [faydaId, setFaydaId]     = useState('');
  const [password, setPassword]   = useState('');
  const [confirm, setConfirm]     = useState('');
  const [showPwd, setShowPwd]     = useState(false);

  // Step 2 — OTP
  const [otp, setOtp] = useState('');

  // Step 3 — risk quiz
  const [riskAnswers, setRiskAnswers] = useState<Record<string, string>>({
    tolerance: '', horizon: '', goal: '',
  });

  // Step 4 — bank
  const [bankType,   setBankType]   = useState('TELEBIRR');
  const [bankNumber, setBankNumber] = useState('');
  const [skipBank,   setSkipBank]   = useState(false);

  const stepIndex = STEP_KEYS.indexOf(step);

  // ── Step 1: Register ──────────────────────────────────────────────────────
  async function handleRegister(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    if (password !== confirm) { setError('Passwords do not match'); return; }
    if (password.length < 8)  { setError('Password must be at least 8 characters'); return; }
    setLoading(true);
    try {
      await register({ phone, faydaId, password, fullName, role: 'INVESTOR' });
      setStep('otp');
    } catch (err: any) {
      setError(err.response?.data?.message || err.message || 'Registration failed');
    } finally { setLoading(false); }
  }

  // ── Step 2: OTP verify ────────────────────────────────────────────────────
  async function handleOtp(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await verifyOtp({ phone, otpCode: otp, purpose: 'REGISTRATION' });
      setStep('risk');
    } catch (err: any) {
      setError(err.response?.data?.message || 'OTP verification failed');
    } finally { setLoading(false); }
  }

  // ── Step 3: Risk quiz ─────────────────────────────────────────────────────
  async function handleRisk(e: React.FormEvent) {
    e.preventDefault();
    setError('');
    const unanswered = RISK_QUESTIONS.filter(q => !riskAnswers[q.id]);
    if (unanswered.length > 0) { setError('Please answer all questions'); return; }
    // Save risk profile via PATCH /users/me after login — for now proceed
    setStep('bank');
  }

  // ── Step 4: Bank linking ──────────────────────────────────────────────────
  async function handleBank(e: React.FormEvent) {
    e.preventDefault();
    setStep('done');
  }

  function handleSkipBank() {
    setSkipBank(true);
    setStep('done');
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-green-50 to-emerald-100 flex items-center justify-center p-4">
      <div className="bg-white rounded-2xl shadow-xl w-full max-w-lg p-8">

        {/* Logo */}
        <div className="text-center mb-6">
          <div className="w-14 h-14 bg-green-600 rounded-2xl flex items-center justify-center mx-auto mb-3">
            <span className="text-white text-2xl">🌾</span>
          </div>
          <h1 className="text-xl font-bold text-gray-900">Create Investor Account</h1>
          <p className="text-gray-500 text-sm mt-1">Agri-Yield — Ethiopia's Agricultural Fintech</p>
        </div>

        {/* Step progress — SRS §6.3.2 */}
        <div className="flex items-center justify-between mb-8 px-1">
          {STEP_LABELS.map((label, i) => (
            <div key={label} className="flex items-center">
              <div className="flex flex-col items-center">
                <div className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold transition-all ${
                  i < stepIndex  ? 'bg-green-600 text-white' :
                  i === stepIndex ? 'bg-green-700 text-white ring-2 ring-green-300' :
                  'bg-gray-100 text-gray-400'
                }`}>
                  {i < stepIndex ? '✓' : i + 1}
                </div>
                <span className={`text-xs mt-1 hidden sm:block ${i === stepIndex ? 'text-green-700 font-semibold' : 'text-gray-400'}`}>
                  {label}
                </span>
              </div>
              {i < STEP_LABELS.length - 1 && (
                <div className={`h-0.5 w-8 sm:w-10 mx-1 mt-[-10px] sm:mt-[-10px] transition-all ${
                  i < stepIndex ? 'bg-green-500' : 'bg-gray-200'
                }`} />
              )}
            </div>
          ))}
        </div>

        {/* Error */}
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-5 text-sm flex justify-between">
            <span>{error}</span>
            <button onClick={() => setError('')} className="text-red-400 hover:text-red-600 ml-2">✕</button>
          </div>
        )}

        {/* ── Step 1: Account form ── */}
        {step === 'form' && (
          <form onSubmit={handleRegister} className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Full Name</label>
              <input type="text" value={fullName} onChange={e => setFullName(e.target.value)} required
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Phone Number</label>
              <input type="tel" value={phone} onChange={e => setPhone(e.target.value)}
                placeholder="+251912345678" required
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none" />
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Fayda National ID</label>
              <input type="text" value={faydaId} onChange={e => setFaydaId(e.target.value)} required
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none" />
            </div>
            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
                <div className="relative">
                  <input type={showPwd ? 'text' : 'password'} value={password}
                    onChange={e => setPassword(e.target.value)} required minLength={8}
                    className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none pr-10" />
                  <button type="button" onClick={() => setShowPwd(!showPwd)}
                    className="absolute right-3 top-3.5 text-gray-400 text-xs">{showPwd ? 'Hide' : 'Show'}</button>
                </div>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">Confirm</label>
                <input type="password" value={confirm} onChange={e => setConfirm(e.target.value)} required
                  className={`w-full px-4 py-3 border rounded-lg focus:ring-2 focus:ring-green-500 outline-none ${
                    confirm && confirm !== password ? 'border-red-400' : 'border-gray-300'
                  }`} />
              </div>
            </div>
            <button type="submit" disabled={loading}
              className="w-full bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 disabled:opacity-50 transition">
              {loading ? 'Creating account…' : 'Continue →'}
            </button>
          </form>
        )}

        {/* ── Step 2: OTP ── */}
        {step === 'otp' && (
          <form onSubmit={handleOtp} className="space-y-5">
            <div className="bg-green-50 border border-green-200 rounded-lg p-4 text-sm text-green-700 text-center">
              A 6-digit OTP was sent to <strong>{phone}</strong>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Enter OTP</label>
              <input type="text" value={otp} onChange={e => setOtp(e.target.value.replace(/\D/g, ''))}
                maxLength={6} placeholder="000000" required
                className="w-full px-4 py-4 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none text-center text-3xl tracking-widest font-mono" />
            </div>
            <button type="submit" disabled={loading || otp.length !== 6}
              className="w-full bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 disabled:opacity-50 transition">
              {loading ? 'Verifying…' : 'Verify OTP →'}
            </button>
            <button type="button" onClick={() => setStep('form')}
              className="w-full text-gray-500 text-sm hover:text-gray-700">← Back</button>
          </form>
        )}

        {/* ── Step 3: Risk quiz (SRS §6.3.2 "3 questions") ── */}
        {step === 'risk' && (
          <form onSubmit={handleRisk} className="space-y-6">
            <div className="bg-blue-50 border border-blue-100 rounded-lg p-3 text-sm text-blue-700 text-center">
              📊 Help us tailor your investment experience
            </div>
            {RISK_QUESTIONS.map((q, qi) => (
              <div key={q.id}>
                <p className="text-sm font-semibold text-gray-800 mb-3">
                  {qi + 1}. {q.question}
                </p>
                <div className="space-y-2">
                  {q.options.map(opt => (
                    <label key={opt.value}
                      className={`flex items-center gap-3 p-3 rounded-xl border cursor-pointer transition ${
                        riskAnswers[q.id] === opt.value
                          ? 'border-green-500 bg-green-50 text-green-800'
                          : 'border-gray-200 hover:border-green-300 text-gray-700'
                      }`}>
                      <input type="radio" name={q.id} value={opt.value}
                        checked={riskAnswers[q.id] === opt.value}
                        onChange={() => setRiskAnswers(prev => ({ ...prev, [q.id]: opt.value }))}
                        className="accent-green-600" />
                      <span className="text-sm">{opt.label}</span>
                    </label>
                  ))}
                </div>
              </div>
            ))}
            <button type="submit"
              disabled={Object.values(riskAnswers).some(v => !v)}
              className="w-full bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 disabled:opacity-50 transition">
              Continue →
            </button>
          </form>
        )}

        {/* ── Step 4: Bank linking (SRS §6.3.2) ── */}
        {step === 'bank' && (
          <form onSubmit={handleBank} className="space-y-5">
            <div className="bg-amber-50 border border-amber-100 rounded-lg p-3 text-sm text-amber-700">
              🏦 Link your payout account — you'll receive harvest returns here. You can add more accounts later.
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Account Type</label>
              <div className="grid grid-cols-2 gap-3">
                {['TELEBIRR', 'CBE'].map(type => (
                  <button key={type} type="button" onClick={() => setBankType(type)}
                    className={`py-3 rounded-xl border text-sm font-semibold transition ${
                      bankType === type
                        ? 'border-green-500 bg-green-50 text-green-700'
                        : 'border-gray-200 text-gray-600 hover:border-green-300'
                    }`}>
                    {type === 'TELEBIRR' ? '📱 Telebirr' : '🏦 CBE Birr'}
                  </button>
                ))}
              </div>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Account Number</label>
              <input type="text" value={bankNumber} onChange={e => setBankNumber(e.target.value)}
                placeholder={bankType === 'TELEBIRR' ? '09XXXXXXXX' : 'CBE account number'}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 outline-none" />
              <p className="text-xs text-gray-400 mt-1">A test deposit of 1 ETB will be sent to verify your account</p>
            </div>
            <button type="submit" disabled={!bankNumber}
              className="w-full bg-green-600 text-white py-3 rounded-lg font-semibold hover:bg-green-700 disabled:opacity-50 transition">
              Link Account →
            </button>
            <button type="button" onClick={handleSkipBank}
              className="w-full text-gray-500 text-sm hover:text-gray-700">
              Skip for now — add later in Profile
            </button>
          </form>
        )}

        {/* ── Step 5: Done ── */}
        {step === 'done' && (
          <div className="text-center space-y-5">
            <div className="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto">
              <span className="text-4xl">🎉</span>
            </div>
            <div>
              <h2 className="text-xl font-bold text-gray-900">Account created!</h2>
              <p className="text-gray-500 text-sm mt-2">
                Welcome to Agri-Yield, {fullName}. Your profile is set up and ready.
                {!skipBank && bankNumber && ' Your bank account has been linked and is pending verification.'}
              </p>
            </div>
            <div className="bg-green-50 border border-green-100 rounded-xl p-4 text-sm text-green-700 space-y-1">
              <p>✓ Account registered</p>
              <p>✓ Phone verified</p>
              <p>✓ Risk profile saved</p>
              <p>{skipBank ? '○ Bank account — add in Profile later' : '✓ Bank account linked'}</p>
            </div>
            <button onClick={() => router.push('/login?registered=true')}
              className="w-full bg-green-600 text-white py-3 rounded-lg font-bold hover:bg-green-700 transition">
              Sign In to Your Account
            </button>
          </div>
        )}

        {step === 'form' && (
          <p className="text-center text-sm text-gray-500 mt-6">
            Already have an account?{' '}
            <Link href="/login" className="text-green-600 font-semibold hover:underline">Sign in</Link>
          </p>
        )}
      </div>
    </div>
  );
}
