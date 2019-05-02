alter table USER add GROUP_ID INTEGER default 0 not null;

create table GROUP (
  ID INTEGER not null,
  NAME varchar(255) not null,
  OWNER_ID INTEGER not null,
  CREATED timestamp not null,
  primary key (ID),
);

create table ACCESS_RIGHT_HISTORY (
  ID INTEGER not null,
  GROUP_ID INTEGER not null,
  CREATED timestamp not null,
  OLD_ACCESS_RIGHT varchar(255),
  primary key (ID),
);

insert into GROUP
(ID, NAME, OWNER_ID, CREATED)
values (0, 'TestGroup', 0, now);
commit;

insert into GROUP
(ID, GROUP_ID, CREATED, OLD_ACCESS_RIGHT)
values (0, 0, now, '...');
commit;