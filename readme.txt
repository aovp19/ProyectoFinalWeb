# Proyecto Final Web – Servidor de Encuestas con REST, gRPC y MongoDB
**ICC-352 Programación Web – PUCMM**

Almy Ventura – 10153712
Maria Jose Cruz – 10164963
12 de Abril 2026

---

## 1. Descripción del Proyecto

Aplicación web fullstack para la gestión de encuestas, desplegada mediante Docker, utilizando Java como backend principal e implementando:

- API REST con Javalin para operaciones CRUD de encuestas y usuarios
- Servicio gRPC para comunicación alternativa con el formulario
- Base de datos MongoDB Atlas gestionada con Morphia
- Autenticación segura mediante tokens JWT
- Frontend estático servido por Nginx
- Soporte offline mediante Service Worker
- Infraestructura orquestada con Docker Compose

La arquitectura permite exponer múltiples servicios (REST y gRPC) desde un mismo contenedor, con el frontend desacoplado en su propio contenedor Nginx.

---

## 2. Tecnologías Utilizadas

| Tecnología | Uso |
|---|---|
| Java | Backend principal |
| Javalin 7.1.0 | Servidor HTTP/REST |
| gRPC 1.63.0 + Protobuf 3.25.3 | Comunicación remota |
| MongoDB Atlas + Morphia 2.5.1 | Base de datos |
| jjwt 0.12.3 | Autenticación JWT |
| Jackson | Serialización JSON |
| Gradle 9.2 + Shadow Plugin | Build y fat JAR |
| HTML, CSS, JavaScript | Frontend |
| Service Worker | Soporte offline |
| Nginx | Servidor de archivos estáticos |
| Docker + Docker Compose | Infraestructura |

---

## 3. Estructura del Proyecto

```
ProyectoFinalWeb/
├── src/main/java/com/pucmm/csti19105488/
│   ├── Main.java                       # Punto de entrada de la aplicación
│   ├── config/
│   │   └── MongoConfig.java            # Configuración de conexión a MongoDB
│   ├── controller/
│   │   ├── EncuestaController.java     # Endpoints REST de encuestas
│   │   └── UsuarioController.java      # Endpoints REST de usuarios
│   ├── dao/
│   │   ├── EncuestaDAO.java            # Acceso a datos de encuestas
│   │   ├── UsuarioDAO.java             # Acceso a datos de usuarios
│   │   └── RepositorioBase.java        # Clase base genérica para DAOs
│   ├── dto/
│   │   └── EncuestaDTO.java            # Objeto de transferencia de datos
│   ├── grpc/
│   │   └── FormularioGrpcService.java  # Implementación del servicio gRPC
│   ├── model/
│   │   ├── Encuesta.java
│   │   ├── Usuario.java
│   │   ├── Ubicacion.java
│   │   ├── NivelEducativo.java
│   │   └── Rol.java
│   ├── service/
│   │   ├── EncuestaService.java        # Lógica de negocio de encuestas
│   │   └── UsuarioService.java         # Lógica de negocio de usuarios
│   └── util/
│       └── JwtUtil.java                # Utilidades para manejo de tokens JWT
├── src/main/proto/
│   └── formulario.proto                # Definición del servicio gRPC
├── src/main/resources/
│   └── config.properties               # URI y nombre de la base de datos
├── frontend/
│   ├── index.html                      # Página principal / login
│   ├── dashboard.html                  # Panel principal
│   ├── encuestas.html                  # Gestión de encuestas
│   ├── formulario.html                 # Formulario de encuesta
│   ├── estadisticas.html               # Visualización de estadísticas
│   ├── usuarios.html                   # Gestión de usuarios
│   ├── perfil.html                     # Perfil de usuario
│   ├── mapa.html                       # Vista de ubicaciones en mapa
│   ├── cliente-rest.html               # Cliente de prueba REST
│   ├── cliente-grpc.html               # Cliente de prueba gRPC
│   ├── css/estilos.css
│   ├── js/
│   │   ├── base.js
│   │   ├── db.js
│   │   ├── paginacion.js
│   │   ├── sw.js
│   │   ├── syncmanager.js
│   │   └── syncworker.js
│   └── img/logo.png
├── docker-compose.yml                  # Orquestación de contenedores
├── dockerfile                          # Imagen del backend
├── nginx.conf                          # Configuración del servidor frontend
└── build.gradle                        # Dependencias y configuración del build
```

---

## 4. Puertos y Servicios

| Servicio | URL |
|---|---|
| API REST | http://localhost:7000 |
| gRPC | localhost:50051 |
| Frontend | http://localhost:8080 |

El frontend se comunica con el backend a través del proxy configurado en `nginx.conf`.

---

## 5. Credenciales

**Login de la aplicación:**
```
user:     admin@ce.pucmm.edu.do
password: admin123
```

**Base de datos MongoDB:**
```
user:     server_user
password: qbwLXhV3b4ocRGx8
```

---

## 6. Configuración de la Base de Datos

La conexión a MongoDB se establece mediante el archivo `src/main/resources/config.properties`.

> ⚠️ Este archivo **debe crearlo manualmente** ya que contiene información sensible y no está incluido en el repositorio.

Contenido del archivo:

```properties
mongo.uri=mongodb+srv://<db_username>:<db_password>@proyectofinalweb.v9hjgvv.mongodb.net/?appName=ProyectoFinalWeb
mongo.database=ProyectoFinalWeb
```

| Parámetro | Descripción |
|---|---|
| `mongo.uri` | URI de conexión a MongoDB Atlas |
| `mongo.database` | Nombre de la base de datos (`ProyectoFinalWeb`) |

---

## 7. Cómo Ejecutar el Proyecto

1. Descomprimir o clonar el proyecto.
2. Crear el archivo `src/main/resources/config.properties` con las credenciales indicadas en la sección anterior.
3. Desde la raíz del proyecto, ejecutar:

```bash
docker-compose up --build
```

4. Abrir el navegador en: http://localhost:8080

---

## 8. Conclusión

Se logró implementar exitosamente una aplicación web fullstack para gestión de encuestas, combinando una API REST y un servicio gRPC sobre el mismo backend Java. La integración con MongoDB Atlas, la autenticación mediante JWT y el soporte offline a través de Service Worker permiten una experiencia robusta y segura. La containerización con Docker garantiza portabilidad y facilidad de despliegue, cumpliendo con los requerimientos del proyecto final de la asignatura.

🔗 **Aplicación en producción:** https://gestion-encuestas-pucmm.aovp.dev/
