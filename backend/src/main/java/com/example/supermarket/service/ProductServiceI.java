package com.example.supermarket.service;

import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.product.ProductCreateRequest;
import com.example.supermarket.dto.request.product.ProductSearchRequest;
import com.example.supermarket.dto.request.product.ProductUpdateRequest;
import com.example.supermarket.dto.response.product.ProductDetailResponse;
import com.example.supermarket.dto.response.product.ProductResponse;
import com.example.supermarket.dto.response.product.StoreStockResponse;

import java.util.List;

public interface ProductServiceI {
    ProductResponse createProduct(ProductCreateRequest request);
    ProductDetailResponse getProductById(Long id);
    PageResponse<ProductResponse> searchProducts(ProductSearchRequest request);
    ProductResponse updateProduct(Long id, ProductUpdateRequest request);
    void deleteProduct(Long id);
    List<StoreStockResponse> getProductStockInStores(Long productId);
    PageResponse<ProductResponse> getLowStockProducts(Integer threshold, Integer page, Integer size);
}
