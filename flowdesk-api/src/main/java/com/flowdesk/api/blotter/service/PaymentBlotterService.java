package com.flowdesk.api.blotter.service;

import com.flowdesk.api.blotter.dto.PaginatedApiResponse;
import com.flowdesk.api.blotter.dto.PaymentCsvRequest;
import com.flowdesk.api.blotter.dto.PaymentSearchFiltersDto;
import com.flowdesk.api.blotter.dto.PaymentSearchRequest;
import com.flowdesk.api.blotter.query.PaymentQueryBuilder;
import lombok.RequiredArgsConstructor;

import static com.flowdesk.api.common.CommonConstants.PAYMENT_TABLE;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PaymentBlotterService {

    private final JdbcTemplate jdbcTemplate;
    private final PaymentQueryBuilder queryBuilder;

    public PaginatedApiResponse search(PaymentSearchRequest request) {
        int offset = (request.getPage() - 1) * request.getLimit();
        PaymentQueryBuilder.BuiltQuery built = queryBuilder.build(
                request.getFilters(),
                request.getSearch(),
                request.getColumns(),
                request.getSort_by(),
                request.getSort_order(),
                request.getLimit(),
                offset
        );
        List<Map<String, Object>> rows = jdbcTemplate.query(
                built.dataSql(),
                (rs, rowNum) -> mapRow(rs),
                built.dataParams().toArray()
        );
        Long total = jdbcTemplate.queryForObject(built.countSql(), Long.class, built.countParams().toArray());
        long totalVal = total != null ? total : 0;
        int totalPages = (int) Math.ceil((double) totalVal / request.getLimit());
        PaginatedApiResponse.Pagination pagination = new PaginatedApiResponse.Pagination(
                request.getPage(), request.getLimit(), totalVal, totalPages
        );
        return PaginatedApiResponse.ok(rows, pagination, "Payments retrieved successfully");
    }

    public Map<String, Object> getById(long paymentId) {
        List<Map<String, Object>> rows = jdbcTemplate.query(
                "SELECT * FROM " + PAYMENT_TABLE + " WHERE payment_id = ?",
                (rs, rowNum) -> mapRow(rs),
                paymentId
        );
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("Payment not found");
        }
        return rows.getFirst();
    }

    public Map<String, Object> stats(PaymentSearchFiltersDto filters) {
        PaymentQueryBuilder.WhereClause where = queryBuilder.buildWhere(filters, null);
        String sql = """
                SELECT COUNT(*) AS total_count,
                       COUNT(*) FILTER (WHERE payment_status = 'PENDING_APPROVAL') AS pending_count,
                       COUNT(*) FILTER (WHERE payment_status = 'PROCESSED') AS completed_count,
                       COUNT(*) FILTER (WHERE payment_status = 'REJECTED') AS failed_count,
                       COALESCE(SUM(sender_amount), 0) AS total_amount,
                       COALESCE(AVG(sender_amount), 0) AS average_amount
                FROM %s
                """.formatted(PAYMENT_TABLE) + where.sql();
        Map<String, Object> row = jdbcTemplate.queryForMap(sql, where.params().toArray());
        Map<String, Object> result = new HashMap<>();
        result.put("total_count", toLong(row.get("total_count")));
        result.put("pending_count", toLong(row.get("pending_count")));
        result.put("completed_count", toLong(row.get("completed_count")));
        result.put("failed_count", toLong(row.get("failed_count")));
        result.put("total_amount", toDouble(row.get("total_amount")));
        result.put("average_amount", toDouble(row.get("average_amount")));
        return result;
    }

    public String exportCsv(PaymentCsvRequest request) {
        int offset = (request.getPage() - 1) * request.getLimit();
        PaymentQueryBuilder.BuiltQuery built = queryBuilder.build(
                request.getFilters(),
                request.getSearch(),
                request.getColumns(),
                request.getSort_by(),
                request.getSort_order(),
                request.getLimit(),
                offset
        );
        List<Map<String, Object>> rows = jdbcTemplate.query(
                built.dataSql(),
                (rs, rowNum) -> mapRow(rs),
                built.dataParams().toArray()
        );
        if (rows.isEmpty()) {
            return "\"payment_id\"";
        }
        List<String> headers = new ArrayList<>(rows.getFirst().keySet());
        StringBuilder csv = new StringBuilder();
        csv.append(headers.stream().map(this::escapeCsv).reduce((a, b) -> a + "," + b).orElse(""));
        csv.append("\n");
        for (Map<String, Object> row : rows) {
            List<String> values = new ArrayList<>();
            for (String h : headers) {
                Object v = row.get(h);
                values.add(escapeCsv(v == null ? "" : String.valueOf(v)));
            }
            csv.append(String.join(",", values)).append("\n");
        }
        return csv.toString();
    }

    public List<String> availableColumns() {
        return List.of(
                "payment_id", "transaction_id", "payment_status", "payment_type", "payment_io_type",
                "delivery_method", "channel", "business_unit", "department", "counter_party",
                "sender_name", "receiver_name", "sender_amount", "receiver_amount", "sender_currency",
                "receiver_currency", "payment_date", "delivery_date", "creation_date", "entry_type",
                "memo_post_status", "fee_amount", "payment_note", "operation_notes"
        );
    }

    private long toLong(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number n) {
            return n.longValue();
        }
        return Long.parseLong(value.toString());
    }

    private Map<String, Object> mapRow(java.sql.ResultSet rs) throws java.sql.SQLException {
        Map<String, Object> row = new LinkedHashMap<>();
        ResultSetMetaData meta = rs.getMetaData();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            String label = meta.getColumnLabel(i);
            Object value = rs.getObject(i);
            if (value instanceof BigDecimal bd) {
                value = bd.doubleValue();
            }
            row.put(label, value);
        }
        return row;
    }

    private double toDouble(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof BigDecimal bd) {
            return bd.doubleValue();
        }
        if (value instanceof Number n) {
            return n.doubleValue();
        }
        return Double.parseDouble(value.toString());
    }

    private String escapeCsv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
