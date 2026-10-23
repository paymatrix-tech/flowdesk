package com.flowdesk.api.blotter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedApiResponse {
    private boolean success;
    private String message;
    private List<Map<String, Object>> data;
    private Pagination pagination;
    private int statusCode;
    private String timestamp;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private int page;
        private int limit;
        private long total;
        private int totalPages;
    }

    public static PaginatedApiResponse ok(List<Map<String, Object>> data, Pagination pagination, String message) {
        return new PaginatedApiResponse(true, message, data, pagination, 200, Instant.now().toString());
    }
}
