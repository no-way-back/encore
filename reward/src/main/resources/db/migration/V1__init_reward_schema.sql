-- reward_service 스키마 생성 (이미 존재할 수 있음)
CREATE SCHEMA IF NOT EXISTS reward_service;

SET search_path TO reward_service;

-- p_rewards 테이블
CREATE TABLE p_rewards (
                           id UUID PRIMARY KEY,
                           project_id UUID NOT NULL,
                           owner_id UUID NOT NULL,
                           name VARCHAR(200) NOT NULL,
                           description VARCHAR(1000),
                           price BIGINT NOT NULL,
                           stock_quantity INTEGER NOT NULL,
                           shipping_fee BIGINT NOT NULL,
                           free_shipping_amount BIGINT,
                           purchase_limit_per_person INTEGER,
                           reward_type VARCHAR(20) NOT NULL,
                           status VARCHAR(20) NOT NULL,
                           is_deleted BOOLEAN NOT NULL DEFAULT false,
                           created_at TIMESTAMP NOT NULL,
                           updated_at TIMESTAMP NOT NULL,
                           CONSTRAINT chk_reward_type CHECK (reward_type IN ('GENERAL', 'TICKET')),
                           CONSTRAINT chk_reward_status CHECK (status IN ('AVAILABLE', 'SOLD_OUT', 'DISCONTINUED'))
);

-- p_reward_option 테이블
CREATE TABLE p_reward_option (
                                 id UUID PRIMARY KEY,
                                 reward_id UUID NOT NULL,
                                 name VARCHAR(30) NOT NULL,
                                 additional_price BIGINT NOT NULL,
                                 stock_quantity INTEGER NOT NULL,
                                 is_required BOOLEAN NOT NULL,
                                 display_order INTEGER NOT NULL,
                                 status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
                                 created_at TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP NOT NULL,
                                 CONSTRAINT fk_reward_option_reward FOREIGN KEY (reward_id) REFERENCES p_rewards(id),
                                 CONSTRAINT chk_option_status CHECK (status IN ('AVAILABLE', 'SOLD_OUT', 'DISCONTINUED'))
);

-- p_stock_reservations 테이블
CREATE TABLE p_stock_reservations (
                                      id UUID PRIMARY KEY,
                                      user_id UUID NOT NULL,
                                      funding_id UUID NOT NULL,
                                      reward_id UUID NOT NULL,
                                      option_id UUID,
                                      quantity INTEGER NOT NULL,
                                      status VARCHAR(20) NOT NULL,
                                      created_at TIMESTAMP NOT NULL,
                                      updated_at TIMESTAMP NOT NULL,
                                      CONSTRAINT chk_reservation_status CHECK (status IN ('DEDUCTED', 'RESTORED'))
);

-- p_qr_codes 테이블
CREATE TABLE p_qr_codes (
                            id UUID PRIMARY KEY,
                            reward_id UUID NOT NULL,
                            funding_id UUID NOT NULL,
                            email VARCHAR(255) NOT NULL,
                            title VARCHAR(255) NOT NULL,
                            qr_code_image_url VARCHAR(1000),
                            status VARCHAR(20) DEFAULT 'UNUSED',
                            used_at TIMESTAMP,
                            CONSTRAINT chk_qr_status CHECK (status IN ('UNUSED', 'USED', 'EXPIRED', 'CANCELLED'))
);

-- p_reward_outbox 테이블
CREATE TABLE p_reward_outbox (
                                 id UUID PRIMARY KEY,
                                 aggregate_type VARCHAR(50) NOT NULL,
                                 aggregate_id UUID NOT NULL,
                                 event_type VARCHAR(100) NOT NULL,
                                 destination VARCHAR(50) NOT NULL,
                                 payload TEXT NOT NULL,
                                 payload_type VARCHAR(500) NOT NULL,
                                 status VARCHAR(20) NOT NULL,
                                 retry_count INTEGER NOT NULL DEFAULT 0,
                                 error_message TEXT,
                                 created_at TIMESTAMP NOT NULL,
                                 published_at TIMESTAMP,
                                 CONSTRAINT chk_outbox_status CHECK (status IN ('PENDING', 'PUBLISHED', 'FAILED'))
);

-- p_idempotent_keys 테이블 (멱등성 보장)
CREATE TABLE p_idempotent_keys (
                                   event_id VARCHAR(100) PRIMARY KEY,
                                   project_id VARCHAR(255) NOT NULL,
                                   processed_at TIMESTAMP NOT NULL
);

-- 인덱스 생성
CREATE INDEX idx_rewards_project_id ON p_rewards(project_id);
CREATE INDEX idx_rewards_owner_id ON p_rewards(owner_id);
CREATE INDEX idx_rewards_status ON p_rewards(status);
CREATE INDEX idx_rewards_is_deleted ON p_rewards(is_deleted);

CREATE INDEX idx_reward_option_reward_id ON p_reward_option(reward_id);
CREATE INDEX idx_reward_option_status ON p_reward_option(status);

CREATE INDEX idx_stock_reservations_user_id ON p_stock_reservations(user_id);
CREATE INDEX idx_stock_reservations_funding_id ON p_stock_reservations(funding_id);
CREATE INDEX idx_stock_reservations_reward_id ON p_stock_reservations(reward_id);
CREATE INDEX idx_stock_reservations_status ON p_stock_reservations(status);

CREATE INDEX idx_qr_codes_reward_id ON p_qr_codes(reward_id);
CREATE INDEX idx_qr_codes_funding_id ON p_qr_codes(funding_id);
CREATE INDEX idx_qr_codes_email ON p_qr_codes(email);
CREATE INDEX idx_qr_codes_status ON p_qr_codes(status);

CREATE INDEX idx_outbox_status ON p_reward_outbox(status);
CREATE INDEX idx_outbox_created_at ON p_reward_outbox(created_at);
CREATE INDEX idx_outbox_aggregate ON p_reward_outbox(aggregate_type, aggregate_id);

CREATE INDEX idx_idempotent_keys_project_id ON p_idempotent_keys(project_id);
CREATE INDEX idx_idempotent_keys_processed_at ON p_idempotent_keys(processed_at);

-- 코멘트 추가
COMMENT ON TABLE p_rewards IS '리워드 메인 테이블';
COMMENT ON TABLE p_reward_option IS '리워드 옵션 테이블';
COMMENT ON TABLE p_stock_reservations IS '재고 예약 테이블 (펀딩 실패 시 재고 복원용)';
COMMENT ON TABLE p_qr_codes IS 'QR 코드 테이블 (티켓형 리워드)';
COMMENT ON TABLE p_reward_outbox IS 'Outbox 패턴을 위한 이벤트 저장 테이블';
COMMENT ON TABLE p_idempotent_keys IS '이벤트 중복 처리 방지 테이블';