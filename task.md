# TobyReminder — Task List

> plan.md 기반 세부 작업 목록. 완료 시 `- [x]`로 체크.

---

## Phase 0 — 프로젝트 기반 구성

### Backend

#### 도메인 모델
- [x] `domain/enums/Priority.java` — enum: NONE, LOW, MEDIUM, HIGH
- [x] `domain/ReminderList.java` — id/name/color/icon/isDefault/sortOrder/createdAt/updatedAt, 커스텀 @Builder 생성자, update(name,color)
- [x] `domain/Reminder.java` — id/list(ManyToOne)/title/notes/dueDate/priority/flagged/completed/completedAt/sortOrder/createdAt, @Builder.Default, update()/toggleComplete()/toggleFlag()
- [ ] `domain/Subtask.java` — @Entity, id/reminder(ManyToOne)/title/completed/sortOrder/createdAt

#### Repository
- [ ] `repository/ReminderListRepository.java` — JpaRepository<ReminderList, Long>
- [x] `repository/ReminderRepository.java` — JpaRepository<Reminder, Long>
- [ ] `repository/SubtaskRepository.java` — JpaRepository<Subtask, Long>

#### 설정
- [ ] `config/WebConfig.java` — CORS 설정 (localhost:3000 허용)
- [x] `application.properties` — H2 콘솔, JPA ddl-auto, show-sql 정리

#### 테스트
- [x] `test/domain/ReminderTest.java` — 생성자/update/createdAt 자동 등록 단위 테스트
- [x] `test/domain/ReminderListTest.java` — 생성자/update/createdAt+updatedAt 자동 등록 단위 테스트
- [ ] `test/domain/SubtaskTest.java`

### Frontend

