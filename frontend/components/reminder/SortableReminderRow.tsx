'use client';

import { useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import type { Reminder } from '@/types';
import ReminderRow from './ReminderRow';

interface Props {
  reminder: Reminder;
  accentColor?: string;
}

export default function SortableReminderRow({ reminder, accentColor }: Props) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id: reminder.id,
  });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  };

  return (
    <div ref={setNodeRef} style={style} {...attributes} {...listeners}>
      <ReminderRow reminder={reminder} accentColor={accentColor} />
    </div>
  );
}
