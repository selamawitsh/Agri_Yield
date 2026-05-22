import type { Metadata } from 'next';
import { Inter } from 'next/font/google';
import './globals.css';
import { Toaster } from 'react-hot-toast';

const inter = Inter({ subsets: ['latin'], variable: '--font-sans' });

export const metadata: Metadata = {
    title: 'Agri-Yield Management Platform',
    description: 'Precision Agriculture Node & User Verification System',
};

export default function RootLayout({
                                       children,
                                   }: {
    children: React.ReactNode;
}) {
    return (
        <html lang="en" className="selection:bg-emerald-600/20 selection:text-emerald-900">
        <body className={`${inter.variable} font-sans bg-[#F4F7F5] text-slate-800 min-h-screen antialiased`}>
        {children}
        <Toaster
            position="top-right"
            toastOptions={{
                style: {
                    background: '#FFFFFF',
                    color: '#0F291B',
                    border: '1px solid #E2EAF1',
                    borderRadius: '16px',
                    fontSize: '14px',
                    boxShadow: '0 10px 25px -5px rgba(0, 0, 0, 0.05)',
                },
            }}
        />
        </body>
        </html>
    );
}