'use client';

import { useMemo } from 'react';
import type { Reminder } from '@/types';

interface Props {
  reminders: Reminder[];
  year: number;
  month: number;
}

export default function FloatingImages({ reminders, year, month }: Props) {
  const items = useMemo(() => {
    return reminders
      .filter((r) => r.imageUrl && r.dueDate)
      .map((r, i) => {
        const d = new Date(r.dueDate!);
        // row 0-5 in calendar grid → map to vertical position (10%~90%)
        const firstDay = (new Date(year, month, 1).getDay() + 6) % 7;
        const weekRow = Math.floor((firstDay + d.getDate() - 1) / 7);
        const baseY = 10 + weekRow * 14; // % of viewport height

        // stagger: each item floats at slightly different height & speed
        const yVariance = (i % 3 - 1) * 30; // -30 | 0 | 30 px
        const duration = 10 + (i % 5) * 2;   // 10~18s
        const delay = -(i * 2.3);             // negative delay = already in motion

        return {
          id: r.id,
          imageUrl: r.imageUrl!,
          isUrl: r.imageUrl!.startsWith('http'),
          baseY,
          yVariance,
          duration,
          delay,
        };
      });
  }, [reminders, year, month]);

  if (items.length === 0) return null;

  return (
    <>
      {items.map((item) => (
        <div
          key={item.id}
          className="float-item"
          style={
            {
              right: 0,
              top: `${item.baseY}vh`,
              '--float-y': `${item.yVariance}px`,
              '--float-dur': `${item.duration}s`,
              '--float-delay': `${item.delay}s`,
              '--float-size': item.isUrl ? '0px' : '28px',
            } as React.CSSProperties
          }
        >
          {item.isUrl ? (
            <img
              src={item.imageUrl}
              alt=""
              className="w-8 h-8 object-contain rounded-lg opacity-80 drop-shadow"
              onError={(e) => { (e.target as HTMLImageElement).style.display = 'none'; }}
            />
          ) : (
            <span className="drop-shadow">{item.imageUrl}</span>
          )}
        </div>
      ))}
    </>
  );
}
