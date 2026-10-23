export interface PaymentSearchFilters {
  payment_status?: string;
  delivery_method?: string;
  payment_type?: string;
  payment_io_type?: string;
  channel?: string;
  business_unit?: string;
  department?: string;
  counter_party?: string;
  sender_name?: string;
  receiver_name?: string;
  transaction_id?: string;
  min_amount?: number;
  max_amount?: number;
  sender_currency?: string;
  receiver_currency?: string;
  payment_date_from?: string;
  payment_date_to?: string;
  delivery_date_from?: string;
  delivery_date_to?: string;
  created_from?: string;
  created_to?: string;
}

export interface PaymentSearchRequest {
  page?: number;
  limit?: number;
  columns?: string[];
  filters?: PaymentSearchFilters;
  sort_by?: string;
  sort_order?: 'asc' | 'desc';
  search?: string;
}

export interface PaginatedPaymentResponse {
  success: boolean;
  message: string;
  data: Record<string, unknown>[];
  pagination: {
    page: number;
    limit: number;
    total: number;
    totalPages: number;
  };
}

export interface EsListResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  aggregations?: Record<string, Record<string, unknown>>;
}

export interface PaymentDocument {
  paymentId?: number;
  paymentDate?: string;
  deliveryMethod?: string;
  paymentType?: string;
  paymentIoType?: string;
  senderAmount?: number;
  paymentStatus?: string;
  senderName?: string;
  receiverName?: string;
  businessUnit?: string;
  suggest?: string[];
}
