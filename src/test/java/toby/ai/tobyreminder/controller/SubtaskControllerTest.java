package toby.ai.tobyreminder.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import toby.ai.tobyreminder.domain.ReminderList;
import toby.ai.tobyreminder.dto.request.ReminderRequest;
import toby.ai.tobyreminder.dto.request.SubtaskRequest;
import toby.ai.tobyreminder.repository.ReminderListRepository;
import toby.ai.tobyreminder.repository.ReminderRepository;
import toby.ai.tobyreminder.repository.SubtaskRepository;
import toby.ai.tobyreminder.service.ports.in.ReminderService;
import toby.ai.tobyreminder.service.ports.in.SubtaskService;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SubtaskControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
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
    @DisplayName("POST /api/reminders/{reminderId}/subtasks")
    class Create {

        @Test
        @DisplayName("서브태스크를 생성하면 201과 생성된 서브태스크를 반환한다")
        void create_returns201() throws Exception {
            var request = new SubtaskRequest("서브태스크1");

            mockMvc.perform(post("/api/reminders/{reminderId}/subtasks", reminderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("서브태스크1"))
                    .andExpect(jsonPath("$.completed").value(false));
        }

        @Test
        @DisplayName("존재하지 않는 reminderId로 생성하면 404를 반환한다")
        void create_invalidReminderId_returns404() throws Exception {
            mockMvc.perform(post("/api/reminders/{reminderId}/subtasks", 99999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new SubtaskRequest("서브태스크"))))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /api/reminders/{reminderId}/subtasks/{subtaskId}/complete")
    class ToggleComplete {

        @Test
        @DisplayName("완료 토글하면 204를 반환한다")
        void toggleComplete_returns204() throws Exception {
            var created = subtaskService.create(reminderId, new SubtaskRequest("서브태스크"));

            mockMvc.perform(patch("/api/reminders/{reminderId}/subtasks/{subtaskId}/complete",
                            reminderId, created.getId()))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("DELETE /api/reminders/{reminderId}/subtasks/{subtaskId}")
    class Delete {

        @Test
        @DisplayName("서브태스크를 삭제하면 204를 반환한다")
        void delete_returns204() throws Exception {
            var created = subtaskService.create(reminderId, new SubtaskRequest("서브태스크"));

            mockMvc.perform(delete("/api/reminders/{reminderId}/subtasks/{subtaskId}",
                            reminderId, created.getId()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("존재하지 않는 서브태스크 삭제 시 404를 반환한다")
        void delete_notFound_returns404() throws Exception {
            mockMvc.perform(delete("/api/reminders/{reminderId}/subtasks/{subtaskId}",
                            reminderId, 99999L))
                    .andExpect(status().isNotFound());
        }
    }
}