- [ ] Next.js 프로젝트 생성 (`create-next-app@latest`, TypeScript, Tailwind, App Router)
- [ ] 패키지 설치: `@tanstack/react-query`, `zustand`, `lucide-react`
- [ ] `tailwind.config.ts` — apple 색상 토큰 설정 (bg/text/blue/red/orange 등)
- [ ] `lib/api.ts` — fetch 기본 설정 (baseURL: http://localhost:8080)
- [ ] `lib/queryClient.ts` — TanStack Query 클라이언트 생성
- [ ] `app/layout.tsx` — 전체 레이아웃 (사이드바 + 콘텐츠 영역, QueryClientProvider)
- [ ] `app/page.tsx` — /smart/all 로 리다이렉트
- [ ] `components/layout/Sidebar.tsx` — 사이드바 껍데기 (260px, 배경색)
- [ ] `components/layout/MainContent.tsx` — 메인 콘텐츠 껍데기

**[ ] Phase 0 완료 기준: `./gradlew bootRun` + `npm run dev` 동시 실행, 레이아웃 틀 확인**

> **규칙**: 기능 추가/수정 시 해당 기능을 검증하는 단위 테스트를 반드시 함께 작성한다.

---

## Phase 1 — 목록(List) CRUD

### Backend

- [ ] `dto/request/ReminderListRequest.java` — name, color, icon
- [ ] `dto/response/ReminderListResponse.java` — id, name, color, icon, sortOrder, reminderCount
- [ ] `service/ReminderListService.java`
  - [ ] `findAll()` — sortOrder ASC
  - [ ] `create(request)` — sortOrder = max+1
  - [ ] `update(id, request)` — 이름/색상/아이콘 수정
  - [ ] `delete(id)` — cascade 삭제
- [ ] `controller/ReminderListController.java`
  - [ ] `GET /api/lists`
  - [ ] `POST /api/lists`
  - [ ] `PUT /api/lists/{id}`
  - [ ] `DELETE /api/lists/{id}`

### Frontend

- [ ] `types/index.ts` — ReminderList, Reminder, Subtask 타입 정의
- [ ] `hooks/useLists.ts`
  - [ ] `useListsQuery()`
  - [ ] `useCreateListMutation()`
  - [ ] `useUpdateListMutation()`
  - [ ] `useDeleteListMutation()`
- [ ] `components/sidebar/MyLists.tsx` — 목록 리스트 렌더링
- [ ] `components/sidebar/ListItem.tsx` — 색상 점 + 이름 + 카운트 + 우클릭 메뉴
- [ ] `components/sidebar/NewListButton.tsx` — "+ 목록 추가" 버튼
- [ ] `components/sidebar/ListFormModal.tsx` — 이름 입력 + 색상 8종 선택 모달

**[ ] Phase 1 완료 기준: 목록 추가 → 사이드바 즉시 반영, 삭제 → 즉시 제거**

---

## Phase 2 — 리마인더 기본 CRUD

### Backend

- [ ] `dto/request/ReminderRequest.java` — title, notes, listId
- [ ] `dto/response/ReminderResponse.java` — id, title, notes, completed, completedAt, createdAt, listId
- [ ] `service/ReminderService.java`
  - [ ] `findByListId(listId)` — 미완료 우선, sortOrder ASC
  - [ ] `create(request)`
  - [ ] `update(id, request)` — 제목/메모 수정
  - [ ] `toggleComplete(id)` — completedAt 설정/해제
  - [ ] `delete(id)`
- [ ] `controller/ReminderController.java`
  - [ ] `GET /api/reminders?listId=`
  - [ ] `POST /api/reminders`
  - [ ] `PUT /api/reminders/{id}`
  - [ ] `PATCH /api/reminders/{id}/complete`
  - [ ] `DELETE /api/reminders/{id}`

### Frontend

- [ ] `hooks/useReminders.ts`
  - [ ] `useRemindersQuery(listId)`
  - [ ] `useCreateReminderMutation()`
  - [ ] `useUpdateReminderMutation()`
  - [ ] `useToggleCompleteMutation()`
  - [ ] `useDeleteReminderMutation()`
- [ ] `app/(main)/lists/[id]/page.tsx` — 커스텀 목록 페이지
- [ ] `components/reminder/ReminderList.tsx` — 리마인더 목록 컨테이너
- [ ] `components/reminder/ReminderRow.tsx`
  - [ ] 원형 체크박스 (목록 accent color, 완료 시 fill + check)
  - [ ] 완료 텍스트 스타일 (`line-through opacity-40`)
  - [ ] 행 hover 스타일 (`bg-apple-bg`)
- [ ] `components/reminder/AddReminderInput.tsx` — Enter 저장, Esc 취소

**[ ] Phase 2 완료 기준: 목록 클릭 → 리마인더 표시, Enter 추가, 체크박스 완료 토글**

---

## Phase 3 — 스마트 목록 & 라우팅

### Backend

- [ ] `ReminderRepository` 파생 쿼리 추가
  - [ ] `findByDueDateBetweenAndCompletedFalse` — 오늘
  - [ ] `findByDueDateNotNullAndCompletedFalse` — 예정
  - [ ] `findByCompletedFalse` — 전체
  - [ ] `findByFlaggedTrueAndCompletedFalse` — 플래그됨
  - [ ] `findByCompletedTrue(Sort)` — 완료됨
- [ ] `ReminderController` 확장
  - [ ] `GET /api/reminders?smart=today|scheduled|all|flagged|completed`
  - [ ] `GET /api/reminders/count` — { today, scheduled, all, flagged, completed }
- [ ] `dto/response/CountResponse.java`

### Frontend

- [ ] `app/(main)/layout.tsx` — 사이드바 포함 공통 레이아웃
- [ ] `app/(main)/smart/[type]/page.tsx` — 스마트 목록 페이지
- [ ] `components/sidebar/SmartLists.tsx` — 2열 그리드 카드 컨테이너
- [ ] `components/sidebar/SmartListCard.tsx`
  - [ ] rounded-2xl, accent tint 배경
  - [ ] Lucide 아이콘 (CalendarDays/Calendar/Tray/Flag/CheckCircle)
  - [ ] 카운트 숫자 (28px bold)
- [ ] 카운트 API 연동 — 실시간 갱신

**[ ] Phase 3 완료 기준: 스마트 목록 카드 클릭 시 필터링, 카운트 정확히 표시**

---

## Phase 4 — 상세 속성 (마감일 · 우선순위 · 플래그)

### Backend

- [ ] `ReminderRequest` 필드 추가 — dueDate, priority, flagged
- [ ] `ReminderResponse` 필드 추가 — dueDate, priority, flagged
- [ ] `ReminderService` 업데이트 — 신규 필드 반영
- [ ] `ReminderController` 엔드포인트 추가
  - [ ] `PATCH /api/reminders/{id}/flag`
  - [ ] `PATCH /api/reminders/{id}/priority`

### Frontend

- [ ] `components/reminder/DetailPanel.tsx`
  - [ ] 우측 슬라이드인 패널 (320px, transform transition 200ms)
  - [ ] 제목 편집 input (onBlur 자동 저장)
  - [ ] 메모 편집 textarea (onBlur 자동 저장)
  - [ ] 마감일 선택 (`<input type="datetime-local">`)
  - [ ] 플래그 토글 버튼
  - [ ] 우선순위 드롭다운 (None/낮음/중간/높음)
  - [ ] 목록 변경 드롭다운
- [ ] `ReminderRow.tsx` 메타 정보 표시 업데이트
  - [ ] 마감일 색상 (지남=red, 오늘=blue, 이후=gray)
  - [ ] 우선순위 `!` 기호 표시
  - [ ] 플래그 아이콘 (활성 시 orange)
- [ ] 리마인더 클릭 시 DetailPanel 열기/닫기 연동

**[ ] Phase 4 완료 기준: 리마인더 클릭 → 우측 패널, 마감일/우선순위/플래그 설정 후 목록 반영**

---

## Phase 5 — 서브태스크

### Backend

- [ ] `dto/request/SubtaskRequest.java` — title
- [ ] `dto/response/SubtaskResponse.java` — id, title, completed, sortOrder
- [ ] `ReminderResponse`에 `List<SubtaskResponse> subtasks` 추가
- [ ] `service/SubtaskService.java`
  - [ ] `create(reminderId, request)`
  - [ ] `toggleComplete(reminderId, subtaskId)`
  - [ ] `delete(reminderId, subtaskId)`
- [ ] `controller/SubtaskController.java`
  - [ ] `POST /api/reminders/{id}/subtasks`
  - [ ] `PATCH /api/reminders/{id}/subtasks/{subtaskId}/complete`
  - [ ] `DELETE /api/reminders/{id}/subtasks/{subtaskId}`

### Frontend

- [ ] `hooks/useSubtasks.ts`
  - [ ] `useCreateSubtaskMutation()`
  - [ ] `useToggleSubtaskMutation()`
  - [ ] `useDeleteSubtaskMutation()`
- [ ] `components/reminder/SubtaskList.tsx` — 서브태스크 목록 (접기/펼치기)
- [ ] `components/reminder/SubtaskRow.tsx` — 들여쓰기 + 원형 체크박스
- [ ] `components/reminder/AddSubtaskInput.tsx` — 상세 패널 내 추가 입력창
- [ ] `ReminderRow.tsx` — 서브태스크 카운트 표시, chevron 토글
- [ ] `Tab` 키 → 서브태스크 입력 전환 연동

**[ ] Phase 5 완료 기준: 서브태스크 추가·완료·삭제, 리마인더 행에 카운트 표시**

---

## Phase 6 — 검색 & 드래그앤드롭 정렬

### 검색

#### Backend
- [ ] `ReminderRepository` — `findByTitleContainingIgnoreCaseOrNotesContainingIgnoreCase`
- [ ] `ReminderController` — `GET /api/reminders?q=검색어`

#### Frontend
- [ ] `components/sidebar/SearchInput.tsx` — 사이드바 상단 검색창
- [ ] 검색 debounce 훅 (300ms)
- [ ] 검색 결과 뷰 컴포넌트

### 드래그앤드롭

#### Backend
- [ ] `PATCH /api/reminders/order` — body: [{id, sortOrder}]
- [ ] `PATCH /api/lists/order` — body: [{id, sortOrder}]

#### Frontend
- [ ] `npm install @dnd-kit/core @dnd-kit/sortable @dnd-kit/utilities`
- [ ] `ReminderList.tsx` — `SortableContext` + `useSortable` 적용
- [ ] 드롭 완료 시 PATCH 호출, 낙관적 업데이트
- [ ] `MyLists.tsx` — 목록 드래그앤드롭 적용

**[ ] Phase 6 완료 기준: 실시간 검색 동작, 드래그 정렬 후 새로고침해도 유지**

---

## Phase 7 — UI 완성도 & 키보드 단축키

### 애니메이션
- [ ] 완료 토글 — 체크박스 fill transition (300ms) → 0.5s 후 행 fadeout + collapse
- [ ] 상세 패널 — `translate-x-full` → `translate-x-0` (200ms ease-out)
- [ ] 목록 전환 — 콘텐츠 `opacity-0` → `opacity-100` (150ms)
- [ ] 리마인더 추가 — 행 `max-height` expand (200ms)

### 키보드 단축키
- [ ] `hooks/useKeyboard.ts` — 전역 키보드 이벤트 훅
- [ ] `Enter` — 리마인더 추가 인풋 포커스 / 저장
- [ ] `Escape` — 상세 패널 닫기 / 편집 취소
- [ ] `Space` — 선택 리마인더 완료 토글
- [ ] `⌘+Backspace` — 선택 리마인더 삭제
- [ ] `Tab` — 서브태스크 입력 전환

### 완료 항목 관리
- [ ] 콘텐츠 상단 "완료됨 n개 보기" 토글 버튼
- [ ] "완료 항목 전체 삭제" 버튼 + 확인 다이얼로그
- [ ] `DELETE /api/reminders/completed?listId=` 엔드포인트

### 빈 상태 (Empty State)
- [ ] 리마인더 없을 때 — 중앙 아이콘 + "리마인더 없음" 메시지
- [ ] 검색 결과 없을 때 — "검색 결과 없음" 메시지

**[ ] Phase 7 완료 기준: 애니메이션 자연스럽고, 키보드만으로 주요 조작 가능**

---

## 전체 진행 현황

| Phase | 내용 | 상태 |
|-------|------|------|
| Phase 0 | 프로젝트 기반 구성 | 🔄 진행 중 |
| Phase 1 | 목록(List) CRUD | ⬜ 미시작 |
| Phase 2 | 리마인더 기본 CRUD | ⬜ 미시작 |
| Phase 3 | 스마트 목록 & 라우팅 | ⬜ 미시작 |
| Phase 4 | 상세 속성 | ⬜ 미시작 |
| Phase 5 | 서브태스크 | ⬜ 미시작 |
| Phase 6 | 검색 & 드래그앤드롭 | ⬜ 미시작 |
| Phase 7 | UI 완성도 & 키보드 | ⬜ 미시작 |
