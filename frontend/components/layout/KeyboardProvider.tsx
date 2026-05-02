'use client';

import { useKeyboard } from '@/hooks/useKeyboard';

export default function KeyboardProvider({ children }: { children: React.ReactNode }) {
  useKeyboard();
  return <>{children}</>;
}
