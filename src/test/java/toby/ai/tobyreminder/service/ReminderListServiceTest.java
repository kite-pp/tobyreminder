package toby.ai.tobyreminder.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import toby.ai.tobyreminder.domain.ReminderList;
import toby.ai.tobyreminder.dto.request.ReminderListRequest;
import toby.ai.tobyreminder.dto.response.ReminderListResponse;
import toby.ai.tobyreminder.repository.ReminderListRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ReminderListServiceTest {

    @Mock
    private ReminderListRepository reminderListRepository;

    @InjectMocks
    private ReminderListService reminderListService;

    private ReminderList buildList(Long id, String name, String color, int sortOrder) {
        ReminderList list = ReminderList.builder()
                .name(name).color(color).isDefault(false).sortOrder(sortOrder)
                .build();
        // reflection으로 id 설정 (JPA가 할당하는 값 시뮬레이션)
        try {
            var field = ReminderList.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(list, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("sortOrder 오름차순으로 전체 목록을 반환한다")
        void findAll_returnsSortedList() {
            ReminderList list1 = buildList(1L, "업무", "#007AFF", 0);
            ReminderList list2 = buildList(2L, "개인", "#FF9500", 1);
            given(reminderListRepository.findAllByOrderBySortOrderAsc())
                    .willReturn(List.of(list1, list2));

            List<ReminderListResponse> result = reminderListService.findAll();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("업무");
            assertThat(result.get(1).getName()).isEqualTo("개인");
        }

        @Test
        @DisplayName("목록이 없으면 빈 리스트를 반환한다")
        void findAll_returnsEmptyList() {
            given(reminderListRepository.findAllByOrderBySortOrderAsc()).willReturn(List.of());

            List<ReminderListResponse> result = reminderListService.findAll();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("첫 번째 목록 생성 시 sortOrder가 0이다")
        void create_firstList_sortOrderIsZero() {
            given(reminderListRepository.findAllByOrderBySortOrderAsc()).willReturn(List.of());
            given(reminderListRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

            ReminderListRequest request = makeRequest("업무", "#007AFF");
            ReminderListResponse result = reminderListService.create(request);

            assertThat(result.getSortOrder()).isZero();
            assertThat(result.getName()).isEqualTo("업무");
            assertThat(result.getColor()).isEqualTo("#007AFF");
        }

        @Test
        @DisplayName("기존 목록이 있으면 sortOrder가 max+1이다")
        void create_appendsToEnd() {
            ReminderList existing = buildList(1L, "기존", "#007AFF", 2);
            given(reminderListRepository.findAllByOrderBySortOrderAsc()).willReturn(List.of(existing));
            given(reminderListRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

            ReminderListRequest request = makeRequest("신규", "#FF9500");
            ReminderListResponse result = reminderListService.create(request);

            assertThat(result.getSortOrder()).isEqualTo(3);
        }

        @Test
        @DisplayName("저장된 ReminderList를 Response로 변환하여 반환한다")
        void create_returnsResponse() {
            given(reminderListRepository.findAllByOrderBySortOrderAsc()).willReturn(List.of());
            given(reminderListRepository.save(any())).willAnswer(inv -> inv.getArgument(0));

            ReminderListResponse result = reminderListService.create(makeRequest("업무", "#34C759"));

            assertThat(result.getName()).isEqualTo("업무");
            assertThat(result.getColor()).isEqualTo("#34C759");
            assertThat(result.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("존재하는 목록의 name과 color를 변경한다")
        void update_changesNameAndColor() {
            ReminderList list = buildList(1L, "업무", "#007AFF", 0);
            given(reminderListRepository.findById(1L)).willReturn(Optional.of(list));

            ReminderListResponse result = reminderListService.update(1L, makeRequest("개인", "#FF9500"));

            assertThat(result.getName()).isEqualTo("개인");
            assertThat(result.getColor()).isEqualTo("#FF9500");
        }

        @Test
        @DisplayName("존재하지 않는 id로 update 시 EntityNotFoundException이 발생한다")
        void update_notFound_throwsException() {
            given(reminderListRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> reminderListService.update(99L, makeRequest("이름", "#007AFF")))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("존재하는 목록을 삭제한다")
        void delete_existingList() {
            given(reminderListRepository.existsById(1L)).willReturn(true);

            reminderListService.delete(1L);

            then(reminderListRepository).should().deleteById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 id로 delete 시 EntityNotFoundException이 발생한다")
        void delete_notFound_throwsException() {
            given(reminderListRepository.existsById(99L)).willReturn(false);

            assertThatThrownBy(() -> reminderListService.delete(99L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("99");

            then(reminderListRepository).should().existsById(99L);
            then(reminderListRepository).shouldHaveNoMoreInteractions();
        }
    }

    private ReminderListRequest makeRequest(String name, String color) {
        try {
            var request = new ReminderListRequest();
            setField(request, "name", name);
            setField(request, "color", color);
            return request;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
