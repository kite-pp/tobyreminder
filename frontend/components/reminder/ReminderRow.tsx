'use client';

import { useState } from 'react';
import { Flag, ChevronDown, ChevronRight } from 'lucide-react';
import type { Reminder } from '@/types';
import { useToggleCompleteMutation } from '@/hooks/useReminders';
import { useReminderStore } from '@/store/reminderStore';
import SubtaskList from './SubtaskList';

interface Props {
  reminder: Reminder;
  accentColor?: string;
}

function getDueDateColor(dueDate: string): string {
  const now = new Date();
  const due = new Date(dueDate);
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const dueDay = new Date(due.getFullYear(), due.getMonth(), due.getDate());

  if (dueDay < today) return '#FF3B30'; // past - red
  if (dueDay.getTime() === today.getTime()) return '#007AFF'; // today - blue
  return '#8E8E93'; // future - gray
}

function formatDueDate(dueDate: string): string {
  const date = new Date(dueDate);
  return date.toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' });
}

const PRIORITY_COLORS: Record<string, string> = {
  HIGH: '#FF3B30',
  MEDIUM: '#FF9500',
  LOW: '#007AFF',
};

export default function ReminderRow({ reminder, accentColor = '#007AFF' }: Props) {
  const [expanded, setExpanded] = useState(false);
  const toggleComplete = useToggleCompleteMutation();
  const selectReminder = useReminderStore((s) => s.selectReminder);
  const selectedReminder = useReminderStore((s) => s.selectedReminder);
  const isSelected = selectedReminder?.id === reminder.id;
  const subtaskCount = reminder.subtasks?.length ?? 0;

  function handleCheckboxClick(e: React.MouseEvent) {
    e.stopPropagation();
    toggleComplete.mutate({ id: reminder.id });
  }

  function handleChevronClick(e: React.MouseEvent) {
    e.stopPropagation();
    setExpanded((prev) => !prev);
  }

  function handleRowClick() {
    selectReminder(isSelected ? null : reminder);
  }

  return (
    <>
      <li
        className={`flex items-start gap-3 px-4 py-2 rounded-lg cursor-pointer group ${
          isSelected ? 'bg-apple-separator' : 'hover:bg-apple-bg'
        }`}
        onClick={handleRowClick}
      >
        <button
          onClick={handleCheckboxClick}
          className="shrink-0 w-5 h-5 mt-0.5 rounded-full border-2 flex items-center justify-center transition-colors"
          style={{
            borderColor: accentColor,
            backgroundColor: reminder.completed ? accentColor : 'transparent',
          }}
          aria-label={reminder.completed ? '완료 취소' : '완료'}
        >
          {reminder.completed && (
            <svg width="10" height="8" viewBox="0 0 10 8" fill="none">
              <path
                d="M1 4L3.5 6.5L9 1"
                stroke="white"
                strokeWidth="1.5"
                strokeLinecap="round"
                strokeLinejoin="round"
              />
            </svg>
          )}
        </button>
        <div className="flex-1 min-w-0">
          <div className="flex items-center gap-1">
            <span
              className={`flex-1 text-sm ${
                reminder.completed ? 'line-through opacity-40 text-apple-text' : 'text-apple-text'
              }`}
            >
              {reminder.title}
            </span>
            {reminder.priority !== 'NONE' && PRIORITY_COLORS[reminder.priority] && (
              <span
                className="text-xs font-bold"
                style={{ color: PRIORITY_COLORS[reminder.priority] }}
              >
                !
              </span>
            )}
            {reminder.flagged && (
              <Flag size={12} className="shrink-0" style={{ color: '#FF9500' }} fill="#FF9500" />
            )}
            {subtaskCount > 0 && (
              <button
                onClick={handleChevronClick}
                className="flex items-center gap-0.5 text-xs text-apple-secondary hover:text-apple-text ml-1"
              >
                {expanded ? <ChevronDown size={12} /> : <ChevronRight size={12} />}
                <span>{subtaskCount}</span>
              </button>
            )}
          </div>
          {reminder.dueDate && (
            <div
              className="text-xs mt-0.5"
              style={{ color: getDueDateColor(reminder.dueDate) }}
            >
              {formatDueDate(reminder.dueDate)}
            </div>
          )}
        </div>
      </li>
      {expanded && subtaskCount > 0 && (
        <SubtaskList reminderId={reminder.id} subtasks={reminder.subtasks} />
      )}
    </>
  );
}
