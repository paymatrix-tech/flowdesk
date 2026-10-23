import { useInfiniteQuery, useQueryClient } from '@tanstack/react-query';
import { flexRender } from '@tanstack/react-table';
import {
  getCoreRowModel,
  useLegacyTable,
  type LegacyColumnDef,
} from '@tanstack/react-table/legacy';
import { useVirtualizer } from '@tanstack/react-virtual';
import { useCallback, useEffect, useMemo, useRef } from 'react';
import { Loader2 } from 'lucide-react';
import { searchPayments } from '../api/payments';
import type { PaymentSearchFilters } from '../api/types';
import { queryKeys } from '../queryKeys';
import type { PaymentRow } from '../types/paymentRow';
import { mapDbRow } from '../utils/mapPaymentRow';
import { formatDate, formatMoney } from '../utils/format';
import { StatusBadge } from './StatusBadge';

const defaultColumns = [
  'payment_date', 'payment_id', 'delivery_method', 'payment_type', 'payment_io_type',
  'entry_type', 'sender_currency', 'sender_amount', 'payment_status', 'memo_post_status',
  'sender_name', 'receiver_name', 'receiver_amount', 'business_unit',
];

const PAGE_SIZE = 50;

type Props = {
  filters: PaymentSearchFilters;
  searchTerm: string;
  searchPending?: boolean;
  sortBy: string;
  sortOrder: 'asc' | 'desc';
  liveEnabled: boolean;
  refetchInterval: number | false;
  onLiveEvent: (handler: (payload: Record<string, unknown>) => void) => void;
};

