package toby.ai.tobyreminder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toby.ai.tobyreminder.domain.Reminder;

import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByListIdOrderByCompletedAscSortOrderAsc(Long listId);
}
