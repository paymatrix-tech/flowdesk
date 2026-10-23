import type { PaymentSearchFilters } from './api/types';

export const queryKeys = {
  analytics: {
    all: ['analytics'] as const,
    summary: () => [...queryKeys.analytics.all, 'summary'] as const,
  },
  search: (keyword: string, page: number, size: number) => ['search', keyword, page, size] as const,
  payments: (
    filters: PaymentSearchFilters,
    sortBy: string,
    sortOrder: string,
    searchTerm: string,
  ) => ['payments', filters, sortBy, sortOrder, searchTerm] as const,
};
