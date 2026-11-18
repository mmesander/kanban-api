CREATE TABLE tasks
(
    id          UUID                        NOT NULL PRIMARY KEY,
    title       VARCHAR(100)                NOT NULL,
    description VARCHAR(500),
    status      VARCHAR(20)                 NOT NULL,
    created_at  TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP(6) WITH TIME ZONE NOT NULL
);