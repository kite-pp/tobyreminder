'use client';

import { useEffect } from 'react';
import { useReminderStore } from '@/store/reminderStore';
import { useToggleCompleteMutation, useDeleteReminderMutation } from './useReminders';

export function useKeyboard() {
  const selectedReminder = useReminderStore((s) => s.selectedReminder);
  const selectReminder = useReminderStore((s) => s.selectReminder);
  const toggleComplete = useToggleCompleteMutation();
  const deleteReminder = useDeleteReminderMutation();

  useEffect(() => {
    function handler(e: KeyboardEvent) {
      const tag = (e.target as HTMLElement).tagName;
      const isEditing = tag === 'INPUT' || tag === 'TEXTAREA' || (e.target as HTMLElement).isContentEditable;

      if (e.key === 'Escape') {
        selectReminder(null);
        return;
      }

      if (isEditing) return;

      if (e.key === ' ' && selectedReminder) {
        e.preventDefault();
        toggleComplete.mutate({ id: selectedReminder.id });
        return;
      }

      if (e.key === 'Backspace' && (e.metaKey || e.ctrlKey) && selectedReminder) {
        e.preventDefault();
        deleteReminder.mutate(selectedReminder.id);
        selectReminder(null);
      }
    }

    window.addEventListener('keydown', handler);
    return () => window.removeEventListener('keydown', handler);
  }, [selectedReminder, selectReminder, toggleComplete, deleteReminder]);
}
