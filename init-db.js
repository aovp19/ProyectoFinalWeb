// ============================================================
// Script de Inicialización - Base de Datos MongoDB
// Proyecto Final ICC-352 - Programación Web
// PUCMM - Encuestas Zona Norte
// ============================================================

db = db.getSiblingDB("encuestas_db");

// ============================================================
// 1. COLECCIÓN: usuarios
// ============================================================
db.createCollection("usuarios");

db.usuarios.createIndex({ email: 1 }, { unique: true });
db.usuarios.createIndex({ rol: 1 });

// Usuario administrador por defecto
// Contraseña: admin123 (BCrypt hash)
db.usuarios.insertOne({
    nombre:      "Administrador",
    apellido:    "Sistema",
    email:       "admin@ce.pucmm.edu.do",
    password:    "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LnwCSlUzPSm",
    rol:         "ADMIN",
    activo:      true,
    fotoBase64:  null
});

print("Colección 'usuarios' creada con usuario admin.");

// ============================================================
// 2. COLECCIÓN: encuestas
// ============================================================
db.createCollection("encuestas");

db.encuestas.createIndex({ "encuestador.$id": 1 });
db.encuestas.createIndex({ fechaRegistro: -1 });
db.encuestas.createIndex({ sincronizado: 1 });
db.encuestas.createIndex({ "ubicacion.latitud": 1, "ubicacion.longitud": 1 });

// Documento de ejemplo con la estructura completa
db.encuestas.insertOne({
    encuestador: {
        $ref: "usuarios",
        $id:  db.usuarios.findOne({ email: "admin@ce.pucmm.edu.do" })._id
    },
    nombreEncuestado:   "Juan",
    apellidoEncuestado: "Pérez",
    cedula:             "001-0000001-1",
    sector:             "Los Jardines",
    educacion:          "GRADO",
    ubicacion: {
        latitud:  19.4517,
        longitud: -70.6970
    },
    fechaRegistro: new Date(),
    fotoBase64:    null,
    sincronizado:  true
});

print("✅ Colección 'encuestas' creada con documento de ejemplo.");

// ============================================================
// Resumen
// ============================================================
print("\nBase de datos 'encuestas_db' inicializada correctamente.");
print("Colecciones: " + db.getCollectionNames().join(", "));
print("Admin:  admin@ce.pucmm.edu.do  /  admin123");
print("Cambia la contraseña del admin antes de publicar.\n");