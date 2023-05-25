show tables;
desc customers;
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
desc category;
desc products;
select * from products;
select * from product_detail;

desc product_images;
select * from product_images;
DELETE from product_images WHERE name = "";
truncate table products;
truncate table product_detail;
truncate table product_images;
select * from category;

SELECT p.name, c.name from products p JOIN category c ON p.category_id = c.id 
WHERE c.all_parent_ids LIKE '%-23-%';
select * from roles;
show tables;

select * from settings;
desc settings;
select * from settings_category;
select * from products where price>1000;

select * from country where name = "Nigeria";
select * from state;
delete from country where name = "China";
select * from state;
truncate state;
delete from country;

select * from country;
select * from state where name = "";
delete from state where name ="";

desc customers;
select * from customers;
select * from customers where email = "abdulmaliknurudeen4@gmail.com";
desc cart_items;