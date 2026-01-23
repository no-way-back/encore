export const concurrencyThresholds = {
    http_req_duration: ['p(95)<5000'],
    http_req_failed: ['rate<0.1'],
};

export const performanceThresholds = {
    http_req_duration: [
        'p(95)<1000',
        'avg<2000',
    ],
    http_req_failed: ['rate<0.5'],  // 실패율 50% 이하 (재고 비율 고려)
};

export const loadTestThresholds = {
    http_req_duration: ['p(95)<3000'],
    http_req_failed: ['rate<0.05'],
};

export const peakLoadThresholds = {
    http_req_duration: ['p(95)<10000'],
    http_req_failed: ['rate<0.15'],
};

export const smokeTestThresholds = {
    http_req_duration: ['p(95)<1000'],
    http_req_failed: ['rate<0.01'],
};