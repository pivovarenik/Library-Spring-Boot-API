Drop TABLE IF EXISTS book_status;

CREATE TABLE IF NOT EXISTS book_status (
                             book_id BIGINT NOT NULL PRIMARY KEY,
                             status VARCHAR(20) NOT NULL,
                             borrowed_at DATETIME DEFAULT NULL,
                             due_at DATETIME DEFAULT NULL
);

ALTER TABLE book_status
ADD CONSTRAINT status_check CHECK (status IN ('AVAILABLE', 'BORROWED', 'DELETED'));

ALTER TABLE book_status
ADD CONSTRAINT borrowed_due_check
CHECK (borrowed_at IS NULL OR due_at IS NULL OR borrowed_at <= due_at);