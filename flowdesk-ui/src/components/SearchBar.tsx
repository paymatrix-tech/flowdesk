import { useMutation } from '@tanstack/react-query';
import { ChevronDown, Download, FlaskConical, RefreshCw, Search } from 'lucide-react';
import { useEffect, useRef, useState } from 'react';
import { flushPayments, simulateMaxThroughput, simulatePaymentBurst } from '../api/demo';
import { exportPaymentsCsv } from '../api/payments';
import type { PaymentSearchFilters } from '../api/types';
import type { RefreshMode } from '../types/refreshMode';

type Props = {
  keyword: string;
  onKeywordChange: (v: string) => void;
  onSearch: () => void;
  refreshMode: RefreshMode;
  onRefreshModeChange: (mode: RefreshMode) => void;
  onManualRefresh: () => void;
  onAfterSimulate?: () => void;
  liveConnected: boolean;
  filters: PaymentSearchFilters;
  isRefreshing: boolean;
};

const REFRESH_LABELS: Record<RefreshMode, string> = {
  manual: 'Manual',
  'poll-30': 'Poll · 30s',
  'poll-60': 'Poll · 60s',
  live: 'Live stream',
};

function liveStatusText(mode: RefreshMode, connected: boolean): string | null {
  if (mode === 'live') {
    return connected ? 'Connected' : 'Connecting…';
  }
  if (mode === 'poll-30' || mode === 'poll-60') {
    return 'Polling';
  }
  return null;
}

