# TobyReminder — Specification

## 1. 개요

Apple Reminders App의 핵심 기능을 웹 환경에서 구현하는 프로젝트.
브라우저에서 할 일 및 리마인더를 생성·관리하고, 목록(List) 단위로 구조화할 수 있다.

---

## 2. 목표

| 목표 | 설명 |
|------|------|
| 기능 등가성 | Apple Reminders의 핵심 기능(CRUD, 목록, 우선순위, 마감일)을 웹에서 동일하게 제공 |
| 사용성 | 클릭 최소화, 키보드 친화적 UX |
| 확장성 | 이후 알림(Notification), 반복(Recurrence), 협업(Sharing) 기능 추가 가능한 구조 |

---

## 3. 사용자 정의

- **Primary User**: 개인 할 일 관리가 필요한 사용자 (1인 사용 기준, 인증 없음)
- **Scope**: 단일 사용자, 로컬 H2 DB 기반 (MVP)

---

## 4. 핵심 기능 (MVP)

### 4.1 리마인더 (Reminder)

| 기능 | 설명 |
|------|------|
| 생성 | 제목, 메모, 마감일시, 우선순위, 목록 지정 |
| 조회 | 스마트 목록(오늘, 예정, 전체, 플래그) 및 커스텀 목록별 조회 |
| 수정 | 인라인 수정 (클릭 즉시 편집 가능) |
| 완료 | 체크박스로 완료 토글 (완료 시 취소선 표시) |
| 삭제 | 개별 삭제 / 완료 항목 일괄 삭제 |
| 우선순위 | None / Low / Medium / High (Apple Reminders와 동일) |
| 플래그 | 플래그 토글 (⚑) |
| 서브태스크 | 리마인더 하위에 서브태스크 추가 |

### 4.2 목록 (List)

| 기능 | 설명 |
|------|------|
| 생성 | 목록 이름 + 색상(6가지) + 아이콘 설정 |
| 수정 | 이름/색상 변경 |
| 삭제 | 목록 삭제 시 포함된 리마인더도 삭제 |
| 순서 변경 | 드래그앤드롭으로 순서 변경 |

### 4.3 스마트 목록 (Smart Lists, 읽기 전용)

| 스마트 목록 | 조건 |
|-------------|------|
| 오늘 (Today) | 마감일 = 오늘 |
| 예정 (Scheduled) | 마감일 설정된 전체 리마인더 |
| 전체 (All) | 완료되지 않은 모든 리마인더 |
| 플래그됨 (Flagged) | 플래그 = true |
| 완료됨 (Completed) | 완료 = true |

### 4.4 검색

- 제목/메모 전문 검색 (실시간 필터)

---

## 5. 화면 구성

```
┌─────────────────────────────────────────────────────────┐
│  [사이드바]                │  [메인 콘텐츠]              │
│                            │                             │
│  ● 스마트 목록              │  [목록 제목]                │
│    ○ 오늘          (3)     │                             │
│    ○ 예정          (7)     │  ☐ 리마인더 제목            │
│    ○ 전체         (12)     │    메모 · 오늘 · !높음      │
│    ○ 플래그됨      (2)     │                             │
│    ○ 완료됨        (5)     │  ☐ 리마인더 제목            │
│                            │    └ ☐ 서브태스크           │
│  ● 나의 목록                │                             │
│    ○ 업무          (4)     │  + 리마인더 추가...         │
│    ○ 개인          (2)     │                             │
│    + 목록 추가             │                             │
└─────────────────────────────────────────────────────────┘
```

---

## 6. UI/UX 상세 명세 (Apple Reminders 준수)

### 6.1 레이아웃

```
┌──────────────────────────────────────────────────────────────────┐
│  사이드바 (260px, 고정)     │  콘텐츠 영역 (flex-1)              │
│  배경: #F2F2F7 (연회색)    │  배경: #FFFFFF                     │
│                             │                                    │
│  [검색창]  🔍               │  [목록 제목]          + 추가버튼   │
│                             │  ──────────────────────────────   │
│  ┌────────────────────┐     │  ○ 리마인더 제목                  │
│  │ 오늘        3      │     │    메모 미리보기 · 오늘 · ⚑       │
│  │ 예정        7      │     │                                   │
│  │ 전체       12      │     │  ○ 리마인더 제목                  │
│  │ 플래그됨    2      │     │    └ ○ 서브태스크                 │
│  │ 완료됨      5      │     │                                   │
│  └────────────────────┘     │  + 리마인더 추가                  │
│                             │                                   │
│  나의 목록                   │                                   │
│  ┌────────────────────┐     │  [우측 상세 패널] (선택 시 표시)  │
│  │ 🔵 업무      4     │     │  ─────────────────────────────   │
│  │ 🟢 개인      2     │     │  제목 편집                        │
│  └────────────────────┘     │  메모 편집                        │
│                             │  마감일 · 우선순위 · 목록 변경    │
│  + 목록 추가                │                                   │
└──────────────────────────────────────────────────────────────────┘
```

