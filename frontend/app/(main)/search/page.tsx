'use client';

import { Suspense } from 'react';
import { useSearchParams } from 'next/navigation';
import { Search } from 'lucide-react';
import { useSearchRemindersQuery } from '@/hooks/useReminders';
import { useDebounce } from '@/hooks/useDebounce';
import ReminderRow from '@/components/reminder/ReminderRow';

function SearchResults() {
  const searchParams = useSearchParams();
  const raw = searchParams.get('q') ?? '';
  const query = useDebounce(raw, 300);

  const { data: reminders, isLoading } = useSearchRemindersQuery(query);

  return (
    <div className="flex flex-col h-full">
      <div className="px-6 pt-6 pb-3">
        <h1 className="text-2xl font-bold text-apple-text flex items-center gap-2">
          <Search size={22} />
          검색 결과
        </h1>
        {raw && <p className="text-sm text-apple-secondary mt-1">"{raw}"</p>}
      </div>
      <div className="flex-1 overflow-y-auto px-2">
        {isLoading && (
          <p className="px-4 py-2 text-sm text-apple-secondary">검색 중...</p>
        )}
        {!isLoading && !raw && (
          <div className="flex flex-col items-center justify-center py-16 text-apple-secondary">
            <Search size={40} strokeWidth={1.5} className="mb-3 opacity-40" />
            <p className="text-sm">검색어를 입력하세요</p>
          </div>
        )}
        {!isLoading && raw && reminders?.length === 0 && (
          <div className="flex flex-col items-center justify-center py-16 text-apple-secondary">
            <Search size={40} strokeWidth={1.5} className="mb-3 opacity-40" />
            <p className="text-sm">검색 결과 없음</p>
          </div>
        )}
        {reminders && reminders.length > 0 && (
          <ul>
            {reminders.map((reminder) => (
              <ReminderRow key={reminder.id} reminder={reminder} />
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}

export default function SearchPage() {
  return (
    <Suspense fallback={<div className="px-6 pt-6 text-sm text-apple-secondary">로딩 중...</div>}>
      <SearchResults />
    </Suspense>
  );
}
