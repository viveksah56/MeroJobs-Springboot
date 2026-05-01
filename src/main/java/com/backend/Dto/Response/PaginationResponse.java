package com.backend.Dto.Response;

import java.util.List;

public record PaginationResponse<T>(
        List<T> data,
        int page,
        int size,
        long total,
        int totalPages,
        String sortBy,
        String sortDir
) {
    public PaginationResponse {
        if (page < 1) throw new IllegalArgumentException("Page index must be >= 1");
        if (size <= 0) throw new IllegalArgumentException("Page size must be > 0");
        if (total < 0) throw new IllegalArgumentException("Total count must be >= 0");
    }



}
