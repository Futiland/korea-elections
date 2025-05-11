CREATE TABLE election
(
    id              BIGINT AUTO_INCREMENT PRIMARY KEY not null  comment '인덱싱 컬럼',
    title           VARCHAR(100) not null comment '선거 제목',
    start_date_time DATETIME     not null comment '선거 시작일',
    end_date_time   DATETIME     not null comment '선거 종료일',
    description     VARCHAR(100) not null comment '설명',
    status          ENUM('ACTIVE', 'INACTIVE', 'EXPIRED', 'DELETED') not null comment '상태',
    created_at      DATETIME     not null comment '생성일',
    deleted_at      DATETIME null comment '삭제일'
);
