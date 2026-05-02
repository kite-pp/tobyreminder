# Coding Conventions

## 패키지 구조

```
service/ports/in/  서비스 인터페이스
service/           서비스 구현체
domain/            엔티티 및 도메인 모델
repository/        JPA 리포지토리
dto/request/       요청 DTO
dto/response/      응답 DTO
```

## 서비스 레이어

- 인터페이스는 `service/ports/in` 패키지에 위치
- 구현체는 `service` 패키지에 `Default{InterfaceName}` 네이밍 (예: `DefaultReminderListService`)
- 클래스 레벨에 `@Transactional(readOnly = true)`, 쓰기 메서드에만 `@Transactional` 오버라이드
- 존재하지 않는 엔티티 조회 시 `EntityNotFoundException` 발생, 메시지에 id 포함

## DTO

- 요청 DTO: `@NoArgsConstructor` + `@AllArgsConstructor` (테스트에서 직접 생성 가능하도록)
- 응답 DTO: 정적 팩토리 메서드 `from(Entity)` 패턴 사용

## 테스트

- 기능 추가/수정 시 반드시 검증 테스트를 함께 작성
- 도메인 엔티티 테스트: 순수 단위 테스트 (Spring/JPA 컨텍스트 사용 금지)
- 서비스 테스트: `@SpringBootTest` + `@Transactional` 통합 테스트, Mockito 사용 금지
  - `@BeforeEach`에서 `repository.deleteAll()`로 상태 초기화

## 참고 문서

- spec.md: 기능 명세
- plan.md: 개발 계획 (7 phases)
- task.md: 구현 태스크 체크리스트
