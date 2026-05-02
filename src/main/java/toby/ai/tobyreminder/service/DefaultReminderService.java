package toby.ai.tobyreminder.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toby.ai.tobyreminder.domain.Reminder;
import toby.ai.tobyreminder.domain.ReminderList;
import toby.ai.tobyreminder.dto.request.ReminderRequest;
import toby.ai.tobyreminder.dto.response.ReminderResponse;
import toby.ai.tobyreminder.repository.ReminderListRepository;
import toby.ai.tobyreminder.repository.ReminderRepository;
import toby.ai.tobyreminder.service.ports.in.ReminderService;

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
        reminder.update(request.getTitle(), request.getNotes(), null, reminder.getPriority());
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

    private Reminder getById(Long id) {
        return reminderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reminder not found with id: " + id));
    }
}
