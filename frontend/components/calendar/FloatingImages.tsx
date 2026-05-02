'use client';

import { useMemo, useEffect, useRef } from 'react';
import type { Reminder } from '@/types';

interface Props {
  reminders: Reminder[];
  year: number;
  month: number;
}

function randomY() {
  // 10vh ~ 78vh — wave amplitude ±35px stays within viewport
  return 10 + Math.random() * 68;
}

export default function FloatingImages({ reminders, year, month }: Props) {
  const elRefs = useRef<Map<string, HTMLDivElement>>(new Map());

  const items = useMemo(() => {
    return reminders
      .filter((r) => r.imageUrl && r.dueDate)
      .map((r, i) => ({
        id: String(r.id),
        imageUrl: r.imageUrl!,
        isUrl: r.imageUrl!.startsWith('http'),
        initialY: randomY(),
        // each item has different travel duration and wave period
        dur: 14 + (i % 5) * 3,       // 14~26s across screen
        waveDur: 3 + (i % 4) * 0.8,  // 3~5.4s per wave cycle
        delay: -(i * 3.1),            // already in motion
      }));
  }, [reminders, year, month]);

  // randomise vertical start position on every loop iteration
  useEffect(() => {
    const cleanups: (() => void)[] = [];
    for (const [, el] of elRefs.current) {
      const handler = () => { el.style.top = `${randomY()}vh`; };
      el.addEventListener('animationiteration', handler);
      cleanups.push(() => el.removeEventListener('animationiteration', handler));
    }
    return () => cleanups.forEach((fn) => fn());
  }, [items]);

  if (items.length === 0) return null;

  return (
    <>
      {items.map((item) => (
        <div
          key={item.id}
          ref={(el) => {
            if (el) elRefs.current.set(item.id, el);
            else elRefs.current.delete(item.id);
          }}
          className="float-item"
          style={
            {
              left: '260px',
              top: `${item.initialY}vh`,
              '--float-dur': `${item.dur}s`,
              '--float-delay': `${item.delay}s`,
            } as React.CSSProperties
          }
        >
          <div
            className="float-item-wave"
            style={
              {
                '--wave-dur': `${item.waveDur}s`,
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
        </div>
      ))}
    </>
  );
}
