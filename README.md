# 📱 인하대학교 동아리 통합 플랫폼: 동네 (Dongne) - Backend API Server

> **"우리 학교 동아리 네트워크, 동네에서 한눈에"**
> 본 저장소는 인하대학교 학생들과 동아리 회장들을 연결하는 통합 서비스 '동네'의 **백엔드(Backend) API 서버** 소스 코드를 관리합니다.

---

## 🚀 프로젝트 개요 (v1.0 MVP)

현재 인하대학교 내 여러 커뮤니티에 흩어져 있는 동아리 홍보 게시글과 지원서 양식을 하나로 통합하여, 학생들에게는 편리한 지원 경험을, 동아리 회장들에게는 효율적인 모집 관리 도구를 제공합니다.

- **파일럿 타겟**: GDG on Campus (GDGoC) Inha 데이터를 기반으로 기능 시연 및 검증 진행
- **배포 전략**: 모바일 환경에 최적화된 **웹앱(Web App)** 형태로 배포하여, 앱스토어 심사 대기 없이 앱과 유사한 사용자 경험(UX)을 제공합니다.
- **저장소 분리**: 프론트엔드(Next.js) 코드와 완전히 분리된 백엔드 전용 레포지토리입니다.

---

## 🛠 Tech Stack (Backend)

생산성과 확장성을 고려하여 최신 백엔드 기술 스택을 채택하였습니다.

- **Language**: Java 17
- **Framework**: Spring Boot 3.2.x
- **Build Tool**: Gradle (Groovy)
- **Database**: PostgreSQL (Managed by Supabase)
- **ORM**: Spring Data JPA
- **Authentication**: Google OAuth2 (@inha.ac.kr 도메인 제한)
- **Validation**: Spring Boot Starter Validation
- **Utilities**: Lombok

---

## 🏗 Key Decisions & Strategy

백엔드 개발 및 시스템 설계 시 합의된 핵심 원칙입니다.

1. **사용자 인증 (Auth)**: 번거로운 메일 인증 대신 **구글 OAuth(도메인 제한)**를 사용하여 사용자 이탈률을 최소화합니다.
2. **유연한 지원서 (JSONB)**: 동아리마다 다른 질문 문항을 수용하기 위해 지원서 데이터를 **JSONB** 형식으로 저장하여 유연성을 확보합니다.
3. **효율적 개발 범위**: 관리자(Admin) UI를 별도로 제작하지 않고 개발팀이 **DB 직접 관리**를 수행하여, 사용자 기능 고도화에 리소스를 집중합니다.
4. **안정성 우선**: '선착순 마감' 기능을 제외하여 트래픽 집중 시 발생할 수 있는 DB 락(Lock) 및 서버 부하를 방지합니다.
5. **API 통신 규격**: 프론트엔드와의 원활한 협업을 위해 모든 API 응답은 일관된 JSON 포맷(`{status, message, data}`)을 준수합니다.

---

## 📂 Project Structure

```text
src/main/java/com/inha/dongne
├── global          # 공통 설정 (Security, Exception, Response DTO 등)
├── domain
│   ├── user        # 사용자 관리 및 인증
│   ├── club        # 동아리 홍보 및 정보 관리
│   ├── application # 지원서 양식 및 제출 관리
│   └── notice      # 공지사항
└── infra           # 외부 서비스 연동 (S3, OAuth 등)
```

---

## ⚙️ 시작하기 (Getting Started)

### 1. 환경 변수 설정

프로젝트 실행을 위해 `src/main/resources` 경로에 `application-secret.yml` 파일을 생성하고 아래 정보를 설정해야 합니다. (해당 파일은 보안을 위해 `.gitignore`에 등록되어 있으며, 절대 커밋하지 마세요.)

```yaml
spring:
  datasource:
    url: ${SUPABASE_DB_URL}
    username: ${SUPABASE_DB_USER}
    password: ${SUPABASE_DB_PASSWORD}
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
```

### 2. 빌드 및 실행

```bash
./gradlew build
java -jar build/libs/dongne-backend-0.0.1-SNAPSHOT.jar
```

---

## 📄 License & Copyright

Copyright ⓒ 2026 **Team Dongne**. All rights reserved.

- **Backend Lead**: [hyeon-jin-park]
- **Organization**: GDG on Campus Inha
- **Note**: 본 프로젝트는 GDG on Campus Inha 활동의 일환으로 제작되었으며, 무단 전재 및 재배포를 금합니다.