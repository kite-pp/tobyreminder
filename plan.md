# TobyReminder — 개발 계획 (Development Plan)

> spec.md 기반. 단순한 것부터 점진적으로 기능을 추가하는 방식으로 구성.
> **테스트 원칙**: 기능 추가/수정 시 해당 기능을 검증하는 테스트를 반드시 함께 작성한다.
> - 도메인 엔티티: 순수 단위 테스트 (Spring/JPA 컨텍스트 없음)
> - Service: `@ExtendWith(MockitoExtension.class)` Mock 테스트
> - Controller: `@WebMvcTest` 슬라이스 테스트 (Spring Boot 4: `org.springframework.boot.webmvc.test.autoconfigure`)
> - Spring Boot 4 `@DataJpaTest` 패키지: `org.springframework.boot.data.jpa.test.autoconfigure`

---

## 기술 스택 요약

### Backend
| 항목 | 기술 | 비고 |
|------|------|------|
| Language | Java 25 | LTS |
| Framework | Spring Boot 4.0.6 | Jakarta EE 11 (`jakarta.*`) |
| ORM | Spring Data JPA + Hibernate 7 | |
| DB | H2 in-memory | `jdbc:h2:mem:tobyreminder` |
| Build | Gradle 9.4 (Kotlin DSL) | `build.gradle.kts` |
| API | REST (JSON) | 포트 8080 |
| CORS | `@CrossOrigin` or `WebMvcConfigurer` | Next.js 3000 허용 |

### Frontend
| 항목 | 기술 | 비고 |
|------|------|------|
| Framework | Next.js (App Router) | TypeScript |
| Styling | Tailwind CSS v4 | Apple HIG 색상 시스템 |
| 상태관리 | TanStack Query v5 | 서버 상태 |
| 클라이언트 상태 | Zustand | 선택된 목록/리마인더 |
| HTTP | fetch (Next.js native) | |
| 아이콘 | Lucide React | SF Symbols 대응 |
| 드래그앤드롭 | dnd-kit | Phase 6 |
| 개발 포트 | 3000 | |

---

## Phase 0 — 프로젝트 기반 구성

**목표**: 백엔드 도메인 골격 + 프론트엔드 프로젝트 초기화. 화면에 "Hello" 뜨면 완료.

> **테스트 원칙**: 모든 도메인 엔티티는 순수 단위 테스트(Spring/JPA 컨텍스트 없음)로 검증한다.

### Backend 작업

#### 0-1. 도메인 모델 생성 (`src/main/java/toby/ai/tobyreminder/`) ✅ 일부 완료

```
domain/                                         ← 패키지명 domain 사용 (entity 아님)
  ReminderList.java     ✅ 완료
    — id/name/color/icon/isDefault/sortOrder/createdAt/updatedAt
    — 커스텀 @Builder 생성자(name,color,isDefault,sortOrder)에서 createdAt/updatedAt 설정
    — update(name, color): name/color/updatedAt 갱신
  Reminder.java         ✅ 완료
    — id/list(ManyToOne)/title/notes/dueDate/priority/flagged/completed/completedAt/sortOrder/createdAt
    — @Builder.Default createdAt = LocalDateTime.now()  (@PrePersist 사용 안 함)
    — update(title,notes,dueDate,priority) / toggleComplete() / toggleFlag()
  Subtask.java          ⬜ 미완료
    — id/reminder(ManyToOne)/title/completed/sortOrder/createdAt
  enums/
    Priority.java       ✅ NONE, LOW, MEDIUM, HIGH
```

- `jakarta.persistence.*` 사용 (Spring Boot 4 / Jakarta EE 11 필수)
- date 자동 설정: `@PrePersist` 대신 Builder/생성자에서 `LocalDateTime.now()` 직접 할당
- `Reminder ↔ ReminderList`: `@ManyToOne(fetch = LAZY)`
- `Subtask ↔ Reminder`: `@ManyToOne(fetch = LAZY)`

#### 0-2. Repository ✅ 일부 완료

```
repository/
  ReminderListRepository.java   ⬜ — JpaRepository<ReminderList, Long>
  ReminderRepository.java       ✅ — JpaRepository<Reminder, Long>
  SubtaskRepository.java        ⬜ — JpaRepository<Subtask, Long>
```

