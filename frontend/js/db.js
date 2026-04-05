// ===== CONFIGURACIÓN DE INDEXEDDB =====
const DB_NOMBRE = "EncuestasPUCMM";
const DB_VERSION = 1;
let db;

// Inicializar la base de datos local
function inicializarDB() {
    return new Promise((resolve, reject) => {

        // Si ya está abierta y no se está cerrando, reutilizarla
        if (db && db.objectStoreNames.length > 0) {
            resolve(db);
            return;
        }
        const request = indexedDB.open(DB_NOMBRE, DB_VERSION);

        request.onupgradeneeded = (event) => {
            db = event.target.result;

            // Almacén de encuestas pendientes
            if (!db.objectStoreNames.contains("encuestas")) {
                const store = db.createObjectStore("encuestas", {
                    keyPath: "id",
                    autoIncrement: true
                });
                store.createIndex("sincronizado", "sincronizado", { unique: false });
            }

            // Almacén de sesión de usuario
            if (!db.objectStoreNames.contains("sesion")) {
                db.createObjectStore("sesion", { keyPath: "clave" });
            }
        };

        request.onsuccess = (event) => {
            db = event.target.result;
            console.log("IndexedDB inicializada correctamente");
            resolve(db);
        };

        request.onerror = (event) => {
            console.error("Error al inicializar IndexedDB:", event.target.error);
            reject(event.target.error);
        };
    });
}

// ===== OPERACIONES DE ENCUESTAS =====

// Guardar encuesta localmente
function guardarEncuestaLocal(encuesta) {
    return new Promise((resolve, reject) => {
        const transaction = db.transaction(["encuestas"], "readwrite");
        const store = transaction.objectStore("encuestas");
        encuesta.sincronizado = false;
        encuesta.fechaLocal = new Date().toISOString();
        const request = store.add(encuesta);
        request.onsuccess = () => resolve(request.result);
        request.onerror = () => reject(request.error);
    });
}

// Obtener todas las encuestas locales
function obtenerEncuestasLocales() {
    return new Promise((resolve, reject) => {
        const transaction = db.transaction(["encuestas"], "readonly");
        const store = transaction.objectStore("encuestas");
        const request = store.getAll();
        request.onsuccess = () => resolve(request.result);
        request.onerror = () => reject(request.error);
    });
}

// Obtener encuestas pendientes de sincronizar
function obtenerEncuestasPendientes() {
    return new Promise((resolve, reject) => {
        const transaction = db.transaction(["encuestas"], "readonly");
        const store = transaction.objectStore("encuestas");
        const request = store.getAll();
        request.onsuccess = () => {
            const pendientes = request.result.filter(e => !e.sincronizado);
            resolve(pendientes);
        };
        request.onerror = () => reject(request.error);
    });
}

// Actualizar encuesta local
function actualizarEncuestaLocal(id, datos) {
    return new Promise((resolve, reject) => {
        const transaction = db.transaction(["encuestas"], "readwrite");
        const store = transaction.objectStore("encuestas");
        const request = store.get(id);
        request.onsuccess = () => {
            if (!request.result) { reject(new Error("Encuesta no encontrada")); return; }
            const encuestaActualizada = { ...request.result, ...datos };
            const put = store.put(encuestaActualizada);
            put.onsuccess = () => resolve();
            put.onerror  = () => reject(put.error);
        };
        request.onerror = () => reject(request.error);
    });
}

// Eliminar encuesta local
function eliminarEncuestaLocal(id) {
    return new Promise((resolve, reject) => {
        const transaction = db.transaction(["encuestas"], "readwrite");
        const store = transaction.objectStore("encuestas");
        const request = store.delete(id);
        request.onsuccess = () => resolve();
        request.onerror = () => reject(request.error);
    });
}

// Marcar encuesta como sincronizada
function marcarComoSincronizada(id) {
    return new Promise((resolve, reject) => {
        const transaction = db.transaction(["encuestas"], "readwrite");
        const store = transaction.objectStore("encuestas");
        const request = store.get(id);
        request.onsuccess = () => {
            const encuesta = request.result;
            encuesta.sincronizado = true;
            store.put(encuesta);
            resolve();
        };
        request.onerror = () => reject(request.error);
    });
}

// ===== OPERACIONES DE SESION =====

// Guardar sesión localmente (para modo offline)
function guardarSesionLocal(token, email, rol) {
    return new Promise((resolve, reject) => {
        const transaction = db.transaction(["sesion"], "readwrite");
        const store = transaction.objectStore("sesion");
        store.put({ clave: "token", valor: token });
        store.put({ clave: "email", valor: email });
        const request = store.put({ clave: "rol", valor: rol });
        request.onsuccess = () => resolve();
        request.onerror = () => reject(request.error);
    });
}

// Obtener sesión local
function obtenerSesionLocal(clave) {
    return new Promise((resolve, reject) => {
        const transaction = db.transaction(["sesion"], "readonly");
        const store = transaction.objectStore("sesion");
        const request = store.get(clave);
        request.onsuccess = () => resolve(request.result?.valor || null);
        request.onerror = () => reject(request.error);
    });
}

// ===== VERIFICAR CONEXION =====
async function estaOnline() {
    try {
        const res = await fetch('/ping', { method: 'HEAD', cache: 'no-store' });
        return res.ok;
    } catch {
        return false;
    }
}