### 6.2 색상 시스템 (Apple Human Interface Guidelines 준수)

| 요소 | 색상 | 값 |
|------|------|-----|
| 앱 배경 | Light Gray | `#F2F2F7` |
| 사이드바 배경 | 반투명 White | `rgba(255,255,255,0.8)` + backdrop-blur |
| 콘텐츠 배경 | White | `#FFFFFF` |
| 기본 텍스트 | Near Black | `#1C1C1E` |
| 보조 텍스트 | Gray | `#6C6C70` |
| 스마트 목록 — 오늘 | Blue | `#007AFF` |
| 스마트 목록 — 예정 | Red | `#FF3B30` |
| 스마트 목록 — 전체 | Dark Gray | `#3A3A3C` |
| 스마트 목록 — 플래그됨 | Orange | `#FF9500` |
| 스마트 목록 — 완료됨 | Gray | `#8E8E93` |
| 우선순위 — High | Red | `#FF3B30` |
| 우선순위 — Medium | Orange | `#FF9500` |
| 우선순위 — Low | Blue | `#007AFF` |
| 완료 체크박스 | List 색상 | 목록별 accent color |
| 구분선 | Light | `#E5E5EA` |

### 6.3 스마트 목록 카드 (사이드바 상단)

- 2열 그리드 배치 (오늘/예정, 전체/플래그됨 순)
- 각 카드: `rounded-2xl`, 배경은 해당 색상의 연한 tint (`opacity-10`)
- 카드 내 아이콘 (SF Symbols 대응 Lucide): 좌측 상단, 색상 강조
- 카운트 숫자: 우측 상단, bold, 해당 색상

```
┌──────────────┐  ┌──────────────┐
│  📅          │  │  📆          │
│              │  │              │
│  오늘     3  │  │  예정     7  │
└──────────────┘  └──────────────┘
┌──────────────┐  ┌──────────────┐
│  ☰           │  │  ⚑           │
│              │  │              │
│  전체    12  │  │  플래그   2  │
└──────────────┘  └──────────────┘
```

### 6.4 리마인더 행 (Row)

```
[○] 제목 텍스트                              [⚑] [···]
    메모 미리보기 (1줄 말줄임)  ·  오늘 17:00  ·  !높음
    └ [○] 서브태스크 제목
```

- **체크박스**: 원형, 테두리는 목록 accent color, 완료 시 배경색 채움 + 체크 아이콘
- **완료 상태**: 제목에 취소선(`line-through`) + 텍스트 `opacity-40`
- **메모**: 회색 소형 텍스트, 1줄 truncate
- **마감일**: 오늘 이전 → 빨간색, 오늘 → 파란색, 이후 → 회색
- **우선순위**: `!` 기호 (높음=`!!!`, 중간=`!!`, 낮음=`!`) + 색상
- **플래그**: `⚑` 아이콘, 활성 시 orange
- **행 hover**: 배경 `#F2F2F7`로 변경
- **행 선택**: 배경 목록 accent color `opacity-10`

### 6.5 상세 패널 (우측 슬라이드인)

선택된 리마인더가 있을 때 우측에서 슬라이드인 (width: 320px)

```
┌─────────────────────────────┐
│  ✕                          │
│                             │
│  [제목 편집 (대형 입력창)]  │
│                             │
│  [메모 편집 (textarea)]     │
│  ─────────────────────────  │
│  📅 마감일    2026.05.10    │
│  ⏰ 마감시간  오후 3:00     │
│  ⚑  플래그    ○ / ●         │
│  !  우선순위  높음 ▾        │
│  📋 목록      업무 ▾        │
│  ─────────────────────────  │
│  서브태스크                  │
│  ○ 서브태스크 제목           │
│  + 서브태스크 추가           │
└─────────────────────────────┘
```

### 6.6 인터랙션 & 애니메이션

| 동작 | 효과 |
|------|------|
| 리마인더 완료 | 체크박스 fill 애니메이션 → 0.5s 후 목록에서 페이드아웃 |
| 목록 선택 | 콘텐츠 영역 fade-in (150ms) |
| 상세 패널 열기 | 우측에서 슬라이드인 (200ms ease-out) |
| 리마인더 추가 | 목록 하단에 즉시 인라인 입력창 등장 |
| 삭제 | 스와이프(모바일) 또는 행 우클릭 컨텍스트 메뉴 |
| 드래그앤드롭 | 순서 변경 시 점선 drop indicator 표시 |

### 6.7 타이포그래피

| 요소 | 폰트 크기 | 굵기 |
|------|-----------|------|
| 목록 제목 | 28px | Bold (700) |
| 스마트 목록 카드 텍스트 | 15px | Semibold (600) |
| 스마트 목록 카운트 | 28px | Bold (700) |
| 리마인더 제목 | 15px | Regular (400) |
| 리마인더 메모/부가정보 | 13px | Regular (400) |
| 사이드바 섹션 헤더 | 13px | Semibold (600) |

