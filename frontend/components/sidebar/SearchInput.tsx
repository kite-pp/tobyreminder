'use client';

import { useState } from 'react';
import { Search, X } from 'lucide-react';
import { useRouter } from 'next/navigation';

export default function SearchInput() {
  const [value, setValue] = useState('');
  const router = useRouter();

  function handleChange(e: React.ChangeEvent<HTMLInputElement>) {
    setValue(e.target.value);
    if (e.target.value.trim()) {
      router.push(`/search?q=${encodeURIComponent(e.target.value.trim())}`);
    }
  }

  function handleClear() {
    setValue('');
    router.push('/smart/all');
  }

  return (
    <div className="relative mx-2 mb-3">
      <Search size={14} className="absolute left-2.5 top-1/2 -translate-y-1/2 text-apple-secondary pointer-events-none" />
      <input
        type="text"
        value={value}
        onChange={handleChange}
        placeholder="검색"
        className="w-full pl-8 pr-7 py-1.5 text-sm bg-apple-bg rounded-lg outline-none placeholder:text-apple-secondary text-apple-text"
      />
      {value && (
        <button
          onClick={handleClear}
          className="absolute right-2 top-1/2 -translate-y-1/2 text-apple-secondary hover:text-apple-text"
        >
          <X size={12} />
        </button>
      )}
    </div>
  );
}
