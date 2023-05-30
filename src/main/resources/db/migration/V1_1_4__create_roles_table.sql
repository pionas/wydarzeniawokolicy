CREATE TABLE IF NOT EXISTS `roles`
(
    `name`       varchar(20) NOT NULL,
    `slug`       varchar(23) NOT NULL,
    `created_at` timestamp   NOT NULL,
    `updated_at` timestamp   NULL DEFAULT NULL,
    `deleted_at` timestamp   NULL DEFAULT NULL
);