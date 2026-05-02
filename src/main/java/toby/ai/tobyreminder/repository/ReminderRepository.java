package toby.ai.tobyreminder.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import toby.ai.tobyreminder.domain.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
}
