'use client';

import { useState, useRef } from 'react';
import { Smile, Link, X } from 'lucide-react';
import dynamic from 'next/dynamic';

const EmojiPicker = dynamic(() => import('emoji-picker-react'), { ssr: false });

interface Props {
  value: string | null;
  onChange: (val: string | null) => void;
}

export default function ImagePicker({ value, onChange }: Props) {
  const [tab, setTab] = useState<'emoji' | 'url'>('emoji');
  const [open, setOpen] = useState(false);
  const [urlInput, setUrlInput] = useState(value?.startsWith('http') ? value : '');
  const containerRef = useRef<HTMLDivElement>(null);

  const isUrl = value?.startsWith('http');

  function handleEmojiClick(emojiData: { emoji: string }) {
    onChange(emojiData.emoji);
    setOpen(false);
  }

  function handleUrlSubmit(e: React.FormEvent) {
    e.preventDefault();
    onChange(urlInput.trim() || null);
    setOpen(false);
  }

  function handleClear(e: React.MouseEvent) {
    e.stopPropagation();
    onChange(null);
    setUrlInput('');
  }

  return (
    <div ref={containerRef} className="relative">
      <div className="flex items-center gap-2">
        {/* Preview + trigger */}
        <button
          type="button"
          onClick={() => setOpen((v) => !v)}
          className="w-10 h-10 rounded-xl bg-apple-bg flex items-center justify-center text-xl hover:brightness-95 transition-all border border-apple-separator"
        >
          {value ? (
            isUrl ? (
              <img src={value} alt="" className="w-7 h-7 object-contain rounded" onError={(e) => { (e.target as HTMLImageElement).src = ''; }} />
            ) : (
              <span>{value}</span>
            )
          ) : (
            <Smile size={18} className="text-apple-secondary" />
          )}
        </button>

        {value && (
          <button
            type="button"
            onClick={handleClear}
            className="text-xs text-apple-secondary hover:text-apple-red flex items-center gap-0.5"
          >
            <X size={12} /> 제거
          </button>
        )}
      </div>

      {open && (
        <>
          <div className="fixed inset-0 z-40" onClick={() => setOpen(false)} />
          <div className="absolute left-0 top-12 z-50 bg-apple-card rounded-2xl shadow-xl overflow-hidden border border-apple-separator w-72">
            {/* Tabs */}
            <div className="flex border-b border-apple-separator">
              <button
                onClick={() => setTab('emoji')}
                className={`flex-1 flex items-center justify-center gap-1.5 py-2 text-xs font-medium transition-colors ${
                  tab === 'emoji' ? 'text-apple-blue border-b-2 border-apple-blue' : 'text-apple-secondary'
                }`}
              >
                <Smile size={13} /> 이모지
              </button>
              <button
                onClick={() => setTab('url')}
                className={`flex-1 flex items-center justify-center gap-1.5 py-2 text-xs font-medium transition-colors ${
                  tab === 'url' ? 'text-apple-blue border-b-2 border-apple-blue' : 'text-apple-secondary'
                }`}
              >
                <Link size={13} /> 이미지 URL
              </button>
            </div>

            {tab === 'emoji' ? (
              <EmojiPicker
                onEmojiClick={handleEmojiClick}
                width="100%"
                height={320}
                searchPlaceholder="이모지 검색..."
                previewConfig={{ showPreview: false }}
              />
            ) : (
              <form onSubmit={handleUrlSubmit} className="p-3 flex flex-col gap-3">
                <input
                  autoFocus
                  type="url"
                  value={urlInput}
                  onChange={(e) => setUrlInput(e.target.value)}
                  placeholder="https://example.com/image.png"
                  className="w-full text-xs bg-apple-bg rounded-lg px-3 py-2 outline-none focus:ring-1 focus:ring-apple-blue text-apple-text"
                />
                {urlInput && (
                  <div className="flex justify-center">
                    <img
                      src={urlInput}
                      alt="미리보기"
                      className="w-16 h-16 object-contain rounded-xl border border-apple-separator"
                      onError={(e) => { (e.target as HTMLImageElement).style.display = 'none'; }}
                    />
                  </div>
                )}
                <button
                  type="submit"
                  className="w-full py-1.5 rounded-lg bg-apple-blue text-white text-xs font-medium disabled:opacity-40"
                  disabled={!urlInput.trim()}
                >
                  적용
                </button>
              </form>
            )}
          </div>
        </>
      )}
    </div>
  );
}
