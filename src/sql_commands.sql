show tables;
desc category;
drop table category;
select * from roles;
select * from users_roles;
select * from users;
select * from category;

truncate category;
truncate users_roles;
truncate users;
SET SQL_SAFE_UPDATES = 1;
delete from users;
delete from roles;

select * from brands;
select * from brands_categories;