-- liquibase formatted sql

-- changeset dKovachev:1
create table notification_task(
    chatId integer primary key,
    task varchar,
    time time
);
-- changeset dKovachev:2
alter table notification_task alter column chatId type bigint;

-- changeset dKovachev:3
alter table notification_task drop column chatId;