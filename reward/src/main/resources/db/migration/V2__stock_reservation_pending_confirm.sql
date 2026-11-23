-- 재고 예약을 임시예약(PENDING) → 확정(CONFIRMED) / 복원(RESTORED) 3단계로 관리한다
-- PENDING 예약은 expires_at까지 결제가 확정되지 않으면 만료 스케줄러가 재고를 복원한다

ALTER TABLE p_stock_reservations ADD COLUMN IF NOT EXISTS expires_at TIMESTAMP;

ALTER TABLE p_stock_reservations DROP CONSTRAINT IF EXISTS chk_reservation_status;

-- 기존 DEDUCTED 예약은 이미 차감이 끝난 건이므로 확정으로 본다 (만료 복원 대상에서 제외)
UPDATE p_stock_reservations SET status = 'CONFIRMED' WHERE status = 'DEDUCTED';

ALTER TABLE p_stock_reservations
    ADD CONSTRAINT chk_reservation_status CHECK (status IN ('PENDING', 'CONFIRMED', 'RESTORED'));

CREATE INDEX IF NOT EXISTS idx_stock_reservation_status_expires_at
    ON p_stock_reservations (status, expires_at);
