package com.flowdesk.api.blotter.query;

import com.flowdesk.api.blotter.dto.PaymentSearchFiltersDto;
import org.springframework.stereotype.Component;

import static com.flowdesk.api.common.CommonConstants.PAYMENT_TABLE;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Component
public class PaymentQueryBuilder {

    /**
     * Text columns on {@code payments.payment} included in the global blotter search box.
     * Covers every column returned by the UI blotter ({@code defaultColumns}) plus related row fields.
     */
    private static final List<String> SEARCH_TEXT_COLUMNS = List.of(
            "transaction_id",
            "payment_status",
            "payment_type",
            "payment_io_type",
            "delivery_method",
            "entry_type",
            "channel",
            "business_unit",
            "department",
            "counter_party",
            "sender_name",
            "receiver_name",
            "sender_currency",
            "receiver_currency",
            "memo_post_status",
            "external_ref",
            "payment_note",
            "operation_notes",
            "return_status",
            "fee_currency",
            "book",
            "cost_center",
            "customer_type",
            "sender_account_number",
            "receiver_account_number"
    );

    /** IDs, amounts, and dates — matched via {@code CAST(... AS TEXT) LIKE}. */
    private static final List<String> SEARCH_CAST_TEXT_COLUMNS = List.of(
            "payment_id",
            "sender_amount",
            "receiver_amount",
            "fee_amount",
            "payment_date",
            "delivery_date",
            "creation_date_time"
    );

    private static final Set<String> SORTABLE = Set.of(
            "payment_id", "transaction_id", "payment_date", "delivery_date", "creation_date_time",
            "sender_amount", "receiver_amount", "payment_status", "payment_type", "payment_io_type",
            "delivery_method", "channel", "business_unit", "department", "counter_party",
            "sender_name", "receiver_name"
    );

    private static final Map<String, String> COLUMN_MAP = Map.ofEntries(
            Map.entry("payment_id", "payment_id"),
            Map.entry("transaction_id", "transaction_id"),
            Map.entry("payment_status", "payment_status"),
            Map.entry("payment_type", "payment_type"),
            Map.entry("payment_io_type", "payment_io_type"),
            Map.entry("delivery_method", "delivery_method"),
            Map.entry("channel", "channel"),
            Map.entry("business_unit", "business_unit"),
            Map.entry("department", "department"),
            Map.entry("counter_party", "counter_party"),
            Map.entry("sender_name", "sender_name"),
            Map.entry("sender_amount", "sender_amount"),
            Map.entry("sender_currency", "sender_currency"),
            Map.entry("receiver_name", "receiver_name"),
            Map.entry("receiver_amount", "receiver_amount"),
            Map.entry("receiver_currency", "receiver_currency"),
            Map.entry("payment_date", "payment_date"),
            Map.entry("delivery_date", "delivery_date"),
            Map.entry("creation_date", "creation_date_time"),
            Map.entry("entry_type", "entry_type"),
            Map.entry("memo_post_status", "memo_post_status"),
            Map.entry("fee_amount", "fee_amount"),
            Map.entry("fee_currency", "fee_currency"),
            Map.entry("book", "book"),
            Map.entry("cost_center", "cost_center"),
            Map.entry("customer_type", "customer_type"),
            Map.entry("external_ref", "external_ref"),
            Map.entry("payment_note", "payment_note"),
            Map.entry("operation_notes", "operation_notes"),
            Map.entry("return_status", "return_status"),
            Map.entry("error_message", "error_message"),
            Map.entry("validation_message", "validation_message"),
            Map.entry("approved_by", "approved_by"),
            Map.entry("approved_time", "approved_time"),
            Map.entry("created_by", "created_by"),
            Map.entry("last_updated_by", "last_updated_by"),
            Map.entry("last_updated_time", "last_updated_time"),
            Map.entry("sender_account", "sender_account_number"),
            Map.entry("receiver_account", "receiver_account_number")
    );

    public BuiltQuery build(PaymentSearchFiltersDto filters, String search, List<String> columns,
                            String sortBy, String sortOrder, Integer limit, Integer offset) {
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        applyFilters(filters, where, params);
        applySearch(search, where, params);
        String orderColumn = resolveSortColumn(sortBy);
        String direction = "desc".equalsIgnoreCase(sortOrder) ? "DESC" : "ASC";
        String select = buildSelect(columns);
        String sql = "SELECT " + select + " FROM " + PAYMENT_TABLE + where + " ORDER BY " + orderColumn + " " + direction;
        if (limit != null) {
            sql += " LIMIT ?";
            params.add(limit);
        }
        if (offset != null) {
            sql += " OFFSET ?";
            params.add(offset);
        }
        String countSql = "SELECT COUNT(*) FROM " + PAYMENT_TABLE + where;
        return new BuiltQuery(sql, countSql, params, whereParamsOnly(filters, search));
    }

    private List<Object> whereParamsOnly(PaymentSearchFiltersDto filters, String search) {
        List<Object> params = new ArrayList<>();
        applyFilters(filters, new StringBuilder(), params);
        applySearch(search, new StringBuilder(), params);
        return params;
    }

