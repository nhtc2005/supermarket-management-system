package com.example.supermarket.mapper;

import com.example.supermarket.dto.response.product.ProductDetailResponse;
import com.example.supermarket.dto.response.product.ProductResponse;
import com.example.supermarket.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "totalVariants", source = "totalVariants")
    @Mapping(target = "availableQuantity", source = "availableQuantity")
    ProductResponse toResponse(
            Product product,
            Integer totalVariants,
            Integer availableQuantity
    );

    @Mapping(target = "totalVariants", source = "variantCount")
    @Mapping(target = "availableQuantity", source = "totalStock")
    ProductDetailResponse toDetailResponse(
            Product product,
            Integer variantCount,
            Integer totalStock
    );
}
