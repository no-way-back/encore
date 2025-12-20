import http from 'k6/http';
import { check, sleep } from 'k6';
import { concurrencyThresholds } from '../../config/thresholds.js';
import { generateUUID } from '../../utils/uuid-generator.js';

/**
 * Reward 서비스 - 재고 차감 동시성 테스트
 *
 * 목적: 동시 요청 환경에서 재고 데이터 정합성 검증
 * 시나리오: 500명이 동시에 재고 100개를 차감 요청
 * 예상 결과: 100명 성공, 400명 품절
 */

export let options = {
    vus: 500,              // 동시 사용자 500명
    duration: '1m',        // 1분간 실행
    thresholds: concurrencyThresholds,
};

// 환경변수 설정
const BASE_URL = __ENV.BASE_URL || 'http://host.docker.internal:18083';
const REWARD_ID = __ENV.TEST_DATA_ID;

export default function() {
    // 매 요청마다 고유한 userId, fundingId 생성
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

    // 재고 차감 API 호출
    const response = http.post(
        `${BASE_URL}/internal/rewards/reserve-stock`,
        payload,
        params
    );

    // 응답 검증
    check(response, {
        'status is 200 or 409': (r) => r.status === 200 || r.status === 409,
        'response has body': (r) => r.body.length > 0,
    });
}