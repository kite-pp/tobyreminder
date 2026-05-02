package toby.ai.tobyreminder.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import toby.ai.tobyreminder.domain.enums.Priority;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderTest {

    @Nested
    @DisplayName("생성자")
    class Constructor {

        @Test
        @DisplayName("Builder로 생성 시 지정한 필드값이 설정된다")
        void builder_setsFields() {
            LocalDateTime due = LocalDateTime.of(2026, 6, 1, 9, 0);

            Reminder reminder = Reminder.builder()
                    .title("회의 준비")
                    .notes("발표 자료 완성")
                    .dueDate(due)
                    .priority(Priority.HIGH)
                    .flagged(true)
                    .build();

            assertThat(reminder.getTitle()).isEqualTo("회의 준비");
            assertThat(reminder.getNotes()).isEqualTo("발표 자료 완성");
            assertThat(reminder.getDueDate()).isEqualTo(due);
            assertThat(reminder.getPriority()).isEqualTo(Priority.HIGH);
            assertThat(reminder.isFlagged()).isTrue();
        }

        @Test
        @DisplayName("Builder 기본값 — priority=NONE, flagged=false, completed=false, sortOrder=0")
        void builder_defaultValues() {
            Reminder reminder = Reminder.builder()
                    .title("기본값 테스트")
                    .build();

            assertThat(reminder.getPriority()).isEqualTo(Priority.NONE);
            assertThat(reminder.isFlagged()).isFalse();
            assertThat(reminder.isCompleted()).isFalse();
            assertThat(reminder.getSortOrder()).isZero();
            assertThat(reminder.getDueDate()).isNull();
            assertThat(reminder.getNotes()).isNull();
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("update() 호출 시 title, notes, dueDate, priority가 변경된다")
        void update_changesFields() {
            Reminder reminder = Reminder.builder().title("원래 제목").build();
            LocalDateTime newDue = LocalDateTime.of(2026, 7, 1, 10, 0);

            reminder.update("수정된 제목", "수정된 메모", newDue, Priority.MEDIUM);

            assertThat(reminder.getTitle()).isEqualTo("수정된 제목");
            assertThat(reminder.getNotes()).isEqualTo("수정된 메모");
            assertThat(reminder.getDueDate()).isEqualTo(newDue);
            assertThat(reminder.getPriority()).isEqualTo(Priority.MEDIUM);
        }

        @Test
        @DisplayName("toggleComplete() 호출 시 완료 상태가 되고 completedAt이 설정된다")
        void toggleComplete_toCompleted() {
            Reminder reminder = Reminder.builder().title("완료 테스트").build();

            reminder.toggleComplete();

            assertThat(reminder.isCompleted()).isTrue();
            assertThat(reminder.getCompletedAt()).isNotNull();
        }

        @Test
        @DisplayName("toggleComplete() 두 번 호출 시 미완료 상태로 돌아오고 completedAt이 제거된다")
        void toggleComplete_backToUncompleted() {
            Reminder reminder = Reminder.builder().title("완료 취소 테스트").build();

            reminder.toggleComplete();
            reminder.toggleComplete();

            assertThat(reminder.isCompleted()).isFalse();
            assertThat(reminder.getCompletedAt()).isNull();
        }

        @Test
        @DisplayName("toggleFlag() 호출 시 flagged 상태가 토글된다")
        void toggleFlag_switchesFlaggedState() {
            Reminder reminder = Reminder.builder().title("플래그 테스트").build();

            reminder.toggleFlag();
            assertThat(reminder.isFlagged()).isTrue();

            reminder.toggleFlag();
            assertThat(reminder.isFlagged()).isFalse();
        }
    }

    @Nested
    @DisplayName("createdAt 자동 등록")
    class CreatedAt {

        @Test
        @DisplayName("Builder로 생성하면 createdAt이 현재 시각으로 자동 설정된다")
        void builder_setsCreatedAt() {
            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            Reminder reminder = Reminder.builder().title("날짜 테스트").build();

            assertThat(reminder.getCreatedAt()).isNotNull();
            assertThat(reminder.getCreatedAt()).isAfter(before);
            assertThat(reminder.getCreatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
        }

        @Test
        @DisplayName("update() 호출 후에도 createdAt은 변경되지 않는다")
        void update_doesNotChangeCreatedAt() {
            Reminder reminder = Reminder.builder().title("불변 날짜 테스트").build();
            LocalDateTime originalCreatedAt = reminder.getCreatedAt();

            reminder.update("변경된 제목", null, null, Priority.LOW);

            assertThat(reminder.getCreatedAt()).isEqualTo(originalCreatedAt);
        }

        @Test
        @DisplayName("각 Reminder 인스턴스는 독립적인 createdAt을 가진다")
        void eachInstance_hasOwnCreatedAt() {
            Reminder r1 = Reminder.builder().title("첫 번째").build();
            Reminder r2 = Reminder.builder().title("두 번째").build();

            assertThat(r1.getCreatedAt()).isNotNull();
            assertThat(r2.getCreatedAt()).isNotNull();
        }
    }
}
