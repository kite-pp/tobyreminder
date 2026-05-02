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
import { SortableContext, verticalListSortingStrategy, arrayMove, useSortable } from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { useListsQuery, useReorderListsMutation } from '@/hooks/useLists';
import ListItem from './ListItem';
import type { ReminderList } from '@/types';

function SortableListItem({ list }: { list: ReminderList }) {
  const { attributes, listeners, setNodeRef, transform, transition, isDragging } = useSortable({
    id: list.id,
  });
  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    opacity: isDragging ? 0.5 : 1,
  };
  return (
    <div ref={setNodeRef} style={style} {...attributes} {...listeners}>
      <ListItem list={list} />
    </div>
  );
}

export default function MyLists() {
  const { data: lists, isLoading } = useListsQuery();
  const reorder = useReorderListsMutation();
  const [optimistic, setOptimistic] = useState<ReminderList[] | null>(null);

  const sensors = useSensors(useSensor(PointerSensor, { activationConstraint: { distance: 5 } }));
  const items = optimistic ?? lists ?? [];

  function handleDragEnd(event: DragEndEvent) {
    const { active, over } = event;
    if (!over || active.id === over.id) return;

    const oldIndex = items.findIndex((l) => l.id === active.id);
    const newIndex = items.findIndex((l) => l.id === over.id);
    const reordered = arrayMove(items, oldIndex, newIndex).map((l, i) => ({
      ...l,
      sortOrder: i,
    }));
    setOptimistic(reordered);
    reorder.mutate(
      reordered.map((l) => ({ id: l.id, sortOrder: l.sortOrder })),
      { onSettled: () => setOptimistic(null) }
    );
  }

  if (isLoading) return null;
  if (!items.length) return null;

  return (
    <section>
      <h3 className="px-2 mb-1 text-xs font-semibold text-apple-secondary uppercase tracking-wide">
        나의 목록
      </h3>
      <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
        <SortableContext items={items.map((l) => l.id)} strategy={verticalListSortingStrategy}>
          {items.map((list) => (
            <SortableListItem key={list.id} list={list} />
          ))}
        </SortableContext>
      </DndContext>
    </section>
  );
}
