import { searchApi } from '../config';
import type { EsListResponse, PaymentDocument } from './types';

export async function searchByKeyword(keyword: string, page = 0, size = 20): Promise<EsListResponse<PaymentDocument>> {
  const params = new URLSearchParams({
    keyword,
    page: String(page),
    size: String(size),
  });
  const res = await fetch(`${searchApi}?${params}`);
  if (!res.ok) {
    throw new Error(`Keyword search failed: ${res.status}`);
  }
  return res.json();
}