#### 0-1-T. 도메인 단위 테스트 (`src/test/java/toby/ai/tobyreminder/domain/`) ✅ 일부 완료

```
ReminderTest.java       ✅ — 생성자/update/createdAt 자동 등록 (9개 케이스)
ReminderListTest.java   ✅ — 생성자/update/createdAt+updatedAt 자동 등록 (8개 케이스)
SubtaskTest.java        ⬜ — Subtask 구현 후 작성
```

#### 0-3. CORS 설정

```java
// config/WebConfig.java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET","POST","PUT","PATCH","DELETE");
    }
}
```

#### 0-4. application.properties 정리

```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.datasource.url=jdbc:h2:mem:tobyreminder;DB_CLOSE_DELAY=-1
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

### Frontend 작업

#### 0-5. Next.js 프로젝트 생성

```bash
npx create-next-app@latest tobyreminder-web \
  --typescript --tailwind --eslint --app --src-dir \
  --no-import-alias
cd tobyreminder-web
npm install @tanstack/react-query zustand lucide-react
```

#### 0-6. 기본 레이아웃 구조

```
src/
  app/
    layout.tsx          — 전체 레이아웃 (사이드바 + 콘텐츠 영역)
    page.tsx            — 기본 리다이렉트 → /lists/today
  components/
    layout/
      Sidebar.tsx       — 사이드바 껍데기
      MainContent.tsx   — 메인 콘텐츠 껍데기
  lib/
    api.ts              — fetch 기본 설정 (baseURL: http://localhost:8080)
    queryClient.ts      — TanStack Query 클라이언트
```

#### 0-7. Tailwind 색상 토큰 설정 (`tailwind.config.ts`)

```ts
colors: {
  apple: {
    bg: '#F2F2F7',
    sidebar: 'rgba(255,255,255,0.8)',
    text: '#1C1C1E',
    gray: '#6C6C70',
    separator: '#E5E5EA',
    blue: '#007AFF',
    red: '#FF3B30',
    orange: '#FF9500',
    green: '#34C759',
    purple: '#AF52DE',
    darkGray: '#3A3A3C',
  }
}
```

**완료 기준**: `./gradlew bootRun` + `npm run dev` 동시 실행, 브라우저에서 레이아웃 틀 확인

---

## Phase 1 — 목록(List) CRUD

**목표**: 사이드바에서 목록을 추가·수정·삭제할 수 있다.

### Backend

#### 1-1. DTO

```
dto/
  request/ReminderListRequest.java    — name, color, icon
  response/ReminderListResponse.java  — id, name, color, icon, sortOrder, reminderCount
```

#### 1-2. Service: `ReminderListService`

| 메서드 | 설명 |
|--------|------|
| `findAll()` | 전체 목록 (sortOrder ASC) |
| `create(request)` | 목록 생성, sortOrder = max+1 |
| `update(id, request)` | 이름/색상/아이콘 수정 |
| `delete(id)` | 목록 삭제 (cascade: 포함된 Reminder도 삭제) |

#### 1-3. Controller: `ReminderListController` (`/api/lists`)

```
GET    /api/lists          → 전체 조회
POST   /api/lists          → 생성
PUT    /api/lists/{id}     → 수정
DELETE /api/lists/{id}     → 삭제
```

### Frontend

#### 1-4. API 훅

```ts
// hooks/useLists.ts
useListsQuery()          — GET /api/lists
useCreateListMutation()  — POST /api/lists
useUpdateListMutation()  — PUT /api/lists/:id
useDeleteListMutation()  — DELETE /api/lists/:id
```

#### 1-5. 컴포넌트

```
components/sidebar/
  MyLists.tsx          — 목록 리스트 렌더링
  ListItem.tsx         — 색상 점 + 이름 + 카운트 + 우클릭 메뉴
  NewListButton.tsx    — "+ 목록 추가" 버튼
  ListFormModal.tsx    — 목록 생성/수정 모달 (이름 + 색상 8종 선택)
```

**완료 기준**: 목록 추가 → 사이드바 반영, 삭제 → 즉시 제거

---

## Phase 2 — 리마인더 기본 CRUD

**목표**: 목록을 선택하면 리마인더를 보고, 추가하고, 완료 토글할 수 있다.

### Backend

#### 2-1. DTO

```
dto/request/ReminderRequest.java     — title, notes, listId
dto/response/ReminderResponse.java   — id, title, notes, completed, completedAt, createdAt, listId
```

#### 2-2. Service: `ReminderService`

| 메서드 | 설명 |
|--------|------|
| `findByListId(listId)` | 목록별 조회 (미완료 우선, sortOrder ASC) |
| `create(request)` | 리마인더 생성 |
| `update(id, request)` | 제목/메모 수정 |
| `toggleComplete(id)` | 완료 토글, completedAt 설정/해제 |
| `delete(id)` | 삭제 |

#### 2-3. Controller: `ReminderController` (`/api/reminders`)

```
GET    /api/reminders?listId=     → 목록별 조회
POST   /api/reminders             → 생성
PUT    /api/reminders/{id}        → 수정
PATCH  /api/reminders/{id}/complete → 완료 토글
DELETE /api/reminders/{id}        → 삭제
```

### Frontend

#### 2-4. API 훅 (`hooks/useReminders.ts`)

```ts
useRemindersQuery(listId)
useCreateReminderMutation()
useUpdateReminderMutation()
useToggleCompleteMutation()
useDeleteReminderMutation()
```

#### 2-5. 컴포넌트

```
components/reminder/
  ReminderList.tsx       — 리마인더 목록 컨테이너
  ReminderRow.tsx        — 원형 체크박스 + 제목 + 완료 토글
  AddReminderInput.tsx   — 하단 인라인 입력창 (Enter → 저장, Esc → 취소)
```

#### 2-6. 리마인더 행 스타일

- 체크박스: `rounded-full border-2` (목록 accent color), 완료 시 fill + 체크
- 완료 텍스트: `line-through opacity-40`
- 행 hover: `bg-apple-bg`

**완료 기준**: 목록 클릭 → 리마인더 표시, Enter로 추가, 체크박스로 완료 토글

---

## Phase 3 — 스마트 목록 & 콘텐츠 라우팅

**목표**: 오늘/예정/전체/플래그됨/완료됨 스마트 목록 카드가 동작한다.

### Backend

#### 3-1. 스마트 목록 쿼리 (`ReminderRepository`)

```java
// 오늘
findByDueDateBetweenAndCompletedFalse(startOfDay, endOfDay)
// 예정
findByDueDateNotNullAndCompletedFalse()
// 전체
findByCompletedFalse()
// 플래그됨
findByFlaggedTrueAndCompletedFalse()
// 완료됨
findByCompletedTrue(Sort.by("completedAt").descending())
```

#### 3-2. Controller 확장

```
GET /api/reminders?smart=today|scheduled|all|flagged|completed
```

#### 3-3. 카운트 API

```
GET /api/reminders/count   → { today, scheduled, all, flagged, completed }
```

### Frontend

#### 3-4. 라우팅 구조

```
app/
  (main)/
    layout.tsx                 — 사이드바 포함 레이아웃
    smart/[type]/page.tsx      — 스마트 목록 페이지 (today|scheduled|all|flagged|completed)
    lists/[id]/page.tsx        — 커스텀 목록 페이지
```

#### 3-5. 스마트 목록 카드 (`components/sidebar/SmartLists.tsx`)

```
2열 그리드, 각 카드:
- rounded-2xl, 배경 accent tint (opacity-15)
- 아이콘 (Lucide): 좌상단, accent color
- 이름: 하단 좌측, 15px semibold
- 카운트: 하단 우측, 28px bold, accent color
```

| 스마트 목록 | 아이콘 | 색상 |
|-------------|--------|------|
| 오늘 | `CalendarDays` | `#007AFF` |
| 예정 | `Calendar` | `#FF3B30` |
| 전체 | `Tray` | `#3A3A3C` |
| 플래그됨 | `Flag` | `#FF9500` |
| 완료됨 | `CheckCircle` | `#8E8E93` |

**완료 기준**: 스마트 목록 카드 클릭 시 해당 리마인더 필터링 표시, 카운트 정확

---

## Phase 4 — 상세 속성 (마감일 · 우선순위 · 플래그)

**목표**: 리마인더에 마감일, 우선순위, 플래그를 설정할 수 있다. 우측 상세 패널 등장.

### Backend

#### 4-1. DTO 확장

```java
// ReminderRequest에 추가
LocalDateTime dueDate;
Priority priority;   // NONE, LOW, MEDIUM, HIGH
boolean flagged;
```

#### 4-2. 토글 API 추가

```
PATCH /api/reminders/{id}/flag        → 플래그 토글
PATCH /api/reminders/{id}/priority    → 우선순위 변경 (body: { priority })
```

### Frontend

#### 4-3. 상세 패널 (`components/reminder/DetailPanel.tsx`)

- 우측에서 슬라이드인 (320px, `transition: transform 200ms ease-out`)
- 내부 구성:
  - 제목 편집 (`<input>`, 자동 저장 onBlur)
  - 메모 편집 (`<textarea>`, 자동 저장 onBlur)
  - 마감일 선택 (native `<input type="datetime-local">`)
  - 플래그 토글 버튼
  - 우선순위 선택 (None/낮음/중간/높음 드롭다운)
  - 목록 변경 드롭다운

#### 4-4. 리마인더 행 메타 표시

```
[○] 제목                                    [⚑] [···]
    메모 · 내일 오후 3시 · !!!
```
- 마감일 색상: 지남=`text-apple-red`, 오늘=`text-apple-blue`, 이후=`text-apple-gray`
- 우선순위: `!`(낮음) / `!!`(중간) / `!!!`(높음)
- 플래그: `Flag` 아이콘, 활성 시 `text-apple-orange`

**완료 기준**: 리마인더 클릭 → 우측 패널, 마감일/우선순위/플래그 설정 후 목록에 반영

---

## Phase 5 — 서브태스크

**목표**: 리마인더 하위에 서브태스크를 추가하고 완료 토글할 수 있다.

### Backend

#### 5-1. DTO

```
dto/request/SubtaskRequest.java     — title
dto/response/SubtaskResponse.java   — id, title, completed, sortOrder
```

#### 5-2. Service: `SubtaskService`

```
create(reminderId, request)
toggleComplete(reminderId, subtaskId)
delete(reminderId, subtaskId)
```

#### 5-3. Controller: `SubtaskController`

```
POST   /api/reminders/{id}/subtasks
PATCH  /api/reminders/{id}/subtasks/{subtaskId}/complete
DELETE /api/reminders/{id}/subtasks/{subtaskId}
```

#### 5-4. ReminderResponse에 subtasks 포함

```java
List<SubtaskResponse> subtasks;
```

### Frontend

#### 5-5. 컴포넌트

```
components/reminder/
  SubtaskList.tsx        — 서브태스크 목록 (리마인더 행 하위)
  SubtaskRow.tsx         — 들여쓰기된 원형 체크박스 + 제목
  AddSubtaskInput.tsx    — 상세 패널 내 서브태스크 추가 입력창
```

- 리마인더 행에서 서브태스크 접기/펼치기 (chevron 아이콘)
- `Tab` 키 → 인라인 서브태스크 입력창 전환

**완료 기준**: 서브태스크 추가·완료·삭제, 리마인더 행에서 서브태스크 카운트 표시

---

## Phase 6 — 검색 & 드래그앤드롭 정렬

**목표**: 검색으로 리마인더를 찾고, 드래그앤드롭으로 순서를 바꿀 수 있다.

### 검색

#### 6-1. Backend

```
GET /api/reminders?q=검색어    → 제목+메모 LIKE 검색
```

```java
// ReminderRepository
findByTitleContainingIgnoreCaseOrNotesContainingIgnoreCase(q, q)
```

#### 6-2. Frontend

- 사이드바 상단 검색창 (`components/sidebar/SearchInput.tsx`)
- 300ms debounce → API 호출
- 결과를 별도 검색 결과 뷰로 표시

### 드래그앤드롭

#### 6-3. Backend

```
PATCH /api/reminders/order         → body: [{ id, sortOrder }, ...]
PATCH /api/lists/order             → body: [{ id, sortOrder }, ...]
```

#### 6-4. Frontend (`dnd-kit`)

```bash
npm install @dnd-kit/core @dnd-kit/sortable @dnd-kit/utilities
```

- `SortableContext` + `useSortable` 으로 리마인더 행 드래그
- 드롭 완료 시 PATCH 호출로 서버 순서 동기화
- 목록(사이드바)도 동일하게 적용

**완료 기준**: 검색어 입력 시 실시간 필터, 드래그로 순서 변경 후 새로고침해도 유지

---

## Phase 7 — UI 완성도 & 키보드 단축키

**목표**: Apple Reminders와 시각적으로 최대한 유사하게, 키보드로 대부분 조작 가능.

### 7-1. 애니메이션

| 동작 | 구현 |
|------|------|
| 완료 토글 | 체크박스 scale + fill (CSS transition 300ms) → 0.5s 후 `opacity-0` + `height-0` 제거 |
| 상세 패널 | `translate-x-full` → `translate-x-0` (200ms ease-out) |
| 목록 전환 | 콘텐츠 영역 `opacity-0` → `opacity-100` (150ms) |
| 리마인더 추가 | 행 `max-height: 0` → `max-height: 60px` (200ms) |

### 7-2. 키보드 단축키 (`useKeyboard` 커스텀 훅)

| 단축키 | 동작 |
|--------|------|
| `Enter` | 리마인더 추가 인풋 포커스 / 입력 후 저장 |
| `Escape` | 상세 패널 닫기 / 편집 취소 |
| `Space` | 선택 리마인더 완료 토글 |
| `⌘+Backspace` | 선택 리마인더 삭제 |
| `Tab` | 서브태스크 입력 전환 |

### 7-3. 완료 항목 관리

- 콘텐츠 상단 "완료됨 n개 보기" 토글 버튼
- "완료 항목 전체 삭제" 버튼 → 확인 다이얼로그

```
DELETE /api/reminders/completed?listId=   → 완료 항목 일괄 삭제
```

### 7-4. 빈 상태 (Empty State)

- 리마인더가 없을 때: 중앙에 아이콘 + "리마인더 없음" 메시지
- 검색 결과 없을 때: "검색 결과 없음"

**완료 기준**: 애니메이션 자연스럽고, 키보드만으로 리마인더 추가·완료·삭제 가능

---

## 개발 순서 요약

```
Phase 0  프로젝트 기반 구성          (Backend 도메인 + Frontend 초기화)
  │
Phase 1  목록(List) CRUD            (사이드바에 목록 추가/수정/삭제)
  │
Phase 2  리마인더 기본 CRUD         (제목 추가, 완료 토글)
  │
Phase 3  스마트 목록                (오늘/예정/전체/플래그/완료)
  │
Phase 4  상세 속성                  (마감일, 우선순위, 플래그, 상세 패널)
  │
Phase 5  서브태스크                 (하위 태스크 CRUD)
  │
Phase 6  검색 & 정렬               (실시간 검색, 드래그앤드롭)
  │
Phase 7  UI 완성도                  (애니메이션, 키보드 단축키)
```

---

## 디렉토리 구조 최종

### Backend (`tobyreminder/src/main/java/toby/ai/tobyreminder/`)

```
config/
  WebConfig.java
domain/
  ReminderList.java
  Reminder.java
  Subtask.java
  enums/Priority.java
repository/
  ReminderListRepository.java
  ReminderRepository.java
  SubtaskRepository.java
service/
  ReminderListService.java
  ReminderService.java
  SubtaskService.java
controller/
  ReminderListController.java
  ReminderController.java
  SubtaskController.java
dto/
  request/
    ReminderListRequest.java
    ReminderRequest.java
    SubtaskRequest.java
  response/
    ReminderListResponse.java
    ReminderResponse.java
    SubtaskResponse.java
    CountResponse.java
```

### Frontend (`tobyreminder-web/src/`)

```
app/
  layout.tsx
  (main)/
    layout.tsx
    smart/[type]/page.tsx
    lists/[id]/page.tsx
components/
  layout/
    Sidebar.tsx
    MainContent.tsx
  sidebar/
    SmartLists.tsx
    SmartListCard.tsx
    MyLists.tsx
    ListItem.tsx
    NewListButton.tsx
    ListFormModal.tsx
    SearchInput.tsx
  reminder/
    ReminderList.tsx
    ReminderRow.tsx
    AddReminderInput.tsx
    DetailPanel.tsx
    SubtaskList.tsx
    SubtaskRow.tsx
hooks/
  useLists.ts
  useReminders.ts
  useSubtasks.ts
  useKeyboard.ts
lib/
  api.ts
  queryClient.ts
types/
  index.ts
```
