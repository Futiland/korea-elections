CREATE TABLE vote
(
    id                    BIGINT AUTO_INCREMENT PRIMARY KEY,
    election_id           BIGINT   NOT NULL,
    selected_candidate_id BIGINT   NOT NULL,
    account_id            BIGINT   NOT NULL,
    created_at            DATETIME NOT NULL,
    updated_at            DATETIME,
    deleted_at            DATETIME,
    UNIQUE (election_id, account_id)
);