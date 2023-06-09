CREATE TABLE IF NOT EXISTS `users_roles`
(
    `user_id`    bigint NOT NULL,
    `role_id`    bigint NOT NULL,
    `created_at` timestamp DEFAULT CURRENT_TIMESTAMP
);