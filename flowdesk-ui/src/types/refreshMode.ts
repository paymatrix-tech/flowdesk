export type RefreshMode = 'manual' | 'poll-30' | 'poll-60' | 'live';

export function pollIntervalMs(mode: RefreshMode): number | false {
  if (mode === 'poll-30') return 30_000;
  if (mode === 'poll-60') return 60_000;
  return false;
}

export function isLiveMode(mode: RefreshMode): boolean {
  return mode === 'live';
}
