package toby.ai.tobyreminder.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toby.ai.tobyreminder.domain.Reminder;
import toby.ai.tobyreminder.domain.Subtask;
import toby.ai.tobyreminder.dto.request.SubtaskRequest;
import toby.ai.tobyreminder.dto.response.SubtaskResponse;
import toby.ai.tobyreminder.repository.ReminderRepository;
import toby.ai.tobyreminder.repository.SubtaskRepository;
import toby.ai.tobyreminder.service.ports.in.SubtaskService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultSubtaskService implements SubtaskService {

    private final SubtaskRepository subtaskRepository;
    private final ReminderRepository reminderRepository;

    @Override
    @Transactional
    public SubtaskResponse create(Long reminderId, SubtaskRequest request) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new EntityNotFoundException("Reminder not found with id: " + reminderId));

        int nextSortOrder = subtaskRepository.findByReminderIdOrderBySortOrderAsc(reminderId)
                .stream()
                .mapToInt(Subtask::getSortOrder)
                .max()
                .orElse(-1) + 1;

        Subtask subtask = Subtask.builder()
                .reminder(reminder)
                .title(request.getTitle())
                .sortOrder(nextSortOrder)
                .build();

        return SubtaskResponse.from(subtaskRepository.save(subtask));
    }

    @Override
    @Transactional
    public void toggleComplete(Long reminderId, Long subtaskId) {
        Subtask subtask = getSubtaskBelongingToReminder(reminderId, subtaskId);
        subtask.toggleComplete();
    }

    @Override
    @Transactional
    public void delete(Long reminderId, Long subtaskId) {
        Subtask subtask = getSubtaskBelongingToReminder(reminderId, subtaskId);
        subtaskRepository.delete(subtask);
    }

    private Subtask getSubtaskBelongingToReminder(Long reminderId, Long subtaskId) {
        Subtask subtask = subtaskRepository.findById(subtaskId)
                .orElseThrow(() -> new EntityNotFoundException("Subtask not found with id: " + subtaskId));
        if (!subtask.getReminder().getId().equals(reminderId)) {
            throw new EntityNotFoundException("Subtask " + subtaskId + " does not belong to reminder " + reminderId);
        }
        return subtask;
    }
}
