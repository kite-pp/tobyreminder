'use client';

import { useState } from 'react';
import { useCreateSubtaskMutation } from '@/hooks/useSubtasks';

interface Props {
  reminderId: number;
}

export default function AddSubtaskInput({ reminderId }: Props) {
  const [value, setValue] = useState('');
  const createSubtask = useCreateSubtaskMutation();

  function handleKeyDown(e: React.KeyboardEvent<HTMLInputElement>) {
    if (e.nativeEvent.isComposing) return;
    if (e.key === 'Enter') {
      const trimmed = value.trim();
      if (trimmed) {
        createSubtask.mutate({ reminderId, title: trimmed });
        setValue('');
      }
    } else if (e.key === 'Escape') {
      setValue('');
      (e.target as HTMLInputElement).blur();
    }
  }

  return (
    <div className="pl-10 pr-4 py-1.5">
      <input
        type="text"
        value={value}
        onChange={(e) => setValue(e.target.value)}
        onKeyDown={handleKeyDown}
        placeholder="서브태스크 추가..."
        className="w-full bg-transparent text-xs text-apple-text placeholder:text-apple-secondary outline-none"
      />
    </div>
  );
}
