alter table USER add USERGROUP_ID INTEGER default null;
drop index accountIdx;
create unique index usergroupAccountIdx on USER(USERGROUP_ID, ACCOUNT);

create table USERGROUP (
  ID INTEGER not null,
  NAME varchar(255) not null,
  OWNER_ID INTEGER not null,
  CREATED timestamp not null,
  primary key (ID),
);
alter table USERGROUP add foreign key (OWNER_ID) references USER(ID) ON DELETE CASCADE;
create unique index usergroupOwnerNameIdx on USERGROUP(OWNER_ID, NAME);
alter table USERGROUP                   alter column ID generated by default as identity (start with 93);

create cached table USERGROUP_ROBOTS (
  USERGROUP_ID INTEGER not null,
  ROBOT_ID INTEGER not null,
  primary key (USERGROUP_ID, ROBOT_ID)
);

create table ACCESSRIGHT_HISTORY (
  ID INTEGER not null,
  USERGROUP_ID INTEGER not null,
  CREATED timestamp not null,
  OLD_ACCESS_RIGHT varchar(255),
  primary key (ID),
);
alter table ACCESSRIGHT_HISTORY add foreign key (USERGROUP_ID) references USERGROUP(ID) ON DELETE CASCADE;
alter table ACCESSRIGHT_HISTORY         alter column ID generated by default as identity (start with 93);
