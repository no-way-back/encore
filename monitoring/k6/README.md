# K6 Load Testing

## 📋 개요
마이크로서비스 환경에서 각 서비스의 동시성 및 부하 테스트 가이드

## 🏗️ 디렉토리 구조
```
k6/
├── README.md
├── config/
│   └── thresholds.js
├── utils/
│   └── uuid-generator.js
└── services/
    ├── reward/
    ├── user/
    ├── project/
    ├── funding/
    └── payment/
```

## 🚀 실행 방법

### 1. 인프라 실행
- 최상위 encore 패키지 docker-compose.yml 실행
- monitoring 패키지 docker-compose.yml 실행

### 2. 애플리케이션 실행
- 인텔리제이로 서비스 실행
- 디스커버리 서비스 실행
- 부하테스트가 필요한 서비스 실행

### 3. 테스트 데이터 준비
- 테스트에 필요한 초기 데이터 생성
- 예: 초기 재고, 테스트 계정 등

### 4. K6 테스트 실행

**기본 실행:**
```
docker-compose run --rm k6 run /scripts/services/{service-name}/{test-name}.js
```

**환경변수와 함께 실행:**
```
docker-compose run --rm k6 run \
  -e BASE_URL=http://host.docker.internal:18083 \
  -e TEST_DATA_ID=your-test-data-id \
  /scripts/services/{service-name}/{test-name}.js
```

## 📊 결과 확인

### K6 콘솔 출력
테스트 종료 후 터미널에서 기본 통계 확인

### Grafana 대시보드
- URL: http://localhost:3000
- 실시간 메트릭 시각화

### Prometheus
- URL: http://localhost:9090
- 원시 메트릭 데이터 조회

### 데이터베이스
테스트 후 DB에 직접 접속하여 데이터 정합성 검증

## 🔧 환경변수

K6 스크립트는 실행 시 환경변수를 통해 설정을 변경 가능

### 테스트 대상 서버 변경
각 서비스는 서로 다른 포트에서 실행되고, 테스트할 서비스에 맞게 `BASE_URL`을 지정

**서비스별 포트:**
- Reward 서비스: `18083`
- User 서비스: `18081`
- Project 서비스: `18085`
- Funding 서비스: `18082`
- Payment 서비스: `18084`

**Reward 서비스 테스트 예시:**
```
docker-compose run --rm k6 run \
  -e BASE_URL=http://host.docker.internal:18083 \
  /scripts/services/reward/concurrency-test.js
```

**User 서비스 테스트 예시:**
```
docker-compose run --rm k6 run \
  -e BASE_URL=http://host.docker.internal:18081 \
  /scripts/services/user/concurrency-test.js
```

**※ 참고:** `BASE_URL`은 서버 주소와 포트만 포함하고, API 경로는 스크립트에 정의.

### 테스트 데이터 변경
테스트할 데이터의 ID를 변경하려면 `TEST_DATA_ID` 지정
```
docker-compose run --rm k6 run \
  -e TEST_DATA_ID=abc-123-def-456 \
  /scripts/services/reward/concurrency-test.js
```

### 여러 환경변수 동시 사용
```
docker-compose run --rm k6 run \
  -e BASE_URL=http://host.docker.internal:18083 \
  -e TEST_DATA_ID=abc-123-def-456 \
  /scripts/services/reward/concurrency-test.js
```

## 📝 테스트 종류

- **concurrency-test.js**: 동시성 제어 검증
- **load-test.js**: 일반 부하 테스트
- **peak-load-test.js**: 피크 부하 테스트
- **smoke-test.js**: 기본 동작 확인

**예시:**
```
/scripts/services/reward/stock-reserve-concurrency-test.js
  → 재고 차감 API 동시성 테스트

/scripts/services/user/signup-load-test.js
  → 회원가입 API 부하 테스트
```