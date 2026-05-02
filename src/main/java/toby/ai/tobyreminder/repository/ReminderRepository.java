package toby.ai.tobyreminder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toby.ai.tobyreminder.domain.Reminder;

import java.time.LocalDateTime;
import java.util.List;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    List<Reminder> findByListIdOrderByCompletedAscSortOrderAsc(Long listId);

    List<Reminder> findByDueDateBetweenAndCompletedFalse(LocalDateTime start, LocalDateTime end);

    List<Reminder> findByDueDateNotNullAndCompletedFalse();

    List<Reminder> findByCompletedFalse();

    List<Reminder> findByFlaggedTrueAndCompletedFalse();

    List<Reminder> findByCompletedTrue();

    long countByDueDateBetweenAndCompletedFalse(LocalDateTime start, LocalDateTime end);

    long countByDueDateNotNullAndCompletedFalse();

    long countByCompletedFalse();

    long countByFlaggedTrueAndCompletedFalse();

    long countByCompletedTrue();
}
