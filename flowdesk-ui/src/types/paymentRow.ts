export type PaymentRow = {
  paymentId?: number | string;
  paymentDate?: string;
  deliveryMethod?: string;
  paymentType?: string;
  paymentIoType?: string;
  entryType?: string;
  senderCurrency?: string;
  senderAmount?: number;
  paymentStatus?: string;
  memoPostStatus?: string;
  senderName?: string;
  receiverName?: string;
  receiverAmount?: number;
  businessUnit?: string;
};
