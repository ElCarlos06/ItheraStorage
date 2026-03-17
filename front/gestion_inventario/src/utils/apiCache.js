const cache = new Map();
const TTL = 5 * 60 * 1000; // 5 minutos en milisegundos

export function getCached(key) {
  const cached = cache.get(key);
  if (!cached) return null;

  // Si ya pasó el tiempo de vida (TTL), borramos la entrada y retornamos null
  if (Date.now() - cached.ts > TTL) {
    cache.delete(key);
    return null;
  }

  return cached.data;
}

export function setCache(key, data) {
  cache.set(key, { data, ts: Date.now() });
}

export function clearCache(key) {
  if (key) cache.delete(key);
  else cache.clear();
}
