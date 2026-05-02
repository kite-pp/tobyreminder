'use client';

import { useState } from 'react';
import Link from 'next/link';
import type { ReminderList } from '@/types';
import ListFormModal from './ListFormModal';
import { useUpdateListMutation, useDeleteListMutation } from '@/hooks/useLists';

interface Props {
  list: ReminderList;
  reminderCount?: number;
}

export default function ListItem({ list, reminderCount }: Props) {
  const [menuPos, setMenuPos] = useState<{ x: number; y: number } | null>(null);
  const [editing, setEditing] = useState(false);
  const updateMutation = useUpdateListMutation();
  const deleteMutation = useDeleteListMutation();

  function handleContextMenu(e: React.MouseEvent) {
    e.preventDefault();
    setMenuPos({ x: e.clientX, y: e.clientY });
  }

  function handleUpdate(name: string, color: string) {
    updateMutation.mutate({ id: list.id, name, color });
    setEditing(false);
  }

  function handleDelete() {
    deleteMutation.mutate(list.id);
    setMenuPos(null);
  }

  return (
    <>
      <Link href={`/lists/${list.id}`} className="block">
        <div
          className="flex items-center gap-2 px-2 py-1.5 rounded-lg hover:bg-apple-bg cursor-pointer"
          onContextMenu={handleContextMenu}
        >
          <span
            className="w-2.5 h-2.5 rounded-full shrink-0"
            style={{ backgroundColor: list.color }}
          />
          <span className="flex-1 text-sm text-apple-text truncate">{list.name}</span>
          {reminderCount != null && reminderCount > 0 && (
            <span className="text-xs text-apple-secondary tabular-nums">{reminderCount}</span>
          )}
        </div>
      </Link>

      {menuPos && (
        <>
          <div className="fixed inset-0 z-40" onClick={() => setMenuPos(null)} />
          <ul
            className="fixed z-50 bg-white rounded-xl shadow-lg py-1 min-w-36 text-sm"
            style={{ top: menuPos.y, left: menuPos.x }}
          >
            <li
              className="px-3 py-1.5 hover:bg-apple-bg cursor-pointer"
              onClick={() => { setEditing(true); setMenuPos(null); }}
            >
              편집
            </li>
            <li
              className="px-3 py-1.5 hover:bg-apple-bg cursor-pointer text-apple-red"
              onClick={handleDelete}
            >
              삭제
            </li>
          </ul>
        </>
      )}

      {editing && (
        <ListFormModal
          initial={{ name: list.name, color: list.color }}
          onSubmit={handleUpdate}
          onClose={() => setEditing(false)}
        />
      )}
    </>
  );
}
