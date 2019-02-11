DECLARE @first_result INT;
DECLARE @second_result INT;

EXEC dbo.create_brand_db @db_name = 'KFC', @db_creation_result =  @first_result OUTPUT;
SELECT @first_result;

EXEC dbo.create_brand_db @db_name = 'SUSHI', @db_creation_result =  @second_result OUTPUT;
SELECT @second_result;