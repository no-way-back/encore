import http from 'k6/http';
import { check } from 'k6';
import { Trend, Counter } from 'k6/metrics';

/**
 * 펀딩 생성 - 재고 예약 중간 실패 테스트
 *
 * 목적: 단일 트랜잭션 방식의 동시성 문제 확인
 * 시나리오: 20명이 10초간 동시에 펀딩 생성 (reward1: 재고 10개, reward2: 재고 0개)
 *
 * 실행 명령어:
 * docker-compose -f docker-compose-monitoring.yml run --rm k6 run \
 *   -e BASE_URL=http://host.docker.internal:8080 \
 *   /scripts/services/funding/concurrent-funding-test.js
 */

export let options = {
    vus: 20,
    duration: '10s',
};

const successCount = new Counter('success_count');
const failCount = new Counter('fail_count');
const successDuration = new Trend('success_response_time');
const failDuration = new Trend('fail_response_time');

const BASE_URL = __ENV.BASE_URL || 'http://host.docker.internal:8080';

export default function() {
    const payload = JSON.stringify({
        projectId: '99999999-9999-9999-9999-999999999999',
        rewardItems: [
            {
                rewardId: '11111111-1111-1111-1111-111111111111',
                optionId: null,
                quantity: 1
            },
            {
                rewardId: '22222222-2222-2222-2222-222222222222',
                optionId: null,
                quantity: 1
            }
        ],
        donationAmount: 1000,
        pgPaymentKey: `test_key_${__VU}_${__ITER}`,
        pgOrderId: `order_${__VU}_${__ITER}`,
        pgMethod: 'CARD'
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            'X-User-Id': '88888888-8888-8888-8888-888888888888',
        },
    };

    const response = http.post(
        `${BASE_URL}/fundings`,
        payload,
        params
    );

    if (response.status === 201) {
        successCount.add(1);
        successDuration.add(response.timings.duration);
    } else if (response.status === 400) {
        failCount.add(1);
        failDuration.add(response.timings.duration);
    }

    check(response, {
        'status is 201 or 400': (r) => r.status === 201 || r.status === 400,
        'response has body': (r) => r.body && r.body.length > 0,
    });
}