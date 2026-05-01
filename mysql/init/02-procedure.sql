USE supermarket_db;

DROP PROCEDURE IF EXISTS sp_create_import;
DROP PROCEDURE IF EXISTS sp_insert_products;
DROP PROCEDURE IF EXISTS sp_insert_variants;
DROP PROCEDURE IF EXISTS sp_insert_new_batches;
DROP PROCEDURE IF EXISTS sp_log_batches_imported;
DROP PROCEDURE IF EXISTS import_existed_batch;
DROP PROCEDURE IF EXISTS sp_update_batches;

DELIMITER $$

CREATE PROCEDURE sp_create_import(
    IN p_employee_id BIGINT,
    IN p_warehouse_id BIGINT,
    IN p_supplier VARCHAR(255),
    IN p_unit_price DECIMAL(10,2),
    OUT p_import_id BIGINT
)
BEGIN
    INSERT INTO warehouse_imports_details(employee_import, warehouse_id, supplier, unit_price, import_date)
    VALUES (p_employee_id, p_warehouse_id, p_supplier, p_unit_price, NOW());

    SET p_import_id = LAST_INSERT_ID();
END$$
DELIMITER ;


DELIMITER $$

CREATE PROCEDURE sp_insert_products(
    IN p_items_json JSON,
    IN p_unit_price DECIMAL(10,2)
)
BEGIN
    INSERT INTO products(id, name, price, description, SKU, barcode)
    SELECT DISTINCT
        CAST(JSON_UNQUOTE(JSON_EXTRACT(value, '$.product_id')) AS UNSIGNED),
        JSON_UNQUOTE(JSON_EXTRACT(value, '$.product_name')),
        p_unit_price,
        JSON_UNQUOTE(JSON_EXTRACT(value, '$.description')),
        JSON_UNQUOTE(JSON_EXTRACT(value, '$.sku')),
        JSON_UNQUOTE(JSON_EXTRACT(value, '$.barcode'))
    FROM JSON_TABLE(p_items_json, '$[*]' COLUMNS(value JSON PATH '$')) AS items
    ON DUPLICATE KEY UPDATE
        name = VALUES(name),
        price = VALUES(price),
        description = VALUES(description),
        SKU = VALUES(SKU),
        barcode = VALUES(barcode);
END$$

DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_insert_variants(
    IN p_items_json JSON
)
BEGIN
    INSERT INTO product_variants(product_id, variant_json)
    SELECT
        CAST(JSON_UNQUOTE(JSON_EXTRACT(value, '$.product_id')) AS UNSIGNED),
        JSON_EXTRACT(value, '$.variant_json')
    FROM JSON_TABLE(p_items_json, '$[*]' COLUMNS(value JSON PATH '$')) AS items
    WHERE NOT EXISTS (
        SELECT 1 FROM product_variants pv
        WHERE pv.product_id = CAST(JSON_UNQUOTE(JSON_EXTRACT(value, '$.product_id')) AS UNSIGNED)
          AND MD5(CAST(pv.variant_json AS CHAR)) = MD5(CAST(JSON_EXTRACT(value, '$.variant_json') AS CHAR))
    );
END$$
DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_insert_new_batches(
    IN p_items_json JSON,
    IN p_warehouse_id BIGINT,
    IN p_supplier VARCHAR(255)
)
BEGIN
    INSERT INTO batches(
        variant_id, product_id, warehouse_id, supplier,
        quantity_total, quantity_available, create_date,
        manufacture, expiry_date
    )
    SELECT
        pv.id,
        pv.product_id,
        p_warehouse_id,
        p_supplier,
        input.quantity,
        input.quantity,
        NOW(),
        input.manufacture,
        input.expiry_date
    FROM (
        SELECT
            CAST(JSON_UNQUOTE(JSON_EXTRACT(value, '$.product_id')) AS UNSIGNED) AS product_id,
            MD5(CAST(JSON_EXTRACT(value, '$.variant_json') AS CHAR)) AS variant_hash,
            CAST(JSON_UNQUOTE(JSON_EXTRACT(value, '$.quantity')) AS UNSIGNED) AS quantity,
            CAST(JSON_UNQUOTE(JSON_EXTRACT(value, '$.manufacture')) AS DATE) AS manufacture,
            CAST(JSON_UNQUOTE(JSON_EXTRACT(value, '$.expiry_date')) AS DATE) AS expiry_date
        FROM JSON_TABLE(p_items_json, '$[*]' COLUMNS(value JSON PATH '$')) AS items
    ) AS input
    INNER JOIN product_variants pv
        ON pv.product_id = input.product_id
        AND MD5(CAST(pv.variant_json AS CHAR)) = input.variant_hash
    LEFT JOIN batches b
        ON b.variant_id = pv.id AND b.warehouse_id = p_warehouse_id
    WHERE b.id IS NULL;
