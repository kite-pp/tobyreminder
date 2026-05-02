'use client';

import { useState } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { useQuery } from '@tanstack/react-query';
import { api } from '@/lib/api';
import type { Reminder } from '@/types';
import { useReminderStore } from '@/store/reminderStore';

function getDaysInMonth(year: number, month: number) {
  return new Date(year, month + 1, 0).getDate();
}

function getFirstDayOfMonth(year: number, month: number) {
  // 0=Sun → shift to Mon-start: Mon=0 … Sun=6
  const d = new Date(year, month, 1).getDay();
  return (d + 6) % 7;
}

const WEEKDAYS = ['월', '화', '수', '목', '금', '토', '일'];

function getDueDateColor(dueDate: string): string {
  const now = new Date();
  const due = new Date(dueDate);
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  const dueDay = new Date(due.getFullYear(), due.getMonth(), due.getDate());
  if (dueDay < today) return '#FF3B30';
  if (dueDay.getTime() === today.getTime()) return '#007AFF';
  return '#34C759';
}

export default function CalendarPage() {
  const now = new Date();
  const [year, setYear] = useState(now.getFullYear());
  const [month, setMonth] = useState(now.getMonth());

  const selectReminder = useReminderStore((s) => s.selectReminder);

  const { data: reminders = [] } = useQuery({
    queryKey: ['reminders', 'calendar', year, month],
    queryFn: () => api.get<Reminder[]>('/api/reminders?smart=all'),
  });

  // reminders grouped by day of current month
  const remindersByDay: Record<number, Reminder[]> = {};
  for (const r of reminders) {
    if (!r.dueDate) continue;
    const d = new Date(r.dueDate);
    if (d.getFullYear() === year && d.getMonth() === month) {
      const day = d.getDate();
      if (!remindersByDay[day]) remindersByDay[day] = [];
      remindersByDay[day].push(r);
    }
  }

  const daysInMonth = getDaysInMonth(year, month);
  const firstDay = getFirstDayOfMonth(year, month);
  const today = now.getFullYear() === year && now.getMonth() === month ? now.getDate() : -1;

  function prevMonth() {
    if (month === 0) { setYear(y => y - 1); setMonth(11); }
    else setMonth(m => m - 1);
  }

  function nextMonth() {
    if (month === 11) { setYear(y => y + 1); setMonth(0); }
    else setMonth(m => m + 1);
  }

  const cells: (number | null)[] = [
    ...Array(firstDay).fill(null),
    ...Array.from({ length: daysInMonth }, (_, i) => i + 1),
  ];
  // pad to full weeks
  while (cells.length % 7 !== 0) cells.push(null);

  return (
    <div className="flex flex-col h-full">
      {/* Header */}
      <div className="flex items-center justify-between px-6 pt-6 pb-4">
        <h1 className="text-2xl font-bold text-apple-text">
          {year}년 {month + 1}월
        </h1>
        <div className="flex items-center gap-1">
          <button
            onClick={prevMonth}
            className="p-1.5 rounded-lg hover:bg-apple-separator text-apple-secondary"
          >
            <ChevronLeft size={18} />
          </button>
          <button
            onClick={() => { setYear(now.getFullYear()); setMonth(now.getMonth()); }}
            className="px-3 py-1 text-xs rounded-lg hover:bg-apple-separator text-apple-secondary font-medium"
          >
            오늘
          </button>
          <button
            onClick={nextMonth}
            className="p-1.5 rounded-lg hover:bg-apple-separator text-apple-secondary"
          >
            <ChevronRight size={18} />
          </button>
        </div>
      </div>

      {/* Weekday headers */}
      <div className="grid grid-cols-7 border-b border-apple-separator px-4">
        {WEEKDAYS.map((d, i) => (
          <div
            key={d}
            className={`text-xs font-medium text-center py-2 ${
              i === 5 ? 'text-apple-blue' : i === 6 ? 'text-apple-red' : 'text-apple-secondary'
            }`}
          >
            {d}
          </div>
        ))}
      </div>

      {/* Calendar grid */}
      <div className="flex-1 overflow-y-auto px-4 pb-4">
        <div className="grid grid-cols-7 h-full" style={{ gridAutoRows: 'minmax(80px, 1fr)' }}>
          {cells.map((day, idx) => {
            const isToday = day === today;
            const dayReminders = day ? (remindersByDay[day] ?? []) : [];
            const isSat = idx % 7 === 5;
            const isSun = idx % 7 === 6;

            return (
              <div
                key={idx}
                className={`border-b border-r border-apple-separator p-1 ${
                  !day ? 'bg-apple-bg/40' : ''
                } ${idx % 7 === 0 ? 'border-l' : ''} ${idx < 7 ? 'border-t' : ''}`}
              >
                {day && (
                  <>
                    <div className="flex justify-end mb-1">
                      <span
                        className={`text-xs w-5 h-5 flex items-center justify-center rounded-full font-medium ${
                          isToday
                            ? 'bg-apple-blue text-white'
                            : isSat
                            ? 'text-apple-blue'
                            : isSun
                            ? 'text-apple-red'
                            : 'text-apple-text'
                        }`}
                      >
                        {day}
                      </span>
                    </div>
                    <div className="flex flex-col gap-0.5">
                      {dayReminders.slice(0, 3).map((r) => (
                        <button
                          key={r.id}
                          onClick={() => selectReminder(r)}
                          className={`w-full text-left text-xs px-1.5 py-0.5 rounded truncate font-medium transition-opacity hover:opacity-80 ${
                            r.completed ? 'line-through opacity-40' : ''
                          }`}
                          style={{
                            backgroundColor: getDueDateColor(r.dueDate!) + '22',
                            color: getDueDateColor(r.dueDate!),
                          }}
                        >
                          {r.title}
                        </button>
                      ))}
                      {dayReminders.length > 3 && (
                        <span className="text-xs text-apple-secondary px-1">
                          +{dayReminders.length - 3}개
                        </span>
                      )}
                    </div>
                  </>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