export function PaymentBlotter({
  filters,
  searchTerm,
  searchPending = false,
  sortBy,
  sortOrder,
  liveEnabled,
  refetchInterval,
  onLiveEvent,
}: Props) {
  const queryClient = useQueryClient();
  const parentRef = useRef<HTMLDivElement>(null);

  const query = useInfiniteQuery({
    queryKey: queryKeys.payments(filters, sortBy, sortOrder, searchTerm),
    queryFn: async ({ pageParam }) => {
      const page = typeof pageParam === 'number' ? pageParam : 1;
      return searchPayments({
        page,
        limit: PAGE_SIZE,
        columns: defaultColumns,
        filters,
        sort_by: sortBy,
        sort_order: sortOrder,
        search: searchTerm || undefined,
      });
    },
    initialPageParam: 1,
    getNextPageParam: (last) => {
      const { page, totalPages } = last.pagination;
      return page < totalPages ? page + 1 : undefined;
    },
    refetchInterval,
  });

  const rows = useMemo(
    () => query.data?.pages.flatMap((p) => p.data.map(mapDbRow)) ?? [],
    [query.data],
  );

  const total = query.data?.pages[0]?.pagination.total ?? 0;

  useEffect(() => {
    onLiveEvent((payload) => {
      if (!liveEnabled) return;
      const op = String(payload.op ?? '');
      if (op !== 'c' && op !== 'u' && op !== 'r') return;
      queryClient.invalidateQueries({ queryKey: queryKeys.payments(filters, sortBy, sortOrder, searchTerm) });
    });
  }, [liveEnabled, onLiveEvent, queryClient, filters, sortBy, sortOrder, searchTerm]);

  const columns = useMemo<LegacyColumnDef<PaymentRow>[]>(
    () => [
      { accessorKey: 'paymentDate', header: 'Date', cell: (c) => formatDate(c.getValue()) },
      { accessorKey: 'paymentId', header: 'Payment ID', cell: (c) => <span className="font-mono text-xs">{String(c.getValue() ?? '')}</span> },
      {
        accessorKey: 'deliveryMethod',
        header: 'Rail',
        cell: (c) => <StatusBadge value={String(c.getValue() ?? '')} variant="rail" />,
      },
      { accessorKey: 'paymentType', header: 'Type' },
      { accessorKey: 'paymentIoType', header: 'Direction' },
      {
        accessorKey: 'senderAmount',
        header: 'Amount',
        cell: (c) => (
          <span className="font-medium tabular-nums">
            {formatMoney(c.getValue(), c.row.original.senderCurrency)}
          </span>
        ),
      },
      {
        accessorKey: 'paymentStatus',
        header: 'Status',
        cell: (c) => <StatusBadge value={String(c.getValue() ?? '')} />,
      },
      { accessorKey: 'senderName', header: 'Sender', cell: (c) => <span className="truncate max-w-[140px] block">{String(c.getValue() ?? '—')}</span> },
      { accessorKey: 'receiverName', header: 'Receiver', cell: (c) => <span className="truncate max-w-[140px] block">{String(c.getValue() ?? '—')}</span> },
      { accessorKey: 'businessUnit', header: 'Business unit' },
    ],
    [],
  );

  const table = useLegacyTable({
    data: rows,
    columns,
    getCoreRowModel: getCoreRowModel(),
  });

  const tableRows = table.getRowModel().rows;
  const virtualizer = useVirtualizer({
    count: tableRows.length,
    getScrollElement: () => parentRef.current,
    estimateSize: () => 48,
    overscan: 12,
  });

  const virtualItems = virtualizer.getVirtualItems();
  const paddingTop = virtualItems.length > 0 ? virtualItems[0].start : 0;
  const paddingBottom =
    virtualItems.length > 0
      ? virtualizer.getTotalSize() - virtualItems[virtualItems.length - 1].end
      : 0;

  const onScroll = useCallback(() => {
    const el = parentRef.current;
    if (!el || !query.hasNextPage || query.isFetchingNextPage) return;
    const nearBottom = el.scrollTop + el.clientHeight >= el.scrollHeight - 200;
    if (nearBottom) {
      query.fetchNextPage();
    }
  }, [query]);

  return (
    <div className="flex h-full min-h-0 flex-col gap-2 p-3 sm:p-4">
      <div className="flex shrink-0 items-center justify-between gap-2">
        <div className="min-w-0">
          <h2 className="text-sm font-semibold text-slate-900">Payment blotter</h2>
          <p className="truncate text-xs text-slate-500">
            {searchTerm
              ? `${total.toLocaleString()} matches for “${searchTerm}” in PostgreSQL`
              : `${total.toLocaleString()} rows from PostgreSQL`}
          </p>
        </div>
        {(searchPending || (query.isFetching && !query.isFetchingNextPage)) && (
          <span className="inline-flex items-center gap-1.5 text-xs text-slate-500">
            <Loader2 className="h-3.5 w-3.5 animate-spin" />
            {searchPending ? 'Searching…' : 'Updating…'}
          </span>
        )}
      </div>

      <div className="flex-1 min-h-0 rounded-xl border border-slate-200/80 bg-white shadow-sm overflow-hidden">
        {query.isError && (
          <div className="p-6 text-sm text-rose-600">Could not load payments. Check API and database connection.</div>
        )}
        {query.isLoading && (
          <div className="flex h-48 items-center justify-center text-slate-500 text-sm gap-2">
            <Loader2 className="h-5 w-5 animate-spin" /> Loading payments…
          </div>
        )}
        {!query.isLoading && !query.isError && (
          <div ref={parentRef} onScroll={onScroll} className="h-full overflow-auto">
            <table className="w-full min-w-[960px] border-collapse text-sm">
              <thead className="sticky top-0 z-10 bg-slate-50/95 backdrop-blur border-b border-slate-200">
                {table.getHeaderGroups().map((hg) => (
                  <tr key={hg.id}>
                    {hg.headers.map((header) => (
                      <th
                        key={header.id}
                        className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wide text-slate-500"
                      >
                        {flexRender(header.column.columnDef.header, header.getContext())}
                      </th>
                    ))}
                  </tr>
                ))}
              </thead>
              <tbody>
                {paddingTop > 0 && (
                  <tr>
                    <td colSpan={table.getAllColumns().length} style={{ height: paddingTop }} />
                  </tr>
                )}
                {virtualItems.map((vRow) => {
                  const row = tableRows[vRow.index];
                  if (!row) return null;
                  return (
                    <tr key={row.id} className="border-b border-slate-100 hover:bg-slate-50/80 transition-colors">
                      {row.getVisibleCells().map((cell) => (
                        <td key={cell.id} className="px-4 py-2.5 align-middle text-slate-700">
                          {flexRender(cell.column.columnDef.cell, cell.getContext())}
                        </td>
                      ))}
                    </tr>
                  );
                })}
                {paddingBottom > 0 && (
                  <tr>
                    <td colSpan={table.getAllColumns().length} style={{ height: paddingBottom }} />
                  </tr>
                )}
              </tbody>
            </table>
            {query.isFetchingNextPage && (
              <div className="flex justify-center py-3 text-xs text-slate-500 gap-2">
                <Loader2 className="h-4 w-4 animate-spin" /> Loading more…
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
