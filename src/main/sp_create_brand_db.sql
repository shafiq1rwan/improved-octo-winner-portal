DELIMITER $$
DROP PROCEDURE IF EXISTS `tenant`.`create_brand_db`$$
CREATE PROCEDURE `tenant`.`create_brand_db` (IN p_db_name VARCHAR(100),OUT p_db_creation_result INT)
BEGIN

IF NOT EXISTS(SELECT * FROM information_schema.schemata WHERE schema_name = p_db_name)
	THEN	

        call sp_exec(CONCAT('CREATE DATABASE ',p_db_name));
        
        IF EXISTS(SELECT * FROM information_schema.schemata WHERE schema_name = p_db_name)
		THEN
            call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.group_category
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				group_category_name NVARCHAR(50) NOT NULL UNIQUE,
				created_date DATETIME(3) NOT NULL,
				publish_version_id bigint,
				tmp_query_file_path nvarchar(150),
				tmp_img_file_path nvarchar(150)
			);'));
            call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.category
			(
				id bigInt PRIMARY KEY auto_increment NOT NULL,
				group_category_id bigInt NOT NULL,
				category_name nvarchar(150) NOT NULL UNIQUE,
				category_description nvarchar(255),
				category_image_path longtext,
				category_sequence INT,
				is_active TINYINT DEFAULT 1,
				created_date DATETIME(3) NOT NULL
			);'));	
			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.menu_item
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				backend_id NVARCHAR(50) NOT NULL UNIQUE,
				modifier_group_id BIGINT,
				menu_item_name NVARCHAR(150) NOT NULL,
				menu_item_alt_name NVARCHAR(50),
				menu_item_barcode NVARCHAR(100) NULL,
				menu_item_description NVARCHAR(255),
				menu_item_image_path LONGTEXT,
				menu_item_base_price DECIMAL(10,2) DEFAULT 0.00,
				menu_item_type INT DEFAULT 0, 
				menu_quantity_stock INT DEFAULT 0, 
				is_taxable TINYINT DEFAULT 0,
				is_discountable TINYINT DEFAULT 0,
				is_weighable TINYINT DEFAULT 0,
				is_active TINYINT DEFAULT 1,
				created_date DATETIME(3) NOT NULL
			);'));
			
			call sp_exec(CONCAT('CREATE UNIQUE INDEX barCodeUnique 
  				ON ',p_db_name,'.menu_item(menu_item_barcode) 
  				-- WHERE menu_item_barcode IS NOT NULL
                ;'));
			 
			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.category_menu_item
			(
				category_id BIGINT NOT NULL,
				menu_item_id BIGINT NOT NULL,
				category_menu_item_sequence INT NOT NULL
			);'));
			
			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.menu_item_group
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				menu_item_group_name NVARCHAR(150) NOT NULL UNIQUE,
				is_active TINYINT DEFAULT 1,
				created_date DATETIME(3) NOT NULL
			);'));
			
			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.menu_item_group_sequence
			(
				menu_item_group_id BigInt,
				menu_item_id BigInt,
				menu_item_group_sequence INT NOT NULL	
			);'));
			
			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.modifier_group
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				modifier_group_name NVARCHAR(100) NOT NULL UNIQUE,
				is_active TINYINT DEFAULT 1,
				created_date DATETIME(3) NOT NULL
			);'));
			
			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.menu_item_modifier_group
			(
				menu_item_id BIGINT,
				modifier_group_id BIGINT,
				menu_item_modifier_group_sequence INT
			);'));
			
			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.modifier_item_sequence
			(
				modifier_group_id BIGINT NOT NULL,
				menu_item_id BIGINT NOT NULL,
				modifier_item_sequence INT
			);'));
			
			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.combo_detail
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				menu_item_id BIGINT NOT NULL,
				combo_detail_name NVARCHAR(50) NOT NULL,
				combo_detail_quantity INT DEFAULT 0,
				combo_detail_sequence INT NOT NULL,
				created_date DATETIME(3) NOT NULL
			);'));
			
			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.combo_item_detail
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				combo_detail_id BIGINT NOT NULL,
				menu_item_id BIGINT,
				menu_item_group_id BIGINT,
				combo_item_detail_sequence INT NOT NULL,
				created_date DATETIME(3) NOT NULL
			);'));
			 
			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.store
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				group_category_id BIGINT DEFAULT 0,
				store_type_id BIGINT DEFAULT 0,
				backend_id NVARCHAR(50) NOT NULL UNIQUE,
				store_name NVARCHAR(150) NOT NULL UNIQUE,
				store_logo_path LONGTEXT,
				state_id INT NOT NULL,
				store_address NVARCHAR(150),
				store_longitude DECIMAL(15,8),
				store_latitude DECIMAL(15,8),
				store_country NVARCHAR(100),
				store_currency NVARCHAR(50),
				store_start_operating_time time(6) NOT NULL,
				store_end_operating_time time(6) NOT NULL,
				store_contact_person VARCHAR(150) NOT NULL,
				store_contact_hp_number VARCHAR(50) NOT NULL,
				store_contact_email VARCHAR(150) NOT NULL,
				last_update_date datetime(3),
				is_publish TINYINT DEFAULT 0,
				ecpos TINYINT DEFAULT 0,
				ecpos_takeaway_detail_flag TINYINT,
				login_type_id BIGINT,
				login_switch_flag TINYINT,
				byod_payment_delay_id BIGINT DEFAULT 0,
				kiosk_payment_delay_id BIGINT DEFAULT 0,
				store_tax_type_id BIGINT DEFAULT 0,
				created_date DATETIME(3) NOT NULL
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.staff 
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				store_id BIGINT DEFAULT 0,
				staff_name NVARCHAR(150) NOT NULL,
				staff_username NVARCHAR(100) NOT NULL,
				staff_password NVARCHAR(200) NOT NULL,
				staff_role INT NOT NULL,
				staff_contact_hp_number NVARCHAR(50) NOT NULL,
				staff_contact_email VARCHAR(150) NOT NULL,
				is_active TINYINT DEFAULT 1 NOT NULL,
				created_date DATETIME(3) NOT NULL,
				last_update_date DATETIME(3)
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.role_lookup
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				role_name NVARCHAR(50) NOT NULL UNIQUE
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.device_info_detail(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				device_info_id BIGINT NOT NULL,
				device_name VARCHAR(50),
				device_url VARCHAR(50),
				device_role_lookup_id INT
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.device_role_lookup(
				device_role_id INT UNIQUE NOT NULL, 
				device_role_name NVARCHAR(50) NOT NULL UNIQUE
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.login_type_lookup
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				login_type_name NVARCHAR(50) NOT NULL UNIQUE
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.store_type_lookup
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				store_type_name NVARCHAR(50) NOT NULL UNIQUE
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.payment_delay_lookup
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				payment_delay_name NVARCHAR(50) NOT NULL UNIQUE
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.table_log
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				login_user_id BIGINT NOT NULL,
				username NVARCHAR(100) NOT NULL,
				user_action LONGTEXT NOT NULL,
				table_name NVARCHAR(100) NOT NULL,
				table_log_datetime DATETIME(3) NOT NULL
			);'));

			-- no del
			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.tax_charge
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				tax_charge_name NVARCHAR(100) NOT NULL,
				rate INT DEFAULT 0,
				charge_type INT DEFAULT 1,
				is_active TINYINT DEFAULT 0,
				created_date DATETIME(3) NOT NULL
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.charge_type_lookup
			(
				charge_type_number INT UNIQUE NOT NULL, 
				charge_type_name NVARCHAR(50) NOT NULL
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.store_tax_type_lookup
			(
				store_tax_type_id INT UNIQUE NOT NULL, 
				store_tax_type_name NVARCHAR(50) NOT NULL UNIQUE
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.group_category_tax_charge
			(
				group_category_id BIGINT NOT NULL,
				tax_charge_id BIGINT NOT NULL
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.menu_item_type_lookup
			(
				menu_item_type_number INT NOT NULL UNIQUE,
				menu_item_type_name NVARCHAR(50) NOT NULL UNIQUE
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.device_info
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				activation_id NVARCHAR(50) NOT NULL UNIQUE,
				activation_key NVARCHAR(50) NOT NULL,
				mac_address NVARCHAR(50),
				status_lookup_id BIGINT DEFAULT 0,
				device_type_lookup_id BIGINT DEFAULT 0,
				ref_id BIGINT DEFAULT 0,
				group_category_id BIGINT DEFAULT 0,
				created_date DATETIME(3) NOT NULL,
				last_update_date DATETIME(3) 
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.device_type_lookup
			(
				id INT UNIQUE NOT NULL, 
				name NVARCHAR(50) NOT NULL,
				prefix NVARCHAR(2) NOT NULL UNIQUE,
				backend_sequence INT NOT NULL,
				modified_date DATETIME NOT NULL
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.status_lookup
			(
				id INT UNIQUE NOT NULL, 
				name NVARCHAR(50) NOT NULL UNIQUE 
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.backend_sequence
			(
				id BIGINT PRIMARY KEY NOT NULL,
				backend_sequence_code NVARCHAR(20) UNIQUE NOT NULL,
				backend_sequence_name NVARCHAR(100) UNIQUE NOT NULL, 
				backend_sequence INT NOT NULL,
				modified_date DATETIME NOT NULL
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.publish_version
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				group_category_id bigInt NOT NULL,
				version_count bigint NOT NULL,  
				menu_file_path nvarchar(150),
				menu_query_file_path nvarchar(150),
				menu_img_file_path nvarchar(150),
				publish_date DATETIME(3) NOT NULL
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.display_period
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				display_period_name NVARCHAR(150) NOT NULL UNIQUE,
				display_period_day NVARCHAR(5) NOT NULL,
				display_period_start_time TIME(6) NOT NULL,
				display_period_end_time TIME(6) NOT NULL
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.promotional_period
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				promo_period_name NVARCHAR(150) NOT NULL UNIQUE,
				promo_period_start_time TIME(6) NOT NULL,
				promo_period_end_time TIME(6) NOT NULL,
				promo_period_start_date DATE NOT NULL,
				promo_period_end_date DATE NOT NULL
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.menu_item_display_period
			(
				menu_item_id BIGINT NOT NULL,
				display_period_id BIGINT NOT NULL
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.menu_item_promo_period
			(
				menu_item_id BIGINT NOT NULL,
				promo_period_id BIGINT NOT NULL
			);'));
			
			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.general_configuration(
				id  BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				description varchar(255) NOT NULL,
				parameter varchar(255) NOT NULL,
				value varchar(255) NOT NULL
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.order_type_lookup
			(
				id INT UNIQUE NOT NULL, 
				name NVARCHAR(50) NOT NULL UNIQUE 
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.check_status_lookup
			(
				id INT UNIQUE NOT NULL, 
				name NVARCHAR(50) NOT NULL UNIQUE 
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.`check`
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				store_id BIGINT NOT NULL,
				device_id BIGINT NULL,
				check_id BIGINT NOT NULL,
				check_number BIGINT NOT NULL,
				check_ref_no varchar(45) DEFAULT NULL,
				receipt_number varchar(100) DEFAULT NULL,
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
				created_date DATETIME(3) NOT NULL,
				updated_date DATETIME(3) NULL
			);'));
			
			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.check_tax_charge 
			(
				store_id BIGINT NOT NULL,
				check_id BIGINT NOT NULL,
				check_number BIGINT NOT NULL,
				tax_charge_id BIGINT NULL,
				total_charge_amount DECIMAL(25, 4) NOT NULL,
				total_charge_amount_rounding_adjustment decimal(25, 4) NOT NULL,
				grand_total_charge_amount decimal(25, 4) NOT NULL
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.check_detail 
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
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
				created_date DATETIME(3) NOT NULL,
				updated_date DATETIME(3) NULL
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.transaction_type_lookup
			(
				id INT UNIQUE NOT NULL, 
				name NVARCHAR(50) NOT NULL UNIQUE 
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.payment_method_lookup
			(
				id INT UNIQUE NOT NULL, 
				name NVARCHAR(50) NOT NULL UNIQUE 
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.payment_type_lookup
			(
				id INT UNIQUE NOT NULL, 
				name NVARCHAR(50) NOT NULL UNIQUE 
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.transaction_settlement_status_lookup
			(
				id INT UNIQUE NOT NULL, 
				name NVARCHAR(50) NOT NULL UNIQUE 
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.transaction 
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
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
				created_date DATETIME(3) NOT NULL,
				response_code nvarchar(255) NULL,
				response_message nvarchar(255) NULL,
				updated_date DATETIME(3) NULL,
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
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.nii_type_lookup
			(
				id INT UNIQUE NOT NULL, 
				name NVARCHAR(50) NOT NULL UNIQUE 
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.settlement 
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				store_id BIGINT NOT NULL,
				settlement_id BIGINT NOT NULL,
				device_id BIGINT NOT NULL,
				staff_id BIGINT NOT NULL,
				nii_type BIGINT NOT NULL,
				settlement_status BIGINT NOT NULL,
				created_date DATETIME(3) NOT NULL,
				response_code nvarchar(255) NULL,
				response_message nvarchar(255) NULL,
				updated_date DATETIME(3) NULL,
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
			);'));

			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.table_setting 
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				store_id BIGINT DEFAULT 0,
				table_name NVARCHAR(150) NOT NULL,
				status_lookup_id BIGINT,
				created_date DATETIME(3) NOT NULL,
				last_update_date DATETIME(3),
				hotel_floor_no int NULL,
				hotel_room_type int NULL,
				hotel_room_category int NULL
			);'));
			
			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.state_lookup 
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				name VARCHAR(255) NOT NULL
			);'));
			
			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.reporttype_lookup 
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				name VARCHAR(255) NOT NULL
			);'));
			
			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.hotel_room_category_lookup 
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				name VARCHAR(50) NOT NULL,
				created_date DATETIME NOT NULL,
				last_updated_date DATETIME NULL
			);'));
			
			call sp_exec(CONCAT('CREATE TABLE ',p_db_name,'.hotel_room_type 
			(
				id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
				name VARCHAR(50) NOT NULL,
				image_path VARCHAR(50) NOT NULL,
				hotel_room_base_price DECIMAL(10,2) NULL,
				created_date DATETIME NOT NULL,
				last_updated_date DATETIME NULL
			);'));

			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.role_lookup (role_name) VALUES (''Admin'');'));
            call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.role_lookup (role_name) VALUES (''Store Manager'');'));
            call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.role_lookup (role_name) VALUES (''Kitchen'');'));
            call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.role_lookup (role_name) VALUES (''Waiter'');'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.store_type_lookup (store_type_name) VALUES (''Retail''),(''F&B''),(''Hotel'');'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.payment_delay_lookup (payment_delay_name) VALUES (''Pay Now/Later''), (''Pay Now''), (''Pay Later'');'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.login_type_lookup (login_type_name) VALUES (''Username & Password''), (''Scan QR'');'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.charge_type_lookup (charge_type_number,charge_type_name) VALUES (1, ''Total Tax''),(2, ''Overall Tax'');'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.store_tax_type_lookup (store_tax_type_id,store_tax_type_name) VALUES (1,''Exclusive Tax''), (2, ''Inclusive Tax'');'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.device_role_lookup (device_role_id, device_role_name) VALUES (1, ''Master'');'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.device_role_lookup (device_role_id, device_role_name) VALUES (2, ''Client'');'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.device_type_lookup (id, name, prefix, backend_sequence, modified_date) VALUES (1, ''ECPOS'', ''EC'', 0, NOW());'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.device_type_lookup (id, name, prefix, backend_sequence, modified_date) VALUES (2, ''BYOD'', ''BD'', 0, NOW());'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.device_type_lookup (id, name, prefix, backend_sequence, modified_date) VALUES (3, ''KIOSK'', ''KK'', 0, NOW());'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.check_status_lookup (id,name) values (1, ''New''), (2, ''Pending''), (3, ''Closed''), (4, ''Cancelled'');'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.order_type_lookup (id,name) values (1, ''table''), (2, ''take away''), (3, ''deposit'');'));
			call sp_exec(CONCAT('insert into ',p_db_name,'.transaction_type_lookup (id,name) values (1, ''Sale''), (2, ''Void''), (3, ''Refund''), (4, ''Reversal'');'));
			call sp_exec(CONCAT('insert into ',p_db_name,'.payment_method_lookup (id,name) values (1, ''Cash''), (2, ''Card''), (3, ''QR''), (4, ''Static QR'');'));
			call sp_exec(CONCAT('insert into ',p_db_name,'.payment_type_lookup (id,name) values (1, ''Full Payment''), (2, ''Partial Payment'');'));
			call sp_exec(CONCAT('insert into ',p_db_name,'.nii_type_lookup (id,name) values (1, ''VISA/MASTER/JCB''), (2, ''AMEX''), (3, ''MCCS''), (4, ''UNIONPAY'');'));
			call sp_exec(CONCAT('insert into ',p_db_name,'.transaction_settlement_status_lookup (id,name) values (1, ''New''), (2, ''Pending''), (3, ''Approved''), (4, ''Declined''), (5, ''Voided''), (6, ''Refunded''), (7, ''Reversed'');'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.status_lookup (id, name) VALUES (1, ''PENDING'');'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.status_lookup (id, name) VALUES (2, ''ACTIVE'');'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.status_lookup (id, name) VALUES (3, ''INACTIVE'');'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.menu_item_type_lookup (`menu_item_type_number`,`menu_item_type_name`) VALUES(0,''A La Carte'');'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.menu_item_type_lookup (`menu_item_type_number`,`menu_item_type_name`) VALUES(1,''Combo'');'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.menu_item_type_lookup (`menu_item_type_number`,`menu_item_type_name`) VALUES(2,''Modifier'');'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.backend_sequence(`id`, `backend_sequence_code`, `backend_sequence_name`, `backend_sequence`, `modified_date`) VALUES(1, ''S'',''Store'',0,NOW());'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.backend_sequence(`id`, `backend_sequence_code`, `backend_sequence_name`, `backend_sequence`, `modified_date`) VALUES(2, ''imgS'',''Store Image'',0,NOW());'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.backend_sequence(`id`, `backend_sequence_code`, `backend_sequence_name`, `backend_sequence`, `modified_date`) VALUES(3, ''imgMI'',''Menu Item Image'', 0,NOW());'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.backend_sequence(`id`, `backend_sequence_code`, `backend_sequence_name`, `backend_sequence`, `modified_date`) VALUES(4, ''imgC'',''Category Image'', 0,NOW());'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.backend_sequence(`id`, `backend_sequence_code`, `backend_sequence_name`, `backend_sequence`, `modified_date`) VALUES(5, ''MF'',''Menu File'', 0,NOW());'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.backend_sequence(`id`, `backend_sequence_code`, `backend_sequence_name`, `backend_sequence`, `modified_date`) VALUES(6, ''TQF'',''Temporary Query File'', 0,NOW());'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.backend_sequence(`id`, `backend_sequence_code`, `backend_sequence_name`, `backend_sequence`, `modified_date`) VALUES(7, ''MQF'',''Menu Query File'', 0,NOW());'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.backend_sequence(`id`, `backend_sequence_code`, `backend_sequence_name`, `backend_sequence`, `modified_date`) VALUES(8, ''TIF'',''Temporary Image File'', 0,NOW());'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.backend_sequence(`id`, `backend_sequence_code`, `backend_sequence_name`, `backend_sequence`, `modified_date`) VALUES(9, ''MIF'',''Menu Image File'', 0,NOW());'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.backend_sequence(`id`, `backend_sequence_code`, `backend_sequence_name`, `backend_sequence`, `modified_date`) VALUES(10, ''ST'',''Setting Logo Image'', 0,NOW());'));
			call sp_exec(CONCAT('INSERT INTO ',p_db_name,'.backend_sequence(`id`, `backend_sequence_code`, `backend_sequence_name`, `backend_sequence`, `modified_date`) VALUES(11, ''imgRT'',''Hotel Room Type Image'', 0,NOW());'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.general_configuration (`description`, `parameter`, `value`) VALUE (''BYOD Setting - Application Name'', ''appName'', ''BYOD'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.general_configuration (`description`, `parameter`, `value`) VALUE (''BYOD Setting - Main Logo Image'', ''mainLogoPath'', ''/assets/images/byodadmin/default/default_main_logo.png'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.general_configuration (`description`, `parameter`, `value`) VALUE (''BYOD Setting - Shortcut Logo Image'', ''shortcutLogoPath'', ''/assets/images/byodadmin/default/default_shortcut_logo.png'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.general_configuration (`description`, `parameter`, `value`) VALUE (''BYOD Setting - Main Background Image'', ''mainBackgroundPath'', ''/assets/images/byodadmin/default/default_background.png'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.general_configuration (`description`, `parameter`, `value`) VALUE (''BYOD Setting - Landing Logo Image'', ''landingLogoPath'', ''/assets/images/byodadmin/default/default_landing_logo.png'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.general_configuration (`description`, `parameter`, `value`) VALUE (''BYOD Setting - Main Color'', ''mainColor'', ''#03d332'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.general_configuration (`description`, `parameter`, `value`) VALUE (''BYOD Setting - Sub Color'', ''subColor'', ''#000000'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.general_configuration (`description`, `parameter`, `value`) VALUE (''BYOD Setting - Main Text Color'', ''mainTextColor'', ''#008040'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.general_configuration (`description`, `parameter`, `value`) VALUE (''BYOD Setting - Sub Text Color'', ''subTextColor'', ''#FFFFFF'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.general_configuration (`description`, `parameter`, `value`) VALUE (''BYOD Setting - Locale Button Color'', ''localeButtonColor'', ''#00ffff'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.general_configuration (`description`, `parameter`, `value`) VALUE (''BYOD Setting - Main Button Text Color'', ''mainButtonTextColor'', ''#FFFFFF'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.general_configuration (`description`, `parameter`, `value`) VALUE (''BYOD Setting - Main Button Background Color'', ''mainButtonBackgroundColor'', ''#C41230'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.general_configuration (`description`, `parameter`, `value`) VALUE (''BYOD Setting - Main Button Background Hover Color'', ''mainButtonBackgroundHoverColor'', ''#C41230'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.general_configuration (`description`, `parameter`, `value`) VALUE (''BYOD Setting - Main Button Background Focus Color'', ''mainButtonBackgroundFocusColor'', ''#C41230'');'));
			
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`state_lookup` (`name`) VALUES (''JOHOR'');'));
            call sp_exec(CONCAT('INSERT into ',p_db_name,'.`state_lookup` (`name`) VALUES (''KEDAH'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`state_lookup` (`name`) VALUES (''KELANTAN'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`state_lookup` (`name`) VALUES (''WILAYAH PERSEKUTUAN KUALA LUMPUR'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`state_lookup` (`name`) VALUES (''WILAYAH PERSEKUTUAN LABUAN'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`state_lookup` (`name`) VALUES (''MELAKA'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`state_lookup` (`name`) VALUES (''NEGERI SEMBILAN'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`state_lookup` (`name`) VALUES (''PAHANG'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`state_lookup` (`name`) VALUES (''PULAU PINANG'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`state_lookup` (`name`) VALUES (''PERAK'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`state_lookup` (`name`) VALUES (''PERLIS'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`state_lookup` (`name`) VALUES (''PUTRAJAYA'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`state_lookup` (`name`) VALUES (''SABAH'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`state_lookup` (`name`) VALUES (''SARAWAK'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`state_lookup` (`name`) VALUES (''SELANGOR'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`state_lookup` (`name`) VALUES (''TERENGGANU'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`state_lookup` (`name`) VALUES (''NOT APPLICABLE'');'));
			
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`reporttype_lookup` (`name`) VALUES (''Summary Report'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`reporttype_lookup` (`name`) VALUES (''Best Selling Item'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`reporttype_lookup` (`name`) VALUES (''Sales by Employee'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`reporttype_lookup` (`name`) VALUES (''Sales by Payment Type'');'));
			call sp_exec(CONCAT('INSERT into ',p_db_name,'.`reporttype_lookup` (`name`) VALUES (''Sales by Modifiers'');'));
			
		END IF;

		SET p_db_creation_result = 1;
ELSE
		SET p_db_creation_result = 0;
END IF;
END$$

DELIMITER ;