'use client';

import { CalendarDays, Calendar, Inbox, Flag, CheckCircle } from 'lucide-react';
import SmartListCard from './SmartListCard';
import { useCountQuery } from '@/hooks/useReminders';

const SMART_ITEMS = [
  { type: 'today', label: '오늘', icon: CalendarDays, color: '#007AFF' },
  { type: 'scheduled', label: '예정', icon: Calendar, color: '#FF3B30' },
  { type: 'all', label: '전체', icon: Inbox, color: '#8E8E93' },
  { type: 'flagged', label: '플래그됨', icon: Flag, color: '#FF9500' },
  { type: 'completed', label: '완료됨', icon: CheckCircle, color: '#8E8E93' },
] as const;

export default function SmartLists() {
  const { data: counts } = useCountQuery();

  return (
    <div className="mb-4">
      <div className="grid grid-cols-2 gap-2 px-2">
        {SMART_ITEMS.map(({ type, label, icon, color }) => (
          <SmartListCard
            key={type}
            type={type}
            label={label}
            icon={icon}
            color={color}
            count={counts ? counts[type as keyof typeof counts] : 0}
          />
        ))}
      </div>
    </div>
  );
}
