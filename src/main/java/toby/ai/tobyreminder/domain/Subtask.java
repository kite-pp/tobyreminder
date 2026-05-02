package toby.ai.tobyreminder.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subtasks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subtask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reminder_id")
    private Reminder reminder;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean completed = false;

    @Column(nullable = false)
    private int sortOrder;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public Subtask(Reminder reminder, String title, int sortOrder) {
        this.reminder = reminder;
        this.title = title;
        this.sortOrder = sortOrder;
        this.createdAt = LocalDateTime.now();
    }

    public void toggleComplete() {
        this.completed = !this.completed;
    }
}
