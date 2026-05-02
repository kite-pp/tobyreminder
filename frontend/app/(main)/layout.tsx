import Sidebar from '@/components/layout/Sidebar';
import MainContent from '@/components/layout/MainContent';

export default function MainLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex h-full">
      <Sidebar />
      <MainContent>{children}</MainContent>
    </div>
  );
}
