-- Add quantity tracking to products table (SRS inventory deduction feature)
ALTER TABLE merchant_service.products
  ADD COLUMN IF NOT EXISTS quantity_in_stock NUMERIC(10,2) NOT NULL DEFAULT 0,
  ADD COLUMN IF NOT EXISTS unit_of_measure   VARCHAR(20);

UPDATE merchant_service.products
  SET unit_of_measure = unit
  WHERE unit_of_measure IS NULL;
