package com.example.supermarket.service;

import com.example.supermarket.dto.PageResponse;
import com.example.supermarket.dto.request.batch.BatchRequest;
import com.example.supermarket.dto.response.batch.BatchResponse;

public interface BatchServiceI {
    BatchResponse createBatch(BatchRequest request);
    PageResponse<BatchResponse> getAllBatches(int page, int size);
    BatchResponse getBatchById(Long id);
    PageResponse<BatchResponse> getExpiredBatches(int page, int size);
    void deleteBatch(Long id);
}
