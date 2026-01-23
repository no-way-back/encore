import http from 'k6/http';
import { check } from 'k6';
import { Trend } from 'k6/metrics';
import { performanceThresholds } from '../../config/thresholds.js';
import { generateUUID } from '../../utils/uuid-generator.js';

/**
 * Reward 서비스 - 재고 차감 성능 테스트
 *
 * 목적: 단계별 성능 개선 효과 측정
 * 시나리오: 500명이 1분간 재고 10,000개를 차감 요청
 *
 * 테스트 단계:
 * - baseline: 비관적 락 + HikariCP 기본 설정
 * - hikaricp: HikariCP 튜닝 (pool-size 50) + 트랜잭션 최소화
 * - redis: Redis 분산 락
 *
 * 실행 명령어 예시:
 * - docker-compose run --rm k6 run \
 *   -e BASE_URL=http://host.docker.internal:18083 \
 *   -e TEST_DATA_ID=1027b841-a102-4047-ab01-bcb9a60b4476 \
 *   -e TEST_STAGE=redis \
 *   /scripts/services/reward/stock-reserve-performance-test.js
 */

export let options = {
    vus: 500,
    duration: '1m',
    thresholds: performanceThresholds,
    tags: {
        test_stage: __ENV.TEST_STAGE || 'baseline',
    },
};

const successDuration = new Trend('success_response_time');
const failDuration = new Trend('fail_response_time');

const BASE_URL = __ENV.BASE_URL || 'http://host.docker.internal:18083';
const REWARD_ID = __ENV.TEST_DATA_ID;

export default function() {
    const userId = generateUUID();
    const fundingId = generateUUID();

    const payload = JSON.stringify({
        fundingId: fundingId,
        items: [
            {
                rewardId: REWARD_ID,
                optionId: null,
                quantity: 1,
            }
        ],
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-User-Id': userId,
        },
    };

    const response = http.post(
        `${BASE_URL}/internal/rewards/reserve-stock`,
        payload,
        params
    );

    if (response.status === 200) {
        successDuration.add(response.timings.duration);
    } else if (response.status === 409) {
        failDuration.add(response.timings.duration);
    }

    check(response, {
        'status is 200 or 409': (r) => r.status === 200 || r.status === 409,
        'response has body': (r) => r.body && r.body.length > 0,
    });
}