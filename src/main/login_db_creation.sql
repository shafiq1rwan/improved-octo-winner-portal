DROP SCHEMA IF EXISTS tenant;
create schema tenant;
USE tenant;
CREATE TABLE users
(
	id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	`name` varchar(150) not null,
	email varchar(100) not null,
	mobileNumber varchar(50),
	`address` varchar(200),
	username varchar(256) not null UNIQUE,
	`password` varchar(256) not null,
	`enabled` tinyint not null
);

CREATE TABLE authorities
(
	id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	user_id BIGINT not null,
	authority varchar(256) not null
);

CREATE TABLE brands
(
	id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	`name` nvarchar(256) NOT NULL,
	brand_db_domain varchar(256) NOT NULL,
	brand_db_name varchar(256) NOT NULL,
	brand_db_user varchar(256) NOT NULL,
	brand_db_password varchar(256) NOT NULL,
	brand_db_port INT NOT NULL
);

CREATE TABLE users_brands
(
	brand_id BIGINT NOT NULL,
	user_id BIGINT NOT NULL,
	permission VARCHAR(10) DEFAULT '0'
);

CREATE TABLE permission_lookup
(
	id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	perm_name nvarchar(256) NOT NULL
);

CREATE TABLE password_reset_token
(
	id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	user_id BIGINT NOT NULL,
	token NVARCHAR(256) NOT NULL,
	`expiry_date` DATETIME(3) NOT NULL
);

-- CREATE TABLE alt_permission_lookup
-- (
--	id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
--	perm_store varchar(10) DEFAULT '0',
--	perm_group_category varchar(10) DEFAULT '0',
--	perm_menu_item varchar(10) DEFAULT '0',
--	perm_user_config varchar(10) DEFAULT '0',
--	perm_setting varchar(10) DEFAULT '0'
-- )

-- Patch first admin and brand-admin
INSERT INTO users(`name`,email,mobileNumber,`address`,username,`password`,`enabled`) VALUES ('admin', 'admin@mpsb.net','013-3456789','usj21','admin','$2a$10$wohHqv2iUEhboYehz8AP8eVvnIreAeH4ZTZYyubCE8JkLbzjEK4c2',1),('KFC','kfc@example.com','011-9871010','klcc','KFC','$2a$10$h9oHzb3EzGUM1c3/20SU0Oq96ESHzfV72oVR3meRMCfVm790M0zIO',1),('SUSHI','sushi@example.com','013-9271010','PJ','SUSHI','$2a$10$Cwa9ruZgdmPL0vz9QN0C4eLc0nWrRJK.O/9kqhkq2Zl6phP69hAEO',1),
('KFCS','kfcSales@example.com','010-9271010','PJ','KFCS','$2a$10$h9oHzb3EzGUM1c3/20SU0Oq96ESHzfV72oVR3meRMCfVm790M0zIO',1),('KFCO','kfcOperation@example.com','011-8271010','PJ','KFCO','$2a$10$h9oHzb3EzGUM1c3/20SU0Oq96ESHzfV72oVR3meRMCfVm790M0zIO',1);
INSERT INTO authorities(user_id,authority)VALUES(1,'ROLE_SUPER_ADMIN'),(2,'ROLE_ADMIN'),(3,'ROLE_ADMIN'),(4,'ROLE_USER'),(5,'ROLE_USER');

INSERT INTO users(`name`,email,mobileNumber,`address`,username,`password`,`enabled`) VALUES ('cg', 'cg@mpsb.net','010-3456789','usj21','CG','$2a$10$wohHqv2iUEhboYehz8AP8eVvnIreAeH4ZTZYyubCE8JkLbzjEK4c2',1);
INSERT INTO authorities(user_id,authority)VALUES(6,'ROLE_SUPER_GROUP_ADMIN');

-- Patch sample brand
-- INSERT INTO brands(`name`, brand_db_domain, brand_db_name, brand_db_user, brand_db_password, brand_db_port)
-- VALUES('kfc','localhost','KFC','sa','MPay@1234',1433),('sushi','localhost','SUSHI','sa','MPay@1234',1433);

-- Patch: Assign brands to users
-- INSERT INTO users_brands(brand_id,user_id,permission) VALUES(1,2,'0'),(2,3,'0'),(1,4,'0'),(1,5,'0');

-- INSERT INTO users_brands(brand_id,user_id,permission) VALUES(1,6,'0'),(2,6,'0');

-- Patch: Permission lookup
INSERT INTO permission_lookup(perm_name) VALUES ('store'),('group-category'),('menu-item'),('user-config'),('report'),('setting');

-- drop table authorities;
-- drop table users;
-- drop table brands;
-- drop table users_brands;
-- drop table permission_lookup;
-- drop table alt_permission_lookup;
-- drop table password_reset_token;

