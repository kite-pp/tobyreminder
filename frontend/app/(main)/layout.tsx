import Sidebar from '@/components/layout/Sidebar';
import MainContent from '@/components/layout/MainContent';
import DetailPanel from '@/components/reminder/DetailPanel';

export default function MainLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex h-full overflow-hidden">
      <Sidebar />
      <MainContent>{children}</MainContent>
      <DetailPanel />
    </div>
  );
}
