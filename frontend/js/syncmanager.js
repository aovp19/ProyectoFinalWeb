import {
    getPendingFormularios,
    markAsSynced,
    countPending
} from './db.js';

let worker      = null;
let _onStatusChange = null;   // callback externo

/**
 * Inicializa el SyncManager.
 * @param {object} opts
 * @param {string}   opts.wsUrl       - URL del WebSocket (wss://...)
 * @param {string}   opts.restUrl     - URL REST de respaldo
 * @param {string}   opts.token       - JWT actual del usuario
 * @param {function} opts.onStatus    - cb(status) donde status = { online, pending, syncing }
 */
export function initSyncManager({ wsUrl, restUrl, token, onStatus } = {}) {
    if (worker) return;   // ya inicializado

    _onStatusChange = onStatus || (() => {});

    worker = new Worker('/js/syncworker.js');

    worker.onmessage = handleWorkerMessage;
    worker.onerror   = (e) => {
        console.error('[SyncManager] Worker error:', e.message);
    };

    // Enviar configuración inicial
    worker.postMessage({ type: 'INIT', payload: { wsUrl, restUrl } });
    if (token) worker.postMessage({ type: 'TOKEN_UPDATE', payload: { token } });
}

// Actualiza el JWT
export function updateToken(token) {
    if (worker) worker.postMessage({ type: 'TOKEN_UPDATE', payload: { token } });
}

// Fuerza una sincronización inmediata
export function forceSync() {
    if (worker) worker.postMessage({ type: 'FORCE_SYNC', payload: {} });
}

//  Manejador de mensajes del Worker
async function handleWorkerMessage({ data }) {
    const { type, payload } = data;

    switch (type) {
        // Worker pide los registros pendientes → leer IndexedDB y devolver
        case 'REQUEST_PENDING_RECORDS': {
            const records = await getPendingFormularios();
            worker.postMessage({ type: 'RECORDS_READY', payload: { records } });
            _onStatusChange({ online: navigator.onLine, pending: records.length, syncing: true });
            break;
        }

        // El servidor confirmó cuáles IDs fueron guardados
        case 'MARK_SYNCED': {
            await markAsSynced(payload.ids);
            const remaining = await countPending();
            _onStatusChange({ online: navigator.onLine, pending: remaining, syncing: false });
            dispatchAppEvent('sync:complete', { syncedIds: payload.ids, pending: remaining });
            break;
        }

        case 'SYNC_STARTED':
            _onStatusChange({ online: navigator.onLine, pending: payload.total, syncing: true });
            break;

        case 'SYNC_COMPLETE': {
            const pending = await countPending();
            _onStatusChange({ online: navigator.onLine, pending, syncing: false });
            break;
        }

        case 'SYNC_ERROR':
            console.warn('[SyncManager] Sync error:', payload.code, payload.detail);
            _onStatusChange({ online: navigator.onLine, pending: null, syncing: false, error: payload });
            break;

        case 'CONNECTIVITY_CHANGE':
            _onStatusChange({ online: payload.online, syncing: false });
            break;

        case 'WS_CONNECTED':
            dispatchAppEvent('sync:ws-connected', {});
            break;

        case 'WS_DISCONNECTED':
            dispatchAppEvent('sync:ws-disconnected', {});
            break;

        case 'SYNC_ACK':
            dispatchAppEvent('sync:ack', payload);
            break;
    }
}

function dispatchAppEvent(name, detail) {
    window.dispatchEvent(new CustomEvent(name, { detail }));
}