package com.example.supermarket;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals; // For JUnit 5

import java.time.*;

import org.springframework.boot.test.context.SpringBootTest;
import com.example.supermarket.entity.*;

@SpringBootTest
class EntityMethodAcceptTest {
	// // ENTITY CLASSES
	// @Test
	// void testBatch(){
	// 	Batch b = new Batch(1L, 2L, 3L, 0L, "pcfood", "bo_pc", 36, 36, "2025-06-03", "2036-06-03");
	// 	assertEquals(1, b.getID());
	// 	assertEquals(2, b.getVariantID());
	// 	assertEquals(3, b.getProductID());
	// 	assertEquals(0, b.getWarehouseID());
	// 	assertEquals("pcfood", b.getManufacturer());
	// 	assertEquals("bo_pc", b.getSupplier());
	// 	assertEquals(36, b.getAvailableQuantity());
	// 	assertEquals(36, b.getTotalQuantity());
	// 	assertEquals(LocalDate.of(2025, 6, 3), b.getManufactureDate());
	// 	assertEquals(LocalDate.of(2036, 6, 3), b.getExpireDate());
	// }
	// @Test
	// void testCart(){
	// 	Cart obj = new Cart(0, 1, "2025-11-20 19:30:00", "2025-11-20 19:36:18");
	// 	assertEquals(0, obj.getID());
	// 	assertEquals(1, obj.getCustomerID());
	// 	assertEquals(LocalDateTime.of(2025, 11, 20, 19, 30, 00), obj.getCreateTime());
	// 	assertEquals(LocalDateTime.of(2025, 11, 20, 19, 36, 18), obj.getUpdateTime());
	// }
	// @Test
	// void testCustomer(){
	// 	Customer obj = new Customer(1, "Momoi", "Saiba", "mynegga@bluearchive.kivotos", "0987654321", null, 0);
	// 	assertEquals(1, obj.getID());
	// 	assertEquals("Momoi", obj.getFirstName());
	// 	assertEquals("Saiba", obj.getLastName());
	// 	assertEquals("mynegga@bluearchive.kivotos", obj.getEmail());
	// 	assertEquals("0987654321", obj.getPhone());
	// 	assertEquals(null, obj.getAddress());
	// 	assertEquals(0, obj.getLoyaltyPt());
	// }
	// @Test
	// void testCartItem(){
	// 	CartItem obj = new CartItem(1, 2, 10, 150.5f);
	// 	assertEquals(1, obj.getCartID());
	// 	assertEquals(2, obj.getProductID());
	// 	assertEquals(10, obj.getQuantity());
	// 	assertEquals(150.5f, obj.getSubtotal());
	// }

	// @Test
	// void testEmployee(){
	// 	Employee obj = new Employee(1L, "john_doe", "pass123", "John", "Doe", "2023-01-15", 2L);
	// 	assertEquals(1L, obj.getID());
	// 	assertEquals("john_doe", obj.getUsername());
	// 	assertEquals("pass123", obj.getPassword());
	// 	assertEquals("John", obj.getFirstName());
	// 	assertEquals("Doe", obj.getLastName());
	// 	assertEquals(LocalDate.of(2023, 1, 15), obj.getHireDate());
	// 	assertEquals(2L, obj.getManagerID());
	// }

	// @Test
	// void testOrder(){
	// 	Order obj = new Order(1, 2, LocalDateTime.of(2025, 11, 20, 14, 30, 0), "pending", 500.75f);
	// 	assertEquals(1, obj.getID());
	// 	assertEquals(2, obj.getCustomerID());
	// 	assertEquals(LocalDateTime.of(2025, 11, 20, 14, 30, 0), obj.getCreatedAt());
	// 	assertEquals("pending", obj.getStatus());
	// 	assertEquals(500.75f, obj.getTotalMoney());
	// }

	// @Test
	// void testOrderDetail(){
	// 	OrderDetail obj = new OrderDetail(1, 2, 5, 250.0f);
	// 	assertEquals(1, obj.getOrderID());
	// 	assertEquals(2, obj.getProductID());
	// 	assertEquals(5, obj.getQuantity());
	// 	assertEquals(250.0f, obj.getSubtotal());
	// }

