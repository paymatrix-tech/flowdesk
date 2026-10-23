import { analyticsApi } from '../config';

export type AnalyticsSummary = {
  highValue: number;
  failed: number;
  pendingReview: number;
  flagged: number;
};

export async function getAnalyticsSummary(): Promise<AnalyticsSummary> {
  const res = await fetch(`${analyticsApi}/payments/summary`);
  if (!res.ok) {
    throw new Error(`Analytics summary failed: ${res.status}`);
  }
  return res.json();
}
