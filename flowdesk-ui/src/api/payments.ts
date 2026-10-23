import { apiV1 } from '../config';
import type { PaginatedPaymentResponse, PaymentSearchRequest } from './types';

export async function searchPayments(body: PaymentSearchRequest): Promise<PaginatedPaymentResponse> {
  const res = await fetch(`${apiV1}/payments/search`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    throw new Error(`Search failed: ${res.status}`);
  }
  return res.json();
}

export async function exportPaymentsCsv(body: PaymentSearchRequest & { limit?: number }): Promise<Blob> {
  const res = await fetch(`${apiV1}/payments/export/csv`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  });
  if (!res.ok) {
    throw new Error(`Export failed: ${res.status}`);
  }
  return res.blob();
}
