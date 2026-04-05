const CACHE = 'pucmm-v1';
const ARCHIVOS = [
    '/',
    '/index.html',
    '/encuestas.html',
    '/formulario.html',
    '/dashboard.html',
    '/css/estilos.css',
    '/js/db.js',
    '/js/base.js',
    '/js/paginacion.js',
    '/js/syncworker.js',
];

self.addEventListener('install', e => {
    e.waitUntil(
        caches.open(CACHE).then(c => c.addAll(ARCHIVOS))
    );
    self.skipWaiting();
});

self.addEventListener('activate', e => {
    self.clients.claim();
});

self.addEventListener('fetch', e => {
    if (e.request.method !== 'GET') return;
    e.respondWith(
        caches.match(e.request).then(cached => {
            return cached || fetch(e.request).catch(() => cached);
        })
    );
});