    public BuiltQuery buildCount(PaymentSearchFiltersDto filters, String search) {
        WhereClause where = buildWhere(filters, search);
        return new BuiltQuery(null, "SELECT COUNT(*) FROM " + PAYMENT_TABLE + where.sql(), where.params(), where.params());
    }

    public WhereClause buildWhere(PaymentSearchFiltersDto filters, String search) {
        StringBuilder where = new StringBuilder(" WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        applyFilters(filters, where, params);
        applySearch(search, where, params);
        return new WhereClause(where.toString(), List.copyOf(params));
    }

    private String buildSelect(List<String> columns) {
        if (columns == null || columns.isEmpty()) {
            return "payment_id, payment_date, delivery_method, payment_type, payment_io_type, entry_type, "
                    + "sender_currency, sender_amount, payment_status, memo_post_status, sender_name, receiver_name, "
                    + "receiver_amount, business_unit, transaction_id, channel, department, counter_party, "
                    + "receiver_currency, delivery_date, creation_date_time";
        }
        Set<String> dbCols = new LinkedHashSet<>();
        dbCols.add("payment_id");
        for (String col : columns) {
            String mapped = COLUMN_MAP.get(col);
            if (mapped != null) {
                dbCols.add(mapped);
            }
        }
        return String.join(", ", dbCols);
    }

    private String resolveSortColumn(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "payment_id";
        }
        String mapped = COLUMN_MAP.getOrDefault(sortBy, sortBy);
        if (!SORTABLE.contains(mapped) && !SORTABLE.contains(sortBy)) {
            return "payment_id";
        }
        return SORTABLE.contains(mapped) ? mapped : sortBy;
    }

    private void applyFilters(PaymentSearchFiltersDto filters, StringBuilder where, List<Object> params) {
        if (filters == null) {
            return;
        }
        eq(where, params, "payment_status", filters.getPayment_status());
        eq(where, params, "delivery_method", filters.getDelivery_method());
        eq(where, params, "payment_type", filters.getPayment_type());
        eq(where, params, "payment_io_type", filters.getPayment_io_type());
        eq(where, params, "sender_currency", filters.getSender_currency());
        eq(where, params, "receiver_currency", filters.getReceiver_currency());
        ilike(where, params, "channel", filters.getChannel());
        ilike(where, params, "business_unit", filters.getBusiness_unit());
        ilike(where, params, "department", filters.getDepartment());
        ilike(where, params, "counter_party", filters.getCounter_party());
        ilike(where, params, "sender_name", filters.getSender_name());
        ilike(where, params, "receiver_name", filters.getReceiver_name());
        ilike(where, params, "transaction_id", filters.getTransaction_id());
        if (filters.getMin_amount() != null) {
            where.append(" AND sender_amount >= ?");
            params.add(filters.getMin_amount());
        }
        if (filters.getMax_amount() != null) {
            where.append(" AND sender_amount <= ?");
            params.add(filters.getMax_amount());
        }
        dateRange(where, params, "payment_date", filters.getPayment_date_from(), filters.getPayment_date_to());
        dateRange(where, params, "delivery_date", filters.getDelivery_date_from(), filters.getDelivery_date_to());
        dateRange(where, params, "creation_date_time", filters.getCreated_from(), filters.getCreated_to());
    }

    private void applySearch(String search, StringBuilder where, List<Object> params) {
        if (search == null || search.isBlank()) {
            return;
        }
        String trimmed = search.trim();
        String pattern = "%" + trimmed.toLowerCase(Locale.ROOT) + "%";
        String castPattern = "%" + trimmed + "%";
        where.append(" AND (");
        boolean first = true;
        for (String column : SEARCH_TEXT_COLUMNS) {
            if (!first) {
                where.append(" OR ");
            }
            where.append("LOWER(").append(column).append(") LIKE ?");
            params.add(pattern);
            first = false;
        }
        for (String column : SEARCH_CAST_TEXT_COLUMNS) {
            if (!first) {
                where.append(" OR ");
            }
            where.append("CAST(").append(column).append(" AS TEXT) ILIKE ?");
            params.add(castPattern);
            first = false;
        }
        where.append(")");
    }

    private void eq(StringBuilder where, List<Object> params, String column, String value) {
        if (value != null && !value.isBlank()) {
            where.append(" AND ").append(column).append(" = ?");
            params.add(value);
        }
    }

    private void ilike(StringBuilder where, List<Object> params, String column, String value) {
        if (value != null && !value.isBlank()) {
            where.append(" AND LOWER(").append(column).append(") LIKE ?");
            params.add("%" + value.toLowerCase(Locale.ROOT) + "%");
        }
    }

    private void dateRange(StringBuilder where, List<Object> params, String column, String from, String to) {
        if (from != null && !from.isBlank()) {
            where.append(" AND ").append(column).append(" >= ?::date");
            params.add(from);
        }
        if (to != null && !to.isBlank()) {
            where.append(" AND ").append(column).append(" <= ?::date");
            params.add(to);
        }
    }

    public record BuiltQuery(String dataSql, String countSql, List<Object> dataParams, List<Object> countParams) {
    }

    public record WhereClause(String sql, List<Object> params) {
    }
}
