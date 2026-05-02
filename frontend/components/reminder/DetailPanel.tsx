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
import ImagePicker from './ImagePicker';

function toInputValue(iso: string | null | undefined): string {
  if (!iso) return '';
  return new Date(iso).toISOString().slice(0, 16);
}

export default function DetailPanel() {
  const selectedReminder = useReminderStore((s) => s.selectedReminder);
  const selectReminder = useReminderStore((s) => s.selectReminder);
  const updateReminder = useUpdateReminderMutation();
  const toggleFlag = useToggleFlagMutation();
  const updatePriority = useUpdatePriorityMutation();
  const { data: lists } = useListsQuery();

  const [title, setTitle] = useState('');
  const [notes, setNotes] = useState('');
  const [startDate, setStartDate] = useState('');
  const [dueDate, setDueDate] = useState('');
  const [imageUrl, setImageUrl] = useState<string | null>(null);

  useEffect(() => {
    if (selectedReminder) {
      setTitle(selectedReminder.title);
      setNotes(selectedReminder.notes ?? '');
      setStartDate(toInputValue(selectedReminder.startDate));
      setDueDate(toInputValue(selectedReminder.dueDate));
      setImageUrl(selectedReminder.imageUrl ?? null);
    }
  }, [selectedReminder?.id]);

  const isOpen = selectedReminder !== null;

  function save(patch: { imageUrl?: string | null; startDate?: string | null; dueDate?: string | null; title?: string; notes?: string | null; listId?: number }) {
    if (!selectedReminder) return;
    updateReminder.mutate({
      id: selectedReminder.id,
      title: patch.title ?? title,
      notes: patch.notes !== undefined ? patch.notes : (notes || null),
      listId: patch.listId ?? selectedReminder.listId,
      imageUrl: patch.imageUrl !== undefined ? patch.imageUrl : imageUrl,
      startDate: patch.startDate !== undefined ? patch.startDate : (startDate || null),
      dueDate: patch.dueDate !== undefined ? patch.dueDate : (dueDate || null),
    });
  }

  function handleImageChange(val: string | null) {
    setImageUrl(val);
    if (!selectedReminder) return;
    updateReminder.mutate({
      id: selectedReminder.id,
      title,
      notes: notes || null,
      listId: selectedReminder.listId,
      imageUrl: val,
      startDate: startDate || null,
      dueDate: dueDate || null,
    });
  }

  function handleTitleBlur() {
    if (title.trim() && title !== selectedReminder!.title) {
      save({ title: title.trim() });
    }
  }

  function handleNotesBlur() {
    if (notes !== (selectedReminder!.notes ?? '')) {
      save({ notes: notes || null });
    }
  }

  function handleStartDateChange(e: React.ChangeEvent<HTMLInputElement>) {
    const val = e.target.value;
    setStartDate(val);
    save({ startDate: val || null });
  }

  function handleDueDateChange(e: React.ChangeEvent<HTMLInputElement>) {
    const val = e.target.value;
    setDueDate(val);
    save({ dueDate: val || null });
  }

  function handleListChange(e: React.ChangeEvent<HTMLSelectElement>) {
    save({ listId: Number(e.target.value) });
  }

  function handlePriorityChange(e: React.ChangeEvent<HTMLSelectElement>) {
    updatePriority.mutate({ id: selectedReminder!.id, priority: e.target.value });
  }

  return (
    <div
      className={`shrink-0 h-full bg-apple-card border-apple-separator flex flex-col overflow-hidden transition-all duration-200 ease-out ${
        isOpen ? 'w-80 border-l' : 'w-0 border-l-0'
      }`}
    >
      {selectedReminder && (
        <>
          <div className="flex items-center justify-between px-4 py-3 border-b border-apple-separator min-w-[320px]">
            <span className="text-sm font-semibold text-apple-text">상세 정보</span>
            <button
              onClick={() => selectReminder(null)}
              className="p-1 rounded-full hover:bg-apple-bg text-apple-secondary"
            >
              <X size={16} />
            </button>
          </div>
          <div className="flex-1 overflow-y-auto p-4 space-y-4 min-w-[320px]">
            {/* Image / Emoji */}
            <div>
              <label className="block text-xs text-apple-secondary mb-1">이모지 / 이미지</label>
              <ImagePicker value={imageUrl} onChange={handleImageChange} />
            </div>

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

            {/* Start Date */}
            <div>
              <label className="block text-xs text-apple-secondary mb-1">시작일</label>
              <input
                type="datetime-local"
                value={startDate}
                onChange={handleStartDateChange}
                className="w-full text-sm text-apple-text bg-apple-bg rounded-lg px-3 py-2 outline-none focus:ring-1 focus:ring-apple-blue"
              />
            </div>

            {/* Due Date */}
            <div>
              <label className="block text-xs text-apple-secondary mb-1">마감일</label>
              <input
                type="datetime-local"
                value={dueDate}
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
        </>
      )}
    </div>
  );
}
