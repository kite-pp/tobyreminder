'use client';

import MyLists from '@/components/sidebar/MyLists';
import NewListButton from '@/components/sidebar/NewListButton';
import SmartLists from '@/components/sidebar/SmartLists';

export default function Sidebar() {
  return (
    <aside className="w-[260px] shrink-0 h-full bg-apple-sidebar border-r border-apple-separator flex flex-col select-none">
      <div className="flex-1 overflow-y-auto py-3">
        <SmartLists />
        <MyLists />
      </div>
      <div className="px-2 pb-3">
        <NewListButton />
      </div>
    </aside>
  );
}
