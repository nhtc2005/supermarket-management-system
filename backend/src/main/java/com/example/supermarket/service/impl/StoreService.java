package com.example.supermarket.service.impl;

import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.store.StoreRequest;
import com.example.supermarket.dto.response.store.StoreResponse;
import com.example.supermarket.entity.Store;
import com.example.supermarket.exception.NotFoundError;
import com.example.supermarket.repository.ProductStoreRepository;
import com.example.supermarket.repository.StoreRepository;
import com.example.supermarket.service.StoreServiceI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService implements StoreServiceI {
    private final StoreRepository storeRepository;
    private final ProductStoreRepository productStoreRepository;

    @Transactional
    public StoreResponse createStore(StoreRequest request) {
        Store store = Store.builder()
                .name(request.getName())
                .location(request.getLocation())
                .build();

        store = storeRepository.save(store);
        return toResponse(store);
    }

    @Transactional(readOnly = true)
    public PageResponse<StoreResponse> getAllStores(int page, int size) {
        Page<Store> storePage = storeRepository.findAll(PageRequest.of(page, size));

        return PageResponse.<StoreResponse>builder()
                .content(storePage.getContent().stream().map(this::toResponse).toList())
                .pageNumber(storePage.getNumber())
                .pageSize(storePage.getSize())
                .totalElements(storePage.getTotalElements())
                .totalPages(storePage.getTotalPages())
                .isLast(storePage.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public StoreResponse getStoreById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Store not found"));
        return toResponse(store);
    }

    @Transactional
    public StoreResponse updateStore(Long id, StoreRequest request) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new NotFoundError("Store not found"));

        store.setName(request.getName());
        store.setLocation(request.getLocation());
        store = storeRepository.save(store);
        return toResponse(store);
    }

    @Transactional
    public void deleteStore(Long id) {
        if (!storeRepository.existsById(id)) {
            throw new NotFoundError("Store not found");
        }
        storeRepository.deleteById(id);
    }

    private StoreResponse toResponse(Store store) {
        Integer totalProducts = productStoreRepository.findByIdProductId(store.getId()).size();
        return StoreResponse.builder()
                .id(store.getId())
                .name(store.getName())
                .location(store.getLocation())
                .totalProducts(totalProducts)
                .build();
    }
}
