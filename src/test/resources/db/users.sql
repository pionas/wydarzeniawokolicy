INSERT INTO users (id, name, email, password, salt, created_at, updated_at, deleted_at)
VALUES (1, 'john.doe', 'john.doe@example.com', '$2a$10$oW/L9Ke5ozjjA0Er.ccFYu4.zje6XIyUiEISvsiAbVxaA2H7zO8TK', 'salt',
        '2023-05-23 13:07:00', null, null);
INSERT INTO users (id, name, email, password, salt, created_at, updated_at, deleted_at)
VALUES (2, 'anna.doe', 'anna.doe@example.com', '$2a$10$oW/L9Ke5ozjjA0Er.ccFYu4.zje6XIyUiEISvsiAbVxaA2H7zO8TK', 'salt', '2023-05-23 13:07:10', '2023-05-23 13:10:00', null);
INSERT INTO users (id, name, email, password, salt, created_at, updated_at, deleted_at)
VALUES (3, 'jane.doe', 'jane.doe@example.com', 'password', 'salt', '2023-05-23 13:07:20', null, null);