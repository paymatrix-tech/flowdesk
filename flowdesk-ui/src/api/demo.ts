import { apiV1 } from '../config';

export type PaymentBurstResult = {
  inserted: number;
  elapsedMs: number;
  writesPerSecond: number;
  maxThroughput: boolean;
};

export async function simulatePaymentBurst(options?: {
  durationSeconds?: number;
  intervalMs?: number;
  notifyLive?: boolean;
}): Promise<PaymentBurstResult> {
  const durationSeconds = options?.durationSeconds ?? 5;
  const intervalMs = options?.intervalMs ?? 100;
  const notifyLive = options?.notifyLive ?? true;
  const res = await fetch(`${apiV1}/demo/payments/burst`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ durationSeconds, intervalMs, notifyLive }),
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `Simulate failed: ${res.status}`);
  }
  return res.json();
}

/** No throttle — as many INSERTs as the DB/API can do in the window. SSE optional (off = faster). */
export function simulateMaxThroughput(
  durationSeconds = 5,
  notifyLive = false,
): Promise<PaymentBurstResult> {
  return simulatePaymentBurst({ durationSeconds, intervalMs: 0, notifyLive });
}

export type FlushResult = {
  paymentsRemovedFromDb: number;
  elasticsearchIndexCleared: boolean;
};

/** Truncate Postgres payments and delete the ES index. Optional seed inserts demo rows when you enable that later. */
export async function flushPayments(seed = 0): Promise<FlushResult> {
  const res = await fetch(`${apiV1}/demo/payments/flush?seed=${seed}`, { method: 'POST' });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `Flush failed: ${res.status}`);
  }
  return res.json();
}
