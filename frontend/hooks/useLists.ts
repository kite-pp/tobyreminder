'use client';

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { api } from '@/lib/api';
import type { ReminderList, ReminderListRequest } from '@/types';

const QUERY_KEY = ['lists'] as const;

export function useListsQuery() {
  return useQuery({
    queryKey: QUERY_KEY,
    queryFn: () => api.get<ReminderList[]>('/api/lists'),
  });
}

export function useCreateListMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (request: ReminderListRequest) =>
      api.post<ReminderList>('/api/lists', request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}

export function useUpdateListMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, ...request }: ReminderListRequest & { id: number }) =>
      api.put<ReminderList>(`/api/lists/${id}`, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}

export function useDeleteListMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => api.delete(`/api/lists/${id}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}

export function useReorderListsMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (items: { id: number; sortOrder: number }[]) =>
      api.patch('/api/lists/order', items),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}
