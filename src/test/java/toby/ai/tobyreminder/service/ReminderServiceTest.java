package toby.ai.tobyreminder.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ReminderServiceTest {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private ReminderListRepository reminderListRepository;

    private ReminderList testList;

    @BeforeEach
    void setUp() {
        reminderRepository.deleteAll();
        reminderListRepository.deleteAll();
        testList = reminderListRepository.save(
                ReminderList.builder().name("테스트").color("#007AFF").isDefault(false).sortOrder(0).build()
        );
    }

    @Nested
    @DisplayName("findByListId")
    class FindByListId {

        @Test
        @DisplayName("미완료 항목이 완료 항목보다 먼저 반환된다")
        void findByListId_incompleteFirst() {
            var r1 = reminderService.create(new ReminderRequest("할일1", null, testList.getId()));
            var r2 = reminderService.create(new ReminderRequest("할일2", null, testList.getId()));
            reminderService.toggleComplete(r1.getId());

            List<ReminderResponse> result = reminderService.findByListId(testList.getId());

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(r2.getId());
            assertThat(result.get(1).getId()).isEqualTo(r1.getId());
        }

        @Test
        @DisplayName("해당 목록의 리마인더만 반환된다")
        void findByListId_returnsOnlyMatchingList() {
            var otherList = reminderListRepository.save(
                    ReminderList.builder().name("다른목록").color("#FF9500").isDefault(false).sortOrder(1).build()
            );
            reminderService.create(new ReminderRequest("내 할일", null, testList.getId()));
            reminderService.create(new ReminderRequest("다른 할일", null, otherList.getId()));

            List<ReminderResponse> result = reminderService.findByListId(testList.getId());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("내 할일");
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("리마인더를 생성하면 저장된 응답을 반환한다")
        void create_returnsResponse() {
            ReminderResponse result = reminderService.create(new ReminderRequest("할일", "메모", testList.getId()));

            assertThat(result.getId()).isNotNull();
            assertThat(result.getTitle()).isEqualTo("할일");
            assertThat(result.getNotes()).isEqualTo("메모");
            assertThat(result.getListId()).isEqualTo(testList.getId());
            assertThat(result.isCompleted()).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 listId로 생성하면 EntityNotFoundException이 발생한다")
        void create_invalidListId_throwsException() {
            assertThatThrownBy(() -> reminderService.create(new ReminderRequest("할일", null, 99999L)))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("제목과 메모를 수정한다")
        void update_changesTitleAndNotes() {
            var created = reminderService.create(new ReminderRequest("원래 제목", "원래 메모", testList.getId()));

            ReminderResponse result = reminderService.update(created.getId(),
                    new ReminderRequest("새 제목", "새 메모", testList.getId()));

            assertThat(result.getTitle()).isEqualTo("새 제목");
            assertThat(result.getNotes()).isEqualTo("새 메모");
        }

        @Test
        @DisplayName("존재하지 않는 id로 수정하면 EntityNotFoundException이 발생한다")
        void update_notFound_throwsException() {
            assertThatThrownBy(() -> reminderService.update(99999L, new ReminderRequest("제목", null, testList.getId())))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("toggleComplete")
    class ToggleComplete {

        @Test
        @DisplayName("미완료 → 완료로 전환하면 completedAt이 설정된다")
        void toggleComplete_setsCompletedAt() {
            var created = reminderService.create(new ReminderRequest("할일", null, testList.getId()));

            reminderService.toggleComplete(created.getId());

            var reminder = reminderRepository.findById(created.getId()).orElseThrow();
            assertThat(reminder.isCompleted()).isTrue();
            assertThat(reminder.getCompletedAt()).isNotNull();
        }

        @Test
        @DisplayName("완료 → 미완료로 전환하면 completedAt이 null이 된다")
        void toggleComplete_clearsCompletedAt() {
            var created = reminderService.create(new ReminderRequest("할일", null, testList.getId()));
            reminderService.toggleComplete(created.getId());

            reminderService.toggleComplete(created.getId());

            var reminder = reminderRepository.findById(created.getId()).orElseThrow();
            assertThat(reminder.isCompleted()).isFalse();
            assertThat(reminder.getCompletedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("존재하는 리마인더를 삭제한다")
        void delete_existingReminder() {
            var created = reminderService.create(new ReminderRequest("할일", null, testList.getId()));

            reminderService.delete(created.getId());

            assertThat(reminderRepository.findById(created.getId())).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 id로 삭제하면 EntityNotFoundException이 발생한다")
        void delete_notFound_throwsException() {
            assertThatThrownBy(() -> reminderService.delete(99999L))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findBySmart")
    class FindBySmart {

        @Test
        @DisplayName("all 타입은 미완료 리마인더를 모두 반환한다")
        void findBySmart_all_returnsIncomplete() {
            var r1 = reminderService.create(new ReminderRequest("할일1", null, testList.getId()));
            var r2 = reminderService.create(new ReminderRequest("할일2", null, testList.getId()));
            reminderService.toggleComplete(r1.getId());

            List<ReminderResponse> result = reminderService.findBySmart("all");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(r2.getId());
        }

        @Test
        @DisplayName("completed 타입은 완료된 리마인더를 반환한다")
        void findBySmart_completed_returnsCompleted() {
            var r1 = reminderService.create(new ReminderRequest("할일1", null, testList.getId()));
            reminderService.create(new ReminderRequest("할일2", null, testList.getId()));
            reminderService.toggleComplete(r1.getId());

            List<ReminderResponse> result = reminderService.findBySmart("completed");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(r1.getId());
        }

        @Test
        @DisplayName("flagged 타입은 플래그된 미완료 리마인더를 반환한다")
        void findBySmart_flagged_returnsFlagged() {
            reminderService.create(new ReminderRequest("할일1", null, testList.getId()));
            var r2 = reminderService.create(new ReminderRequest("할일2", null, testList.getId()));
            Reminder reminder = reminderRepository.findById(r2.getId()).orElseThrow();
            reminder.toggleFlag();
            reminderRepository.save(reminder);

            List<ReminderResponse> result = reminderService.findBySmart("flagged");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(r2.getId());
        }

        @Test
        @DisplayName("알 수 없는 타입은 IllegalArgumentException을 발생시킨다")
        void findBySmart_unknownType_throwsException() {
            assertThatThrownBy(() -> reminderService.findBySmart("unknown"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("getCount")
    class GetCount {

        @Test
        @DisplayName("카운트가 올바르게 반환된다")
        void getCount_returnsCorrectCounts() {
            var r1 = reminderService.create(new ReminderRequest("할일1", null, testList.getId()));
            reminderService.create(new ReminderRequest("할일2", null, testList.getId()));
            reminderService.toggleComplete(r1.getId());

            CountResponse count = reminderService.getCount();

            assertThat(count.getAll()).isEqualTo(1);
            assertThat(count.getCompleted()).isEqualTo(1);
        }
    }
}
