package toby.ai.tobyreminder.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toby.ai.tobyreminder.domain.ReminderList;
import toby.ai.tobyreminder.dto.request.OrderItem;
import toby.ai.tobyreminder.dto.request.ReminderListRequest;
import toby.ai.tobyreminder.dto.response.ReminderListResponse;
import toby.ai.tobyreminder.service.ports.in.ReminderListService;
import toby.ai.tobyreminder.repository.ReminderListRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultReminderListService implements ReminderListService {

    private final ReminderListRepository reminderListRepository;

    @Override
    public List<ReminderListResponse> findAll() {
        return reminderListRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(ReminderListResponse::from)
                .toList();
    }

    @Override
    public ReminderListResponse findById(Long id) {
        return ReminderListResponse.from(getById(id));
    }

    @Override
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
                .isDefault(Boolean.TRUE.equals(request.getIsDefault()))
                .sortOrder(nextSortOrder)
                .build();

        return ReminderListResponse.from(reminderListRepository.save(reminderList));
    }

    @Override
    @Transactional
    public ReminderListResponse update(Long id, ReminderListRequest request) {
        ReminderList reminderList = getById(id);
        reminderList.update(request.getName(), request.getColor());
        return ReminderListResponse.from(reminderList);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!reminderListRepository.existsById(id)) {
            throw new EntityNotFoundException("ReminderList not found with id: " + id);
        }
        reminderListRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void reorder(List<OrderItem> items) {
        Map<Long, ReminderList> listMap = reminderListRepository.findAll().stream()
                .collect(java.util.stream.Collectors.toMap(ReminderList::getId, rl -> rl));
        for (OrderItem item : items) {
            ReminderList rl = listMap.get(item.getId());
            if (rl == null) throw new EntityNotFoundException("ReminderList not found with id: " + item.getId());
            rl.updateSortOrder(item.getSortOrder());
        }
    }

    private ReminderList getById(Long id) {
        return reminderListRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ReminderList not found with id: " + id));
    }
}
