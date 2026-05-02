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
import tools.jackson.databind.ObjectMapper;
import toby.ai.tobyreminder.dto.request.ReminderListRequest;
import toby.ai.tobyreminder.repository.ReminderListRepository;
import toby.ai.tobyreminder.service.ports.in.ReminderListService;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReminderListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReminderListService reminderListService;

    @Autowired
    private ReminderListRepository reminderListRepository;

    @BeforeEach
    void setUp() {
        reminderListRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /api/lists")
    class FindAll {

        @Test
        @DisplayName("전체 목록을 sortOrder 오름차순으로 반환한다")
        void findAll_returnsSortedList() throws Exception {
            reminderListService.create(new ReminderListRequest("업무", "#007AFF", null, false));
            reminderListService.create(new ReminderListRequest("개인", "#FF9500", null, false));

            mockMvc.perform(get("/api/lists"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name").value("업무"))
                    .andExpect(jsonPath("$[1].name").value("개인"));
        }

        @Test
        @DisplayName("목록이 없으면 빈 배열을 반환한다")
        void findAll_returnsEmptyArray() throws Exception {
            mockMvc.perform(get("/api/lists"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/lists/{id}")
    class FindById {

        @Test
        @DisplayName("존재하는 id로 조회하면 200과 목록을 반환한다")
        void findById_returnsResponse() throws Exception {
            var created = reminderListService.create(new ReminderListRequest("업무", "#007AFF", null, false));

            mockMvc.perform(get("/api/lists/{id}", created.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(created.getId()))
                    .andExpect(jsonPath("$.name").value("업무"))
                    .andExpect(jsonPath("$.color").value("#007AFF"));
        }

        @Test
        @DisplayName("존재하지 않는 id로 조회하면 404를 반환한다")
        void findById_notFound_returns404() throws Exception {
            mockMvc.perform(get("/api/lists/{id}", 99999L))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("99999")));
        }
    }

    @Nested
    @DisplayName("POST /api/lists")
    class Create {

        @Test
        @DisplayName("목록을 생성하면 201과 생성된 목록을 반환한다")
        void create_returns201() throws Exception {
            var request = new ReminderListRequest("업무", "#007AFF", null, false);

            mockMvc.perform(post("/api/lists")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value("업무"))
                    .andExpect(jsonPath("$.color").value("#007AFF"))
                    .andExpect(jsonPath("$.sortOrder").value(0))
                    .andExpect(jsonPath("$.id").isNotEmpty());
        }
    }

    @Nested
    @DisplayName("PUT /api/lists/{id}")
    class Update {

        @Test
        @DisplayName("목록을 수정하면 200과 수정된 목록을 반환한다")
        void update_returns200() throws Exception {
            var created = reminderListService.create(new ReminderListRequest("업무", "#007AFF", null, false));
            var request = new ReminderListRequest("개인", "#FF9500", null, false);

            mockMvc.perform(put("/api/lists/{id}", created.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("개인"))
                    .andExpect(jsonPath("$.color").value("#FF9500"));
        }

        @Test
        @DisplayName("존재하지 않는 id로 수정하면 404를 반환한다")
        void update_notFound_returns404() throws Exception {
            var request = new ReminderListRequest("개인", "#FF9500", null, false);

            mockMvc.perform(put("/api/lists/{id}", 99999L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/lists/{id}")
    class Delete {

        @Test
        @DisplayName("목록을 삭제하면 204를 반환한다")
        void delete_returns204() throws Exception {
            var created = reminderListService.create(new ReminderListRequest("업무", "#007AFF", null, false));

            mockMvc.perform(delete("/api/lists/{id}", created.getId()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("존재하지 않는 id로 삭제하면 404를 반환한다")
        void delete_notFound_returns404() throws Exception {
            mockMvc.perform(delete("/api/lists/{id}", 99999L))
                    .andExpect(status().isNotFound());
        }
    }
}
