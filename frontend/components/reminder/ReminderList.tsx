'use client';

import { useState } from 'react';
import {
  DndContext,
  closestCenter,
  PointerSensor,
  useSensor,
  useSensors,
  DragEndEvent,
} from '@dnd-kit/core';
import { SortableContext, verticalListSortingStrategy, arrayMove } from '@dnd-kit/sortable';
import { useRemindersQuery, useReorderRemindersMutation } from '@/hooks/useReminders';
import type { Reminder } from '@/types';
import SortableReminderRow from './SortableReminderRow';

interface Props {
  listId: number;
  accentColor?: string;
}

export default function ReminderList({ listId, accentColor = '#007AFF' }: Props) {
  const { data: reminders, isLoading } = useRemindersQuery(listId);
  const reorder = useReorderRemindersMutation();
  const [optimistic, setOptimistic] = useState<Reminder[] | null>(null);

  const sensors = useSensors(useSensor(PointerSensor, { activationConstraint: { distance: 5 } }));

  const items = (optimistic ?? reminders ?? []).filter((r) => !r.completed);

  function handleDragEnd(event: DragEndEvent) {
    const { active, over } = event;
    if (!over || active.id === over.id) return;

    const oldIndex = items.findIndex((r) => r.id === active.id);
    const newIndex = items.findIndex((r) => r.id === over.id);
    const reordered = arrayMove(items, oldIndex, newIndex).map((r, i) => ({
      ...r,
      sortOrder: i,
    }));
    setOptimistic(reordered);
    reorder.mutate(
      reordered.map((r) => ({ id: r.id, sortOrder: r.sortOrder })),
      { onSettled: () => setOptimistic(null) }
    );
  }

  if (isLoading) {
    return <div className="px-4 py-2 text-sm text-apple-secondary">로딩 중...</div>;
  }

  if (!items.length) {
    return (
      <div className="flex items-center justify-center py-16 text-sm text-apple-secondary">
        리마인더 없음
      </div>
    );
  }

  return (
    <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
      <SortableContext items={items.map((r) => r.id)} strategy={verticalListSortingStrategy}>
        <ul>
          {items.map((reminder) => (
            <SortableReminderRow key={reminder.id} reminder={reminder} accentColor={accentColor} />
          ))}
        </ul>
      </SortableContext>
    </DndContext>
  );
}
