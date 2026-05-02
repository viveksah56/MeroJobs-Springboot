package com.backend.Util;

import com.backend.Dto.Request.PaginationRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageRequestBuilder {

    private static final int DEFAULT_PAGE        = 1;
    private static final int DEFAULT_SIZE        = 10;
    private static final int MAX_SIZE            = 100;
    private static final String DEFAULT_SORT_BY  = "createdAt";
    private static final String DEFAULT_SORT_DIR = "desc";

    private PageRequestBuilder() {}

    public static Pageable build(int page, int size, String sortBy, String sortDir) {
        int resolvedPage    = page < 1 ? DEFAULT_PAGE : page;
        int resolvedSize    = size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        String resolvedSort = (sortBy == null  || sortBy.isBlank())  ? DEFAULT_SORT_BY  : sortBy;
        String resolvedDir  = (sortDir == null || sortDir.isBlank()) ? DEFAULT_SORT_DIR : sortDir;

        Sort sort = resolvedDir.equalsIgnoreCase("desc")
                ? Sort.by(resolvedSort).descending()
                : Sort.by(resolvedSort).ascending();

        return PageRequest.of(resolvedPage - 1, resolvedSize, sort);
    }

    public static Pageable build(PaginationRequest request) {
        return build(
                request.resolvedPage(),
                request.resolvedSize(),
                request.resolvedSort(),
                request.resolvedSortDirection()
        );
    }

    public static Pageable buildWithDefaultSort(int page, int size) {
        return build(page, size, DEFAULT_SORT_BY, DEFAULT_SORT_DIR);
    }
}