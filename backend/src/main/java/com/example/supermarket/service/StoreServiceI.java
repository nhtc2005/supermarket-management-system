package com.example.supermarket.service;

import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.store.StoreRequest;
import com.example.supermarket.dto.response.store.StoreResponse;

public interface StoreServiceI {
    StoreResponse createStore(StoreRequest request);
    PageResponse<StoreResponse> getAllStores(int page, int size);
    StoreResponse getStoreById(Long id);
    StoreResponse updateStore(Long id, StoreRequest request);
    void deleteStore(Long id);

}