	// @Test
	// void testProduct(){
	// 	Product obj = new Product(1, "Laptop", "Gaming laptop", 1500.99f, "LAP-001", "123456789012");
	// 	assertEquals(1, obj.getID());
	// 	assertEquals("Laptop", obj.getName());
	// 	assertEquals("Gaming laptop", obj.getDescription());
	// 	assertEquals(1500.99f, obj.getPrice());
	// 	assertEquals("LAP-001", obj.getSKU());
	// 	assertEquals("123456789012", obj.getBarcode());
	// }

	// @Test
	// void testProductStore(){
	// 	ProductStore obj = new ProductStore(1L, 2L, 50);
	// 	assertEquals(1L, obj.getProductID());
	// 	assertEquals(2L, obj.getStoreID());
	// 	assertEquals(50, obj.getQuantity());
	// }

	// @Test
	// void testProductVariant(){
	// 	ProductVariant obj = new ProductVariant(1L, 2L, "{\"color\":\"red\",\"size\":\"M\"}");
	// 	assertEquals(1L, obj.getID());
	// 	assertEquals(2L, obj.getProductID());
	// 	assertEquals("{\"color\":\"red\",\"size\":\"M\"}", obj.getVariantJson().getBody());
	// }

	// @Test
	// void testSaleEmployee(){
	// 	SalesEmployee obj = new SalesEmployee(1L, 2L, 15000.50d);
	// 	assertEquals(1L, obj.getEmployeeID());
	// 	assertEquals(2L, obj.getStoreID());
	// 	assertEquals(15000.50f, obj.getTotalSales());
	// }

	// @Test
	// void testStore(){
	// 	Store obj = new Store(1L, "Downtown Store", "123 Main St", 2L);
	// 	assertEquals(1L, obj.getID());
	// 	assertEquals("Downtown Store", obj.getName());
	// 	assertEquals("123 Main St", obj.getLocation());
	// 	assertEquals(2L, obj.getManagerID());
	// }

	// @Test
	// void testWarehouse(){
	// 	Warehouse obj = new Warehouse(1L, "Central Warehouse", "456 Industrial Blvd", 2L);
	// 	assertEquals(1L, obj.getID());
	// 	assertEquals("Central Warehouse", obj.getName());
	// 	assertEquals("456 Industrial Blvd", obj.getLocation());
	// 	assertEquals(2L, obj.getManagerID());
	// }

	// @Test
	// void testWarehouseEmployee(){
	// 	WarehouseEmployee obj = new WarehouseEmployee(1L, 2L);
	// 	assertEquals(1L, obj.getEmployeeID());
	// 	assertEquals(2L, obj.getWarehouseID());
	// }

	/// @Test
	// void testWarehouseExportDetail(){
	// 	WarehouseExportDetail obj = new WarehouseExportDetail(1L, "Damaged goods", 2L, 
	// 								LocalDateTime.of(2025, 11, 20, 10, 0, 0), 3L);
	// 	assertEquals(1L, obj.getDetailsID());
	// 	assertEquals("Damaged goods", obj.getReason());
	// 	assertEquals(2L, obj.getEmployeeExport());
	// 	assertEquals(LocalDateTime.of(2025, 11, 20, 10, 0, 0), obj.getExportDate());
	// 	assertEquals(3L, obj.getWarehouseID());
	// }

	// @Test
	// void testWarehouseImportDetail(){
	// 	WarehouseImportDetail obj = new WarehouseImportDetail(1L, "ABC Supplier", 
	// 								LocalDateTime.of(2025, 11, 20, 9, 0, 0), 25.5f, 2L, 3L);
	// 	assertEquals(1L, obj.getDetailsID());
	// 	assertEquals("ABC Supplier", obj.getSupplier());
	// 	assertEquals(LocalDateTime.of(2025, 11, 20, 9, 0, 0), obj.getImportDate());
	// 	assertEquals(25.5f, obj.getUnitPrice());
	// 	assertEquals(2L, obj.getEmployeeImport());
	// 	assertEquals(3L, obj.getWarehouseID());
	// }

	// // AUXILIARY CLASSES	
	
}