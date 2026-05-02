'use client';

import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { api } from '@/lib/api';
import type { CountResponse, Reminder } from '@/types';

const QUERY_KEY = ['reminders'] as const;

export function useRemindersQuery(listId: number) {
  return useQuery({
    queryKey: [...QUERY_KEY, listId],
    queryFn: () => api.get<Reminder[]>(`/api/reminders?listId=${listId}`),
    enabled: !!listId,
  });
}

export function useCreateReminderMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (request: { title: string; notes?: string | null; listId: number }) =>
      api.post<Reminder>('/api/reminders', request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}

export function useUpdateReminderMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id, ...request }: { id: number; title: string; notes?: string | null; listId: number }) =>
      api.put<Reminder>(`/api/reminders/${id}`, request),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}

export function useToggleCompleteMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ id }: { id: number }) => api.patch(`/api/reminders/${id}/complete`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}

export function useDeleteReminderMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => api.delete(`/api/reminders/${id}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: QUERY_KEY }),
  });
}

export function useSmartRemindersQuery(type: string) {
  return useQuery({
    queryKey: [...QUERY_KEY, 'smart', type],
    queryFn: () => api.get<Reminder[]>(`/api/reminders?smart=${type}`),
    enabled: !!type,
  });
}

export function useCountQuery() {
  return useQuery({
    queryKey: [...QUERY_KEY, 'count'],
    queryFn: () => api.get<CountResponse>('/api/reminders/count'),
  });
}
