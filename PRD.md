# 📝 제품 요구사항 문서 (PRD): 동네 (Dongne) v1.1

## 1. 제품 개요 (Product Overview)
* **제품 명칭:** 동네 (동아리 네트워크)
* **한 줄 정의:** 인하대학교 학생과 동아리를 잇는 통합 홍보 및 지원 플랫폼
* **핵심 가치:** 흩어진 동아리 정보를 통합하고, 데이터 기반의 효율적인 모집 관리 환경 제공

---

## 2. 구현 전략 및 의사결정 (Implementation Strategy)

### 2.1. 지원서 동적 폼: JSONB 전략 채택
다양한 동아리의 가변적인 질문 문항을 처리하기 위해 **PostgreSQL JSONB**를 채택합니다.
* **선택 이유**: 
    - **유연성**: 동아리마다 질문 개수와 타입이 달라도 스키마 변경 없이 대응 가능.
    - **생산성**: EAV 구조 대비 JOIN 연산이 적어 API 개발 및 프론트엔드 통신 속도가 빠름.
    - **인덱싱**: PostgreSQL의 GIN 인덱스를 활용해 필요시 JSON 내부 데이터 검색 성능 확보 가능.

### 2.2. 사용자 권한 논리: 역할 기반 접근 제어 (RBAC)
* **통합 계정 체계**: 모든 사용자는 기본적으로 `User` 권한을 가지며, 일반 부원의 기능을 100% 사용합니다.
* **관리 권한 분리**: 특정 동아리의 '회장' 여부는 별도의 매핑 테이블(`club_managers`)을 통해 검증합니다.
* **작동 원리**: 유저가 특정 동아리 편집을 요청할 때, 서버는 `현재 접속 유저 ID`와 `해당 동아리 ID`가 매핑 테이블에 존재하는지 확인하여 권한을 승인합니다. 이를 통해 회장도 다른 동아리에는 일반 유저로서 자유롭게 지원할 수 있습니다.

---

## 3. 데이터베이스 상세 설계 (Database Schema)

모든 테이블은 데이터 무결성을 위해 엄격한 제약사항(Constraint)을 적용합니다.

### 3.1. 테이블 명세 및 제약사항

#### ① users (회원)
| 컬럼명 | 타입 | 제약사항 | 설명 |
| :--- | :--- | :--- | :--- |
| id | Long | PK, Identity | 고유 식별자 |
| email | String | Unique, Not Null | 구글 OAuth 이메일 (@inha.ac.kr) |
| name | String | Not Null | 사용자 실명 |

#### ② clubs (동아리)
| 컬럼명 | 타입 | 제약사항 | 설명 |
| :--- | :--- | :--- | :--- |
| id | Long | PK, Identity | 고유 식별자 |
| name | String | Not Null, Unique | 동아리 명칭 |
| description| Text | - | 동아리 소개글 |
| profile_img| String | - | 로고 이미지 URL |

#### ③ club_managers (운영진 매핑) ⭐️
| 컬럼명 | 타입 | 제약사항 | 설명 |
| :--- | :--- | :--- | :--- |
| id | Long | PK, Identity | 고유 식별자 |
| user_id | Long | FK (users.id), Cascade | 관리자 유저 ID |
| club_id | Long | FK (clubs.id), Cascade | 관리 대상 동아리 ID |
| role | String | Not Null | PRESIDENT, MANAGER 등 |
| **Unique** | (user_id, club_id) | **중복 방지** | 한 유저가 한 동아리에 중복 등록 방지 |

#### ④ recruitments (모집 공고)
| 컬럼명 | 타입 | 제약사항 | 설명 |
| :--- | :--- | :--- | :--- |
| id | Long | PK, Identity | 고유 식별자 |
| club_id | Long | FK (clubs.id) | 공고를 올린 동아리 |
| title | String | Not Null | 모집 공고 제목 |
| form_schema| **JSONB** | Default '[]' | 지원서 질문 구성 데이터 |
| is_active | Boolean | Default true | 모집 진행 여부 |

#### ⑤ applications (제출된 지원서)
| 컬럼명 | 타입 | 제약사항 | 설명 |
| :--- | :--- | :--- | :--- |
| id | Long | PK, Identity | 고유 식별자 |
| recruitment_id| Long | FK (recruitments.id) | 해당 모집 공고 ID |
| user_id | Long | FK (users.id) | 지원자 유저 ID |
| answers | **JSONB** | Not Null | 학생이 작성한 답변 데이터 |
| status | String | Check (Status 인자) | PENDING, ACCEPTED, REJECTED |
| **Unique** | (recruitment_id, user_id) | **중복 지원 금지** | 동일 공고에 대한 중복 지원 원천 차단 |

---

## 4. 핵심 기능 요구사항

### 4.1. 회원 및 권한
- 구글 로그인을 통해 학교계정으로 자동 회원가입(인하대의 자체 지원 서비스가 있는 경우 최우선 적용).
- 로그인된 세션을 기반으로 본인이 관리자인 동아리 리스트를 반환.

### 4.2. 동아리 및 모집 (회장 권한)
- 내 동아리의 정보(소개, 로고) 수정.
- 모집 공고 생성 시 JSON 형태로 질문 리스트 정의.
- 지원자 목록 조회 및 상태(합격/불합격) 변경.

### 4.3. 탐색 및 지원 (일반 유저 권한)
- 전체 동아리 목록 및 상세 페이지 조회.
- 활성화된 모집 공고에 지원서 작성 및 제출.
- 내 지원 현황(결과 대기 등) 실시간 확인.

---

## 5. 로드맵 (Roadmap)
1. **Phase 1**: 도커 DB 연동 및 기초 엔티티(User, Club) 생성
2. **Phase 2**: JSONB를 활용한 공고 생성 및 지원서 제출 로직 구현
3. **Phase 3**: Spring Security를 이용한 회장/부원 권한 검증 로직 적용
4. **Phase 4**: 파일럿 운영 및 피드백 반영

---ㄴ