- 시스템 폰트: `-apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif`

### 6.8 목록 색상 팔레트 (사용자 선택)

Red / Orange / Yellow / Green / Blue / Purple / Brown / Gray (8가지, Apple Reminders 동일)

### 6.9 키보드 단축키

| 단축키 | 동작 |
|--------|------|
| `Enter` | 리마인더 추가 / 다음 리마인더로 커서 이동 |
| `Escape` | 편집 취소 / 상세 패널 닫기 |
| `Space` | 선택된 리마인더 완료 토글 |
| `⌘ + Backspace` | 선택된 리마인더 삭제 |
| `Tab` | 서브태스크로 들여쓰기 |

---

## 7. 기술 스택

### Backend
| 항목 | 선택 |
|------|------|
| Framework | Spring Boot 4.0.6 |
| Language | Java 25 |
| ORM | Spring Data JPA (Hibernate 7) |
| DB | H2 (in-memory, MVP) |
| API 방식 | REST API (JSON) |
| Build | Gradle Kotlin DSL |

### Frontend
| 항목 | 선택 |
|------|------|
| Framework | Next.js (latest) |
| Language | TypeScript |
| Styling | Tailwind CSS |
| 상태관리 | Zustand (또는 React Query + Context) |
| HTTP Client | Axios 또는 fetch (React Query 조합) |
| 아이콘 | Lucide React |

---

## 8. API 설계 (Backend)

### Reminder

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/reminders` | 전체 조회 (queryParam: listId, smart, completed) |
| GET | `/api/reminders/{id}` | 단건 조회 |
| POST | `/api/reminders` | 생성 |
| PUT | `/api/reminders/{id}` | 수정 |
| PATCH | `/api/reminders/{id}/complete` | 완료 토글 |
| PATCH | `/api/reminders/{id}/flag` | 플래그 토글 |
| DELETE | `/api/reminders/{id}` | 삭제 |
| DELETE | `/api/reminders/completed` | 완료 항목 일괄 삭제 |

### List

| Method | Endpoint | 설명 |
|--------|----------|------|
| GET | `/api/lists` | 전체 목록 조회 |
| POST | `/api/lists` | 목록 생성 |
| PUT | `/api/lists/{id}` | 목록 수정 |
| DELETE | `/api/lists/{id}` | 목록 삭제 |
| PATCH | `/api/lists/order` | 목록 순서 변경 |

### Subtask

| Method | Endpoint | 설명 |
|--------|----------|------|
| POST | `/api/reminders/{id}/subtasks` | 서브태스크 추가 |
| PATCH | `/api/reminders/{id}/subtasks/{subtaskId}/complete` | 완료 토글 |
| DELETE | `/api/reminders/{id}/subtasks/{subtaskId}` | 삭제 |

---

## 9. 도메인 모델

### ReminderList
```
id          Long        PK
name        String      목록 이름
color       String      색상 코드 (#HEX)
icon        String      이모지 or 아이콘명
sortOrder   Int         정렬 순서
createdAt   DateTime
```

### Reminder
```
id          Long        PK
list        ReminderList FK (nullable → 기본 목록)
title       String      필수
notes       String      메모
dueDate     DateTime    마감일시 (nullable)
priority    Enum        NONE / LOW / MEDIUM / HIGH
flagged     Boolean     플래그 여부
completed   Boolean     완료 여부
completedAt DateTime    완료 시각
sortOrder   Int         목록 내 정렬 순서
createdAt   DateTime
```

### Subtask
```
id          Long        PK
reminder    Reminder    FK
title       String      필수
completed   Boolean
sortOrder   Int
createdAt   DateTime
```

---

## 10. 비기능 요구사항

| 항목 | 기준 |
|------|------|
| 응답속도 | API 응답 200ms 이하 (로컬 H2 기준) |
| 반응형 | 1280px 이상 데스크톱 우선 (모바일 MVP 제외) |
| 접근성 | 키보드 네비게이션 (Tab, Enter, Esc) 지원 |
| CORS | Next.js dev server(3000) ↔ Spring Boot(8080) 허용 |

---

## 11. 개발 단계 (Milestone)

| Phase | 내용 |
|-------|------|
| Phase 1 | Backend API 완성 (List, Reminder, Subtask CRUD) |
| Phase 2 | Next.js 프로젝트 초기화 + 사이드바 + 스마트 목록 UI |
| Phase 3 | 리마인더 CRUD UI + 인라인 편집 |
| Phase 4 | 서브태스크, 우선순위, 플래그, 검색 |
| Phase 5 | 드래그앤드롭 정렬, 완료 항목 관리 |

---

## 12. 미포함 (Out of Scope, MVP)

- 사용자 인증/계정
- 알림 (Push / Email)
- 반복 리마인더
- 다중 사용자 / 공유
- 모바일 앱
- 다크모드 (이후 Phase에서 추가)
