'use client';

import { useState, useEffect } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import toast from 'react-hot-toast';
import api from '@/lib/api';
import { User } from '@/lib/types';
import {
    LayoutDashboard,
    Sprout,
    Wallet,
    History,
    FileText,
    CloudSun,
    User as UserIcon,
    LogOut,
    Menu,
    X
} from 'lucide-react';

interface DashboardSidebarProps {
    activeTab?: string;
}

export default function DashboardSidebar({ activeTab = 'dashboard' }: DashboardSidebarProps) {
    const router = useRouter();
    const [profile, setProfile] = useState<User | null>(null);
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

    useEffect(() => {
        api.get('/users/me')
            .then((res) => {
                if (res.data.success) setProfile(res.data.data);
            })
            .catch(() => {});
    }, []);

    const handleLogout = () => {
        localStorage.removeItem('access_token');
        router.push('/login');
    };

    const navLinks = [
        { id: 'dashboard', label: 'Dashboard', icon: LayoutDashboard, href: '/dashboard' },
        { id: 'farms', label: 'Farms Listing', icon: Sprout, href: '/listings' },
        { id: 'portfolio', label: 'My Portfolio', icon: Wallet, href: '/portfolio' },
        { id: 'payouts', label: 'Payout History', icon: History, href: '/payouts' },
        { id: 'statements', label: 'Statements', icon: FileText, href: '/statements' },
        { id: 'weather', label: 'Weather Alerts', icon: CloudSun, href: '/dashboard#weather-section' },
        { id: 'profile', label: 'Profile Settings', icon: UserIcon, href: '/profile' },
    ];

    return (
        <>
            {/* Mobile View Toggle Strip */}
            <div className="lg:hidden fixed top-0 left-0 right-0 z-50 bg-white border-b border-slate-200/80 px-4 py-3.5 flex items-center justify-between flex-shrink-0">
                <div className="flex items-center gap-2">
                    <div className="w-8 h-8 rounded-lg bg-emerald-800 flex items-center justify-center text-white font-bold text-xs">
                        Y
                    </div>
                    <span className="font-bold text-sm text-slate-900 tracking-tight">Agri Yield</span>
                </div>
                <button
                    onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                    className="p-2 text-slate-600 hover:bg-slate-50 rounded-xl transition-colors border border-slate-100"
                >
                    {isMobileMenuOpen ? <X className="w-5 h-5" /> : <Menu className="w-5 h-5" />}
                </button>
            </div>

            {/* Mobile drawer backdrop overlay */}
            {isMobileMenuOpen && (
                <div
                    onClick={() => setIsMobileMenuOpen(false)}
                    className="fixed inset-0 bg-slate-900/30 backdrop-blur-xs z-40 lg:hidden"
                />
            )}

            {/* Permanent Sidebar Container */}
            <aside className={`
        fixed inset-y-0 left-0 z-50 w-72 bg-white border-r border-slate-100 p-6 flex flex-col justify-between 
        transition-transform duration-300 ease-in-out transform lg:translate-x-0 lg:static lg:flex-shrink-0
        ${isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full lg:translate-x-0'}
      `}>
                <div className="space-y-8 overflow-y-auto pr-1 flex-1">
                    {/* Dashboard Application Logo */}
                    <div className="flex items-center gap-3 px-2">
                        <div className="w-9 h-9 rounded-xl bg-emerald-800 flex items-center justify-center text-white font-black text-base shadow-sm shadow-emerald-800/20">
                            Y
                        </div>
                        <div className="flex flex-col">
                            <span className="font-extrabold text-sm text-slate-900 tracking-tight leading-none">Agri Yield</span>
                            <span className="text-[10px] font-semibold text-emerald-600 tracking-wider uppercase mt-1">Platform</span>
                        </div>
                    </div>

                    {/* User Profile Info */}
                    <div className="flex items-center gap-3.5 p-3 bg-slate-50 rounded-xl border border-slate-100/60">
                        <div className="w-10 h-10 rounded-lg bg-gradient-to-br from-emerald-600 to-teal-700 flex items-center justify-center text-white font-bold text-xs shadow-xs">
                            {profile?.phone ? profile.phone.slice(-2) : 'FI'}
                        </div>
                        <div className="overflow-hidden flex-1">
                            <p className="text-xs font-bold text-slate-900 truncate tracking-tight">{profile?.phone || 'Investor Account'}</p>
                            <span className={`inline-block text-[9px] font-extrabold uppercase tracking-widest mt-0.5 ${profile?.kycStatus === 'VERIFIED' ? 'text-emerald-600' : 'text-amber-600'}`}>
                {profile?.kycStatus === 'VERIFIED' ? 'Verified Profile' : 'Pending Verification'}
              </span>
                        </div>
                    </div>

                    {/* Navigation Items */}
                    <nav className="space-y-1">
                        <p className="text-[10px] font-bold tracking-widest text-slate-400 uppercase mb-3 px-3">Main Menu</p>
                        {navLinks.map((link) => {
                            const Icon = link.icon;
                            const isActive = activeTab === link.id;
                            return (
                                <Link
                                    key={link.id}
                                    href={link.href}
                                    onClick={() => setIsMobileMenuOpen(false)}
                                    className={`flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all duration-150 ${
                                        isActive
                                            ? 'bg-slate-900 text-white shadow-sm font-semibold'
                                            : 'text-slate-500 hover:bg-slate-50 hover:text-slate-900'
                                    }`}
                                >
                                    <Icon className={`w-4 h-4 ${isActive ? 'text-emerald-400' : 'text-slate-400'}`} />
                                    {link.label}
                                </Link>
                            );
                        })}
                    </nav>
                </div>

                {/* Sidebar Footer */}
                <div className="space-y-4 pt-4 border-t border-slate-100 flex-shrink-0">
                    <button
                        onClick={handleLogout}
                        className="flex items-center gap-3 w-full px-4 py-3 text-slate-500 hover:bg-rose-50 hover:text-rose-600 rounded-xl text-sm font-medium transition-colors group"
                    >
                        <LogOut className="w-4 h-4 text-slate-400 group-hover:text-rose-500" />
                        Sign Out
                    </button>
                    <div className="text-[10px] text-slate-400 font-semibold px-4 tracking-wide uppercase">
                        System v2.6.0
                    </div>
                </div>
            </aside>
        </>
    );
}