export function SearchBar({
  keyword,
  onKeywordChange,
  onSearch,
  refreshMode,
  onRefreshModeChange,
  onManualRefresh,
  onAfterSimulate,
  liveConnected,
  filters,
  isRefreshing,
}: Props) {
  const [demoOpen, setDemoOpen] = useState(false);
  const demoRef = useRef<HTMLDivElement>(null);

  const simulateMutation = useMutation({
    mutationFn: () => simulatePaymentBurst({ durationSeconds: 5, intervalMs: 100, notifyLive: true }),
    onSuccess: () => {
      onAfterSimulate?.();
      setDemoOpen(false);
    },
  });

  const maxThroughputMutation = useMutation({
    mutationFn: () => simulateMaxThroughput(5, true),
    onSuccess: () => {
      onAfterSimulate?.();
      setDemoOpen(false);
    },
  });

  const flushMutation = useMutation({
    mutationFn: () => flushPayments(0),
    onSuccess: () => {
      onAfterSimulate?.();
      setDemoOpen(false);
    },
  });

  const exportMutation = useMutation({
    mutationFn: () =>
      exportPaymentsCsv({
        limit: 500,
        filters,
        sort_by: 'payment_date',
        sort_order: 'desc',
      }),
    onSuccess: (blob) => {
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `payments_export_${new Date().toISOString().slice(0, 10)}.csv`;
      a.click();
      URL.revokeObjectURL(url);
    },
  });

  const demoBusy =
    simulateMutation.isPending || maxThroughputMutation.isPending || flushMutation.isPending;
  const statusText = liveStatusText(refreshMode, liveConnected);

  useEffect(() => {
    if (!demoOpen) return;
    const onDoc = (e: MouseEvent) => {
      if (demoRef.current && !demoRef.current.contains(e.target as Node)) {
        setDemoOpen(false);
      }
    };
    document.addEventListener('mousedown', onDoc);
    return () => document.removeEventListener('mousedown', onDoc);
  }, [demoOpen]);

  return (
    <section className="shrink-0 rounded-2xl border border-slate-200/80 bg-white p-3 shadow-sm">
      <div className="flex flex-wrap items-center gap-2">
        <div className="relative min-w-[12rem] flex-1">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400" />
          <input
            data-search-input
            className="w-full rounded-lg border border-slate-200 bg-slate-50 py-2 pl-9 pr-3 text-sm focus:border-indigo-400 focus:bg-white focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
            placeholder="Search payments — updates as you type (/)"
            value={keyword}
            onChange={(e) => onKeywordChange(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && onSearch()}
          />
        </div>
        <button
          type="button"
          onClick={onSearch}
          className="shrink-0 rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-500"
        >
          Run search
        </button>

        <div className="hidden h-8 w-px bg-slate-200 sm:block" aria-hidden />

        <select
          className="min-w-0 max-w-full rounded-lg border border-slate-200 bg-white py-2 pl-2.5 pr-8 text-sm sm:max-w-[11rem]"
          value={refreshMode}
          onChange={(e) => onRefreshModeChange(e.target.value as RefreshMode)}
          aria-label="Blotter refresh mode"
        >
          {(Object.keys(REFRESH_LABELS) as RefreshMode[]).map((mode) => (
            <option key={mode} value={mode}>
              {REFRESH_LABELS[mode]}
            </option>
          ))}
        </select>
        {statusText && (
          <span
            className={`inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 text-xs font-medium ${
              refreshMode === 'live' && liveConnected
                ? 'bg-emerald-100 text-emerald-800'
                : refreshMode === 'live'
                  ? 'bg-amber-100 text-amber-900'
                  : 'bg-slate-100 text-slate-600'
            }`}
          >
            {refreshMode === 'live' && (
              <span
                className={`h-1.5 w-1.5 rounded-full ${
                  liveConnected ? 'bg-emerald-600' : 'bg-amber-500 animate-pulse'
                }`}
              />
            )}
            {statusText}
          </span>
        )}

        <button
          type="button"
          onClick={onManualRefresh}
          title="Refresh blotter and analytics (⌘R)"
          className="inline-flex items-center gap-1.5 rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 hover:bg-slate-50"
        >
          <RefreshCw className={`h-4 w-4 ${isRefreshing ? 'animate-spin' : ''}`} />
          Refresh
        </button>
        <button
          type="button"
          disabled={exportMutation.isPending}
          onClick={() => exportMutation.mutate()}
          className="inline-flex items-center gap-1.5 rounded-lg border border-slate-200 bg-white px-3 py-2 text-sm text-slate-700 hover:bg-slate-50 disabled:opacity-50"
        >
          <Download className="h-4 w-4" />
          CSV
        </button>
        {import.meta.env.DEV && (
          <div className="relative" ref={demoRef}>
            <button
              type="button"
              disabled={demoBusy}
              onClick={() => setDemoOpen((o) => !o)}
              className="inline-flex items-center gap-1 rounded-lg border border-dashed border-slate-300 bg-white px-3 py-2 text-sm text-slate-600 hover:bg-slate-50 disabled:opacity-50"
            >
              <FlaskConical className="h-4 w-4" />
              {demoBusy ? '…' : 'Demo'}
              <ChevronDown className="h-3.5 w-3.5 opacity-50" />
            </button>
            {demoOpen && (
              <div
                className="absolute right-0 top-full z-50 mt-1 w-44 rounded-lg border border-slate-200 bg-white py-1 shadow-lg"
                role="menu"
              >
                <button
                  type="button"
                  className="block w-full px-3 py-2 text-left text-sm hover:bg-slate-50"
                  onClick={() => simulateMutation.mutate()}
                >
                  Steady (5s)
                </button>
                  <button
                    type="button"
                    className="block w-full px-3 py-2 text-left text-sm hover:bg-slate-50"
                    onClick={() => maxThroughputMutation.mutate()}
                  >
                    Max speed (5s)
                  </button>
                  <button
                    type="button"
                    className="block w-full border-t border-slate-100 px-3 py-2 text-left text-sm text-rose-700 hover:bg-rose-50"
                    onClick={() => {
                      if (
                        window.confirm(
                          'Delete all payments in Postgres and the Elasticsearch search index?',
                        )
                      ) {
                        flushMutation.mutate();
                      }
                    }}
                  >
                    Flush DB + search index
                  </button>
                </div>
              )}
          </div>
        )}
      </div>
    </section>
  );
}
