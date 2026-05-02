'use client';

import { useState } from 'react';
import { Plus } from 'lucide-react';
import ListFormModal from './ListFormModal';
import { useCreateListMutation } from '@/hooks/useLists';

export default function NewListButton() {
  const [open, setOpen] = useState(false);
  const createMutation = useCreateListMutation();

  function handleCreate(name: string, color: string) {
    createMutation.mutate({ name, color });
    setOpen(false);
  }

  return (
    <>
      <button
        onClick={() => setOpen(true)}
        className="flex items-center gap-1.5 w-full px-2 py-1.5 text-apple-blue text-sm rounded-lg hover:bg-apple-bg"
      >
        <Plus className="w-4 h-4" strokeWidth={2.5} />
        목록 추가
      </button>

      {open && (
        <ListFormModal onSubmit={handleCreate} onClose={() => setOpen(false)} />
      )}
    </>
  );
}