END$$
DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_update_batches(
    IN p_items_json JSON,
    IN p_warehouse_id BIGINT
)
BEGIN
    UPDATE batches b
    INNER JOIN product_variants pv
        ON b.variant_id = pv.id AND b.warehouse_id = p_warehouse_id
    INNER JOIN (
        SELECT
            CAST(JSON_UNQUOTE(JSON_EXTRACT(value, '$.product_id')) AS UNSIGNED) AS product_id,
            MD5(CAST(JSON_EXTRACT(value, '$.variant_json') AS CHAR)) AS variant_hash,
            CAST(JSON_UNQUOTE(JSON_EXTRACT(value, '$.quantity')) AS UNSIGNED) AS quantity,
            CAST(JSON_UNQUOTE(JSON_EXTRACT(value, '$.manufacture')) AS DATE) AS manufacture,
            CAST(JSON_UNQUOTE(JSON_EXTRACT(value, '$.expiry_date')) AS DATE) AS expiry_date
        FROM JSON_TABLE(p_items_json, '$[*]' COLUMNS(value JSON PATH '$')) AS items
    ) AS input
    ON pv.product_id = input.product_id AND MD5(CAST(pv.variant_json AS CHAR)) = input.variant_hash
    SET b.quantity_total = b.quantity_total + input.quantity,
        b.quantity_available = b.quantity_available + input.quantity,
        b.manufacture = COALESCE(input.manufacture, b.manufacture),
        b.expiry_date = COALESCE(input.expiry_date, b.expiry_date);
END$$
DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_log_batches_imported(
    IN p_items_json JSON,
    IN p_warehouse_id BIGINT,
    IN p_import_id BIGINT
)
BEGIN
    INSERT INTO batches_imported(batch_import_id, import_details_id, quantity)
    SELECT
        b.id,
        p_import_id,
        input.quantity
    FROM (
        SELECT
            CAST(JSON_UNQUOTE(JSON_EXTRACT(value, '$.product_id')) AS UNSIGNED) AS product_id,
            MD5(CAST(JSON_EXTRACT(value, '$.variant_json') AS CHAR)) AS variant_hash,
            CAST(JSON_UNQUOTE(JSON_EXTRACT(value, '$.quantity')) AS UNSIGNED) AS quantity
        FROM JSON_TABLE(p_items_json, '$[*]' COLUMNS(value JSON PATH '$')) AS items
    ) AS input
    INNER JOIN product_variants pv
        ON pv.product_id = input.product_id
        AND MD5(CAST(pv.variant_json AS CHAR)) = input.variant_hash
    INNER JOIN batches b
        ON b.variant_id = pv.id AND b.warehouse_id = p_warehouse_id;
END$$
DELIMITER ;

DELIMITER $$

CREATE PROCEDURE import_existed_batch(
    IN p_employee_id BIGINT,
    IN p_warehouse_id BIGINT,
    IN p_supplier VARCHAR(255),
    IN p_unit_price DECIMAL(10,2),
    IN p_variant_id BIGINT,
    IN p_product_id BIGINT,
    IN p_quantity INT
)
BEGIN
    DECLARE v_import_id BIGINT;
    DECLARE v_batch_id BIGINT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Error during existing batch import';
    END;

    START TRANSACTION;

    -- 1. Tạo phiếu nhập
    INSERT INTO warehouse_imports_details(employee_import, warehouse_id, supplier, unit_price, import_date)
    VALUES (p_employee_id, p_warehouse_id, p_supplier, p_unit_price, NOW());
    SET v_import_id = LAST_INSERT_ID();

    -- 2. Lấy batch đã tồn tại
    SELECT id INTO v_batch_id
    FROM batches
    WHERE variant_id = p_variant_id AND product_id = p_product_id AND warehouse_id = p_warehouse_id
    LIMIT 1;

    IF v_batch_id IS NULL THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'batch does not exist';
    END IF;

    -- 3. Cập nhật batch
    UPDATE batches
    SET quantity_total = quantity_total + p_quantity,
        quantity_available = quantity_available + p_quantity
    WHERE id = v_batch_id;

    -- 4. Ghi log nhập
    INSERT INTO batches_imported(batch_import_id, import_details_id, quantity)
    VALUES (v_batch_id, v_import_id, p_quantity);

    COMMIT;
