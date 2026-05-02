'use client';

import type { Reminder } from '@/types';
import { useToggleCompleteMutation } from '@/hooks/useReminders';

interface Props {
  reminder: Reminder;
  accentColor?: string;
}

export default function ReminderRow({ reminder, accentColor = '#007AFF' }: Props) {
  const toggleComplete = useToggleCompleteMutation();

  function handleCheckboxClick(e: React.MouseEvent) {
    e.stopPropagation();
    toggleComplete.mutate({ id: reminder.id });
  }

  return (
    <li className="flex items-center gap-3 px-4 py-2 hover:bg-apple-bg rounded-lg cursor-pointer group">
      <button
        onClick={handleCheckboxClick}
        className="shrink-0 w-5 h-5 rounded-full border-2 flex items-center justify-center transition-colors"
        style={{
          borderColor: accentColor,
          backgroundColor: reminder.completed ? accentColor : 'transparent',
        }}
        aria-label={reminder.completed ? '완료 취소' : '완료'}
      >
        {reminder.completed && (
          <svg width="10" height="8" viewBox="0 0 10 8" fill="none">
            <path
              d="M1 4L3.5 6.5L9 1"
              stroke="white"
              strokeWidth="1.5"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          </svg>
        )}
      </button>
      <span
        className={`flex-1 text-sm ${
          reminder.completed ? 'line-through opacity-40 text-apple-text' : 'text-apple-text'
        }`}
      >
        {reminder.title}
      </span>
    </li>
  );
}
