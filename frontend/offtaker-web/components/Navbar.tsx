'use client';

import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import { clearTokens } from '@/lib/auth';
import { logout } from '@/lib/api';

const NAV = [
  { href: '/dashboard',  label: 'Dashboard',  icon: '🏠' },
  { href: '/farms',      label: 'Find Farms',  icon: '🌾' },
  { href: '/bids',       label: 'My Bids',     icon: '💰' },
  { href: '/logistics',  label: 'Logistics',   icon: '🚚' },
  { href: '/analytics',  label: 'Analytics',   icon: '📊' },
];

export default function Navbar() {
  const pathname = usePathname();
  const router   = useRouter();

  const handleLogout = async () => {
    try { await logout(); } catch {}
    clearTokens();
    router.push('/login');
  };

  return (
    <nav className="bg-white border-b border-gray-100 shadow-sm sticky top-0 z-40">
      <div className="container mx-auto px-6 py-0 max-w-7xl flex items-center justify-between h-14">
        <Link href="/dashboard" className="flex items-center gap-2 font-bold text-teal-700 text-lg">
          🚛 <span>Agri-Yield</span>
          <span className="text-xs font-normal text-gray-400 hidden sm:block">Off-Taker Portal</span>
        </Link>
        <div className="flex items-center gap-1">
          {NAV.map(n => (
            <Link key={n.href} href={n.href}
              className={`px-3 py-1.5 rounded-lg text-sm font-medium transition hidden md:flex items-center gap-1.5 ${
                pathname.startsWith(n.href)
                  ? 'bg-teal-50 text-teal-700'
                  : 'text-gray-500 hover:bg-gray-50 hover:text-gray-800'
              }`}>
              <span>{n.icon}</span>{n.label}
            </Link>
          ))}
          <Link href="/profile"
            className="ml-2 w-8 h-8 rounded-full bg-teal-100 text-teal-700 flex items-center justify-center text-sm font-bold hover:bg-teal-200 transition">
            👤
          </Link>
          <button onClick={handleLogout}
            className="ml-2 text-xs text-gray-400 hover:text-red-500 transition px-2 py-1">
            Sign out
          </button>
        </div>
      </div>
      {/* Mobile bottom nav */}
      <div className="md:hidden fixed bottom-0 left-0 right-0 bg-white border-t border-gray-100 flex justify-around py-2 z-40">
        {NAV.map(n => (
          <Link key={n.href} href={n.href}
            className={`flex flex-col items-center text-xs gap-0.5 px-2 ${
              pathname.startsWith(n.href) ? 'text-teal-700' : 'text-gray-400'
            }`}>
            <span className="text-lg">{n.icon}</span>
            {n.label.split(' ')[0]}
          </Link>
        ))}
      </div>
    </nav>
  );
}
