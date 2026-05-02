package toby.ai.tobyreminder.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SubtaskTest {

    @Test
    @DisplayName("서브태스크 생성 시 기본값이 올바르게 설정된다")
    void subtask_creation_defaultValues() {
        Subtask subtask = Subtask.builder()
                .reminder(null)
                .title("서브태스크")
                .sortOrder(0)
                .build();

        assertThat(subtask.getTitle()).isEqualTo("서브태스크");
        assertThat(subtask.getSortOrder()).isEqualTo(0);
        assertThat(subtask.isCompleted()).isFalse();
        assertThat(subtask.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("toggleComplete로 완료 상태가 토글된다")
    void toggleComplete_flipsCompleted() {
        Subtask subtask = Subtask.builder()
                .reminder(null)
                .title("서브태스크")
                .sortOrder(0)
                .build();

        subtask.toggleComplete();
        assertThat(subtask.isCompleted()).isTrue();

        subtask.toggleComplete();
        assertThat(subtask.isCompleted()).isFalse();
    }
}
