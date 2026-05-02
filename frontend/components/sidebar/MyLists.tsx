'use client';

import { useListsQuery } from '@/hooks/useLists';
import ListItem from './ListItem';

export default function MyLists() {
  const { data: lists, isLoading } = useListsQuery();

  if (isLoading) return null;
  if (!lists?.length) return null;

  return (
    <section>
      <h3 className="px-2 mb-1 text-xs font-semibold text-apple-secondary uppercase tracking-wide">
        나의 목록
      </h3>
      {lists.map((list) => (
        <ListItem key={list.id} list={list} />
      ))}
    </section>
  );
}
