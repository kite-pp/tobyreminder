package toby.ai.tobyreminder.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toby.ai.tobyreminder.domain.Reminder;
import toby.ai.tobyreminder.domain.ReminderList;
import toby.ai.tobyreminder.domain.enums.Priority;
import toby.ai.tobyreminder.dto.request.ReminderRequest;
import toby.ai.tobyreminder.dto.response.CountResponse;
import toby.ai.tobyreminder.dto.response.ReminderResponse;
import toby.ai.tobyreminder.repository.ReminderListRepository;
import toby.ai.tobyreminder.repository.ReminderRepository;
import toby.ai.tobyreminder.service.ports.in.ReminderService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultReminderService implements ReminderService {

    private final ReminderRepository reminderRepository;
    private final ReminderListRepository reminderListRepository;

    @Override
    public List<ReminderResponse> findByListId(Long listId) {
        return reminderRepository.findByListIdOrderByCompletedAscSortOrderAsc(listId)
                .stream()
                .map(ReminderResponse::from)
                .toList();
    }

    @Override
    @Transactional
    public ReminderResponse create(ReminderRequest request) {
        ReminderList list = reminderListRepository.findById(request.getListId())
                .orElseThrow(() -> new EntityNotFoundException("ReminderList not found with id: " + request.getListId()));

        int nextSortOrder = reminderRepository.findByListIdOrderByCompletedAscSortOrderAsc(list.getId())
                .stream()
                .mapToInt(Reminder::getSortOrder)
                .max()
                .orElse(-1) + 1;

        Reminder reminder = Reminder.builder()
                .list(list)
                .title(request.getTitle())
                .notes(request.getNotes())
                .sortOrder(nextSortOrder)
                .build();

        return ReminderResponse.from(reminderRepository.save(reminder));
    }

    @Override
    @Transactional
    public ReminderResponse update(Long id, ReminderRequest request) {
        Reminder reminder = getById(id);
        reminder.update(
                request.getTitle(),
                request.getNotes(),
                request.getDueDate(),
                request.getPriority() != null ? request.getPriority() : reminder.getPriority()
        );
        if (request.getFlagged() != null) {
            if (request.getFlagged() != reminder.isFlagged()) reminder.toggleFlag();
        }
        return ReminderResponse.from(reminder);
    }

    @Override
    @Transactional
    public void toggleComplete(Long id) {
        getById(id).toggleComplete();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!reminderRepository.existsById(id)) {
            throw new EntityNotFoundException("Reminder not found with id: " + id);
        }
        reminderRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void toggleFlag(Long id) {
        getById(id).toggleFlag();
    }

    @Override
    @Transactional
    public void updatePriority(Long id, Priority priority) {
        Reminder r = getById(id);
        r.update(r.getTitle(), r.getNotes(), r.getDueDate(), priority);
    }

    @Override
    public List<ReminderResponse> findBySmart(String type) {
        LocalDateTime now = LocalDateTime.now();
        List<Reminder> result = switch (type) {
            case "today" -> reminderRepository.findByDueDateBetweenAndCompletedFalse(
                    now.toLocalDate().atStartOfDay(), now.toLocalDate().atTime(23, 59, 59));
            case "scheduled" -> reminderRepository.findByDueDateNotNullAndCompletedFalse();
            case "all" -> reminderRepository.findByCompletedFalse();
            case "flagged" -> reminderRepository.findByFlaggedTrueAndCompletedFalse();
            case "completed" -> reminderRepository.findByCompletedTrue();
            default -> throw new IllegalArgumentException("Unknown smart type: " + type);
        };
        return result.stream().map(ReminderResponse::from).toList();
    }

    @Override
    public CountResponse getCount() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.toLocalDate().atStartOfDay();
        LocalDateTime end = now.toLocalDate().atTime(23, 59, 59);
        return CountResponse.builder()
                .today(reminderRepository.countByDueDateBetweenAndCompletedFalse(start, end))
                .scheduled(reminderRepository.countByDueDateNotNullAndCompletedFalse())
                .all(reminderRepository.countByCompletedFalse())
                .flagged(reminderRepository.countByFlaggedTrueAndCompletedFalse())
                .completed(reminderRepository.countByCompletedTrue())
                .build();
    }

    private Reminder getById(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reminder not found with id: " + id));
    }
}
