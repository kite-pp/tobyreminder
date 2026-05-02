'use client';

import { useState } from 'react';
import { ChevronDown, ChevronRight, Trash2 } from 'lucide-react';
import { useRemindersQuery, useDeleteCompletedMutation } from '@/hooks/useReminders';
import ReminderRow from './ReminderRow';

interface Props {
  listId: number;
  accentColor?: string;
}

export default function CompletedSection({ listId, accentColor }: Props) {
  const [expanded, setExpanded] = useState(false);
  const [confirmDelete, setConfirmDelete] = useState(false);
  const { data: reminders } = useRemindersQuery(listId);
  const deleteCompleted = useDeleteCompletedMutation();

  const completed = reminders?.filter((r) => r.completed) ?? [];
  if (completed.length === 0) return null;

  function handleDeleteAll() {
    if (confirmDelete) {
      deleteCompleted.mutate(listId);
      setConfirmDelete(false);
      setExpanded(false);
    } else {
      setConfirmDelete(true);
    }
  }

  return (
    <div className="mt-2">
      <div className="flex items-center justify-between px-4 py-1">
        <button
          onClick={() => setExpanded((v) => !v)}
          className="flex items-center gap-1.5 text-sm text-apple-secondary hover:text-apple-text"
        >
          {expanded ? <ChevronDown size={14} /> : <ChevronRight size={14} />}
          완료됨 {completed.length}개
        </button>
        {expanded && (
          <button
            onClick={handleDeleteAll}
            className={`flex items-center gap-1 text-xs px-2 py-1 rounded-lg transition-colors ${
              confirmDelete
                ? 'bg-apple-red text-white'
                : 'text-apple-secondary hover:text-apple-red'
            }`}
            onBlur={() => setConfirmDelete(false)}
          >
            <Trash2 size={12} />
            {confirmDelete ? '확인' : '전체 삭제'}
          </button>
        )}
      </div>
      {expanded && (
        <ul className="opacity-70">
          {completed.map((reminder) => (
            <ReminderRow key={reminder.id} reminder={reminder} accentColor={accentColor} />
          ))}
        </ul>
      )}
    </div>
  );
}
