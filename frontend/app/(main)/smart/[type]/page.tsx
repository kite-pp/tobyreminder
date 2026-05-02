'use client';

import { useParams } from 'next/navigation';
import { useSmartRemindersQuery } from '@/hooks/useReminders';
import ReminderRow from '@/components/reminder/ReminderRow';
import AddReminderInput from '@/components/reminder/AddReminderInput';
import { useListsQuery } from '@/hooks/useLists';
import { CalendarDays, Calendar, Inbox, Flag, CheckCircle } from 'lucide-react';
import type { LucideIcon } from 'lucide-react';

const SMART_META: Record<string, { label: string; icon: LucideIcon; color: string }> = {
  today: { label: '오늘', icon: CalendarDays, color: '#007AFF' },
  scheduled: { label: '예정', icon: Calendar, color: '#FF3B30' },
  all: { label: '전체', icon: Inbox, color: '#8E8E93' },
  flagged: { label: '플래그됨', icon: Flag, color: '#FF9500' },
  completed: { label: '완료됨', icon: CheckCircle, color: '#8E8E93' },
};

export default function SmartListPage() {
  const { type } = useParams<{ type: string }>();
  const { data: reminders, isLoading } = useSmartRemindersQuery(type);
  const { data: lists } = useListsQuery();

  const meta = SMART_META[type] ?? { label: type, icon: Inbox, color: '#8E8E93' };
  const Icon = meta.icon;

  // For "all" smart list, use the first list for adding reminders
  const firstList = lists?.[0];

  return (
    <div className="flex flex-col h-full">
      <div className="px-6 pt-8 pb-4 flex items-center gap-3">
        <Icon size={28} style={{ color: meta.color }} />
        <h1 className="text-3xl font-bold" style={{ color: meta.color }}>
          {meta.label}
        </h1>
      </div>
      <div className="flex-1 overflow-y-auto px-2">
        {isLoading ? (
          <div className="px-4 py-2 text-sm text-apple-secondary">로딩 중...</div>
        ) : !reminders || reminders.length === 0 ? (
          <div className="flex items-center justify-center py-16 text-sm text-apple-secondary">
            리마인더 없음
          </div>
        ) : (
          <ul>
            {reminders.map((reminder) => (
              <ReminderRow key={reminder.id} reminder={reminder} accentColor={meta.color} />
            ))}
          </ul>
        )}
      </div>
      {type === 'all' && firstList && (
        <div className="border-t border-apple-separator">
          <AddReminderInput listId={firstList.id} />
        </div>
      )}
    </div>
  );
}
