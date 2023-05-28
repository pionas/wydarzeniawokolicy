CREATE TABLE IF NOT EXISTS `users`
(
    `id`         bigint       NOT NULL AUTO_INCREMENT,
    `name`       varchar(30)  NOT NULL,
    `email`      varchar(320) NOT NULL,
    `password`   varchar(100) NOT NULL,
    `salt`       varchar(10)  NOT NULL,
    `created_at` timestamp    NOT NULL,
    `updated_at` timestamp    NULL DEFAULT NULL,
    `deleted_at` timestamp    NULL DEFAULT NULL
);

-- password validPassword
INSERT INTO users (id, name, email, password, salt, created_at, updated_at, deleted_at) VALUES (1, 'john.doe', 'john.doe@example.com', '$2a$10$oW/L9Ke5ozjjA0Er.ccFYu4.zje6XIyUiEISvsiAbVxaA2H7zO8TK', 'salt', '2023-05-23 13:07:00', null, null);