'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';
import api from '@/lib/api';

export default function RegisterPage() {
  const router = useRouter();
  const [step, setStep] = useState(1);
  const [loading, setLoading] = useState(false);
  const [phoneNumber, setPhoneNumber] = useState('');
  const [password, setPassword] = useState('');
  
  const [formData, setFormData] = useState({
    phone: '',
    faydaId: '',
    password: '',
    confirmPassword: '',
    fullName: '',
    companyName: '',
    tinNumber: '',
  });
  
  const [otpCode, setOtpCode] = useState('');

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    if (formData.password !== formData.confirmPassword) {
      toast.error('Passwords do not match');
      return;
    }
    
    setLoading(true);
    try {
      const response = await api.post('/auth/register', {
        phone: formData.phone,
        faydaId: formData.faydaId,
        password: formData.password,
        role: 'OFF_TAKER',
        fullName: formData.fullName,
      });
      
      if (response.data.success) {
        setPhoneNumber(formData.phone);
        setPassword(formData.password);
        toast.success('OTP sent! Check backend terminal');
        setStep(2);
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  const verifyOtp = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await api.post('/auth/otp/verify', {
        phone: phoneNumber,
        otpCode: otpCode,
        purpose: 'REGISTRATION',
      });
      
      if (response.data.success) {
        toast.success('Phone verified! Please login');
        setStep(3);
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Invalid OTP');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="container mx-auto max-w-2xl px-4">
        <div className="bg-white rounded-lg shadow-lg p-8">
          <div className="text-center mb-8">
            <h1 className="text-3xl font-bold text-teal-600">Agri-Yield</h1>
            <p className="text-gray-600 mt-2">Off-Taker Registration</p>
            <div className="flex justify-center gap-2 mt-4">
              {[1, 2, 3].map((s) => (
                <div key={s} className={`w-3 h-3 rounded-full ${s === step ? 'bg-teal-600' : 'bg-gray-300'}`} />
              ))}
            </div>
          </div>

          {step === 1 && (
            <form onSubmit={handleRegister}>
              <div className="space-y-4">
                <div>
                  <label className="block text-gray-700 mb-2">Company Name</label>
                  <input type="text" required
                    value={formData.companyName}
                    onChange={(e) => setFormData({...formData, companyName: e.target.value})}
                    className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-teal-500"
                  />
                </div>
                <div>
                  <label className="block text-gray-700 mb-2">TIN Number</label>
                  <input type="text" required
                    value={formData.tinNumber}
                    onChange={(e) => setFormData({...formData, tinNumber: e.target.value})}
                    className="w-full px-3 py-2 border rounded-lg"
                  />
                </div>
                <div>
                  <label className="block text-gray-700 mb-2">Contact Person</label>
                  <input type="text" required
                    value={formData.fullName}
                    onChange={(e) => setFormData({...formData, fullName: e.target.value})}
                    className="w-full px-3 py-2 border rounded-lg"
                  />
                </div>
                <div>
                  <label className="block text-gray-700 mb-2">Phone Number</label>
                  <input type="tel" required placeholder="+251912345678"
                    value={formData.phone}
                    onChange={(e) => setFormData({...formData, phone: e.target.value})}
                    className="w-full px-3 py-2 border rounded-lg"
                  />
                </div>
                <div>
                  <label className="block text-gray-700 mb-2">Fayda National ID</label>
                  <input type="text" required
                    value={formData.faydaId}
                    onChange={(e) => setFormData({...formData, faydaId: e.target.value})}
                    className="w-full px-3 py-2 border rounded-lg"
                  />
                </div>
                <div>
                  <label className="block text-gray-700 mb-2">Password</label>
                  <input type="password" required
                    value={formData.password}
                    onChange={(e) => setFormData({...formData, password: e.target.value})}
                    className="w-full px-3 py-2 border rounded-lg"
                  />
                </div>
                <div>
                  <label className="block text-gray-700 mb-2">Confirm Password</label>
                  <input type="password" required
                    value={formData.confirmPassword}
                    onChange={(e) => setFormData({...formData, confirmPassword: e.target.value})}
                    className="w-full px-3 py-2 border rounded-lg"
                  />
                </div>
              </div>
              <button type="submit" disabled={loading}
                className="w-full mt-6 bg-teal-600 text-white py-2 rounded-lg hover:bg-teal-700">
                {loading ? 'Sending OTP...' : 'Register'}
              </button>
            </form>
          )}

          {step === 2 && (
            <form onSubmit={verifyOtp}>
              <div>
                <label className="block text-gray-700 mb-2">Enter OTP sent to {phoneNumber}</label>
                <p className="text-sm text-gray-500 mb-2">Check backend terminal for OTP</p>
                <input type="text" required placeholder="123456"
                  value={otpCode} onChange={(e) => setOtpCode(e.target.value)}
                  className="w-full px-3 py-2 border rounded-lg"
                />
              </div>
              <button type="submit" disabled={loading}
                className="w-full mt-6 bg-teal-600 text-white py-2 rounded-lg hover:bg-teal-700">
                {loading ? 'Verifying...' : 'Verify OTP'}
              </button>
            </form>
          )}

          {step === 3 && (
            <div className="text-center">
              <div className="text-green-600 text-5xl mb-4">✓</div>
              <h2 className="text-2xl font-bold mb-2">Registration Complete!</h2>
              <p className="text-gray-600 mb-6">Your account has been created successfully.</p>
              <button
                onClick={() => router.push('/login')}
                className="bg-teal-600 text-white px-6 py-2 rounded-lg hover:bg-teal-700"
              >
                Go to Login
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
