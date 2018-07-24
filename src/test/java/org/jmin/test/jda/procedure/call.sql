create or replace procedure sumInt(a in integer, b in integer,c out integer) is
begin

  c:=a+b;
end sumInt;
/

create or replace function getMax(a in integer, b in integer) return  Integer is
begin

  insert into CALL_TEST values('f1','f2','f3');
  
  if(a >b ) then
   return a;
  else
   return b;
  end if;

end getMax;
/

drop table CALL_TEST;
create table CALL_TEST(
  F1 VARCHAR(10)  NULL,
  F2 VARCHAR(10)  NULL,
  F3 VARCHAR(10)  NULL
);
/

create or replace function getList(a in integer, b in integer,PO_CUR out sys_refcursor) return Integer is
begin

  OPEN PO_CUR FOR SELECT * FROM CALL_TEST;
  
  return a +b;
  
end getList;
/