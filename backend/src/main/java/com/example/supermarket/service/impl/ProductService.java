package com.example.supermarket.service.impl;

import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.product.ProductCreateRequest;
import com.example.supermarket.dto.request.product.ProductSearchRequest;
import com.example.supermarket.dto.request.product.ProductUpdateRequest;
import com.example.supermarket.dto.response.product.ProductDetailResponse;
import com.example.supermarket.dto.response.product.ProductResponse;
import com.example.supermarket.dto.response.product.StoreStockResponse;
import com.example.supermarket.entity.Product;
import com.example.supermarket.entity.ProductStore;
import com.example.supermarket.exception.ConflictError;
import com.example.supermarket.exception.NotFoundError;
import com.example.supermarket.mapper.ProductMapper;
import com.example.supermarket.repository.ProductRepository;
import com.example.supermarket.repository.ProductStoreRepository;
import com.example.supermarket.repository.ProductVariantRepository;
import com.example.supermarket.service.ProductServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService implements ProductServiceI {
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductStoreRepository productStoreRepository;
    private final ProductMapper productMapper;

    /**
     * Create new product
     */
    @Transactional
    public ProductResponse createProduct(ProductCreateRequest request) {
        log.info("Creating new product: {}", request.getName());

        // Check SKU uniqueness
        if (request.getSku() != null && productRepository.existsBySku(request.getSku())) {
            throw new ConflictError("SKU already exists: " + request.getSku());
        }

        // Check Barcode uniqueness
        if (request.getBarcode() != null && productRepository.existsByBarcode(request.getBarcode())) {
            throw new ConflictError("Barcode already exists: " + request.getBarcode());
        }

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .sku(request.getSku())
                .barcode(request.getBarcode())
                .build();

        product = productRepository.save(product);
        log.info("Product created successfully with ID: {}", product.getId());

        return productMapper.toResponse(product, 0, 0);
    }

    /**
     * Get product by ID
     */
    @Transactional(readOnly = true)
    public ProductDetailResponse getProductById(Long id) {
        log.info("Getting product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Product not found with ID: " + id));

        // Get variants
        Integer variantCount = productVariantRepository.countByProductId(id);

        // Get total stock from all warehouses via batches
        Integer totalStock = productVariantRepository.sumTotalStockByProductId(id);

        return productMapper.toDetailResponse(product, variantCount, totalStock != null ? totalStock : 0);
    }

    /**
     * Search products with filters
     */
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> searchProducts(ProductSearchRequest request) {
        log.info("Searching products with filters");

        Sort sort = request.getSortDirection().equalsIgnoreCase("DESC")
                ? Sort.by(request.getSortBy()).descending()
                : Sort.by(request.getSortBy()).ascending();

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<Product> productPage;

        // Apply filters
        if (request.getSku() != null && !request.getSku().isEmpty()) {
            productPage = productRepository.findBySkuContaining(request.getSku(), pageable);
        } else if (request.getBarcode() != null && !request.getBarcode().isEmpty()) {
            productPage = productRepository.findByBarcodeContaining(request.getBarcode(), pageable);
        } else if (request.getName() != null && !request.getName().isEmpty()) {
            productPage = productRepository.findByNameContaining(request.getName(), pageable);
        } else if (request.getMinPrice() != null && request.getMaxPrice() != null) {
            productPage = productRepository.findByPriceBetween(
                    request.getMinPrice(),
                    request.getMaxPrice(),
                    pageable
            );
        } else {
            productPage = productRepository.findAll(pageable);
        }

        List<ProductResponse> content = productPage.getContent().stream()
                .map(product -> {
                    Integer variantCount = productVariantRepository.countByProductId(product.getId());
                    Integer totalStock = productVariantRepository.sumTotalStockByProductId(product.getId());
                    return productMapper.toResponse(product, variantCount, totalStock != null ? totalStock : 0);
                })
                .toList();

        return PageResponse.<ProductResponse>builder()
                .content(content)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .isLast(productPage.isLast())
                .build();
    }

    /**
     * Update product
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
        log.info("Updating product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Product not found with ID: " + id));

        // Check SKU uniqueness if changed
        if (request.getSku() != null && !request.getSku().equals(product.getSku())) {
            if (productRepository.existsBySku(request.getSku())) {
                throw new ConflictError("SKU already exists: " + request.getSku());
            }
            product.setSku(request.getSku());
        }

        // Check Barcode uniqueness if changed
        if (request.getBarcode() != null && !request.getBarcode().equals(product.getBarcode())) {
            if (productRepository.existsByBarcode(request.getBarcode())) {
                throw new ConflictError("Barcode already exists: " + request.getBarcode());
            }
            product.setBarcode(request.getBarcode());
        }

        // Update fields
        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        product = productRepository.save(product);
        log.info("Product updated successfully: {}", id);

        Integer variantCount = productVariantRepository.countByProductId(id);
        Integer totalStock = productVariantRepository.sumTotalStockByProductId(id);

        return productMapper.toResponse(product, variantCount, totalStock != null ? totalStock : 0);
    }

    /**
     * Delete product
     */
    @Transactional
    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Product not found with ID: " + id));

        // Check if product has variants with stock
        Integer totalStock = productVariantRepository.sumTotalStockByProductId(id);
        if (totalStock != null && totalStock > 0) {
            throw new ConflictError("Cannot delete product with existing stock");
        }

        productRepository.delete(product);
        log.info("Product deleted successfully: {}", id);
    }

    /**
     * Get product stock in stores
     */
    @Transactional(readOnly = true)
    public List<StoreStockResponse> getProductStockInStores(Long productId) {
        log.info("Getting stock for product ID: {} in all stores", productId);

        if (!productRepository.existsById(productId)) {
            throw new NotFoundError("Product not found with ID: " + productId);
        }

        List<ProductStore> productStores = productStoreRepository.findByIdProductId(productId);

        return productStores.stream()
                .map(ps -> StoreStockResponse.builder()
                        .storeId(ps.getId().getStoreId())
                        .productId(ps.getId().getProductId())
                        .quantityInStock(ps.getQuantityInStock())
                        .build())
                .toList();
    }

    /**
     * Get low stock products
     */
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getLowStockProducts(Integer threshold, Integer page, Integer size) {
        log.info("Getting low stock products with threshold: {}", threshold);

        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);

        List<ProductResponse> lowStockProducts = productPage.getContent().stream()
                .map(product -> {
                    Integer variantCount = productVariantRepository.countByProductId(product.getId());
                    Integer totalStock = productVariantRepository.sumTotalStockByProductId(product.getId());
                    return productMapper.toResponse(product, variantCount, totalStock != null ? totalStock : 0);
                })
                .filter(product -> product.getAvailableQuantity() < threshold)
                .toList();

        return PageResponse.<ProductResponse>builder()
                .content(lowStockProducts)
                .pageNumber(page)
                .pageSize(size)
                .totalElements((long) lowStockProducts.size())
                .totalPages((int) Math.ceil((double) lowStockProducts.size() / size))
                .isLast(lowStockProducts.size() <= size)
                .build();
    }
}
