create table VCD_USER (
  USER_ID varchar(20) not null ,
  VAPP_ID varchar(100) not null ,
  VERSION_NO integer
);

alter table VCD_USER add primary key (
          USER_ID,
          VAPP_ID
);





create table DELETED_VAPP (

	VAPP_ID varchar(100) not null primary key,
	VAPP_NAME varchar(100) not null ,
  	CPU integer not null ,
  	MEMORY_SIZE_MB integer not null ,
  	TOTAL_HDDGB integer not null ,
  	LAST_MAX_COST integer not null ,
  	AUTHER1 varchar(100) not null ,
  	AUTHER2 varchar(100) not null ,
  	DELETE_DATE DATE not null ,
	VERSION_NO integer
);






