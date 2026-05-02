export interface ReminderList {
  id: number;
  name: string;
  color: string;
  icon: string | null;
  isDefault: boolean;
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
}

export interface Reminder {
  id: number;
  listId: number;
  title: string;
  notes: string | null;
  dueDate: string | null;
  priority: 'NONE' | 'LOW' | 'MEDIUM' | 'HIGH';
  flagged: boolean;
  completed: boolean;
  completedAt: string | null;
  sortOrder: number;
  createdAt: string;
}

export interface Subtask {
  id: number;
  reminderId: number;
  title: string;
  completed: boolean;
  sortOrder: number;
  createdAt: string;
}

export interface ReminderListRequest {
  name: string;
  color: string;
  icon?: string | null;
  isDefault?: boolean;
}

export interface CountResponse {
  today: number;
  scheduled: number;
  all: number;
  flagged: number;
  completed: number;
}
