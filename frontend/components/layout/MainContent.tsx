export default function MainContent({ children }: { children: React.ReactNode }) {
  return (
    <main className="flex-1 h-full overflow-y-auto bg-apple-bg">
      <div className="page-fade-in h-full">
        {children}
      </div>
    </main>
  );
}
