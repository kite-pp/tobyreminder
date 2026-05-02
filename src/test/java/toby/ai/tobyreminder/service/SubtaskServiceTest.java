package toby.ai.tobyreminder.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import toby.ai.tobyreminder.domain.ReminderList;
import toby.ai.tobyreminder.dto.request.ReminderRequest;
import toby.ai.tobyreminder.dto.request.SubtaskRequest;
import toby.ai.tobyreminder.dto.response.SubtaskResponse;
import toby.ai.tobyreminder.repository.ReminderListRepository;
import toby.ai.tobyreminder.repository.ReminderRepository;
import toby.ai.tobyreminder.repository.SubtaskRepository;
import toby.ai.tobyreminder.service.ports.in.ReminderService;
import toby.ai.tobyreminder.service.ports.in.SubtaskService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class SubtaskServiceTest {

    @Autowired private SubtaskService subtaskService;
    @Autowired private ReminderService reminderService;
    @Autowired private ReminderRepository reminderRepository;
    @Autowired private ReminderListRepository reminderListRepository;
    @Autowired private SubtaskRepository subtaskRepository;

    private Long reminderId;

    @BeforeEach
    void setUp() {
        subtaskRepository.deleteAll();
        reminderRepository.deleteAll();
        reminderListRepository.deleteAll();
        ReminderList list = reminderListRepository.save(
                ReminderList.builder().name("테스트").color("#007AFF").isDefault(false).sortOrder(0).build()
        );
        reminderId = reminderService.create(new ReminderRequest("할일", null, list.getId())).getId();
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("서브태스크를 생성하면 저장된 응답을 반환한다")
        void create_returnsResponse() {
            SubtaskResponse result = subtaskService.create(reminderId, new SubtaskRequest("서브태스크1"));

            assertThat(result.getId()).isNotNull();
            assertThat(result.getTitle()).isEqualTo("서브태스크1");
            assertThat(result.isCompleted()).isFalse();
            assertThat(result.getSortOrder()).isEqualTo(0);
        }

        @Test
        @DisplayName("sortOrder가 순차적으로 증가한다")
        void create_incrementsSortOrder() {
            subtaskService.create(reminderId, new SubtaskRequest("첫번째"));
            SubtaskResponse second = subtaskService.create(reminderId, new SubtaskRequest("두번째"));

            assertThat(second.getSortOrder()).isEqualTo(1);
        }

        @Test
        @DisplayName("존재하지 않는 reminderId로 생성하면 EntityNotFoundException이 발생한다")
        void create_invalidReminderId_throwsException() {
            assertThatThrownBy(() -> subtaskService.create(99999L, new SubtaskRequest("서브태스크")))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("toggleComplete")
    class ToggleComplete {

        @Test
        @DisplayName("완료 상태를 토글한다")
        void toggleComplete_flipsCompleted() {
            var created = subtaskService.create(reminderId, new SubtaskRequest("서브태스크"));

            subtaskService.toggleComplete(reminderId, created.getId());

            var subtask = subtaskRepository.findById(created.getId()).orElseThrow();
            assertThat(subtask.isCompleted()).isTrue();
        }

        @Test
        @DisplayName("다른 리마인더의 서브태스크 토글 시 EntityNotFoundException이 발생한다")
        void toggleComplete_wrongReminder_throwsException() {
            var created = subtaskService.create(reminderId, new SubtaskRequest("서브태스크"));

            assertThatThrownBy(() -> subtaskService.toggleComplete(99999L, created.getId()))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("서브태스크를 삭제한다")
        void delete_removesSubtask() {
            var created = subtaskService.create(reminderId, new SubtaskRequest("서브태스크"));

            subtaskService.delete(reminderId, created.getId());

            assertThat(subtaskRepository.findById(created.getId())).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 서브태스크 삭제 시 EntityNotFoundException이 발생한다")
        void delete_notFound_throwsException() {
            assertThatThrownBy(() -> subtaskService.delete(reminderId, 99999L))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}
