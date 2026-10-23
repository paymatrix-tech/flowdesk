import { useQuery, useQueryClient } from '@tanstack/react-query';
import { useCallback, useEffect, useRef, useState } from 'react';
import { Activity } from 'lucide-react';
import { getAnalyticsSummary } from './api/analytics';
import type { PaymentSearchFilters } from './api/types';
import { AnalyticsDashboard } from './components/AnalyticsDashboard';
import { PaymentBlotter } from './components/PaymentBlotter';
import { SearchBar } from './components/SearchBar';
import { useDebouncedValue } from './hooks/useDebouncedValue';
import { usePaymentSse } from './hooks/usePaymentSse';
import { queryKeys } from './queryKeys';
import { isLiveMode, pollIntervalMs, type RefreshMode } from './types/refreshMode';

const CARD_FILTERS: Record<string, PaymentSearchFilters> = {
  'high-value': { min_amount: 1_000_000 },
  failed: { payment_status: 'VALIDATION_FAILED' },
  'pending-review': { payment_status: 'PENDING_APPROVAL' },
  flagged: { payment_status: 'COMPLIANCE_REJECTED' },
};

const SEARCH_DEBOUNCE_MS = 350;

export default function App() {
  const queryClient = useQueryClient();
  const [keyword, setKeyword] = useState('');
  const debouncedKeyword = useDebouncedValue(keyword, SEARCH_DEBOUNCE_MS);
  const [searchTerm, setSearchTerm] = useState('');
  const [filters, setFilters] = useState<PaymentSearchFilters>({});
  const [activeCard, setActiveCard] = useState<string | null>(null);
  const [refreshMode, setRefreshMode] = useState<RefreshMode>('manual');
  const liveHandlerRef = useRef<(payload: Record<string, unknown>) => void>(() => {});

  const liveStreamEnabled = isLiveMode(refreshMode);
  const refetchInterval = pollIntervalMs(refreshMode);

  const analyticsQuery = useQuery({
    queryKey: queryKeys.analytics.summary(),
    queryFn: getAnalyticsSummary,
    refetchInterval,
  });

  const liveConnected = usePaymentSse(
    liveStreamEnabled,
    useCallback((event) => {
      liveHandlerRef.current(event as unknown as Record<string, unknown>);
    }, []),
  );

  useEffect(() => {
    const trimmed = debouncedKeyword.trim();
    setSearchTerm(trimmed);
    if (trimmed) {
      setActiveCard(null);
      setFilters({});
    }
  }, [debouncedKeyword]);

  const onSearch = () => {
    const trimmed = keyword.trim();
    setSearchTerm(trimmed);
    if (!trimmed) {
      return;
    }
    setActiveCard(null);
    setFilters({});
    queryClient.invalidateQueries({ queryKey: ['payments'] });
  };

  const onCardSelect = (card: string) => {
    setActiveCard(card);
    setFilters(CARD_FILTERS[card] ?? {});
    setKeyword('');
    setSearchTerm('');
  };

  const refreshAll = useCallback(() => {
    queryClient.invalidateQueries({ queryKey: queryKeys.analytics.all });
    queryClient.invalidateQueries({ queryKey: ['payments'] });
  }, [queryClient]);

  useEffect(() => {
    const onKey = (e: KeyboardEvent) => {
      if (e.key === '/' && !(e.target instanceof HTMLInputElement)) {
        e.preventDefault();
        document.querySelector<HTMLInputElement>('[data-search-input]')?.focus();
      }
      if (e.key === 'r' && (e.metaKey || e.ctrlKey)) {
        e.preventDefault();
        refreshAll();
      }
    };
    window.addEventListener('keydown', onKey);
    return () => window.removeEventListener('keydown', onKey);
  }, [refreshAll]);

  const searchPending = keyword.trim() !== searchTerm && keyword.trim().length > 0;

  return (
    <div className="flex h-screen flex-col overflow-hidden bg-gradient-to-b from-slate-50 via-white to-indigo-50/30">
      <header className="shrink-0 border-b border-slate-800/90 bg-slate-950 px-4 py-3 text-white shadow-sm">
        <div className="mx-auto flex max-w-[1600px] items-center gap-3">
          <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-gradient-to-br from-indigo-400 to-violet-600 shadow-lg shadow-indigo-500/25">
            <Activity className="h-4 w-4" />
          </div>
          <div>
            <p className="text-base font-semibold leading-tight tracking-tight">Flowdesk</p>
            <p className="text-xs text-slate-400">Payment operations</p>
          </div>
        </div>
      </header>

      <div className="mx-auto flex min-h-0 w-full max-w-[1600px] flex-1 flex-col gap-4 overflow-y-auto overflow-x-hidden p-4">
      <AnalyticsDashboard
        activeCard={activeCard}
        summary={analyticsQuery.data}
        isLoading={analyticsQuery.isLoading}
        isFetching={analyticsQuery.isFetching}
        onCardSelect={onCardSelect}
        onClearFilter={() => {
          setActiveCard(null);
          setFilters({});
        }}
      />

      <SearchBar
        keyword={keyword}
        onKeywordChange={setKeyword}
        onSearch={onSearch}
        refreshMode={refreshMode}
        onRefreshModeChange={setRefreshMode}
        onManualRefresh={refreshAll}
        onAfterSimulate={refreshAll}
        liveConnected={liveConnected}
        filters={filters}
        isRefreshing={analyticsQuery.isFetching}
      />

      <main className="flex min-h-[min(24rem,50vh)] flex-1 flex-col overflow-hidden rounded-2xl border border-slate-200/80 bg-white shadow-sm">
        <PaymentBlotter
          filters={filters}
          searchTerm={searchTerm}
          searchPending={searchPending}
          sortBy="payment_id"
          sortOrder="desc"
          liveEnabled={liveStreamEnabled}
          refetchInterval={refetchInterval}
          onLiveEvent={(handler) => {
            liveHandlerRef.current = handler;
          }}
        />
      </main>
      </div>
    </div>
  );
}
