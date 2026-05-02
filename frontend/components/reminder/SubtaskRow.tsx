'use client';

import { Trash2 } from 'lucide-react';
import type { Subtask } from '@/types';
import { useToggleSubtaskMutation, useDeleteSubtaskMutation } from '@/hooks/useSubtasks';

interface Props {
  subtask: Subtask;
  reminderId: number;
}

export default function SubtaskRow({ subtask, reminderId }: Props) {
  const toggleSubtask = useToggleSubtaskMutation();
  const deleteSubtask = useDeleteSubtaskMutation();

  function handleCheckboxClick(e: React.MouseEvent) {
    e.stopPropagation();
    toggleSubtask.mutate({ reminderId, subtaskId: subtask.id });
  }

  function handleDelete(e: React.MouseEvent) {
    e.stopPropagation();
    deleteSubtask.mutate({ reminderId, subtaskId: subtask.id });
  }

  return (
    <li className="flex items-center gap-2 pl-10 pr-4 py-1.5 hover:bg-apple-bg rounded-lg group cursor-pointer">
      <button
        onClick={handleCheckboxClick}
        className="shrink-0 w-4 h-4 rounded-full border-2 border-apple-gray flex items-center justify-center transition-colors"
        style={{ backgroundColor: subtask.completed ? '#8E8E93' : 'transparent' }}
        aria-label={subtask.completed ? '완료 취소' : '완료'}
      >
        {subtask.completed && (
          <svg width="8" height="6" viewBox="0 0 8 6" fill="none">
            <path
              d="M1 3L3 5L7 1"
              stroke="white"
              strokeWidth="1.5"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          </svg>
        )}
      </button>
      <span
        className={`flex-1 text-xs ${
          subtask.completed ? 'line-through opacity-40 text-apple-text' : 'text-apple-text'
        }`}
      >
        {subtask.title}
      </span>
      <button
        onClick={handleDelete}
        className="opacity-0 group-hover:opacity-100 p-1 rounded hover:bg-apple-separator text-apple-secondary"
        aria-label="삭제"
      >
        <Trash2 size={12} />
      </button>
    </li>
  );
}
