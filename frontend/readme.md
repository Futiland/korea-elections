# KEP - 시민이 만드는 여론조사

> "참여해야 보인다"

KEP는 본인 인증된 시민만 참여하는 여론조사 플랫폼입니다. 투표에 참여한 사람만 결과를 확인할 수 있습니다.

구경꾼의 여론이 아닌, 직접 참여한 시민의 선택만 남습니다.

## 📋 목차

- [서비스 소개](#서비스-소개)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
  - [프론트엔드](#프론트엔드)
  - [백엔드](#백엔드)
- [시작하기](#시작하기)
  - [프론트엔드](#프론트엔드-1)
  - [백엔드](#백엔드-1)
- [프로젝트 구조](#프로젝트-구조)
- [스크립트](#스크립트)

## 🎯 서비스 소개

### 세 가지 원칙

#### 1. 검증된 참여

- 휴대폰 본인인증을 거친 만 18세 이상 내국인만 참여합니다.
- 1인 1표, 중복 참여는 불가능합니다.

#### 2. 참여자만 결과 확인

- 투표해야 결과를 볼 수 있습니다.
- 관망하는 사람 없이, 의견을 가진 시민만 남습니다.

#### 3. 시민이 질문을 만든다

- 궁금한 이슈가 있다면 직접 여론조사를 만드세요.
- 당신의 질문에 시민이 응답합니다.

### 기존 여론조사와 다른 점

| 구분 | 기존 여론조사      | KEP                |
| ---- | ------------------ | ------------------ |
| 표본 | 무작위 전화 표본   | 직접 참여한 시민   |
| 질문 | 업체가 만든 질문   | 시민이 직접 질문   |
| 결과 | 언론이 해석한 결과 | 참여자가 직접 확인 |

KEP에서는 여론이 전달되지 않습니다. 직접 만들어지고, 직접 확인됩니다.

### 익명성 보장

- ✓ 투표 기록에 개인 정보 미포함
- ✓ 누가 어떤 선택을 했는지 추적 불가
- ✓ 이름, 전화번호 암호화 저장
- ✓ 탈퇴 시 인증 정보 익명화

## ✨ 주요 기능

- **투표 생성**: 시민이 직접 여론조사를 만들 수 있습니다

  - 단일 선택, 다중 선택, 점수제 (0-10점) 지원
  - 재투표 가능 옵션
  - 종료일 설정

- **투표 참여**: 본인 인증을 통한 안전한 투표 참여

  - 1인 1표 보장
  - 참여자만 결과 확인 가능

- **결과 확인**: 투표 결과를 차트로 시각화

  - 단일 선택: 파이 차트
  - 다중 선택: 바 차트
  - 점수제: 바 차트 + 평균 점수

- **투표 관리**: 내가 만든 투표와 참여한 투표 관리

## 🛠 기술 스택

### 프론트엔드

#### 핵심 프레임워크

- **Next.js 16.0.7** - React 프레임워크
- **React 19.0.0** - UI 라이브러리
- **TypeScript 5** - 타입 안정성

#### UI/UX

- **Tailwind CSS 4** - 유틸리티 CSS 프레임워크
- **Radix UI** - 접근성 기반 헤드리스 UI 컴포넌트
- **lucide-react** - 아이콘 라이브러리
- **Recharts** - 차트 라이브러리

#### 상태 관리 & 데이터

- **TanStack Query (React Query) 5.75.7** - 서버 상태 관리
- **React Hook Form 7.65.0** - 폼 관리
- **Zod 4.1.12** - 스키마 검증

#### 기타

- **date-fns** - 날짜 유틸리티
- **Sonner** - 토스트 알림
- **Embla Carousel** - 캐러셀
- **@portone/browser-sdk** - 포트원 본인인증

### 백엔드

#### 핵심 프레임워크

- **Spring Boot 3.4.5** - Java 애플리케이션 프레임워크
- **Kotlin 1.9.25** - 프로그래밍 언어
- **Java 17** - JVM 언어

#### 데이터베이스 & ORM

- **Spring Data JPA** - 데이터베이스 접근
- **MySQL** - 관계형 데이터베이스
- **H2** - 테스트용 인메모리 데이터베이스

#### 보안 & 인증

- **Spring Security** - 보안 프레임워크
- **JWT (java-jwt 4.1.0)** - 토큰 기반 인증
- **AES 암호화** - 개인정보 암호화

#### API & 통신

- **Spring Web** - RESTful API
- **Spring Cloud OpenFeign** - HTTP 클라이언트
- **SpringDoc OpenAPI** - API 문서화

#### 비동기 처리

- **Kotlin Coroutines** - 비동기 프로그래밍
- **Project Reactor** - 리액티브 프로그래밍

#### 기타

- **Spring Boot Actuator** - 모니터링 및 관리
- **Thymeleaf** - 템플릿 엔진
- **Jackson** - JSON 직렬화/역직렬화

## 🚀 시작하기

### 프론트엔드

#### 필수 요구사항

- Node.js 18+
- pnpm 10.10.0+

#### 설치

```bash
cd frontend
pnpm install
```

#### 환경 변수 설정

`.env.local` 파일을 생성하고 다음 환경 변수를 설정하세요:

```env
NEXT_PUBLIC_API_URL=your_api_url
NEXT_PUBLIC_BASE_URL=your_base_url
NEXT_PUBLIC_KAKAO_JAVASCRIPT_KEY=your_kakao_key
NEXT_PUBLIC_MAINTENANCE_MODE=false
```

#### 개발 서버 실행

```bash
pnpm dev
```

브라우저에서 [http://localhost:3000](http://localhost:3000)을 열어 확인하세요.

#### 빌드

```bash
# 프로덕션 빌드
pnpm build

# 프로덕션 서버 실행
pnpm start
```

### 백엔드

#### 필수 요구사항

- Java 17+
- Gradle 8+
- MySQL 8+

#### 설치

```bash
cd backend/election/vote
./gradlew build
```

#### 환경 변수 설정

`application.yml` 또는 환경 변수로 다음 설정을 구성하세요:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/kep
    username: your_username
    password: your_password
  jpa:
    hibernate:
      ddl-auto: validate
```

#### 개발 서버 실행

```bash
./gradlew bootRun
```

서버는 기본적으로 [http://localhost:8080](http://localhost:8080)에서 실행됩니다.

#### API 문서

SpringDoc OpenAPI를 사용하므로 다음 URL에서 API 문서를 확인할 수 있습니다:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 📁 프로젝트 구조

### 프론트엔드

```
frontend/
├── src/
│   ├── components/          # 재사용 가능한 컴포넌트
│   │   ├── Charts/         # 차트 컴포넌트
│   │   ├── CreatePoll/     # 투표 생성 관련
│   │   ├── Home/           # 홈 페이지 컴포넌트
│   │   ├── PollCard/       # 투표 카드 컴포넌트
│   │   ├── PollOptions/    # 투표 옵션 컴포넌트
│   │   └── ui/             # 기본 UI 컴포넌트
│   ├── hooks/              # 커스텀 훅
│   ├── lib/                # 유틸리티 및 API
│   │   ├── api/            # API 함수
│   │   ├── types/          # TypeScript 타입
│   │   └── utils.ts        # 유틸리티 함수
│   ├── pages/              # Next.js 페이지
│   │   ├── everyone-polls/ # 모두의 투표
│   │   ├── opinion-polls/  # 민심 투표
│   │   └── mypage/         # 마이페이지
│   └── styles/             # 전역 스타일
├── public/                  # 정적 파일
└── package.json
```

### 백엔드

```
backend/election/vote/
├── src/
│   ├── main/
│   │   ├── kotlin/com/futiland/vote/
│   │   │   ├── application/    # 애플리케이션 레이어
│   │   │   │   ├── account/    # 계정 관리
│   │   │   │   ├── poll/       # 투표 관리
│   │   │   │   ├── vote/       # 선거 투표
│   │   │   │   ├── config/     # 설정
│   │   │   │   ├── aop/        # AOP (마스킹 등)
│   │   │   │   └── scheduler/  # 스케줄러
│   │   │   └── domain/         # 도메인 레이어
│   │   │       ├── account/    # 계정 도메인
│   │   │       ├── poll/       # 투표 도메인
│   │   │       └── vote/       # 선거 도메인
│   │   └── resources/          # 설정 파일
│   └── test/                    # 테스트 코드
├── build.gradle.kts            # Gradle 빌드 설정
└── Dockerfile                   # Docker 설정
```

## 📜 스크립트

### 프론트엔드

```bash
# 개발 서버 실행
pnpm dev

# 프로덕션 빌드
pnpm build

# 프로덕션 서버 실행
pnpm start

# 린트 검사
pnpm lint

# 테스트 실행
pnpm test

# 테스트 (watch 모드)
pnpm test:watch

# 테스트 커버리지
pnpm test:coverage
```

### 백엔드

```bash
# 애플리케이션 실행
./gradlew bootRun

# 빌드
./gradlew build

# 테스트 실행
./gradlew test

# 클린 빌드
./gradlew clean build

# Docker 이미지 빌드
docker build -t kep-vote .
```

## 📝 라이선스

이 프로젝트는 private입니다.
