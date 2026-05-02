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
import toby.ai.tobyreminder.repository.ReminderListRepository;
import toby.ai.tobyreminder.repository.ReminderRepository;
import toby.ai.tobyreminder.service.ports.in.ReminderService;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReminderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ReminderService reminderService;
    @Autowired private ReminderRepository reminderRepository;
    @Autowired private ReminderListRepository reminderListRepository;

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
    @DisplayName("GET /api/reminders?listId=")
    class FindByListId {

        @Test
        @DisplayName("listId에 해당하는 리마인더 목록을 반환한다")
        void findByListId_returnsList() throws Exception {
            reminderService.create(new ReminderRequest("할일1", null, testList.getId()));
            reminderService.create(new ReminderRequest("할일2", null, testList.getId()));

            mockMvc.perform(get("/api/reminders").param("listId", testList.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("파라미터 없이 요청하면 400을 반환한다")
        void findReminders_noParam_returns400() throws Exception {
            mockMvc.perform(get("/api/reminders"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/reminders?smart=")
    class FindBySmart {

        @Test
        @DisplayName("smart=all 은 미완료 리마인더를 반환한다")
        void findBySmart_all_returnsIncomplete() throws Exception {
            var r1 = reminderService.create(new ReminderRequest("할일1", null, testList.getId()));
            reminderService.create(new ReminderRequest("할일2", null, testList.getId()));
            reminderService.toggleComplete(r1.getId());

            mockMvc.perform(get("/api/reminders").param("smart", "all"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("smart=completed 는 완료된 리마인더를 반환한다")
        void findBySmart_completed() throws Exception {
            var r1 = reminderService.create(new ReminderRequest("할일1", null, testList.getId()));
            reminderService.toggleComplete(r1.getId());

            mockMvc.perform(get("/api/reminders").param("smart", "completed"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }

        @Test
        @DisplayName("알 수 없는 smart 타입은 400을 반환한다")
        void findBySmart_unknownType_returns400() throws Exception {
            mockMvc.perform(get("/api/reminders").param("smart", "unknown"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/reminders/count")
    class GetCount {

        @Test
        @DisplayName("카운트 정보를 반환한다")
        void getCount_returnsCount() throws Exception {
            var r1 = reminderService.create(new ReminderRequest("할일1", null, testList.getId()));
            reminderService.create(new ReminderRequest("할일2", null, testList.getId()));
            reminderService.toggleComplete(r1.getId());

            mockMvc.perform(get("/api/reminders/count"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.all").value(1))
                    .andExpect(jsonPath("$.completed").value(1));
        }
    }

    @Nested
    @DisplayName("POST /api/reminders")
    class Create {

        @Test
        @DisplayName("리마인더를 생성하면 201과 생성된 리마인더를 반환한다")
        void create_returns201() throws Exception {
            var request = new ReminderRequest("할일", "메모", testList.getId());

            mockMvc.perform(post("/api/reminders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.title").value("할일"))
                    .andExpect(jsonPath("$.notes").value("메모"))
                    .andExpect(jsonPath("$.completed").value(false));
        }
    }

    @Nested
    @DisplayName("PUT /api/reminders/{id}")
    class Update {

        @Test
        @DisplayName("리마인더를 수정하면 200과 수정된 내용을 반환한다")
        void update_returns200() throws Exception {
            var created = reminderService.create(new ReminderRequest("원래 제목", null, testList.getId()));
            var request = new ReminderRequest("새 제목", "새 메모", testList.getId());

            mockMvc.perform(put("/api/reminders/{id}", created.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("새 제목"))
                    .andExpect(jsonPath("$.notes").value("새 메모"));
        }

        @Test
        @DisplayName("존재하지 않는 id로 수정하면 404를 반환한다")
        void update_notFound_returns404() throws Exception {
            mockMvc.perform(put("/api/reminders/{id}", 99999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(new ReminderRequest("제목", null, testList.getId()))))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /api/reminders/{id}/complete")
    class ToggleComplete {

        @Test
        @DisplayName("완료 토글하면 204를 반환한다")
        void toggleComplete_returns204() throws Exception {
            var created = reminderService.create(new ReminderRequest("할일", null, testList.getId()));

            mockMvc.perform(patch("/api/reminders/{id}/complete", created.getId()))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    @DisplayName("DELETE /api/reminders/{id}")
    class Delete {

        @Test
        @DisplayName("리마인더를 삭제하면 204를 반환한다")
        void delete_returns204() throws Exception {
            var created = reminderService.create(new ReminderRequest("할일", null, testList.getId()));

            mockMvc.perform(delete("/api/reminders/{id}", created.getId()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("존재하지 않는 id로 삭제하면 404를 반환한다")
        void delete_notFound_returns404() throws Exception {
            mockMvc.perform(delete("/api/reminders/{id}", 99999L))
                    .andExpect(status().isNotFound());
        }
    }
}
