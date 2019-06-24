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
GO
--PRINT 'Checking for the existence of this procedure'
IF (SELECT OBJECT_ID('create_brand_db','P')) IS NOT NULL --means, the procedure already exists
	BEGIN
		--PRINT 'Procedure already exists. So, dropping it'
		DROP PROC create_brand_db
	END
GO

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
				menu_item_barcode NVARCHAR(100) NULL,
				menu_item_description NVARCHAR(255),
				menu_item_image_path NVARCHAR(MAX),
				menu_item_base_price DECIMAL(10,2) DEFAULT 0.00,
				menu_item_type INT DEFAULT 0, 
				is_taxable BIT DEFAULT 0,
				is_discountable BIT DEFAULT 0,
				is_active BIT DEFAULT 1,
				created_date DATETIME NOT NULL
			);
			
			CREATE UNIQUE INDEX barCodeUnique 
  				ON menu_item(menu_item_barcode) 
  				WHERE menu_item_barcode IS NOT NULL;

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
				store_type_id BIGINT DEFAULT 0,
				backend_id NVARCHAR(50) NOT NULL UNIQUE,
				store_name NVARCHAR(150) NOT NULL UNIQUE,
				store_logo_path NVARCHAR(MAX),
				store_address NVARCHAR(150),
				store_longitude DECIMAL(15,8),
				store_latitude DECIMAL(15,8),
				store_country NVARCHAR(100),
				store_currency NVARCHAR(50),
				store_start_operating_time time NOT NULL,
				store_end_operating_time time NOT NULL,
				store_contact_person VARCHAR(150) NOT NULL,
				store_contact_hp_number VARCHAR(50) NOT NULL,
				store_contact_email VARCHAR(150) NOT NULL,
				last_update_date datetime,
				is_publish BIT DEFAULT 0,
				ecpos BIT DEFAULT 0,
				ecpos_takeaway_detail_flag BIT,
				login_type_id BIGINT,
				login_switch_flag BIT,
				byod_payment_delay_id BIGINT DEFAULT 0,
				kiosk_payment_delay_id BIGINT DEFAULT 0,
				store_tax_type_id BIGINT DEFAULT 0,
				created_date DATETIME NOT NULL
			);

			CREATE TABLE staff 
			(
				id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
				store_id BIGINT DEFAULT 0,
				staff_name NVARCHAR(150) NOT NULL,
				staff_username NVARCHAR(100) NOT NULL,
				staff_password NVARCHAR(200) NOT NULL,
				staff_role INT NOT NULL,
				staff_contact_hp_number NVARCHAR(50) NOT NULL,
				staff_contact_email VARCHAR(150) NOT NULL,
				is_active BIT DEFAULT 1 NOT NULL,
				created_date DATETIME NOT NULL,
				last_update_date DATETIME
			);

			CREATE TABLE role_lookup
			(
				id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
				role_name NVARCHAR(50) NOT NULL UNIQUE
			);

			CREATE TABLE device_info_detail(
				id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
				device_info_id BIGINT NOT NULL,
				device_name VARCHAR(50),
				device_url VARCHAR(50),
				device_role_lookup_id INT
			)

			CREATE TABLE device_role_lookup(
				device_role_id INT UNIQUE NOT NULL, 
				device_role_name NVARCHAR(50) NOT NULL UNIQUE
			)

			CREATE TABLE login_type_lookup
			(
				id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
				login_type_name NVARCHAR(50) NOT NULL UNIQUE
			);

			CREATE TABLE store_type_lookup
			(
				id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
				store_type_name NVARCHAR(50) NOT NULL UNIQUE
			);

			CREATE TABLE payment_delay_lookup
			(
				id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
				payment_delay_name NVARCHAR(50) NOT NULL UNIQUE
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
				tax_charge_name NVARCHAR(100) NOT NULL,
				rate INT DEFAULT 0,
				charge_type INT DEFAULT 1,
				is_active BIT DEFAULT 0,
				created_date DATETIME NOT NULL
			);

			CREATE TABLE charge_type_lookup
			(
				charge_type_number INT UNIQUE NOT NULL, 
				charge_type_name NVARCHAR(50) NOT NULL
			);

			CREATE TABLE store_tax_type_lookup
			(
				store_tax_type_id INT UNIQUE NOT NULL, 
				store_tax_type_name NVARCHAR(50) NOT NULL UNIQUE
			);

			CREATE TABLE group_category_tax_charge
			(
				group_category_id BIGINT NOT NULL,
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
				group_category_id BIGINT DEFAULT 0,
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

			CREATE TABLE order_type_lookup
			(
				id INT UNIQUE NOT NULL, 
				name NVARCHAR(50) NOT NULL UNIQUE 
			);

			CREATE TABLE check_status_lookup
			(
				id INT UNIQUE NOT NULL, 
				name NVARCHAR(50) NOT NULL UNIQUE 
			);

			CREATE TABLE [check] 
			(
				id BIGINT IDENTITY(1,1) NOT NULL,
				store_id BIGINT NOT NULL,
				device_id BIGINT NULL,
				check_id BIGINT NOT NULL,
				check_number BIGINT NOT NULL,
				staff_id BIGINT NULL,
				order_type BIGINT NOT NULL,
				customer_name varchar(250) NULL,
				table_number INT NULL,
				total_item_quantity INT NOT NULL,
				total_amount DECIMAL(25, 4) NOT NULL,
				total_amount_with_tax DECIMAL(25, 4) NOT NULL,
				total_amount_with_tax_rounding_adjustment DECIMAL(25, 4) NOT NULL,
				grand_total_amount DECIMAL(25, 4) NOT NULL,
				tender_amount DECIMAL(25, 4) NOT NULL,
				overdue_amount DECIMAL(25, 4) NOT NULL,
				check_status BIGINT NOT NULL,
				created_date DATETIME NOT NULL,
				updated_date DATETIME NULL
			);
			
			CREATE TABLE [check_tax_charge] 
			(
				store_id BIGINT NOT NULL,
				check_id BIGINT NOT NULL,
				check_number BIGINT NOT NULL,
				tax_charge_id BIGINT NULL,
				total_charge_amount DECIMAL(25, 4) NOT NULL,
				total_charge_amount_rounding_adjustment decimal(25, 4) NOT NULL,
				grand_total_charge_amount decimal(25, 4) NOT NULL
			);

			CREATE TABLE check_detail 
			(
				id BIGINT IDENTITY(1,1) NOT NULL,
				store_id BIGINT NOT NULL,
				check_detail_id BIGINT NOT NULL,
				check_id BIGINT NOT NULL,
				check_number BIGINT NOT NULL,
				device_type BIGINT NOT NULL,
				parent_check_detail_id BIGINT NULL,
				menu_item_id BIGINT NOT NULL,
				menu_item_code nvarchar(50) NOT NULL,
				menu_item_name nvarchar(150) NOT NULL,
				menu_item_price DECIMAL(25, 4) NOT NULL,
				quantity INT NOT NULL,
				total_amount DECIMAL(25, 4) NOT NULL,
				check_detail_status BIGINT NOT NULL,
				transaction_id BIGINT NULL,
				created_date DATETIME NOT NULL,
				updated_date DATETIME NULL
			);

			CREATE TABLE transaction_type_lookup
			(
				id INT UNIQUE NOT NULL, 
				name NVARCHAR(50) NOT NULL UNIQUE 
			);

			CREATE TABLE payment_method_lookup
			(
				id INT UNIQUE NOT NULL, 
				name NVARCHAR(50) NOT NULL UNIQUE 
			);

			CREATE TABLE payment_type_lookup
			(
				id INT UNIQUE NOT NULL, 
				name NVARCHAR(50) NOT NULL UNIQUE 
			);

			CREATE TABLE transaction_settlement_status_lookup
			(
				id INT UNIQUE NOT NULL, 
				name NVARCHAR(50) NOT NULL UNIQUE 
			);

			CREATE TABLE [transaction] 
			(
				id BIGINT IDENTITY(1,1) NOT NULL,
				store_id BIGINT NOT NULL,
				transaction_id BIGINT NOT NULL,
				device_id BIGINT NOT NULL,
				staff_id BIGINT NOT NULL,
				check_id BIGINT NOT NULL,
				check_number BIGINT NOT NULL,
				transaction_type BIGINT NOT NULL,
				payment_method BIGINT NOT NULL,
				payment_type BIGINT NOT NULL,
				terminal_serial_number nvarchar(255) NULL,
				transaction_currency nvarchar(100) NOT NULL,
				transaction_amount DECIMAL(25, 4) NOT NULL,
				received_amount DECIMAL(25, 4) NOT NULL,
				transaction_tips DECIMAL(25, 4) NULL,
				change_amount DECIMAL(25, 4) NOT NULL,
				transaction_status BIGINT NOT NULL,
				unique_trans_number nvarchar(255) NULL,
				qr_content nvarchar(255) NULL,
				created_date DATETIME NOT NULL,
				response_code nvarchar(255) NULL,
				response_message nvarchar(255) NULL,
				updated_date DATETIME NULL,
				wifi_ip nvarchar(255) NULL,
				wifi_port nvarchar(255) NULL,
				approval_code nvarchar(255) NULL,
				bank_mid nvarchar(255) NULL,
				bank_tid nvarchar(255) NULL,
				transaction_date nvarchar(255) NULL,
				transaction_time nvarchar(255) NULL,
				original_invoice_number nvarchar(255) NULL,
				invoice_number nvarchar(255) NULL,
				merchant_info nvarchar(255) NULL,
				card_issuer_name nvarchar(255) NULL,
				masked_card_number nvarchar(255) NULL,
				card_expiry_date nvarchar(255) NULL,
				batch_number nvarchar(255) NULL,
				rrn nvarchar(255) NULL,
				card_issuer_id nvarchar(255) NULL,
				cardholder_name nvarchar(255) NULL,
				aid nvarchar(255) NULL,
				app_label nvarchar(255) NULL,
				tc nvarchar(255) NULL,
				terminal_verification_result nvarchar(255) NULL,
				original_trace_number nvarchar(255) NULL,
				trace_number nvarchar(255) NULL,
				qr_issuer_type nvarchar(255) NULL,
				mpay_mid nvarchar(255) NULL,
				mpay_tid nvarchar(255) NULL,
				qr_ref_id nvarchar(255) NULL,
				qr_user_id nvarchar(255) NULL,
				qr_amount_myr nvarchar(255) NULL,
				qr_amount_rmb nvarchar(255) NULL
			);

			CREATE TABLE nii_type_lookup
			(
				id INT UNIQUE NOT NULL, 
				name NVARCHAR(50) NOT NULL UNIQUE 
			);

			CREATE TABLE settlement 
			(
				id BIGINT IDENTITY(1,1) NOT NULL,
				store_id BIGINT NOT NULL,
				settlement_id BIGINT NOT NULL,
				device_id BIGINT NOT NULL,
				staff_id BIGINT NOT NULL,
				nii_type BIGINT NOT NULL,
				settlement_status BIGINT NOT NULL,
				created_date DATETIME NOT NULL,
				response_code nvarchar(255) NULL,
				response_message nvarchar(255) NULL,
				updated_date DATETIME NULL,
				wifi_ip nvarchar(255) NULL,
				wifi_port nvarchar(255) NULL,
				merchant_info nvarchar(255) NULL,
				bank_mid nvarchar(255) NULL,
				bank_tid nvarchar(255) NULL,
				batch_number nvarchar(255) NULL,
				transaction_date nvarchar(255) NULL,
				transaction_time nvarchar(255) NULL,
				batch_total nvarchar(255) NULL,
				nii nvarchar(255) NULL
			);

			CREATE TABLE table_setting 
			(
				id BIGINT PRIMARY KEY IDENTITY(1,1) NOT NULL,
				store_id BIGINT DEFAULT 0,
				table_name NVARCHAR(150) NOT NULL,
				status_lookup_id BIGINT,
				created_date DATETIME NOT NULL,
				last_update_date DATETIME
			);

			INSERT INTO role_lookup VALUES (''Admin''),(''Store Manager'');
			INSERT INTO store_type_lookup VALUES (''Retail''),(''F&B'');
			INSERT INTO payment_delay_lookup VALUES (''Pay Now/Later''), (''Pay Now''), (''Pay Later'')
			INSERT INTO login_type_lookup VALUES (''Username & Password''), (''Scan QR'')

			INSERT INTO charge_type_lookup VALUES (1, ''Total Tax''),(2, ''Overall Tax'');
			INSERT INTO store_tax_type_lookup VALUES (1,''Exclusive Tax''), (2, ''Inclusive Tax'');

			INSERT INTO device_role_lookup (device_role_id, device_role_name) VALUES (1, ''Master'');
			INSERT INTO device_role_lookup (device_role_id, device_role_name) VALUES (2, ''Client'');

			INSERT INTO device_type_lookup (id, name, prefix, backend_sequence, modified_date) VALUES (1, ''ECPOS'', ''EC'', 0, GETDATE())
			INSERT INTO device_type_lookup (id, name, prefix, backend_sequence, modified_date) VALUES (2, ''BYOD'', ''BD'', 0, GETDATE())
			INSERT INTO device_type_lookup (id, name, prefix, backend_sequence, modified_date) VALUES (3, ''KIOSK'', ''KK'', 0, GETDATE())

			INSERT INTO check_status_lookup values (1, ''New''), (2, ''Pending''), (3, ''Closed''), (4, ''Cancelled'');

			INSERT INTO order_type_lookup values (1, ''table''), (2, ''take away''), (3, ''deposit'');

			insert into transaction_type_lookup values (1, ''Sale''), (2, ''Void''), (3, ''Refund''), (4, ''Reversal'');

			insert into payment_method_lookup values (1, ''Cash''), (2, ''Card''), (3, ''QR'');

			insert into payment_type_lookup values (1, ''Full Payment''), (2, ''Partial Payment'');

			insert into nii_type_lookup values (1, ''VISA/MASTER/JCB''), (2, ''AMEX''), (3, ''MCCS''), (4, ''UNIONPAY'');

			insert into transaction_settlement_status_lookup values (1, ''New''), (2, ''Pending''), (3, ''Approved''), (4, ''Declined''), (5, ''Voided''), (6, ''Refunded''), (7, ''Reversed'');

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
			INSERT INTO backend_sequence([id], [backend_sequence_code], [backend_sequence_name], [backend_sequence], [modified_date]) VALUES(10, ''ST'',''Setting Logo Image'', 0,GETDATE());

			INSERT general_configuration ([description], [parameter], [value]) VALUES (''BYOD Setting - Application Name'', ''appName'', ''BYOD'')
			INSERT general_configuration ([description], [parameter], [value]) VALUES (''BYOD Setting - Main Logo Image'', ''mainLogoPath'', ''/assets/images/byodadmin/default/default_main_logo.png'')
			INSERT general_configuration ([description], [parameter], [value]) VALUES (''BYOD Setting - Shortcut Logo Image'', ''shortcutLogoPath'', ''/assets/images/byodadmin/default/default_shortcut_logo.png'')
			INSERT general_configuration ([description], [parameter], [value]) VALUES (''BYOD Setting - Main Background Image'', ''mainBackgroundPath'', ''/assets/images/byodadmin/default/default_background.png'')
			INSERT general_configuration ([description], [parameter], [value]) VALUES (''BYOD Setting - Landing Logo Image'', ''landingLogoPath'', ''/assets/images/byodadmin/default/default_landing_logo.png'')
			INSERT general_configuration ([description], [parameter], [value]) VALUES (''BYOD Setting - Main Color'', ''mainColor'', ''#03d332'')
			INSERT general_configuration ([description], [parameter], [value]) VALUES (''BYOD Setting - Sub Color'', ''subColor'', ''#000000'')
			INSERT general_configuration ([description], [parameter], [value]) VALUES (''BYOD Setting - Main Text Color'', ''mainTextColor'', ''#008040'')
			INSERT general_configuration ([description], [parameter], [value]) VALUES (''BYOD Setting - Sub Text Color'', ''subTextColor'', ''#FFFFFF'')
			INSERT general_configuration ([description], [parameter], [value]) VALUES (''BYOD Setting - Locale Button Color'', ''localeButtonColor'', ''#00ffff'')
			INSERT general_configuration ([description], [parameter], [value]) VALUES (''BYOD Setting - Main Button Text Color'', ''mainButtonTextColor'', ''#FFFFFF'')
			INSERT general_configuration ([description], [parameter], [value]) VALUES (''BYOD Setting - Main Button Background Color'', ''mainButtonBackgroundColor'', ''#C41230'')
			INSERT general_configuration ([description], [parameter], [value]) VALUES (''BYOD Setting - Main Button Background Hover Color'', ''mainButtonBackgroundHoverColor'', ''#C41230'')
			INSERT general_configuration ([description], [parameter], [value]) VALUES (''BYOD Setting - Main Button Background Focus Color'', ''mainButtonBackgroundFocusColor'', ''#C41230'')

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