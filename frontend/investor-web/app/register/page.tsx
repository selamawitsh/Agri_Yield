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
  });
  
  const [riskProfile, setRiskProfile] = useState({
    riskTolerance: '',
    investmentGoal: '',
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
        role: 'INVESTOR',
        fullName: formData.fullName,
      });
      
      if (response.data.success) {
        setPhoneNumber(formData.phone);
        setPassword(formData.password);
        toast.success('OTP sent! Check backend terminal for code');
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
        toast.success('Phone verified! Complete your profile');
        setStep(3);
      }
    } catch (error: any) {
      toast.error(error.response?.data?.message || 'Invalid OTP');
    } finally {
      setLoading(false);
    }
  };

  const saveProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!riskProfile.riskTolerance) {
      toast.error('Please select risk tolerance');
      return;
    }
    if (!riskProfile.investmentGoal) {
      toast.error('Please enter investment goal');
      return;
    }
    
    setLoading(true);
    try {
      // First, login to get the JWT token
      const loginResponse = await api.post('/auth/login', {
        phone: phoneNumber,
        password: password,
      });
      
      if (loginResponse.data.success) {
        const jwtToken = loginResponse.data.data.accessToken;
        localStorage.setItem('access_token', jwtToken);
        localStorage.setItem('refresh_token', loginResponse.data.data.refreshToken);
        
        // Update profile
        const updateData = {
          riskTolerance: riskProfile.riskTolerance,
          investmentGoal: riskProfile.investmentGoal
        };
        
        const profileResponse = await api.patch('/users/me', updateData);
        
        if (profileResponse.data.success) {
          toast.success('Registration complete!');
          router.push('/dashboard');
        }
      }
    } catch (error: any) {
      console.error('Error:', error);
      toast.error(error.response?.data?.message || 'Failed to save profile');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 py-12">
      <div className="container mx-auto max-w-2xl px-4">
        <div className="bg-white rounded-lg shadow-lg p-8">
          <div className="text-center mb-8">
            <h1 className="text-3xl font-bold text-green-600">Agri-Yield</h1>
            <p className="text-gray-600 mt-2">Investor Registration</p>
            <div className="flex justify-center gap-2 mt-4">
              {[1, 2, 3].map((s) => (
                <div key={s} className={`w-3 h-3 rounded-full ${s === step ? 'bg-green-600' : 'bg-gray-300'}`} />
              ))}
            </div>
          </div>

          {step === 1 && (
            <form onSubmit={handleRegister}>
              <div className="space-y-4">
                <div>
                  <label className="block text-gray-700 mb-2">Full Name</label>
                  <input type="text" required value={formData.fullName}
                    onChange={(e) => setFormData({...formData, fullName: e.target.value})}
                    className="w-full px-3 py-2 border rounded-lg" />
                </div>
                <div>
                  <label className="block text-gray-700 mb-2">Phone Number</label>
                  <input type="tel" required placeholder="+251912345678"
                    value={formData.phone}
                    onChange={(e) => setFormData({...formData, phone: e.target.value})}
                    className="w-full px-3 py-2 border rounded-lg" />
                </div>
                <div>
                  <label className="block text-gray-700 mb-2">Fayda National ID</label>
                  <input type="text" required
                    value={formData.faydaId}
                    onChange={(e) => setFormData({...formData, faydaId: e.target.value})}
                    className="w-full px-3 py-2 border rounded-lg" />
                </div>
                <div>
                  <label className="block text-gray-700 mb-2">Password</label>
                  <input type="password" required
                    value={formData.password}
                    onChange={(e) => setFormData({...formData, password: e.target.value})}
                    className="w-full px-3 py-2 border rounded-lg" />
                </div>
                <div>
                  <label className="block text-gray-700 mb-2">Confirm Password</label>
                  <input type="password" required
                    value={formData.confirmPassword}
                    onChange={(e) => setFormData({...formData, confirmPassword: e.target.value})}
                    className="w-full px-3 py-2 border rounded-lg" />
                </div>
              </div>
              <button type="submit" disabled={loading}
                className="w-full mt-6 bg-green-600 text-white py-2 rounded-lg hover:bg-green-700 disabled:opacity-50">
                {loading ? 'Sending OTP...' : 'Register'}
              </button>
            </form>
          )}

          {step === 2 && (
            <form onSubmit={verifyOtp}>
              <div>
                <label className="block text-gray-700 mb-2">Enter OTP</label>
                <p className="text-sm text-gray-500 mb-2">Check backend terminal for the OTP code</p>
                <input type="text" required placeholder="123456"
                  value={otpCode} onChange={(e) => setOtpCode(e.target.value)}
                  className="w-full px-3 py-2 border rounded-lg" />
              </div>
              <button type="submit" disabled={loading}
                className="w-full mt-6 bg-green-600 text-white py-2 rounded-lg hover:bg-green-700 disabled:opacity-50">
                {loading ? 'Verifying...' : 'Verify OTP'}
              </button>
            </form>
          )}

          {step === 3 && (
            <form onSubmit={saveProfile}>
              <div className="space-y-4">
                <div>
                  <label className="block text-gray-700 mb-2">Risk Tolerance</label>
                  <select 
                    required
                    value={riskProfile.riskTolerance}
                    onChange={(e) => setRiskProfile({...riskProfile, riskTolerance: e.target.value})}
                    className="w-full px-3 py-2 border rounded-lg">
                    <option value="">Select...</option>
                    <option value="LOW">Low - Prefer stable, lower returns</option>
                    <option value="MODERATE">Moderate - Balanced risk/return</option>
                    <option value="HIGH">High - Willing to accept volatility</option>
                  </select>
                </div>
                <div>
                  <label className="block text-gray-700 mb-2">Investment Goal</label>
                  <input type="text" required
                    value={riskProfile.investmentGoal}
                    onChange={(e) => setRiskProfile({...riskProfile, investmentGoal: e.target.value})}
                    placeholder="e.g., Retirement, Wealth growth, Income"
                    className="w-full px-3 py-2 border rounded-lg" />
                </div>
              </div>
              <button type="submit" disabled={loading}
                className="w-full mt-6 bg-green-600 text-white py-2 rounded-lg hover:bg-green-700 disabled:opacity-50">
                {loading ? 'Completing Registration...' : 'Complete Registration'}
              </button>
            </form>
          )}
        </div>
      </div>
    </div>
  );
}
