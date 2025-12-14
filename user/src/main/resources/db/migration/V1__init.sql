create table if not exists p_users
(
    id         uuid         not null    primary key,
    username   varchar(20)  not null    unique,
    password   varchar(100) not null,
    email      varchar(255) not null    unique,
    nickname   varchar(20)  not null    unique,
    role       varchar(10)  not null,
    status     varchar(20)  not null,
    created_at timestamp(6),
    created_by uuid,
    updated_at timestamp(6),
    updated_by uuid,
    deleted_at timestamp(6),
    deleted_by uuid
);

-- Comments
comment on table p_users is '유저 테이블';