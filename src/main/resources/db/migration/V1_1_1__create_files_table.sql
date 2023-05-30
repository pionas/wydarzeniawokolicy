CREATE TABLE IF NOT EXISTS `files`
(
    `hash`       varchar(32)  NOT NULL,
    `name`       varchar(50)  NOT NULL,
    `path`       varchar(250) NOT NULL,
    `created_at` timestamp    NOT NULL,
    `updated_at` timestamp    NULL DEFAULT NULL,
    `deleted_at` timestamp    NULL DEFAULT NULL
);