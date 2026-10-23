const styles: Record<string, string> = {
  PROCESSED: 'bg-emerald-500/15 text-emerald-700 ring-emerald-500/30',
  PENDING_APPROVAL: 'bg-amber-500/15 text-amber-800 ring-amber-500/30',
  REJECTED: 'bg-rose-500/15 text-rose-700 ring-rose-500/30',
  VALIDATION_FAILED: 'bg-rose-500/15 text-rose-700 ring-rose-500/30',
};

const railStyles: Record<string, string> = {
  ACH: 'bg-sky-500/15 text-sky-800',
  FEDWIRE: 'bg-emerald-500/15 text-emerald-800',
  FEDNOW: 'bg-cyan-500/15 text-cyan-800',
  RTP: 'bg-violet-500/15 text-violet-800',
  BOOK_TRANSFER: 'bg-slate-500/15 text-slate-700',
  SWIFT: 'bg-indigo-500/15 text-indigo-800',
};

type Props = { value?: string | null; variant?: 'status' | 'rail' };

export function StatusBadge({ value, variant = 'status' }: Props) {
  if (!value) return <span className="text-slate-400">—</span>;
  const map = variant === 'rail' ? railStyles : styles;
  const cls = map[value] ?? 'bg-slate-100 text-slate-700 ring-slate-200';
  return (
    <span className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ring-1 ring-inset ${cls}`}>
      {value.replace(/_/g, ' ')}
    </span>
  );
}
