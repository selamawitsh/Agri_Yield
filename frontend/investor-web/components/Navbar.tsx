'use client';

import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';

export default function Navbar() {
  const router = useRouter();
  const pathname = usePathname();

  const handleLogout = () => {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    router.push('/login');
  };

  const navLinks = [
    { href: '/dashboard', label: 'Dashboard' },
    { href: '/listings', label: 'Browse Farms' },
    { href: '/portfolio', label: 'Portfolio' },
    { href: '/payouts', label: 'Payouts' },
    { href: '/profile', label: 'Profile' },
  ];

  return (
      <nav className="bg-white shadow-md sticky top-0 z-50">
        <div className="container mx-auto px-6 py-4">
          <div className="flex justify-between items-center">
            <Link href="/dashboard" className="text-2xl font-bold text-green-600">
              Agri-Yield
            </Link>
            <div className="flex items-center gap-6">
              {navLinks.map((link) => (
                  <Link
                      key={link.href}
                      href={link.href}
                      className={`text-sm font-medium transition-colors ${
                          pathname === link.href
                              ? 'text-green-600 border-b-2 border-green-600 pb-1'
                              : 'text-gray-600 hover:text-green-600'
                      }`}
                  >
                    {link.label}
                  </Link>
              ))}
              <button
                  onClick={handleLogout}
                  className="bg-red-500 text-white px-4 py-2 rounded-lg text-sm hover:bg-red-600 transition"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>
  );
}