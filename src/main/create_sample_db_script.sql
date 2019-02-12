USE tenant
DECLARE @first_result INT;
DECLARE @second_result INT;

EXEC dbo.create_brand_db @db_name = 'KFC', @db_creation_result =  @first_result OUTPUT;
SELECT @first_result;

EXEC dbo.create_brand_db @db_name = 'SUSHI', @db_creation_result =  @second_result OUTPUT;
SELECT @second_result;

---- insert brand id
--DECLARE @first_id INT;
--DECLARE @second_id INT;

--SELECT @first_id = id FROM brands WHERE brand_db_name='KFC'
--SELECT @second_id = id FROM brands WHERE brand_db_name='SUSHI'

--USE KFC
--INSERT INTO general_configuration (description, parameter, value) VALUES ('Brand Identity Number', 'BRAND_ID', @first_id);

--USE SUSHI
--INSERT INTO general_configuration (description, parameter, value) VALUES ('Brand Identity Number', 'BRAND_ID', @second_id);

