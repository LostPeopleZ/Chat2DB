import assert from 'node:assert/strict';
import {
  DEFAULT_KIMI_BASE_URL,
  DEFAULT_MINIMAX_BASE_URL,
  resolveBaseUrlOnProviderChange,
  resolveProviderBaseUrl,
} from './modelConfigDefaults';

assert.equal(resolveProviderBaseUrl('MINIMAX', ''), DEFAULT_MINIMAX_BASE_URL);
assert.equal(resolveProviderBaseUrl('MINIMAX', '   '), DEFAULT_MINIMAX_BASE_URL);
assert.equal(resolveProviderBaseUrl('MINIMAX', 'https://api.minimax.chat/v1'), 'https://api.minimax.chat/v1');
assert.equal(resolveProviderBaseUrl('OPENAI', ''), '');
assert.equal(resolveProviderBaseUrl('KIMI', ''), DEFAULT_KIMI_BASE_URL);
assert.equal(resolveProviderBaseUrl('KIMI', '   '), DEFAULT_KIMI_BASE_URL);
assert.equal(resolveProviderBaseUrl('KIMI', 'https://moonshot-proxy.example.com/v1'), 'https://moonshot-proxy.example.com/v1');
assert.equal(resolveProviderBaseUrl('WORKBUDDY2', ''), '');

assert.equal(resolveBaseUrlOnProviderChange('MINIMAX', ''), DEFAULT_MINIMAX_BASE_URL);
assert.equal(resolveBaseUrlOnProviderChange('OPENAI', DEFAULT_MINIMAX_BASE_URL), '');
assert.equal(
  resolveBaseUrlOnProviderChange('OPENAI', 'https://proxy.example.com/v1'),
  'https://proxy.example.com/v1',
);
assert.equal(resolveBaseUrlOnProviderChange('KIMI', ''), DEFAULT_KIMI_BASE_URL);
assert.equal(resolveBaseUrlOnProviderChange('KIMI', DEFAULT_MINIMAX_BASE_URL), DEFAULT_KIMI_BASE_URL);
assert.equal(resolveBaseUrlOnProviderChange('MINIMAX', DEFAULT_KIMI_BASE_URL), DEFAULT_MINIMAX_BASE_URL);
assert.equal(resolveBaseUrlOnProviderChange('WORKBUDDY2', DEFAULT_KIMI_BASE_URL), '');
assert.equal(resolveBaseUrlOnProviderChange('WORKBUDDY2', ''), '');
assert.equal(resolveBaseUrlOnProviderChange('WORKBUDDY2', 'http://127.0.0.1:8000/v1'), 'http://127.0.0.1:8000/v1');
assert.equal(resolveBaseUrlOnProviderChange('KIMI', 'http://127.0.0.1:8000/v1'), 'http://127.0.0.1:8000/v1');

console.log('AI model config default tests passed.');
