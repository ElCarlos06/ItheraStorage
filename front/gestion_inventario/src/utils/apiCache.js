const cache = new Map();

export function getCached(key) {
  return cache.get(key)?.data ?? null;
}

export function setCache(key, data) {
  cache.set(key, { data, ts: Date.now() });
}

export function clearCache(key) {
  if (key) cache.delete(key);
  else cache.clear();
}
