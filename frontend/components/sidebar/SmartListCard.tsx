'use client';

import Link from 'next/link';
import type { LucideIcon } from 'lucide-react';

interface Props {
  type: string;
  label: string;
  icon: LucideIcon;
  color: string;
  count: number;
}

export default function SmartListCard({ type, label, icon: Icon, color, count }: Props) {
  return (
    <Link href={`/smart/${type}`} className="block">
      <div
        className="rounded-2xl p-3 flex flex-col gap-2 cursor-pointer hover:brightness-95 transition-all"
        style={{ backgroundColor: color + '26' }}
      >
        <Icon size={20} style={{ color }} />
        <div>
          <div className="text-3xl font-bold" style={{ color }}>
            {count}
          </div>
          <div className="text-xs text-apple-secondary">{label}</div>
        </div>
      </div>
    </Link>
  );
}
