# TobyReminder — 코딩 관례 (CLAUDE.md)

## 프로젝트 개요

- **Backend**: Spring Boot 4.0.6 / Java 25 / Gradle Kotlin DSL
- **Frontend**: Next.js (App Router) / TypeScript / Tailwind CSS
- **DB**: H2 in-memory (`jdbc:h2:mem:tobyreminder`)
- **Spec**: `spec.md` / **개발 계획**: `plan.md` / **작업 목록**: `task.md`

---

## 1. 테스트

### 기능과 테스트는 항상 함께 작성한다

기능을 추가하거나 수정할 때 반드시 해당 기능을 검증하는 테스트를 함께 작성한다.

### 테스트 레이어별 방식

| 레이어 | 방식 | 비고 |
|--------|------|------|
| Domain 엔티티 | 순수 단위 테스트 | Spring/JPA 컨텍스트 없음 |
| Service | `@ExtendWith(MockitoExtension.class)` | Repository Mock 처리 |
| Repository | `@DataJpaTest` | Spring Boot 4 패키지 주의 |
| Controller | `@WebMvcTest` 슬라이스 테스트 | |

### Spring Boot 4 테스트 패키지 경로 (기존과 다름)

```java
// DataJpaTest
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

// TestEntityManager
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

// WebMvcTest
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
```

### 테스트 구조

```java
class ReminderTest {

    @Nested
    @DisplayName("생성자")
    class Constructor { ... }

    @Nested
    @DisplayName("update")
    class Update { ... }
}
```

- `@Nested` + `@DisplayName`으로 그룹화한다
- 테스트 메서드명: `기능_상황` 형식 (예: `builder_setsFields`, `update_changesFields`)
- AssertJ (`assertThat`) 사용

---

## 2. 도메인 엔티티

### 패키지

```
toby.ai.tobyreminder.domain          ← 엔티티
toby.ai.tobyreminder.domain.enums    ← enum
```

### Jakarta EE 11 (Spring Boot 4 필수)

```java
// ✅
import jakarta.persistence.*;

// ❌ Spring Boot 4에서 동작 안 함
import javax.persistence.*;
```

### date 필드 설정 — @PrePersist 사용 금지

`@PrePersist` 대신 Builder 또는 생성자에서 직접 설정한다.

```java
// ✅ @Builder.Default 방식 (Reminder)
@Builder.Default
private LocalDateTime createdAt = LocalDateTime.now();

// ✅ 커스텀 생성자 방식 (ReminderList)
@Builder
public ReminderList(String name, String color, boolean isDefault, int sortOrder) {
    var now = LocalDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
}

// ❌ 사용 금지
@PrePersist
protected void onCreate() {
    this.createdAt = LocalDateTime.now();
}
```

### Lombok 사용 규칙

```java
// 기본 엔티티 구성 (@Builder.Default 사용 시)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reminder { ... }

// 커스텀 @Builder 생성자가 있을 때 → 클래스 레벨 @Builder, @AllArgsConstructor 제거
@Getter
@Setter
@NoArgsConstructor
public class ReminderList {
    @Builder
    public ReminderList(String name, ...) { ... }
}
```

- 클래스 레벨 `@Builder`와 생성자 레벨 `@Builder`를 동시에 쓰지 않는다 (충돌)
- `@Setter`는 필요한 경우에만 클래스 레벨에 선언한다
- JPA 엔티티는 `@NoArgsConstructor` 필수

### 연관관계

```java
// 모든 @ManyToOne은 LAZY 로딩
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "list_id")
private ReminderList list;
```

### 도메인 메서드

상태 변경 로직은 엔티티 내부 메서드로 캡슐화한다.

```java
// ✅
reminder.toggleComplete();
reminder.update(title, notes, dueDate, priority);

// ❌ 외부에서 setter 직접 호출
reminder.setCompleted(true);
reminder.setCompletedAt(LocalDateTime.now());
```

---

## 3. 패키지 구조 (Backend)

```
toby.ai.tobyreminder
├── domain/               엔티티 + enums
├── repository/           JpaRepository 인터페이스
├── service/              비즈니스 로직
├── controller/           REST 컨트롤러
├── dto/
│   ├── request/          입력 DTO
│   └── response/         출력 DTO
└── config/               설정 클래스 (CORS 등)
```

---

## 4. REST API

```java
// 응답 코드
GET    → 200 OK
POST   → 201 Created
PUT    → 200 OK
PATCH  → 200 OK
DELETE → 204 No Content

// 에러 — 없는 리소스
throw new EntityNotFoundException("Reminder not found with id: " + id);
// import jakarta.persistence.EntityNotFoundException;
```

---

## 5. 빌드 / 의존성

### Spring Boot 4 의존성 이름 (기존과 다름)

```kotlin
// ✅ Spring Boot 4
implementation("org.springframework.boot:spring-boot-starter-webmvc")
implementation("org.springframework.boot:spring-boot-h2console")
testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

// ❌ Spring Boot 3 방식 (동작 안 함)
implementation("org.springframework.boot:spring-boot-starter-web")
testImplementation("org.springframework.boot:spring-boot-starter-test")
```

---

## 6. Git

- 기능 단위로 커밋한다
- 커밋 메시지: 영어, 명령형 (`Add`, `Fix`, `Update`, `Remove`)
- 작업 완료 후 `task.md` 체크박스를 업데이트한다
- 변경이 있으면 `spec.md` / `plan.md` 도 함께 갱신한다
