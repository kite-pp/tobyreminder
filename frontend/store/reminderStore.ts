'use client';

import { create } from 'zustand';
import type { Reminder } from '@/types';

interface Store {
  selectedReminder: Reminder | null;
  selectReminder: (r: Reminder | null) => void;
}

export const useReminderStore = create<Store>((set) => ({
  selectedReminder: null,
  selectReminder: (r) => set({ selectedReminder: r }),
}));
