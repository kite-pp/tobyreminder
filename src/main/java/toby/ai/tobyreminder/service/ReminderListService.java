package toby.ai.tobyreminder.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toby.ai.tobyreminder.domain.ReminderList;
import toby.ai.tobyreminder.dto.request.ReminderListRequest;
import toby.ai.tobyreminder.dto.response.ReminderListResponse;
import toby.ai.tobyreminder.repository.ReminderListRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReminderListService {

    private final ReminderListRepository reminderListRepository;

    public List<ReminderListResponse> findAll() {
        return reminderListRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(ReminderListResponse::from)
                .toList();
    }

    @Transactional
    public ReminderListResponse create(ReminderListRequest request) {
        int nextSortOrder = reminderListRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .mapToInt(ReminderList::getSortOrder)
                .max()
                .orElse(-1) + 1;

        ReminderList reminderList = ReminderList.builder()
                .name(request.getName())
                .color(request.getColor())
                .isDefault(request.isDefault())
                .sortOrder(nextSortOrder)
                .build();

        return ReminderListResponse.from(reminderListRepository.save(reminderList));
    }

    @Transactional
    public ReminderListResponse update(Long id, ReminderListRequest request) {
        ReminderList reminderList = findById(id);
        reminderList.update(request.getName(), request.getColor());
        return ReminderListResponse.from(reminderList);
    }

    @Transactional
    public void delete(Long id) {
        if (!reminderListRepository.existsById(id)) {
            throw new EntityNotFoundException("ReminderList not found with id: " + id);
        }
        reminderListRepository.deleteById(id);
    }

    private ReminderList findById(Long id) {
        return reminderListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ReminderList not found with id: " + id));
    }
}
