export function formatMoney(amount: unknown, currency?: unknown) {
  const n = typeof amount === 'number' ? amount : Number(amount);
  if (Number.isNaN(n)) return '—';
  const cur = typeof currency === 'string' && currency ? currency : 'USD';
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: cur, maximumFractionDigits: 2 }).format(n);
}

export function formatDate(value: unknown) {
  if (!value) return '—';
  const d = new Date(String(value));
  if (Number.isNaN(d.getTime())) return String(value);
  return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
}
