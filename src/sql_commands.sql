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
delete from brands;

select * from brands;
select * from brands_categories;
truncate brands;
truncate brands_categories;
select * from brands_categories;

desc products;
drop table products;