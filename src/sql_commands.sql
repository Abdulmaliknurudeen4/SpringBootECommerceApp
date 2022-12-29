show tables;
select * from user_roles;
select * from users;

truncate user_roles;
truncate users;
SET SQL_SAFE_UPDATES = 1;
delete from users;