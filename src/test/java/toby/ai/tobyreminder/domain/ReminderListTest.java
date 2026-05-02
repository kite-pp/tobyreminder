package toby.ai.tobyreminder.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderListTest {

    @Nested
    @DisplayName("생성자")
    class Constructor {

        @Test
        @DisplayName("Builder로 생성 시 지정한 필드값이 설정된다")
        void builder_setsFields() {
            ReminderList list = ReminderList.builder()
                    .name("업무")
                    .color("#007AFF")
                    .isDefault(false)
                    .sortOrder(1)
                    .build();

            assertThat(list.getName()).isEqualTo("업무");
            assertThat(list.getColor()).isEqualTo("#007AFF");
            assertThat(list.isDefault()).isFalse();
            assertThat(list.getSortOrder()).isEqualTo(1);
        }

        @Test
        @DisplayName("isDefault=true로 생성 시 기본 목록으로 설정된다")
        void builder_defaultList() {
            ReminderList list = ReminderList.builder()
                    .name("기본 목록")
                    .color("#8E8E93")
                    .isDefault(true)
                    .sortOrder(0)
                    .build();

            assertThat(list.isDefault()).isTrue();
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("update() 호출 시 name과 color가 변경된다")
        void update_changesNameAndColor() {
            ReminderList list = ReminderList.builder()
                    .name("업무")
                    .color("#007AFF")
                    .isDefault(false)
                    .sortOrder(1)
                    .build();

            list.update("개인", "#FF9500");

            assertThat(list.getName()).isEqualTo("개인");
            assertThat(list.getColor()).isEqualTo("#FF9500");
        }

        @Test
        @DisplayName("update() 호출 시 updatedAt이 갱신된다")
        void update_refreshesUpdatedAt() {
            ReminderList list = ReminderList.builder()
                    .name("업무")
                    .color("#007AFF")
                    .isDefault(false)
                    .sortOrder(1)
                    .build();
            LocalDateTime beforeUpdate = list.getUpdatedAt();

            list.update("수정된 이름", "#FF3B30");

            assertThat(list.getUpdatedAt()).isAfterOrEqualTo(beforeUpdate);
        }
    }

    @Nested
    @DisplayName("date 자동 등록")
    class AutoDate {

        @Test
        @DisplayName("Builder로 생성하면 createdAt이 현재 시각으로 자동 설정된다")
        void builder_setsCreatedAt() {
            LocalDateTime before = LocalDateTime.now().minusSeconds(1);

            ReminderList list = ReminderList.builder()
                    .name("날짜 테스트")
                    .color("#007AFF")
                    .isDefault(false)
                    .sortOrder(0)
                    .build();

            assertThat(list.getCreatedAt()).isNotNull();
            assertThat(list.getCreatedAt()).isAfter(before);
            assertThat(list.getCreatedAt()).isBefore(LocalDateTime.now().plusSeconds(1));
        }

        @Test
        @DisplayName("Builder로 생성하면 updatedAt이 createdAt과 동일하게 설정된다")
        void builder_setsUpdatedAtSameAsCreatedAt() {
            ReminderList list = ReminderList.builder()
                    .name("날짜 동기화 테스트")
                    .color("#007AFF")
                    .isDefault(false)
                    .sortOrder(0)
                    .build();

            assertThat(list.getUpdatedAt()).isEqualTo(list.getCreatedAt());
        }

        @Test
        @DisplayName("update() 호출 후에도 createdAt은 변경되지 않는다")
        void update_doesNotChangeCreatedAt() {
            ReminderList list = ReminderList.builder()
                    .name("불변 날짜 테스트")
                    .color("#007AFF")
                    .isDefault(false)
                    .sortOrder(0)
                    .build();
            LocalDateTime originalCreatedAt = list.getCreatedAt();

            list.update("수정된 이름", "#FF3B30");

            assertThat(list.getCreatedAt()).isEqualTo(originalCreatedAt);
        }
    }
}
