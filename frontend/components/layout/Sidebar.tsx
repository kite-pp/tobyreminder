'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { CalendarDays } from 'lucide-react';
import MyLists from '@/components/sidebar/MyLists';
import NewListButton from '@/components/sidebar/NewListButton';
import SmartLists from '@/components/sidebar/SmartLists';
import SearchInput from '@/components/sidebar/SearchInput';

export default function Sidebar() {
  const pathname = usePathname();
  const isCalendar = pathname === '/calendar';

  return (
    <aside className="w-[260px] shrink-0 h-full bg-apple-sidebar border-r border-apple-separator flex flex-col select-none">
      <div className="flex-1 overflow-y-auto py-3">
        <SearchInput />
        <SmartLists />
        {/* Calendar Tab */}
        <div className="px-2 mt-2 mb-1">
          <Link href="/calendar">
            <div
              className={`flex items-center gap-2 px-3 py-2 rounded-xl transition-colors ${
                isCalendar
                  ? 'bg-apple-blue/10 text-apple-blue'
                  : 'hover:bg-apple-bg text-apple-text'
              }`}
            >
              <CalendarDays size={18} style={{ color: isCalendar ? '#007AFF' : '#8E8E93' }} />
              <span className="text-sm font-medium">캘린더</span>
            </div>
          </Link>
        </div>
        <MyLists />
      </div>
      <div className="px-2 pb-3">
        <NewListButton />
      </div>
    </aside>
  );
}
