'use client';

import type { Subtask } from '@/types';
import SubtaskRow from './SubtaskRow';
import AddSubtaskInput from './AddSubtaskInput';

interface Props {
  reminderId: number;
  subtasks: Subtask[];
}

export default function SubtaskList({ reminderId, subtasks }: Props) {
  return (
    <div>
      {subtasks.length > 0 && (
        <ul>
          {subtasks.map((subtask) => (
            <SubtaskRow key={subtask.id} subtask={subtask} reminderId={reminderId} />
          ))}
        </ul>
      )}
      <AddSubtaskInput reminderId={reminderId} />
    </div>
  );
}
