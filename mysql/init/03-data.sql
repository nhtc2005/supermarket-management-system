USE supermarket_db;

INSERT INTO employees (id, username, password, first_name, last_name)
VALUES 
(1, 'manager1', '$2a$10$Xyi2NvY76JWSU7ZV1RbIoev1hgJTfV62GZ548qrx/7mw32wdaNT5e', 'Manager', 'One'),
(2, 'sales1',   '$2a$10$Xyi2NvY76JWSU7ZV1RbIoev1hgJTfV62GZ548qrx/7mw32wdaNT5e', 'Sales', 'One'),
(3, 'warehouse1','$2a$10$Xyi2NvY76JWSU7ZV1RbIoev1hgJTfV62GZ548qrx/7mw32wdaNT5e', 'Warehouse', 'One');

INSERT INTO sales_employees (employee_id, store_id, total_sales)
VALUES (2, NULL, 0);

INSERT INTO warehouse_employees (employee_id, warehouse_id)
VALUES (3, NULL);

INSERT INTO stores (id, name, location, manager_id)
VALUES (1, 'Da Lat Supermarket', 'Da Lat City', 2);

INSERT INTO warehouses (id, name, location, manager_id)
VALUES (1, 'Central Warehouse', 'Lam Dong', 3);

UPDATE sales_employees SET store_id = 1 WHERE employee_id = 2;
UPDATE warehouse_employees SET warehouse_id = 1 WHERE employee_id = 3;

INSERT INTO customers (first_name, last_name, email, phone, address, loyalty_points, password)
VALUES 
('Nguyen', 'An', 'an@gmail.com', '0900000001', 'Da Lat', 10, 'pass'),
('Tran', 'Binh', 'binh@gmail.com', '0900000002', 'HCM', 5, 'pass');

INSERT INTO products (name, description, price, SKU, barcode)
VALUES 
('Milk Vinamilk', 'Fresh milk 1L', 30000, 'MILK01', '123456789'),
('Bread Whole Wheat', 'Healthy bread', 20000, 'BREAD01', '987654321'),
('Eggs', 'Chicken eggs 10pcs', 45000, 'EGG01', '111222333');

INSERT INTO product_variants (product_id, variant_json)
VALUES 
(1, '{"Size":"1L","Type":"Full Cream"}'),
(2, '{"Size":"500g","Type":"Brown"}'),
(3, '{"Pack":"10 eggs"}');

INSERT INTO product_stores (product_id, store_id, quantity_in_stock)
VALUES 
(1, 1, 100),
(2, 1, 50),
(3, 1, 80);

INSERT INTO batches (variant_id, product_id, warehouse_id, manufacture, supplier, quantity_total, quantity_available, create_date, expiry_date)
VALUES 
(1, 1, 1, 'Vinamilk Factory', 'Vinamilk', 200, 180, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY)),
(2, 2, 1, 'Bakery A', 'Local Supplier', 100, 90, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY));

INSERT INTO orders (customer_id, status, total_money)
VALUES (1, 'Pending', 50000);

INSERT INTO order_details (order_id, product_id, quantity, sub_total)
VALUES 
(1, 1, 1, 30000),
(1, 2, 1, 20000);

INSERT INTO warehouse_imports_details (supplier, unit_price, employee_import, warehouse_id)
VALUES 
('Vinamilk Supplier', 25000, 3, 1);

INSERT INTO batches_imported (batch_import_id, import_details_id, quantity)
VALUES 
(1, 1, 200);

INSERT INTO warehouse_exports_details (reason, employee_export, warehouse_id)
VALUES 
('Send to store', 3, 1);

INSERT INTO batches_exported (batch_export_id, export_details_id, quantity)
VALUES 
(1, 1, 50);
