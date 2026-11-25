import type { AIProvider } from '@/service/aiModelConfig';

export const DEFAULT_MINIMAX_BASE_URL = 'https://api.minimax.io/v1';
export const DEFAULT_KIMI_BASE_URL = 'https://api.moonshot.cn/v1';

const PROVIDER_DEFAULT_BASE_URLS: Partial<Record<AIProvider, string>> = {
  MINIMAX: DEFAULT_MINIMAX_BASE_URL,
  KIMI: DEFAULT_KIMI_BASE_URL,
};

export const resolveProviderBaseUrl = (provider: AIProvider, baseUrl?: string): string => {
  const defaultBaseUrl = PROVIDER_DEFAULT_BASE_URLS[provider];
  if (defaultBaseUrl && !baseUrl?.trim()) {
    return defaultBaseUrl;
  }
  return baseUrl || '';
};

export const resolveBaseUrlOnProviderChange = (provider: AIProvider, baseUrl?: string): string => {
  const trimmed = baseUrl?.trim() || '';
  // Values auto-filled for another provider are replaced on provider switch;
  // user-entered custom URLs are kept.
  const isAutoFilledDefault = Object.values(PROVIDER_DEFAULT_BASE_URLS).includes(trimmed);
  return resolveProviderBaseUrl(provider, isAutoFilledDefault ? '' : trimmed);
};
