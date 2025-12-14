CREATE TABLE IF NOT EXISTS p_fundings
(
    id               UUID PRIMARY KEY,
    user_id          UUID         NOT NULL,
    project_id       UUID         NOT NULL,
    amount           BIGINT       NOT NULL CHECK (amount > 0),
    status           VARCHAR(20)  NOT NULL,
    payment_id       UUID,
    fail_reason      VARCHAR(500),
    idempotency_key  VARCHAR(100) NOT NULL UNIQUE,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- 펀딩 예약 테이블
CREATE TABLE IF NOT EXISTS p_funding_reservations
(
    id              UUID PRIMARY KEY,
    funding_id      UUID      NOT NULL,
    reservation_id  UUID      NOT NULL UNIQUE,
    reward_id       UUID      NOT NULL,
    option_id       UUID,
    quantity        INTEGER   NOT NULL,
    amount          BIGINT    NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_funding_reservation_funding FOREIGN KEY (funding_id) REFERENCES p_fundings (id) ON DELETE CASCADE
    );

-- 펀딩 프로젝트 통계 테이블
CREATE TABLE IF NOT EXISTS p_funding_project_statistics
(
    id                UUID PRIMARY KEY,
    project_id        UUID         NOT NULL UNIQUE,
    creator_id        UUID         NOT NULL,
    target_amount     BIGINT       NOT NULL CHECK (target_amount > 0),
    current_amount    BIGINT       NOT NULL DEFAULT 0 CHECK (current_amount >= 0),
    participant_count INTEGER      NOT NULL DEFAULT 0 CHECK (participant_count >= 0),
    start_date        TIMESTAMP    NOT NULL,
    end_date          TIMESTAMP    NOT NULL,
    status            VARCHAR(30)  NOT NULL,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- Outbox 테이블
CREATE TABLE IF NOT EXISTS funding_outbox
(
    id             UUID PRIMARY KEY,
    aggregate_type VARCHAR(50)  NOT NULL,
    aggregate_id   UUID         NOT NULL,
    event_type     VARCHAR(100) NOT NULL,
    payload        TEXT         NOT NULL,
    status         VARCHAR(20)  NOT NULL,
    retry_count    INTEGER      NOT NULL DEFAULT 0,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at   TIMESTAMP
    );

-- ===========================
-- 인덱스 생성
-- ===========================

-- 펀딩 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_funding_user_project_status
    ON p_fundings (user_id, project_id, status);

CREATE INDEX IF NOT EXISTS idx_funding_project_status
    ON p_fundings (project_id, status);

CREATE INDEX IF NOT EXISTS idx_funding_user_created
    ON p_fundings (user_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_funding_user_amount
    ON p_fundings (user_id, amount DESC);

-- 펀딩 프로젝트 통계 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_funding_project_end_status
    ON p_funding_project_statistics (end_date, status)
    WHERE status IN ('PROCESSING', 'REFUND_IN_PROGRESS');

-- Outbox 테이블 인덱스
CREATE INDEX IF NOT EXISTS idx_outbox_status_created
    ON funding_outbox (status, created_at)
    WHERE status IN ('PENDING', 'FAILED');

CREATE INDEX IF NOT EXISTS idx_outbox_aggregate
    ON funding_outbox (aggregate_type, aggregate_id);

-- ===========================
-- 코멘트
-- ===========================

COMMENT ON TABLE p_fundings IS '펀딩 건 테이블';
COMMENT ON TABLE p_funding_reservations IS '펀딩 예약 (리워드) 테이블';
COMMENT ON TABLE p_funding_project_statistics IS '프로젝트별 펀딩 통계 테이블';
COMMENT ON TABLE funding_outbox IS 'Outbox 이벤트 테이블';

COMMENT ON COLUMN p_fundings.status IS 'PENDING, COMPLETED, FAILED, CANCELLED';
COMMENT ON COLUMN p_funding_project_statistics.status IS 'SCHEDULED, PROCESSING, SUCCESS, REFUND_IN_PROGRESS, SETTLEMENT_IN_PROGRESS, FAILED';
COMMENT ON COLUMN funding_outbox.status IS 'PENDING, PUBLISHED, FAILED';