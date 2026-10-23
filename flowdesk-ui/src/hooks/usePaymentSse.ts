import { useEffect, useRef, useState } from 'react';
import { apiV1 } from '../config';

export interface PaymentUpdateEvent {
  op: string;
  before?: Record<string, unknown>;
  after?: Record<string, unknown>;
  ts_ms?: number;
}

export function usePaymentSse(enabled: boolean, onEvent: (event: PaymentUpdateEvent) => void) {
  const [connected, setConnected] = useState(false);
  const sourceRef = useRef<EventSource | null>(null);
  const onEventRef = useRef(onEvent);
  onEventRef.current = onEvent;

  useEffect(() => {
    if (!enabled) {
      sourceRef.current?.close();
      sourceRef.current = null;
      setConnected(false);
      return;
    }
    const url = `${apiV1}/payment/updates`;
    const source = new EventSource(url);
    sourceRef.current = source;
    const markConnected = () => setConnected(true);
    source.onopen = markConnected;
    source.addEventListener('connected', markConnected);
    source.onerror = () => {
      if (source.readyState === EventSource.OPEN) {
        setConnected(true);
        return;
      }
      if (source.readyState === EventSource.CLOSED) {
        setConnected(false);
      }
    };
    source.addEventListener('payment', (msg) => {
      try {
        const parsed = JSON.parse(msg.data) as PaymentUpdateEvent;
        onEventRef.current(parsed);
      } catch {
        // ignore malformed events
      }
    });
    return () => {
      source.close();
      sourceRef.current = null;
      setConnected(false);
    };
  }, [enabled]);

  return connected;
}
