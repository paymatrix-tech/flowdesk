import type { PaymentRow } from '../types/paymentRow';

export function mapDbRow(row: Record<string, unknown>): PaymentRow {
  return {
    paymentId: row.payment_id as PaymentRow['paymentId'],
    paymentDate: row.payment_date as string | undefined,
    deliveryMethod: row.delivery_method as string | undefined,
    paymentType: row.payment_type as string | undefined,
    paymentIoType: row.payment_io_type as string | undefined,
    entryType: row.entry_type as string | undefined,
    senderCurrency: row.sender_currency as string | undefined,
    senderAmount: row.sender_amount as number | undefined,
    paymentStatus: row.payment_status as string | undefined,
    memoPostStatus: row.memo_post_status as string | undefined,
    senderName: row.sender_name as string | undefined,
    receiverName: row.receiver_name as string | undefined,
    receiverAmount: row.receiver_amount as number | undefined,
    businessUnit: row.business_unit as string | undefined,
  };
}
