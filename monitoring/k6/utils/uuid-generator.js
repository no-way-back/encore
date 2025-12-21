/**
 * UUID v4 생성 함수
 *
 * K6 테스트에서 고유한 식별자 생성 시 사용
 * 예: userId, fundingId, orderId 등
 *
 * @returns {string} UUID v4 형식 문자열
 *
 * 사용 예시:
 * const userId = generateUUID();
 * const fundingId = generateUUID();
 */
export function generateUUID() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        const r = Math.random() * 16 | 0;
        const v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}
