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