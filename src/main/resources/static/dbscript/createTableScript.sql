CREATE TABLE group_category
(
	id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
	group_category_name NVARCHAR(50) NOT NULL UNIQUE,
	created_date DATETIME NOT NULL DEFAULT GETDATE()
);

CREATE TABLE category
(
	id bigInt PRIMARY KEY identity(1,1) NOT NULL,
	group_category_id bigInt NOT NULL,
	category_name nvarchar(150) NOT NULL UNIQUE,
	category_description nvarchar(255),
	category_image_path nvarchar(MAX),
	category_sequence INT,
	is_active BIT DEFAULT 1,
	created_date DATETIME NOT NULL DEFAULT GETDATE()
);

CREATE TABLE menu_item
(
	id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
	backend_id NVARCHAR(50) NOT NULL UNIQUE,
	modifier_group_id BIGINT,
	menu_item_name NVARCHAR(150) NOT NULL,
	menu_item_description NVARCHAR(255),
	menu_item_image_path NVARCHAR(MAX),
	menu_item_base_price DECIMAL(10,2) DEFAULT 0.00,
	menu_item_type INT DEFAULT 0, 
	is_taxable BIT DEFAULT 0,
	is_discountable BIT DEFAULT 0,
	is_active BIT DEFAULT 1,
	created_date DATETIME NOT NULL DEFAULT GETDATE()
);

CREATE TABLE category_menu_item
(
	category_id BIGINT NOT NULL,
	menu_item_id BIGINT NOT NULL,
	category_menu_item_sequence INT NOT NULL
);

CREATE TABLE menu_item_group
(
	id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
	menu_item_group_name NVARCHAR(150) NOT NULL UNIQUE,
	created_date DATETIME NOT NULL DEFAULT GETDATE()
);

--CREATE TABLE menu_item_group_menu_item
--(	
--	menu_item_group_id BigInt,
--	menu_item_id BigInt,
--	menu_item_group_menu_item_sequence INT NOT NULL	
--);

CREATE TABLE menu_item_group_sequence
(
	menu_item_group_id BigInt,
	menu_item_id BigInt,
	menu_item_group_sequence INT NOT NULL	
);

CREATE TABLE modifier_group
(
	id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
	modifier_group_name NVARCHAR(100) NOT NULL UNIQUE,
	is_active BIT DEFAULT 1,
	created_date DATETIME NOT NULL DEFAULT GETDATE()
);

CREATE TABLE menu_item_modifier_group
(
	menu_item_id BIGINT,
	modifier_group_id BIGINT,
	menu_item_modifier_group_sequence INT
);

CREATE TABLE modifier_item_sequence
(
	modifier_group_id BIGINT NOT NULL,
	menu_item_id BIGINT NOT NULL,
	modifier_item_sequence INT
);

CREATE TABLE combo_detail
(
	id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
	menu_item_id BIGINT NOT NULL,
	combo_detail_name NVARCHAR(50) NOT NULL,
	combo_detail_quantity INT DEFAULT 0,
	combo_detail_sequence INT NOT NULL,
	created_date DATETIME NOT NULL DEFAULT GETDATE()
);

CREATE TABLE combo_item_detail
(
	id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
	combo_detail_id BIGINT NOT NULL,
	menu_item_id BIGINT,
	menu_item_group_id BIGINT,
	combo_item_detail_sequence INT NOT NULL,
	created_date DATETIME NOT NULL DEFAULT GETDATE()
);

CREATE TABLE store
(
		id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
		group_category_id BIGINT DEFAULT 0,
		tax_charge_id BIGINT DEFAULT 0,
		backend_id NVARCHAR(50) NOT NULL UNIQUE,
		store_name NVARCHAR(150) NOT NULL UNIQUE,
		store_logo_path NVARCHAR(MAX),
		store_address NVARCHAR(150),
		store_longitude DECIMAL(15,8),
		store_latitude DECIMAL(15,8),
		store_country NVARCHAR(100),
		store_currency NVARCHAR(50),
		store_table_count INT DEFAULT 0,
		store_start_operating_time time NOT NULL,
		store_end_operating_time time NOT NULL,
		last_update_date datetime,
		is_publish BIT DEFAULT 0,
		ecpos BIT DEFAULT 0,
		created_date DATETIME NOT NULL DEFAULT GETDATE()
)

CREATE TABLE staff 
(
		id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
		store_id BIGINT DEFAULT 0,
		staff_name NVARCHAR(150) NOT NULL,
		staff_username NVARCHAR(100) NOT NULL UNIQUE,
		staff_password NVARCHAR(200) NOT NULL,
		staff_role INT NOT NULL,
		staff_contact_hp_number NVARCHAR(50) NOT NULL UNIQUE,
		staff_contact_email VARCHAR(320) NOT NULL UNIQUE,
		is_active BIT DEFAULT 1 NOT NULL,
		created_date DATETIME NOT NULL DEFAULT GETDATE(),
		last_update_date DATETIME
)

