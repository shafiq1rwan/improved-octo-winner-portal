-- ================================================
-- Template generated from Template Explorer using:
-- Create Procedure (New Menu).SQL
--
-- Use the Specify Values for Template Parameters 
-- command (Ctrl-Shift-M) to fill in the parameter 
-- values below.
--
-- This block of comments will not be included in
-- the definition of the procedure.
-- ================================================
USE tenant
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE PROCEDURE create_brand_db @db_name VARCHAR(100), @db_creation_result INT OUTPUT
AS
DECLARE @createDbSql NVARCHAR(100);
DECLARE @sql NVARCHAR(MAX);
DECLARE @metasql NVARCHAR(MAX);

IF NOT EXISTS(SELECT * FROM sys.databases WHERE name = @db_name)
	BEGIN	
		SET @createDbSql = N'CREATE DATABASE ' + @db_name;
		EXECUTE sp_executesql @createDbSql
	
		IF(db_id(@db_name) IS NOT NULL)
		BEGIN
			SET @sql ='
			CREATE PROCEDURE populate_all_tables
			AS
			BEGIN
				
				CREATE TABLE group_category
				(
					id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
					group_category_name NVARCHAR(50) NOT NULL UNIQUE,
					created_date DATETIME NOT NULL,
					publish_version_id bigint,
					tmp_query_file_path nvarchar(150),
					tmp_img_file_path nvarchar(150)
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
					created_date DATETIME NOT NULL
				);

				CREATE TABLE menu_item
				(
					id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
					backend_id NVARCHAR(50) NOT NULL UNIQUE,
					modifier_group_id BIGINT,
					menu_item_name NVARCHAR(150) NOT NULL,
					menu_item_alt_name NVARCHAR(50),
					menu_item_description NVARCHAR(255),
					menu_item_image_path NVARCHAR(MAX),
					menu_item_base_price DECIMAL(10,2) DEFAULT 0.00,
					menu_item_type INT DEFAULT 0, 
					is_taxable BIT DEFAULT 0,
					is_discountable BIT DEFAULT 0,
					is_active BIT DEFAULT 1,
					created_date DATETIME NOT NULL
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
					is_active BIT DEFAULT 1,
					created_date DATETIME NOT NULL
				);

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
					created_date DATETIME NOT NULL
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
					created_date DATETIME NOT NULL
				);

				CREATE TABLE combo_item_detail
				(
					id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
					combo_detail_id BIGINT NOT NULL,
					menu_item_id BIGINT,
					menu_item_group_id BIGINT,
					combo_item_detail_sequence INT NOT NULL,
					created_date DATETIME NOT NULL
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
						created_date DATETIME NOT NULL
				);

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
						created_date DATETIME NOT NULL,
						last_update_date DATETIME
				);

				CREATE TABLE role_lookup
				(
					id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
					role_name NVARCHAR(50) NOT NULL UNIQUE
				);

				CREATE TABLE table_log
				(
					id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
					login_user_id BIGINT NOT NULL,
					username NVARCHAR(100) NOT NULL,
					user_action NVARCHAR(MAX) NOT NULL,
					table_name NVARCHAR(100) NOT NULL,
					table_log_datetime DATETIME NOT NULL
				);

			-- no del
			CREATE TABLE tax_charge
			(
				id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
				tax_charge_name NVARCHAR(100) NOT NULL UNIQUE,
				rate INT DEFAULT 0,
				charge_type INT DEFAULT 1,
				is_active BIT DEFAULT 0,
				created_date DATETIME NOT NULL
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

			CREATE TABLE menu_item_type_lookup
			(
				menu_item_type_number INT NOT NULL UNIQUE,
				menu_item_type_name NVARCHAR(50) NOT NULL UNIQUE
			);

			CREATE TABLE device_info
			(
				id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
				activation_id NVARCHAR(50) NOT NULL UNIQUE,
				activation_key NVARCHAR(50) NOT NULL,
				mac_address NVARCHAR(50),
				status_lookup_id BIGINT DEFAULT 0,
				device_type_lookup_id BIGINT DEFAULT 0,
				ref_id BIGINT DEFAULT 0,
				created_date DATETIME NOT NULL,
				last_update_date DATETIME 
			);

			CREATE TABLE device_type_lookup
			(
				id INT UNIQUE NOT NULL, 
				name NVARCHAR(50) NOT NULL,
				prefix NVARCHAR(2) NOT NULL UNIQUE,
				backend_sequence INT NOT NULL,
				modified_date DATE NOT NULL DEFAULT GETDATE()
			);

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

			CREATE TABLE publish_version
			(
				id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
				group_category_id bigInt NOT NULL,
				version_count bigint NOT NULL,  
				menu_file_path nvarchar(150),
				menu_query_file_path nvarchar(150),
				menu_img_file_path nvarchar(150),
				publish_date DATETIME NOT NULL
			);

			CREATE TABLE display_period
			(
				id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
				display_period_name NVARCHAR(150) NOT NULL UNIQUE,
				display_period_day NVARCHAR(5) NOT NULL,
				display_period_start_time TIME NOT NULL,
				display_period_end_time TIME NOT NULL
			);

			CREATE TABLE promotional_period
			(
				id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
				promo_period_name NVARCHAR(150) NOT NULL UNIQUE,
				promo_period_start_time TIME NOT NULL,
				promo_period_end_time TIME NOT NULL,
				promo_period_start_date DATE NOT NULL,
				promo_period_end_date DATE NOT NULL
			);

			CREATE TABLE menu_item_display_period
			(
				menu_item_id BIGINT NOT NULL,
				display_period_id BIGINT NOT NULL
			);

			CREATE TABLE menu_item_promo_period
			(
				menu_item_id BIGINT NOT NULL,
				promo_period_id BIGINT NOT NULL
			);
			
			CREATE TABLE general_configuration(
				id BIGINT IDENTITY(1,1) NOT NULL,
				description varchar(255) NOT NULL,
				parameter varchar(255) NOT NULL,
				value varchar(255) NOT NULL
			); 

			INSERT INTO role_lookup VALUES (''Admin''),(''Store Manager'');

			INSERT INTO charge_type_lookup VALUES (0, ''None''),(1, ''Tax''),(2, ''Charge'');

			INSERT INTO device_type_lookup (id, name, prefix, backend_sequence, modified_date) VALUES (1, ''ECPOS'', ''EC'', 0, GETDATE())
			INSERT INTO device_type_lookup (id, name, prefix, backend_sequence, modified_date) VALUES (2, ''BYOD'', ''BD'', 0, GETDATE())
			INSERT INTO device_type_lookup (id, name, prefix, backend_sequence, modified_date) VALUES (3, ''KIOSK'', ''KK'', 0, GETDATE())

			INSERT INTO status_lookup (id, name) VALUES (1, ''PENDING'');
			INSERT INTO status_lookup (id, name) VALUES (2, ''ACTIVE'');
			INSERT INTO status_lookup (id, name) VALUES (3, ''INACTIVE'');

			INSERT INTO menu_item_type_lookup ([menu_item_type_number],[menu_item_type_name]) VALUES(0,''A La Carte'');
			INSERT INTO menu_item_type_lookup ([menu_item_type_number],[menu_item_type_name]) VALUES(1,''Combo'');
			INSERT INTO menu_item_type_lookup ([menu_item_type_number],[menu_item_type_name]) VALUES(2,''Modifier'');

			INSERT INTO backend_sequence([id], [backend_sequence_code], [backend_sequence_name], [backend_sequence], [modified_date]) VALUES(1, ''S'',''Store'',0,GETDATE());
			INSERT INTO backend_sequence([id], [backend_sequence_code], [backend_sequence_name], [backend_sequence], [modified_date]) VALUES(2, ''imgS'',''Store Image'',0,GETDATE());
			INSERT INTO backend_sequence([id], [backend_sequence_code], [backend_sequence_name], [backend_sequence], [modified_date]) VALUES(3, ''imgMI'',''Menu Item Image'', 0,GETDATE());
			INSERT INTO backend_sequence([id], [backend_sequence_code], [backend_sequence_name], [backend_sequence], [modified_date]) VALUES(4, ''imgC'',''Category Image'', 0,GETDATE());
			INSERT INTO backend_sequence([id], [backend_sequence_code], [backend_sequence_name], [backend_sequence], [modified_date]) VALUES(5, ''MF'',''Menu File'', 0,GETDATE());
			INSERT INTO backend_sequence([id], [backend_sequence_code], [backend_sequence_name], [backend_sequence], [modified_date]) VALUES(6, ''TQF'',''Temporary Query File'', 0,GETDATE());
			INSERT INTO backend_sequence([id], [backend_sequence_code], [backend_sequence_name], [backend_sequence], [modified_date]) VALUES(7, ''MQF'',''Menu Query File'', 0,GETDATE());
			INSERT INTO backend_sequence([id], [backend_sequence_code], [backend_sequence_name], [backend_sequence], [modified_date]) VALUES(8, ''TIF'',''Temporary Image File'', 0,GETDATE());
			INSERT INTO backend_sequence([id], [backend_sequence_code], [backend_sequence_name], [backend_sequence], [modified_date]) VALUES(9, ''MIF'',''Menu Image File'', 0,GETDATE());

			END
			'

			SET @metasql = '
				USE ['+ @db_name + ']
				EXEC (''' + REPLACE(@sql, '''', '''''') + ''');
				EXEC dbo.populate_all_tables;'

			EXEC(@metasql);
	
		END

		SELECT @db_creation_result = 1;
	END
ELSE
		SELECT @db_creation_result = 0;
GO
	






	


