CREATE TABLE candidate
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL COMMENT '인덱싱 컬럼',
    election_id BIGINT       NOT NULL COMMENT '선거 ID',
    name        VARCHAR(100) NOT NULL COMMENT '후보 성함',
    party_id    BIGINT       NOT NULL COMMENT '정당 ID',
    number      INT(100)     NOT NULL COMMENT '후보 번호',
    description VARCHAR(100) NOT NULL COMMENT '설명',
    status      ENUM('ACTIVE', 'INACTIVE') NOT NULL COMMENT '상태',
    created_at  DATETIME     NOT NULL COMMENT '생성일',
    deleted_at  DATETIME NULL COMMENT '삭제일',
);

ALTER TABLE candidate
    ADD COLUMN party VARCHAR(100) NOT NULL DEFAULT '정당' COMMENT '정당' AFTER name;

ALTER TABLE candidate
DROP COLUMN party,
    ADD COLUMN party_id BIGINT NOT NULL DEFAULT 1 COMMENT '정당 ID' AFTER name;