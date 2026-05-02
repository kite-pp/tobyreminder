package toby.ai.tobyreminder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toby.ai.tobyreminder.domain.Subtask;

import java.util.List;

public interface SubtaskRepository extends JpaRepository<Subtask, Long> {

    List<Subtask> findByReminderIdOrderBySortOrderAsc(Long reminderId);
}
