'use client';

import { useState } from 'react';
import type { ReminderList } from '@/types';

const COLORS = [
  { label: '빨강', value: '#FF3B30' },
  { label: '주황', value: '#FF9500' },
  { label: '노랑', value: '#FFCC00' },
  { label: '초록', value: '#34C759' },
  { label: '파랑', value: '#007AFF' },
  { label: '보라', value: '#AF52DE' },
  { label: '분홍', value: '#FF2D55' },
  { label: '회색', value: '#8E8E93' },
];

interface Props {
  initial?: Pick<ReminderList, 'name' | 'color'>;
  onSubmit: (name: string, color: string) => void;
  onClose: () => void;
}

export default function ListFormModal({ initial, onSubmit, onClose }: Props) {
  const [name, setName] = useState(initial?.name ?? '');
  const [color, setColor] = useState(initial?.color ?? COLORS[4].value);

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!name.trim()) return;
    onSubmit(name.trim(), color);
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/30" onClick={onClose}>
      <div
        className="bg-white rounded-2xl shadow-xl p-5 w-72 flex flex-col gap-4"
        onClick={(e) => e.stopPropagation()}
      >
        <h2 className="text-base font-semibold text-apple-text text-center">
          {initial ? '목록 편집' : '새 목록'}
        </h2>

        <div
          className="w-14 h-14 rounded-full mx-auto flex items-center justify-center"
          style={{ backgroundColor: color }}
        >
          <span className="text-white text-2xl font-bold">
            {name.charAt(0).toUpperCase() || '?'}
          </span>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <input
            autoFocus
            type="text"
            placeholder="목록 이름"
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="w-full px-3 py-2 rounded-lg bg-apple-bg text-apple-text text-sm outline-none"
          />

          <div className="grid grid-cols-4 gap-2">
            {COLORS.map((c) => (
              <button
                key={c.value}
                type="button"
                onClick={() => setColor(c.value)}
                className="w-10 h-10 rounded-full mx-auto flex items-center justify-center"
                style={{ backgroundColor: c.value }}
              >
                {color === c.value && (
                  <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" strokeWidth={3} viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7" />
                  </svg>
                )}
              </button>
            ))}
          </div>

          <div className="flex gap-2 mt-1">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 py-2 rounded-xl text-apple-blue text-sm font-medium bg-apple-bg"
            >
              취소
            </button>
            <button
              type="submit"
              disabled={!name.trim()}
              className="flex-1 py-2 rounded-xl text-white text-sm font-medium bg-apple-blue disabled:opacity-40"
            >
              {initial ? '저장' : '추가'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
