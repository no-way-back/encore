/**
 * K6 테스트 성공 기준 정의
 *
 * Thresholds는 테스트의 통과/실패 기준을 설정합니다.
 * - http_req_duration: HTTP 요청의 응답 시간
 * - http_req_failed: 실패한 요청의 비율
 *
 * p(95): 95 백분위수 (상위 5% 제외한 95%의 요청)
 * rate: 비율 (0.05 = 5%)
 */

export const concurrencyThresholds = {
    http_req_duration: ['p(95)<5000'],   // 동시성 테스트: 95%가 5초 이내
    http_req_failed: ['rate<0.1'],       // 에러율 10% 미만 (품절 등 정상 실패 포함)
};

export const loadTestThresholds = {
    http_req_duration: ['p(95)<3000'],   // 일반 부하: 95%가 3초 이내
    http_req_failed: ['rate<0.05'],      // 에러율 5% 미만
};

export const peakLoadThresholds = {
    http_req_duration: ['p(95)<10000'],  // 피크 부하: 95%가 10초 이내
    http_req_failed: ['rate<0.15'],      // 에러율 15% 미만
};

export const smokeTestThresholds = {
    http_req_duration: ['p(95)<1000'],   // 스모크: 95%가 1초 이내
    http_req_failed: ['rate<0.01'],      // 에러율 1% 미만
};