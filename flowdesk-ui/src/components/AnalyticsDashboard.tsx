import { AlertTriangle, BadgeDollarSign, Flag, Loader2, ShieldAlert } from 'lucide-react';
import type { AnalyticsSummary } from '../api/analytics';

type CardConfig = {
  id: string;
  title: string;
  subtitle: string;
  icon: typeof AlertTriangle;
  accent: string;
  countKey: keyof AnalyticsSummary;
};

const cards: CardConfig[] = [
  {
    id: 'high-value',
    title: 'High value',
    subtitle: '≥ $1M',
    icon: BadgeDollarSign,
    accent: 'from-violet-500 to-indigo-600',
    countKey: 'highValue',
  },
  {
    id: 'failed',
    title: 'Failed',
    subtitle: 'Processing errors',
    icon: AlertTriangle,
    accent: 'from-rose-500 to-orange-500',
    countKey: 'failed',
  },
  {
    id: 'pending-review',
    title: 'Pending review',
    subtitle: 'Needs action',
    icon: ShieldAlert,
    accent: 'from-amber-500 to-yellow-500',
    countKey: 'pendingReview',
  },
  {
    id: 'flagged',
    title: 'Flagged',
    subtitle: 'Compliance / fraud',
    icon: Flag,
    accent: 'from-fuchsia-500 to-pink-500',
    countKey: 'flagged',
  },
];

type Props = {
  activeCard: string | null;
  summary: AnalyticsSummary | undefined;
  isLoading: boolean;
  isFetching: boolean;
  onCardSelect: (card: string) => void;
  onClearFilter: () => void;
};

export function AnalyticsDashboard(props: Props) {
  return (
    <section className="shrink-0">
      <div className="mb-2 flex flex-wrap items-center gap-x-3 gap-y-1">
        <div className="min-w-0 flex-1">
          <p className="text-xs font-semibold uppercase tracking-wide text-slate-400">Today&apos;s payment queue</p>
          <p className="text-sm text-slate-600">Tap a card to filter the blotter below</p>
        </div>
        <div className="flex items-center gap-2">
          {props.isFetching && !props.isLoading && (
            <Loader2 className="h-4 w-4 shrink-0 animate-spin text-slate-400" aria-label="Updating" />
          )}
          {props.activeCard && (
            <button
              type="button"
              onClick={props.onClearFilter}
              className="shrink-0 rounded-lg border border-indigo-200 bg-indigo-50 px-2.5 py-1 text-xs font-medium text-indigo-700 hover:bg-indigo-100"
            >
              Clear filter
            </button>
          )}
        </div>
      </div>
      <div className="grid grid-cols-2 gap-2 lg:grid-cols-4 lg:gap-3">
        {cards.map((card) => {
          const count = props.summary?.[card.countKey] ?? 0;
          const active = props.activeCard === card.id;
          const Icon = card.icon;
          return (
            <button
              key={card.id}
              type="button"
              onClick={() => props.onCardSelect(card.id)}
              className={`group relative overflow-hidden rounded-xl border text-left transition-all ${
                active
                  ? 'border-indigo-300 bg-white shadow-md ring-2 ring-indigo-500/15'
                  : 'border-slate-200 bg-white hover:border-slate-300 hover:shadow-sm'
              }`}
            >
              <div className={`absolute inset-x-0 top-0 h-0.5 bg-gradient-to-r ${card.accent}`} />
              <div className="p-3 pt-3.5">
                <div className="flex items-start justify-between gap-1">
                  <div className="min-w-0">
                    <p className="truncate text-xs font-medium text-slate-800 sm:text-sm">{card.title}</p>
                    <p className="hidden truncate text-xs text-slate-500 sm:block">{card.subtitle}</p>
                  </div>
                  <div className={`shrink-0 rounded-lg bg-gradient-to-br ${card.accent} p-1.5 text-white`}>
                    <Icon className="h-3.5 w-3.5 sm:h-4 sm:w-4" />
                  </div>
                </div>
                <p className="mt-2 text-2xl font-semibold tabular-nums text-slate-900 sm:mt-3 sm:text-3xl">
                  {props.isLoading ? '—' : count.toLocaleString()}
                </p>
              </div>
            </button>
          );
        })}
      </div>
    </section>
  );
}
