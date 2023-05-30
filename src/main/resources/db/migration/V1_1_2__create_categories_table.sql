CREATE TABLE IF NOT EXISTS `categories`
(
    `name`       varchar(100) NOT NULL,
    `slug`       varchar(103) NOT NULL,
    `created_at` timestamp    NOT NULL,
    `updated_at` timestamp    NULL DEFAULT NULL,
    `deleted_at` timestamp    NULL DEFAULT NULL
);