CREATE TABLE role_lookup
(
	id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
	role_name NVARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO role_lookup VALUES ('Admin'),('Store Manager');

CREATE TABLE table_log
(
	id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
	login_user_id BIGINT NOT NULL,
	username NVARCHAR(100) NOT NULL,
	user_action NVARCHAR(MAX) NOT NULL,
	table_name NVARCHAR(100) NOT NULL,
	table_log_datetime DATETIME NOT NULL DEFAULT GETDATE()
);

-- no del
CREATE TABLE tax_charge
(
	id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
	tax_charge_name NVARCHAR(100) NOT NULL UNIQUE,
	rate INT DEFAULT 0,
	charge_type INT DEFAULT 1,
	is_active BIT DEFAULT 0,
	created_date DATETIME NOT NULL DEFAULT GETDATE()
);

CREATE TABLE charge_type_lookup
(
	charge_type_number INT UNIQUE NOT NULL, 
	charge_type_name NVARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE menu_item_tax_charge
(
	menu_item_id BIGINT NOT NULL,
	tax_charge_id BIGINT NOT NULL
);

INSERT INTO charge_type_lookup VALUES (0, 'None'),(1, 'Tax'),(2, 'Charge');

CREATE TABLE menu_item_type_lookup
(
	menu_item_type_number INT NOT NULL UNIQUE,
	menu_item_type_name NVARCHAR(50) NOT NULL UNIQUE
)

CREATE TABLE device_info
(
	id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
	activation_id NVARCHAR(50) NOT NULL UNIQUE,
	activation_key NVARCHAR(50) NOT NULL,
	mac_address NVARCHAR(50),
	status_lookup_id BIGINT DEFAULT 0,
	device_type_lookup_id BIGINT DEFAULT 0,
	ref_id BIGINT DEFAULT 0,
	created_date DATETIME NOT NULL DEFAULT GETDATE(),
	last_update_date DATETIME 
);

CREATE TABLE device_type_lookup
(
	id INT UNIQUE NOT NULL, 
	name NVARCHAR(50) NOT NULL,
	prefix NVARCHAR(2) NOT NULL UNIQUE
);

INSERT INTO device_type_lookup (id, name, prefix) VALUES (1, 'ECPOS', 'EC')
INSERT INTO device_type_lookup (id, name, prefix) VALUES (2, 'BYOD', 'BD')
INSERT INTO device_type_lookup (id, name, prefix) VALUES (3, 'KIOSK', 'KK')

CREATE TABLE status_lookup
(
	id INT UNIQUE NOT NULL, 
	name NVARCHAR(50) NOT NULL UNIQUE 
);

CREATE TABLE backend_sequence
(
	id BIGINT PRIMARY KEY NOT NULL,
	backend_sequence_code NVARCHAR(20) UNIQUE NOT NULL,
	backend_sequence_name NVARCHAR(100) UNIQUE NOT NULL, 
	backend_sequence INT NOT NULL,
	modified_date DATE NOT NULL DEFAULT GETDATE()
);

INSERT INTO status_lookup (id, name) VALUES (1, 'PENDING')
INSERT INTO status_lookup (id, name) VALUES (2, 'ACTIVE')
INSERT INTO status_lookup (id, name) VALUES (3, 'INACTIVE')

INSERT INTO menu_item_type_lookup ([menu_item_type_number],[menu_item_type_name]) VALUES(0,'A La Carte');
INSERT INTO menu_item_type_lookup ([menu_item_type_number],[menu_item_type_name]) VALUES(1,'Combo');
INSERT INTO menu_item_type_lookup ([menu_item_type_number],[menu_item_type_name]) VALUES(2,'Modifier');

INSERT INTO group_category([group_category_name]) VALUES ('Breakfast kfc');

INSERT INTO backend_sequence([id], [backend_sequence_code], [backend_sequence_name], [backend_sequence], [modified_date]) VALUES(1, 'S','Store',0,GETDATE());
INSERT INTO backend_sequence([id], [backend_sequence_code], [backend_sequence_name], [backend_sequence], [modified_date]) VALUES(2, 'imgS','Store Image',0,GETDATE());
INSERT INTO backend_sequence([id], [backend_sequence_code], [backend_sequence_name], [backend_sequence], [modified_date]) VALUES(3, 'imgMI','Menu Item Image', 0,GETDATE());
INSERT INTO backend_sequence([id], [backend_sequence_code], [backend_sequence_name], [backend_sequence], [modified_date]) VALUES(4, 'imgC','Category Image', 0,GETDATE());

/*Drop all Table*/
--DROP TABLE group_category;
--DROP TABLE store;
--DROP TABLE staff;
--DROP TABLE role_lookup;
--DROP TABLE category;
--DROP TABLE menu_item;
--DROP TABLE category_menu_item;
--DROP TABLE menu_item_group;
--DROP TABLE menu_item_group_sequence;
--DROP TABLE modifier_group;
--DROP TABLE menu_item_modifier_group;
--DROP TABLE modifier_item_sequence;
--DROP TABLE combo_detail;
--DROP TABLE combo_item_detail;
--DROP TABLE menu_item_type_lookup;
--DROP TABLE tax_charge;
--DROP TABLE charge_type_lookup;
--DROP TABLE menu_item_tax_charge;
--DROP TABLE table_log;
--DROP TABLE device_info;
--DROP TABLE device_type_lookup;
--DROP TABLE status_lookup;
--DROP TABLE backend_sequence;
