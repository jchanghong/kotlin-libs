drop table if exists tb_test1;
create table tb_test1(
    id bigserial primary key ,
    name varchar,
    info text,
    aint bigint,
    ajsonb jsonb,
    ajson json
);