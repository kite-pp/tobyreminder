'use client';

import { useParams } from 'next/navigation';
import { useListsQuery } from '@/hooks/useLists';
import ReminderList from '@/components/reminder/ReminderList';
import AddReminderInput from '@/components/reminder/AddReminderInput';

export default function ListPage() {
  const { id } = useParams<{ id: string }>();
  const listId = Number(id);

  const { data: lists } = useListsQuery();
  const list = lists?.find((l) => l.id === listId);

  return (
    <div className="flex flex-col h-full">
      <div className="px-6 pt-8 pb-4">
        <h1
          className="text-3xl font-bold"
          style={{ color: list?.color ?? '#007AFF' }}
        >
          {list?.name ?? '목록'}
        </h1>
      </div>
      <div className="flex-1 overflow-y-auto px-2">
        <ReminderList listId={listId} accentColor={list?.color ?? '#007AFF'} />
      </div>
      <div className="border-t border-apple-separator">
        <AddReminderInput listId={listId} />
      </div>
    </div>
  );
}
