import type { Metadata } from 'next';
import './globals.css';
import Providers from './providers';
import Sidebar from '@/components/layout/Sidebar';
import MainContent from '@/components/layout/MainContent';

export const metadata: Metadata = {
  title: 'TobyReminder',
  description: 'Apple Reminders web clone',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ko" className="h-full">
      <body className="h-full flex overflow-hidden">
        <Providers>
          <Sidebar />
          <MainContent>{children}</MainContent>
        </Providers>
      </body>
    </html>
  );
}
