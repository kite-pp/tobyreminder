package toby.ai.tobyreminder.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toby.ai.tobyreminder.dto.request.ReminderListRequest;
import toby.ai.tobyreminder.dto.response.ReminderListResponse;
import toby.ai.tobyreminder.service.ports.in.ReminderListService;
import toby.ai.tobyreminder.repository.ReminderListRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ReminderListServiceTest {

    @Autowired
    private ReminderListService reminderListService;

    @Autowired
    private ReminderListRepository reminderListRepository;

    @BeforeEach
    void setUp() {
        reminderListRepository.deleteAll();
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("sortOrder 오름차순으로 전체 목록을 반환한다")
        void findAll_returnsSortedList() {
            reminderListService.create(new ReminderListRequest("업무", "#007AFF", null, false));
            reminderListService.create(new ReminderListRequest("개인", "#FF9500", null, false));

            List<ReminderListResponse> result = reminderListService.findAll();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("업무");
            assertThat(result.get(1).getName()).isEqualTo("개인");
        }

        @Test
        @DisplayName("목록이 없으면 빈 리스트를 반환한다")
        void findAll_returnsEmptyList() {
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
            ReminderListResponse result = reminderListService.create(
                    new ReminderListRequest("업무", "#007AFF", null, false));

            assertThat(result.getSortOrder()).isZero();
            assertThat(result.getName()).isEqualTo("업무");
            assertThat(result.getColor()).isEqualTo("#007AFF");
        }

        @Test
        @DisplayName("기존 목록이 있으면 sortOrder가 max+1이다")
        void create_appendsToEnd() {
            reminderListService.create(new ReminderListRequest("기존", "#007AFF", null, false));

            ReminderListResponse result = reminderListService.create(
                    new ReminderListRequest("신규", "#FF9500", null, false));

            assertThat(result.getSortOrder()).isEqualTo(1);
        }

        @Test
        @DisplayName("저장된 ReminderList를 Response로 변환하여 반환한다")
        void create_returnsResponse() {
            ReminderListResponse result = reminderListService.create(
                    new ReminderListRequest("업무", "#34C759", null, false));

            assertThat(result.getName()).isEqualTo("업무");
            assertThat(result.getColor()).isEqualTo("#34C759");
            assertThat(result.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("존재하는 id로 조회하면 해당 목록을 반환한다")
        void findById_returnsResponse() {
            ReminderListResponse created = reminderListService.create(
                    new ReminderListRequest("업무", "#007AFF", null, false));

            ReminderListResponse result = reminderListService.findById(created.getId());

            assertThat(result.getId()).isEqualTo(created.getId());
            assertThat(result.getName()).isEqualTo("업무");
        }

        @Test
        @DisplayName("존재하지 않는 id로 조회하면 EntityNotFoundException이 발생한다")
        void findById_notFound_throwsException() {
            assertThatThrownBy(() -> reminderListService.findById(99999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("99999");
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("존재하는 목록의 name과 color를 변경한다")
        void update_changesNameAndColor() {
            ReminderListResponse created = reminderListService.create(
                    new ReminderListRequest("업무", "#007AFF", null, false));

            ReminderListResponse result = reminderListService.update(
                    created.getId(), new ReminderListRequest("개인", "#FF9500", null, false));

            assertThat(result.getName()).isEqualTo("개인");
            assertThat(result.getColor()).isEqualTo("#FF9500");
        }

        @Test
        @DisplayName("존재하지 않는 id로 update 시 EntityNotFoundException이 발생한다")
        void update_notFound_throwsException() {
            assertThatThrownBy(() -> reminderListService.update(
                    99999L, new ReminderListRequest("이름", "#007AFF", null, false)))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("99999");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("존재하는 목록을 삭제한다")
        void delete_existingList() {
            ReminderListResponse created = reminderListService.create(
                    new ReminderListRequest("업무", "#007AFF", null, false));

            reminderListService.delete(created.getId());

            assertThat(reminderListRepository.findById(created.getId())).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 id로 delete 시 EntityNotFoundException이 발생한다")
        void delete_notFound_throwsException() {
            assertThatThrownBy(() -> reminderListService.delete(99999L))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessageContaining("99999");
        }
    }
}
