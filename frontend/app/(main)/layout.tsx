import Sidebar from '@/components/layout/Sidebar';
import MainContent from '@/components/layout/MainContent';
import DetailPanel from '@/components/reminder/DetailPanel';
import KeyboardProvider from '@/components/layout/KeyboardProvider';

export default function MainLayout({ children }: { children: React.ReactNode }) {
  return (
    <KeyboardProvider>
      <div className="flex h-full overflow-hidden">
        <Sidebar />
        <MainContent>{children}</MainContent>
        <DetailPanel />
      </div>
    </KeyboardProvider>
  );
}
