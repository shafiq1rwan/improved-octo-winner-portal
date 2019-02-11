CREATE TABLE users
(
	id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
	"name" varchar(150) not null,
	email varchar(100) not null,
	mobileNumber varchar(50),
	"address" varchar(200),
	username varchar(256) not null UNIQUE,
	"password" varchar(256) not null,
	"enabled" bit not null
);

CREATE TABLE authorities
(
	id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
	user_id BIGINT not null,
	authority varchar(256) not null
);

CREATE TABLE brands
(
	id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
	"name" nvarchar(256) NOT NULL,
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
	permission VARCHAR(10) NOT NULL
);

-- Patch first admin and brand-admin
INSERT INTO users("name",email,mobileNumber,"address",username,"password","enabled") VALUES ('admin', 'admin@mpsb.net','013-3456789','usj21','admin','$2a$10$wohHqv2iUEhboYehz8AP8eVvnIreAeH4ZTZYyubCE8JkLbzjEK4c2',1),('KFC','kfc@example.com','011-9871010','klcc','KFC','$2a$10$h9oHzb3EzGUM1c3/20SU0Oq96ESHzfV72oVR3meRMCfVm790M0zIO',1),('SUSHI','sushi@example.com','013-9271010','PJ','SUSHI','$2a$10$Cwa9ruZgdmPL0vz9QN0C4eLc0nWrRJK.O/9kqhkq2Zl6phP69hAEO',1);
INSERT INTO authorities(user_id,authority)VALUES(1,'ROLE_SUPER_ADMIN'),(2,'ROLE_ADMIN'),(3,'ROLE_ADMIN');

-- Patch sample brand
INSERT INTO brands("name", brand_db_domain, brand_db_name, brand_db_user, brand_db_password, brand_db_port)
VALUES('kfc','localhost','KFC','sa','MPay@1234',1433),('sushi','localhost','SUSHI','sa','MPay@1234',1433);

-- Patch: Assign brands to users
INSERT INTO users_brands(brand_id,user_id,permission) VALUES(1,2,'0'),(2,3,'0');

--drop table authorities;
--drop table users;
--drop table brands;
--drop table users_brands;

