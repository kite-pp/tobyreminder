'use client';

import { useRemindersQuery } from '@/hooks/useReminders';
import ReminderRow from './ReminderRow';

interface Props {
  listId: number;
  accentColor?: string;
}

export default function ReminderList({ listId, accentColor = '#007AFF' }: Props) {
  const { data: reminders, isLoading } = useRemindersQuery(listId);

  if (isLoading) {
    return <div className="px-4 py-2 text-sm text-apple-secondary">로딩 중...</div>;
  }

  if (!reminders || reminders.length === 0) {
    return (
      <div className="flex items-center justify-center py-16 text-sm text-apple-secondary">
        리마인더 없음
      </div>
    );
  }

  return (
    <ul>
      {reminders.map((reminder) => (
        <ReminderRow key={reminder.id} reminder={reminder} accentColor={accentColor} />
      ))}
    </ul>
  );
}
