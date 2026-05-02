'use client';

import { useState } from 'react';
import { Plus } from 'lucide-react';
import { useCreateReminderMutation } from '@/hooks/useReminders';

interface Props {
  listId: number;
}

export default function AddReminderInput({ listId }: Props) {
  const [value, setValue] = useState('');
  const createReminder = useCreateReminderMutation();

  function handleKeyDown(e: React.KeyboardEvent<HTMLInputElement>) {
    if (e.key === 'Enter') {
      const trimmed = value.trim();
      if (trimmed) {
        createReminder.mutate({ title: trimmed, listId });
        setValue('');
      }
    } else if (e.key === 'Escape') {
      setValue('');
      (e.target as HTMLInputElement).blur();
    }
  }

  return (
    <div className="flex items-center gap-2 px-4 py-2">
      <Plus size={16} className="text-apple-blue shrink-0" />
      <input
        type="text"
        value={value}
        onChange={(e) => setValue(e.target.value)}
        onKeyDown={handleKeyDown}
        placeholder="리마인더 추가..."
        className="flex-1 bg-transparent text-sm text-apple-text placeholder:text-apple-secondary outline-none"
      />
    </div>
  );
}
