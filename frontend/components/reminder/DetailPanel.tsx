'use client';

import { useState, useEffect } from 'react';
import { X, Flag } from 'lucide-react';
import { useReminderStore } from '@/store/reminderStore';
import {
  useUpdateReminderMutation,
  useToggleFlagMutation,
  useUpdatePriorityMutation,
} from '@/hooks/useReminders';
import { useListsQuery } from '@/hooks/useLists';
import SubtaskList from './SubtaskList';

export default function DetailPanel() {
  const selectedReminder = useReminderStore((s) => s.selectedReminder);
  const selectReminder = useReminderStore((s) => s.selectReminder);
  const updateReminder = useUpdateReminderMutation();
  const toggleFlag = useToggleFlagMutation();
  const updatePriority = useUpdatePriorityMutation();
  const { data: lists } = useListsQuery();

  const [title, setTitle] = useState('');
  const [notes, setNotes] = useState('');

  useEffect(() => {
    if (selectedReminder) {
      setTitle(selectedReminder.title);
      setNotes(selectedReminder.notes ?? '');
    }
  }, [selectedReminder?.id]);

  const isOpen = selectedReminder !== null;

  if (!selectedReminder) {
    return null;
  }

  function handleTitleBlur() {
    if (title.trim() && title !== selectedReminder!.title) {
      updateReminder.mutate({
        id: selectedReminder!.id,
        title: title.trim(),
        notes: selectedReminder!.notes,
        listId: selectedReminder!.listId,
      });
    }
  }

  function handleNotesBlur() {
    if (notes !== (selectedReminder!.notes ?? '')) {
      updateReminder.mutate({
        id: selectedReminder!.id,
        title: selectedReminder!.title,
        notes: notes || null,
        listId: selectedReminder!.listId,
      });
    }
  }

  function handleDueDateChange(e: React.ChangeEvent<HTMLInputElement>) {
    const dueDate = e.target.value || null;
    updateReminder.mutate({
      id: selectedReminder!.id,
      title: selectedReminder!.title,
      notes: selectedReminder!.notes,
      listId: selectedReminder!.listId,
      dueDate,
    });
  }

  function handleListChange(e: React.ChangeEvent<HTMLSelectElement>) {
    updateReminder.mutate({
      id: selectedReminder!.id,
      title: selectedReminder!.title,
      notes: selectedReminder!.notes,
      listId: Number(e.target.value),
    });
  }

  function handlePriorityChange(e: React.ChangeEvent<HTMLSelectElement>) {
    updatePriority.mutate({ id: selectedReminder!.id, priority: e.target.value });
  }

  const dueDateValue = selectedReminder.dueDate
    ? new Date(selectedReminder.dueDate).toISOString().slice(0, 16)
    : '';

  return (
    <div
      className={`w-80 shrink-0 h-full bg-white border-l border-apple-separator flex flex-col transform transition-transform duration-200 ease-out ${
        isOpen ? 'translate-x-0' : 'translate-x-full'
      }`}
    >
      <div className="flex items-center justify-between px-4 py-3 border-b border-apple-separator">
        <span className="text-sm font-semibold text-apple-text">상세 정보</span>
        <button
          onClick={() => selectReminder(null)}
          className="p-1 rounded-full hover:bg-apple-bg text-apple-secondary"
        >
          <X size={16} />
        </button>
      </div>
      <div className="flex-1 overflow-y-auto p-4 space-y-4">
        {/* Title */}
        <div>
          <label className="block text-xs text-apple-secondary mb-1">제목</label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            onBlur={handleTitleBlur}
            className="w-full text-sm text-apple-text bg-apple-bg rounded-lg px-3 py-2 outline-none focus:ring-1 focus:ring-apple-blue"
          />
        </div>

        {/* Notes */}
        <div>
          <label className="block text-xs text-apple-secondary mb-1">메모</label>
          <textarea
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            onBlur={handleNotesBlur}
            rows={3}
            className="w-full text-sm text-apple-text bg-apple-bg rounded-lg px-3 py-2 outline-none focus:ring-1 focus:ring-apple-blue resize-none"
          />
        </div>

        {/* Due Date */}
        <div>
          <label className="block text-xs text-apple-secondary mb-1">마감일</label>
          <input
            type="datetime-local"
            value={dueDateValue}
            onChange={handleDueDateChange}
            className="w-full text-sm text-apple-text bg-apple-bg rounded-lg px-3 py-2 outline-none focus:ring-1 focus:ring-apple-blue"
          />
        </div>

        {/* Flag */}
        <div className="flex items-center justify-between">
          <label className="text-xs text-apple-secondary">플래그</label>
          <button
            onClick={() => toggleFlag.mutate({ id: selectedReminder.id })}
            className="p-2 rounded-lg hover:bg-apple-bg"
          >
            <Flag
              size={16}
              style={{ color: selectedReminder.flagged ? '#FF9500' : '#8E8E93' }}
              fill={selectedReminder.flagged ? '#FF9500' : 'none'}
            />
          </button>
        </div>

        {/* Priority */}
        <div>
          <label className="block text-xs text-apple-secondary mb-1">우선순위</label>
          <select
            value={selectedReminder.priority}
            onChange={handlePriorityChange}
            className="w-full text-sm text-apple-text bg-apple-bg rounded-lg px-3 py-2 outline-none focus:ring-1 focus:ring-apple-blue"
          >
            <option value="NONE">없음</option>
            <option value="LOW">낮음</option>
            <option value="MEDIUM">중간</option>
            <option value="HIGH">높음</option>
          </select>
        </div>

        {/* List */}
        <div>
          <label className="block text-xs text-apple-secondary mb-1">목록</label>
          <select
            value={selectedReminder.listId}
            onChange={handleListChange}
            className="w-full text-sm text-apple-text bg-apple-bg rounded-lg px-3 py-2 outline-none focus:ring-1 focus:ring-apple-blue"
          >
            {lists?.map((list) => (
              <option key={list.id} value={list.id}>
                {list.name}
              </option>
            ))}
          </select>
        </div>

        {/* Subtasks */}
        <div>
          <label className="block text-xs text-apple-secondary mb-1">서브태스크</label>
          <div className="bg-apple-bg rounded-lg overflow-hidden">
            <SubtaskList
              reminderId={selectedReminder.id}
              subtasks={selectedReminder.subtasks ?? []}
            />
          </div>
        </div>
      </div>
    </div>
  );
}
