CREATE TABLE account
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY not null comment '인덱싱 컬럼',
    phone_number VARCHAR(100) not null comment '전화번호',
    ci           VARCHAR(100) not null comment 'Connecting Information 본인인증기관에서 개인의 주민등록번호를 암호화하여 생성한 고유한 값',
    name         VARCHAR(100) not null comment '이름',
    password     VARCHAR(100) not null comment '해싱한 비밀번호',
    gender       ENUM('MALE', 'FEMALE') not null comment '성별',
    birth_date   DATE         not null comment '생년월일',
    status       ENUM('ACTIVE', 'INACTIVE') not null comment '상태',
    created_at   DATETIME     not null comment '생성일',
    deleted_at   DATETIME null comment '삭제일'
);
