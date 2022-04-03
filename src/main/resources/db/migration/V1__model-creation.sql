create table player (
  id      varchar(255)   primary key,
  balance decimal(19, 2) not null,
  version bigint         not null
);

alter table player add check (balance >= 0);

create table transaction (
  id             identity       primary key,
  transaction_id varchar(255)   not null unique,
  player_id      varchar(255)   not null,
  type           integer        not null,
  amount         decimal(19, 2) not null
);

create index on transaction (player_id);
alter table transaction add foreign key (player_id) references player (id);
alter table transaction add check (amount >= 0);

