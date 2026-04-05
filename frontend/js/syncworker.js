let ws = null;
let syncInProgress = false;
let _token = null;
let _wsUrl = 'ws://localhost/sync';

// Un solo onmessage
self.onmessage = (e) => {
    const { type, payload } = e.data;
    switch (type) {
        case 'INIT':
            if (payload.wsUrl)  _wsUrl  = payload.wsUrl;
            if (payload.token)  _token  = payload.token;
            startConnectivityMonitor();
            break;
        case 'TOKEN_UPDATE':
            _token = payload.token;
            break;
        case 'FORCE_SYNC':
            attemptSync();
            break;
        case 'RECORDS_READY':
            sendBatch(payload.records);
            break;
    }
};

function startConnectivityMonitor() {
    self.addEventListener('online',  () => {
        postToMain('CONNECTIVITY_CHANGE', { online: true });
        attemptSync();
    });
    self.addEventListener('offline', () => {
        closeWs();
        postToMain('CONNECTIVITY_CHANGE', { online: false });
    });
    setInterval(pollConnectivity, 10_000);
    pollConnectivity();
}

async function pollConnectivity() {
    try {
        await fetch('/ping', { method: 'HEAD', cache: 'no-store' });
        attemptSync();
    } catch { }
}

function attemptSync() {
    if (syncInProgress) return;
    postToMain('REQUEST_PENDING_RECORDS', {});
}

function sendBatch(records) {
    if (!records || records.length === 0) {
        postToMain('SYNC_COMPLETE', { synced: 0 });
        return;
    }
    syncInProgress = true;
    postToMain('SYNC_STARTED', { total: records.length });
    navigator.onLine ? syncViaWebSocket(records) : syncViaRest(records);
}

function syncViaWebSocket(records) {
    if (ws && ws.readyState === WebSocket.OPEN) {
        sendOverWs(records);
        return;
    }
    try {
        ws = new WebSocket(_wsUrl);
    } catch (err) {
        syncViaRest(records);
        return;
    }
    ws.onopen    = () => { postToMain('WS_CONNECTED', {}); sendOverWs(records); };
    ws.onmessage = (e) => { try { handleWsMessage(JSON.parse(e.data)); } catch {} };
    ws.onerror   = ()  => { syncViaRest(records); };
    ws.onclose   = ()  => { postToMain('WS_DISCONNECTED', {}); ws = null; };
}

function sendOverWs(records) {
    ws.send(JSON.stringify({ type: 'BATCH_SYNC', payload: { records } }));
}

function handleWsMessage(msg) {
    if (msg.type === 'ACK') {
        syncInProgress = false;
        postToMain('MARK_SYNCED', { ids: msg.payload.syncedIds });
        postToMain('SYNC_ACK', msg.payload);
    } else if (msg.type === 'ERROR') {
        syncInProgress = false;
        postToMain('SYNC_ERROR', { code: 'SERVER_ERROR', detail: msg.payload.message });
    }
}

function closeWs() {
    if (ws) { try { ws.close(); } catch {} ws = null; }
}

async function syncViaRest(records) {
    try {
        const res = await fetch('/encuestas/sync', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${_token}`
            },
            body: JSON.stringify({ records })
        });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const { syncedIds = [], errors = [] } = await res.json();
        syncInProgress = false;
        postToMain('MARK_SYNCED', { ids: syncedIds });
        postToMain('SYNC_ACK', { syncedIds, errors });
    } catch (err) {
        syncInProgress = false;
        postToMain('SYNC_ERROR', { code: 'REST_FAILED', detail: err.message });
        if (navigator.onLine) setTimeout(() => sendBatch(records), 30_000);
    }
}

function postToMain(type, payload) {
    self.postMessage({ type, payload });
}