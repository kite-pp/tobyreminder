package toby.ai.tobyreminder.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reminder_lists")
@Getter
@Setter
@NoArgsConstructor
public class ReminderList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;

    private String icon;
    private boolean isDefault;

    @Column(nullable = false)
    private int sortOrder;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    public ReminderList(String name, String color, boolean isDefault, int sortOrder) {
        this.name = name;
        this.color = color;
        this.isDefault = isDefault;
        this.sortOrder = sortOrder;
        var now =  LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    public void update(String name, String color){
        this.name = name;
        this.color = color;
        this.updatedAt = LocalDateTime.now();
    }
}