END$$
DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_create_export(
    IN p_employee_id BIGINT,
    IN p_warehouse_id BIGINT,
    IN p_reason VARCHAR(255),
    OUT p_export_id BIGINT
)
BEGIN
    INSERT INTO warehouse_exports_details(employee_export, warehouse_id, reason, export_date)
    VALUES (p_employee_id, p_warehouse_id, p_reason, NOW());

    SET p_export_id = LAST_INSERT_ID();
END$$
DELIMITER ;

DELIMITER $$

CREATE PROCEDURE sp_select_batches_for_export(
    IN p_warehouse_id BIGINT,
    IN p_product_id BIGINT,
    IN p_variant_id BIGINT
)
BEGIN
    SELECT b.id AS batch_id,
           b.product_id,
           b.variant_id,
           b.quantity_available
    FROM batches b
    WHERE b.warehouse_id = p_warehouse_id
      AND b.quantity_available > 0
      AND (p_product_id = 0 OR b.product_id = p_product_id)
      AND (p_variant_id = 0 OR b.variant_id = p_variant_id);
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_log_batches_exported;
DELIMITER $$

CREATE PROCEDURE sp_log_batches_exported(
    IN p_export_id BIGINT,
    IN p_batch_id BIGINT,
    IN p_quantity INT
)
BEGIN
    DECLARE v_quantity INT;

    SELECT LEAST(p_quantity, quantity_available) INTO v_quantity
    FROM batches
    WHERE id = p_batch_id;

    IF v_quantity < p_quantity THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Batch has no available quantity';
    END IF;

    -- Ghi log
    INSERT INTO batches_exported(batch_export_id, export_details_id, quantity)
    VALUES (p_batch_id, p_export_id, v_quantity);

    -- Cập nhật batch
    UPDATE batches
    SET quantity_available = quantity_available - v_quantity
    WHERE id = p_batch_id;
END$$
DELIMITER ;

DROP PROCEDURE IF EXISTS sp_export_warehouse_batches;
DELIMITER $$

CREATE PROCEDURE sp_export_warehouse_batches(
    IN p_export_id BIGINT
)
BEGIN
    SELECT JSON_OBJECT(
        'export_id', wed.details_id,
        'warehouse_id', wed.warehouse_id,
        'employee_export', wed.employee_export,
        'reason', wed.reason,
        'export_date', DATE_FORMAT(wed.export_date, '%Y-%m-%d %H:%i:%s'),
        'batches', JSON_ARRAYAGG(
            JSON_OBJECT(
                'batch_id', b.id,
                'product_id', p.id,
                'product_name', p.name,
                'variant_id', pv.id,
                'variant', pv.variant_json,
                'quantity_exported', GREATEST(be.quantity,0),
                'quantity_total', b.quantity_total,
                'quantity_available', b.quantity_available,
                'supplier', b.supplier,
                'manufacture', DATE_FORMAT(b.manufacture,'%Y-%m-%d'),
                'expiry_date', DATE_FORMAT(b.expiry_date,'%Y-%m-%d')
            )
        )
    ) AS export_details
    FROM warehouse_exports_details wed
    INNER JOIN batches_exported be ON wed.details_id = be.export_details_id
    INNER JOIN batches b ON be.batch_export_id = b.id
    INNER JOIN products p ON b.product_id = p.id
    INNER JOIN product_variants pv ON b.variant_id = pv.id
    WHERE wed.details_id = p_export_id
    GROUP BY wed.details_id;
END$$
DELIMITER ;

DELIMITER $$
CREATE FUNCTION fn_variant_exists(
    p_keyword VARCHAR(255),
    p_variant_filter JSON,
    p_warehouse_id BIGINT
)
RETURNS BOOLEAN
DETERMINISTIC
BEGIN
    DECLARE v_count INT;

    SELECT COUNT(*) INTO v_count
    FROM product_variants pv
    JOIN products p ON pv.product_id = p.id
    JOIN batches b ON b.variant_id = pv.id
    WHERE p.name COLLATE utf8mb4_general_ci LIKE CONCAT('%', p_keyword, '%') COLLATE utf8mb4_general_ci
      AND b.warehouse_id = p_warehouse_id
      AND b.quantity_available > 0
      AND (p_variant_filter IS NULL OR JSON_CONTAINS(pv.variant_json, p_variant_filter));

    RETURN v_count > 0;
END$$
DELIMITER ;
