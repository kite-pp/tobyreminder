'use client';

import { useMutation, useQueryClient } from '@tanstack/react-query';
import { api } from '@/lib/api';

const REMINDERS_QUERY_KEY = ['reminders'] as const;

export function useCreateSubtaskMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ reminderId, title }: { reminderId: number; title: string }) =>
      api.post(`/api/reminders/${reminderId}/subtasks`, { title }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: REMINDERS_QUERY_KEY }),
  });
}

export function useToggleSubtaskMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ reminderId, subtaskId }: { reminderId: number; subtaskId: number }) =>
      api.patch(`/api/reminders/${reminderId}/subtasks/${subtaskId}/complete`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: REMINDERS_QUERY_KEY }),
  });
}

export function useDeleteSubtaskMutation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ reminderId, subtaskId }: { reminderId: number; subtaskId: number }) =>
      api.delete(`/api/reminders/${reminderId}/subtasks/${subtaskId}`),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: REMINDERS_QUERY_KEY }),
  });
}
