-- =====================================================
-- OAuth 관련 테이블 DDL
-- =====================================================

-- social_account 테이블 생성
CREATE TABLE social_account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '인덱싱 컬럼',
    account_id BIGINT NOT NULL COMMENT 'Account 참조 (약한 결합, FK 없음)',
    provider ENUM('KAKAO', 'NAVER', 'GOOGLE') NOT NULL COMMENT 'OAuth Provider',
    provider_account_id VARCHAR(100) NOT NULL COMMENT 'Provider 고유 ID',
    access_token VARCHAR(500) NOT NULL COMMENT 'Access Token (암호화)',
    refresh_token VARCHAR(500) NULL COMMENT 'Refresh Token (암호화)',
    access_token_expires_at DATETIME NOT NULL COMMENT 'Access Token 만료 시간',
    refresh_token_expires_at DATETIME NULL COMMENT 'Refresh Token 만료 시간',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '소셜 계정 상태 (ACTIVE, INACTIVE)',
    created_at DATETIME NOT NULL COMMENT '생성일',
    updated_at DATETIME NULL COMMENT '수정일',
    deleted_at DATETIME NULL COMMENT '비활성화(삭제) 일시',
    UNIQUE KEY uk_provider_account (provider, provider_account_id),
    INDEX idx_account_id (account_id),
    INDEX idx_status (status),
    INDEX idx_account_id_status (account_id, status)
) COMMENT 'OAuth 소셜 로그인 연동 정보 (Account와 약한 결합)';

-- oauth_state 테이블 생성
CREATE TABLE oauth_state (
    state VARCHAR(36) PRIMARY KEY COMMENT 'UUID State 값',
    provider ENUM('KAKAO', 'NAVER', 'GOOGLE') NOT NULL COMMENT 'OAuth Provider',
    created_at DATETIME NOT NULL COMMENT '생성일',
    expires_at DATETIME NOT NULL COMMENT '만료 시간',
    INDEX idx_expires_at (expires_at)
) COMMENT 'OAuth CSRF 방지용 State 저장';
