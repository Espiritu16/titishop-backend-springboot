# TitiShop Backend API

## 1. Descripcion del proyecto
Backend del sistema TitiShop para gestion operativa de inventario, productos, proveedores, usuarios, movimientos, reportes y panel administrativo.

Permite:
- autenticacion stateless con JWT Bearer,
- autorizacion por roles de usuario,
- gestion de productos, categorias y marcas,
- gestion de proveedores con consulta externa de RUC,
- carga y publicacion de imagenes de productos,
- control de stock por producto,
- registro y anulacion de movimientos de inventario,
- consultas de reportes operativos,
- resumen administrativo para dashboard,
- migraciones versionadas de base de datos con Flyway.

## 2. Objetivo del backend
Centralizar las operaciones del negocio con una arquitectura modular y reglas de negocio consistentes:

`Autenticacion -> Catalogos -> Proveedores -> Inventario -> Movimientos -> Reportes -> Panel`

La aplicacion expone una API REST bajo `/api`, mantiene la logica de negocio en servicios, valida las entradas con DTOs y Jakarta Bean Validation, y persiste los datos en MySQL mediante Spring Data JPA.

## 3. Arquitectura y stack
| Stack | Descripcion |
|---|---|
| Java 21 | Lenguaje base del backend. |
| Spring Boot 4.0.6 | Framework principal de la aplicacion. |
| Spring Web MVC | Exposicion de endpoints REST. |
| Spring Data JPA | Persistencia ORM y repositorios. |
| Spring Security | Autenticacion, autorizacion y proteccion de rutas. |
| OAuth2 Resource Server | Validacion de tokens JWT Bearer. |
| BCrypt | Hash de contrasenas de usuarios. |
| MySQL 8+ | Base de datos relacional objetivo. |
| Flyway | Migraciones versionadas de esquema y datos semilla. |
| Springdoc OpenAPI | Documentacion interactiva de API. |
| H2 | Base de datos en memoria para pruebas. |
| JUnit 5 | Pruebas automatizadas. |

## 4. Dependencias principales
| Dependencia | Uso |
|---|---|
| `spring-boot-starter-webmvc` | Controladores REST y ciclo HTTP. |
| `spring-boot-starter-data-jpa` | Entidades, repositorios y persistencia. |
| `spring-boot-starter-security` | Seguridad HTTP y reglas de acceso. |
| `spring-boot-starter-oauth2-resource-server` | Validacion de JWT en requests protegidos. |
| `spring-boot-starter-validation` | Validacion de DTOs con anotaciones Jakarta. |
| `springdoc-openapi-starter-webmvc-ui` | Swagger UI y OpenAPI JSON. |
| `mysql-connector-j` | Driver MySQL en tiempo de ejecucion. |
| `flyway-core` / `flyway-mysql` | Migraciones para MySQL. |
| `jackson-datatype-jsr310` | Serializacion de fechas Java Time. |
| `spring-boot-starter-test` | Base de pruebas unitarias e integracion. |
| `spring-boot-starter-security-test` | Utilidades de pruebas de seguridad. |
| `spring-boot-starter-webmvc-test` | Pruebas de controladores MVC. |
| `h2` | Base de datos para pruebas automatizadas. |

## 5. Estructura del proyecto
```text
titishop-backend-springboot/
├── src/
│   ├── main/
│   │   ├── java/com/titishop/
│   │   │   ├── archivos/           # Carga y publicacion de archivos de productos
│   │   │   ├── autenticacion/      # Login, JWT y configuracion de seguridad
│   │   │   ├── compartido/         # Configuracion, errores, auditoria y utilidades comunes
│   │   │   ├── inventario/         # Stock, stock minimo, ubicacion y estado
│   │   │   ├── movimientos/        # Entradas, salidas, ajustes y anulaciones
│   │   │   ├── panel/              # Resumen administrativo
│   │   │   ├── productos/          # Productos, categorias y marcas
│   │   │   ├── proveedores/        # Proveedores y consulta RUC externa
│   │   │   ├── reportes/           # Reportes de movimientos, stock y valorizacion
│   │   │   ├── usuarios/           # Usuarios, roles y estados
│   │   │   └── TitishopBackendApplication.java
│   │   └── resources/
│   │       ├── Base de datos/
│   │       │   └── creacion-base-datos.md
│   │       ├── db/migration/
│   │       │   ├── V1__crear_esquema_titishop.sql
│   │       │   ├── V2__sembrar_usuario_administrador.sql
│   │       │   ├── V3__actualizar_usuario_administrador_kevin.sql
│   │       │   ├── V4__permitir_contacto_opcional_proveedores.sql
│   │       │   └── V5__sembrar_catalogo_inventario_movimientos.sql
│   │       └── application.properties
│   └── test/
│       ├── java/com/titishop/       # Pruebas unitarias, seguridad e integracion
│       └── resources/
│           └── application.properties
├── Dockerfile
├── pom.xml
├── mvnw
└── README.md
```

## 6. Modulos funcionales
| Modulo | Descripcion |
|---|---|
| `autenticacion` | Login por email/password, emision de JWT y datos de sesion. |
| `usuarios` | CRUD de usuarios, roles `ADMINISTRADOR`, `ALMACENERO`, `SUPERVISOR` y estados. |
| `productos` | CRUD de productos con SKU, descripcion, imagen, categoria, marca y precios. |
| `productos/categorias` | Catalogo de categorias activas/inactivas. |
| `productos/marcas` | Catalogo de marcas activas/inactivas. |
| `proveedores` | CRUD de proveedores, validacion de RUC/email y consulta externa por RUC. |
| `inventario` | Registro unico de inventario por producto, stock actual, stock minimo y ubicacion. |
| `movimientos` | Registro historico de entradas, salidas, ajustes y anulacion de movimientos. |
| `reportes` | Consultas de movimientos, stock, stock critico y valorizacion. |
| `panel` | Indicadores administrativos para dashboard. |
| `archivos` | Carga de imagenes de productos en formato multipart. |
| `compartido` | Auditoria, CORS, OpenAPI, Flyway, errores globales y validaciones comunes. |

## 7. Reglas de negocio clave
- Roles permitidos: `ADMINISTRADOR`, `ALMACENERO`, `SUPERVISOR`.
- Estados de catalogo y usuarios: `ACTIVO` / `INACTIVO`.
- La autenticacion usa JWT Bearer y sesiones stateless.
- El secreto JWT debe tener al menos 32 bytes.
- Los productos tienen SKU unico y deben pertenecer a una categoria y una marca.
- El precio de venta no puede ser menor que el precio de compra.
- El inventario mantiene un registro unico por producto.
- El stock actual y el stock minimo no pueden ser negativos.
- Los movimientos soportan tipos `ENTRADA`, `SALIDA` y `AJUSTE`.
- Los movimientos guardan stock antes y stock despues para trazabilidad.
- Las salidas no pueden dejar stock negativo.
- Las entradas pueden asociarse a un proveedor.
- Los movimientos historicos no se eliminan; se anulan registrando usuario, fecha y motivo.
- Las entidades administrables heredan auditoria comun: creacion, actualizacion e inactivacion.
- La logica de negocio se mantiene en servicios siguiendo el flujo `controller -> service -> repository`.

## 8. API principal
Base URL local: `http://localhost:8080/api`

Base URL produccion: `https://api-titishop.proyectoutp.com/api`

| Modulo | Metodo | Endpoint | Acceso |
|---|---|---|---|
| Autenticacion | `POST` | `/api/autenticacion/login` | Publico |
| Usuarios | `GET` | `/api/usuarios` | ADMINISTRADOR |
| Usuarios | `GET` | `/api/usuarios/{id}` | ADMINISTRADOR |
| Usuarios | `POST` | `/api/usuarios` | ADMINISTRADOR |
| Usuarios | `PUT` | `/api/usuarios/{id}` | ADMINISTRADOR |
| Usuarios | `DELETE` | `/api/usuarios/{id}` | ADMINISTRADOR |
| Categorias | `GET` | `/api/categorias` | ADMINISTRADOR / ALMACENERO |
| Categorias | `GET` | `/api/categorias/{id}` | ADMINISTRADOR / ALMACENERO |
| Categorias | `POST` | `/api/categorias` | ADMINISTRADOR / ALMACENERO |
| Categorias | `PUT` | `/api/categorias/{id}` | ADMINISTRADOR / ALMACENERO |
| Categorias | `DELETE` | `/api/categorias/{id}` | ADMINISTRADOR / ALMACENERO |
| Marcas | `GET` | `/api/marcas` | ADMINISTRADOR / ALMACENERO |
| Marcas | `GET` | `/api/marcas/{id}` | ADMINISTRADOR / ALMACENERO |
| Marcas | `POST` | `/api/marcas` | ADMINISTRADOR / ALMACENERO |
| Marcas | `PUT` | `/api/marcas/{id}` | ADMINISTRADOR / ALMACENERO |
| Marcas | `DELETE` | `/api/marcas/{id}` | ADMINISTRADOR / ALMACENERO |
| Productos | `GET` | `/api/productos` | ADMINISTRADOR / ALMACENERO |
| Productos | `GET` | `/api/productos/{id}` | ADMINISTRADOR / ALMACENERO |
| Productos | `POST` | `/api/productos` | ADMINISTRADOR / ALMACENERO |
| Productos | `PUT` | `/api/productos/{id}` | ADMINISTRADOR / ALMACENERO |
| Productos | `DELETE` | `/api/productos/{id}` | ADMINISTRADOR / ALMACENERO |
| Proveedores | `GET` | `/api/proveedores` | ADMINISTRADOR / ALMACENERO |
| Proveedores | `GET` | `/api/proveedores/{id}` | ADMINISTRADOR / ALMACENERO |
| Proveedores | `GET` | `/api/proveedores/consulta-ruc/{ruc}` | Publico |
| Proveedores | `POST` | `/api/proveedores` | ADMINISTRADOR / ALMACENERO |
| Proveedores | `PUT` | `/api/proveedores/{id}` | ADMINISTRADOR / ALMACENERO |
| Proveedores | `DELETE` | `/api/proveedores/{id}` | ADMINISTRADOR / ALMACENERO |
| Inventario | `GET` | `/api/inventario` | ADMINISTRADOR / ALMACENERO |
| Inventario | `GET` | `/api/inventario/{id}` | ADMINISTRADOR / ALMACENERO |
| Inventario | `POST` | `/api/inventario` | ADMINISTRADOR / ALMACENERO |
| Inventario | `PUT` | `/api/inventario/{id}` | ADMINISTRADOR / ALMACENERO |
| Inventario | `DELETE` | `/api/inventario/{id}` | ADMINISTRADOR / ALMACENERO |
| Movimientos | `GET` | `/api/movimientos` | ADMINISTRADOR / ALMACENERO |
| Movimientos | `GET` | `/api/movimientos/{id}` | ADMINISTRADOR / ALMACENERO |
| Movimientos | `POST` | `/api/movimientos` | ADMINISTRADOR / ALMACENERO |
| Movimientos | `POST` | `/api/movimientos/{id}/anulacion` | ADMINISTRADOR / ALMACENERO |
| Reportes | `GET` | `/api/reportes/movimientos` | ADMINISTRADOR / SUPERVISOR |
| Reportes | `GET` | `/api/reportes/stock` | ADMINISTRADOR / SUPERVISOR |
| Reportes | `GET` | `/api/reportes/stock-critico` | ADMINISTRADOR / SUPERVISOR |
| Reportes | `GET` | `/api/reportes/valorizacion` | ADMINISTRADOR / SUPERVISOR |
| Panel | `GET` | `/api/panel/resumen` | ADMINISTRADOR / SUPERVISOR |
| Archivos | `POST` | `/api/archivos/productos` | ADMINISTRADOR / ALMACENERO |
| Swagger | `GET` | `/swagger` | Publico |
| OpenAPI | `GET` | `/v3/api-docs` | Publico cuando Springdoc esta habilitado |

Documentacion interactiva:
- Swagger UI local: `http://localhost:8080/swagger`
- Swagger UI produccion: `https://api-titishop.proyectoutp.com/swagger`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## 9. Seguridad
- Autenticacion con JWT Bearer.
- Politica de sesion `STATELESS`.
- CSRF deshabilitado para API REST.
- Password hashing con `BCryptPasswordEncoder`.
- CORS configurable por variable `CORS_ALLOWED_ORIGINS`.
- Validacion de autoridades desde el claim JWT `authorities`.
- Rutas publicas:
  - `/api/autenticacion/**`
  - `/api/proveedores/consulta-ruc/**`
  - `/uploads/**`
  - `/swagger`
  - `/swagger/**`
  - `/swagger-ui/**`
  - `/swagger-ui.html`
  - `/v3/api-docs/**`
- Rutas protegidas:
  - `/api/usuarios/**`: requiere `ADMINISTRADOR`.
  - `/api/categorias/**`, `/api/marcas/**`, `/api/productos/**`, `/api/proveedores/**`, `/api/inventario/**`, `/api/movimientos/**`, `/api/archivos/**`: requieren `ADMINISTRADOR` o `ALMACENERO`.
  - `/api/reportes/**`, `/api/panel/**`: requieren `ADMINISTRADOR` o `SUPERVISOR`.
  - cualquier otra ruta requiere autenticacion.

## 10. Roles y permisos
| Accion | ADMINISTRADOR | ALMACENERO | SUPERVISOR |
|---|---|---|---|
| Iniciar sesion | Si | Si | Si |
| Gestionar usuarios | Si | No | No |
| Gestionar categorias y marcas | Si | Si | No |
| Gestionar productos | Si | Si | No |
| Gestionar proveedores | Si | Si | No |
| Consultar RUC de proveedor | Si | Si | Si |
| Gestionar inventario | Si | Si | No |
| Registrar movimientos | Si | Si | No |
| Anular movimientos | Si | Si | No |
| Cargar imagenes de productos | Si | Si | No |
| Consultar reportes | Si | No | Si |
| Consultar panel administrativo | Si | No | Si |

## 11. Configuracion por entorno
Variables principales:

```env
SPRING_DATASOURCE_URL=jdbc:mysql://DB_HOST:3306/titishop?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Lima
SPRING_DATASOURCE_USERNAME=usuario
SPRING_DATASOURCE_PASSWORD=password
JWT_SECRET=clave-segura-de-al-menos-32-bytes
FRONTEND_PUBLIC_URL=https://titishop.proyectoutp.com
APP_PUBLIC_URL=https://api-titishop.proyectoutp.com
CORS_ALLOWED_ORIGINS=https://titishop.proyectoutp.com,https://www.titishop.proyectoutp.com
APP_UPLOAD_DIR=uploads
APP_UPLOAD_PUBLIC_URL=https://api-titishop.proyectoutp.com/uploads
APP_UPLOAD_MAX_FILE_SIZE=5MB
APP_UPLOAD_MAX_REQUEST_SIZE=6MB
FACTILIZA_API_TOKEN=token-factiliza
FACTILIZA_API_BASE_URL=https://api.factiliza.com/v1
FACTILIZA_CONNECT_TIMEOUT=3s
FACTILIZA_READ_TIMEOUT=6s
SPRINGDOC_API_DOCS_ENABLED=true
SPRINGDOC_SWAGGER_UI_ENABLED=true
```

Propiedades relevantes:

```properties
spring.application.name=titishop-backend
spring.jpa.open-in-view=false
spring.jpa.hibernate.ddl-auto=none
spring.flyway.enabled=true
app.jwt.issuer=titishop-backend
app.jwt.expiration-minutes=120
springdoc.swagger-ui.path=/swagger-ui.html
```

## 12. Ejecucion local
Requisitos:
- Java 21
- Maven Wrapper incluido en el repositorio
- MySQL 8+
- Variables de entorno o archivo `.env` compatible con `application.properties`

Comandos:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

Aplicacion local:
- API: `http://localhost:8080/api`
- Swagger: `http://localhost:8080/swagger`
- Archivos publicos: `http://localhost:8080/uploads`

## 13. Ejecucion con Docker
```bash
docker build -t titishop-backend .
docker run --rm -p 8080:8080 --env-file .env titishop-backend
```

En produccion la API debe publicarse detras de Nginx/Cloudflare en:
- Aplicacion: `https://api-titishop.proyectoutp.com`
- Swagger UI: `https://api-titishop.proyectoutp.com/swagger`
- Archivos publicos: `https://api-titishop.proyectoutp.com/uploads`

## 14. Migraciones y datos semilla
Flyway ejecuta las migraciones ubicadas en `src/main/resources/db/migration/`.

| Migracion | Proposito |
|---|---|
| `V1__crear_esquema_titishop.sql` | Crea tablas principales, restricciones, indices y relaciones. |
| `V2__sembrar_usuario_administrador.sql` | Inserta el usuario administrador inicial. |
| `V3__actualizar_usuario_administrador_kevin.sql` | Actualiza credenciales/datos del administrador semilla. |
| `V4__permitir_contacto_opcional_proveedores.sql` | Ajusta campos de contacto de proveedores. |
| `V5__sembrar_catalogo_inventario_movimientos.sql` | Inserta datos base para catalogos, inventario y movimientos. |

Usuario administrador semilla:

```text
email: kevin@gmail.com
password: kevin123
rol: ADMINISTRADOR
```

## 15. Modelo logico de base de datos
```mermaid
erDiagram
    USUARIOS {
        BINARY id PK
        VARCHAR nombre_completo
        VARCHAR email UK
        VARCHAR password_hash
        VARCHAR rol
        VARCHAR estado
        TIMESTAMP creado_en
        BINARY creado_por_id FK
        TIMESTAMP actualizado_en
        BINARY actualizado_por_id FK
        TIMESTAMP inactivado_en
        BINARY inactivado_por_id FK
    }

    CATEGORIAS {
        BINARY id PK
        VARCHAR nombre UK
        VARCHAR estado
        TIMESTAMP creado_en
        BINARY creado_por_id FK
        TIMESTAMP actualizado_en
        BINARY actualizado_por_id FK
        TIMESTAMP inactivado_en
        BINARY inactivado_por_id FK
    }

    MARCAS {
        BINARY id PK
        VARCHAR nombre UK
        VARCHAR estado
        TIMESTAMP creado_en
        BINARY creado_por_id FK
        TIMESTAMP actualizado_en
        BINARY actualizado_por_id FK
        TIMESTAMP inactivado_en
        BINARY inactivado_por_id FK
    }

    PRODUCTOS {
        BINARY id PK
        VARCHAR nombre
        VARCHAR sku UK
        TEXT descripcion
        VARCHAR imagen_url
        BINARY categoria_id FK
        BINARY marca_id FK
        DECIMAL precio_compra
        DECIMAL precio_venta
        VARCHAR estado
        TIMESTAMP creado_en
        BINARY creado_por_id FK
        TIMESTAMP actualizado_en
        BINARY actualizado_por_id FK
        TIMESTAMP inactivado_en
        BINARY inactivado_por_id FK
    }

    PROVEEDORES {
        BINARY id PK
        VARCHAR razon_social
        CHAR ruc UK
        CHAR celular
        VARCHAR telefono
        VARCHAR email UK
        VARCHAR direccion
        VARCHAR estado
        TIMESTAMP creado_en
        BINARY creado_por_id FK
        TIMESTAMP actualizado_en
        BINARY actualizado_por_id FK
        TIMESTAMP inactivado_en
        BINARY inactivado_por_id FK
    }

    INVENTARIO {
        BINARY id PK
        BINARY producto_id FK,UK
        INT stock_actual
        INT stock_minimo
        VARCHAR ubicacion
        VARCHAR estado
        TIMESTAMP creado_en
        BINARY creado_por_id FK
        TIMESTAMP actualizado_en
        BINARY actualizado_por_id FK
        TIMESTAMP inactivado_en
        BINARY inactivado_por_id FK
    }

    MOVIMIENTOS {
        BINARY id PK
        BINARY producto_id FK
        BINARY proveedor_id FK
        VARCHAR tipo
        INT cantidad
        VARCHAR motivo
        INT stock_antes
        INT stock_despues
        TIMESTAMP creado_en
        BINARY creado_por_id FK
        TIMESTAMP anulado_en
        BINARY anulado_por_id FK
        VARCHAR motivo_anulacion
    }

    CATEGORIAS ||--o{ PRODUCTOS : "clasifica"
    MARCAS ||--o{ PRODUCTOS : "identifica"
    PRODUCTOS ||--|| INVENTARIO : "controla"
    PRODUCTOS ||--o{ MOVIMIENTOS : "registra"
    PROVEEDORES ||--o{ MOVIMIENTOS : "abastece"
    USUARIOS ||--o{ MOVIMIENTOS : "crea"
```

## 16. Integraciones externas y archivos
### Factiliza
El modulo `proveedores` puede consultar datos de una empresa por RUC mediante Factiliza.

Configuracion:
- `FACTILIZA_API_TOKEN`
- `FACTILIZA_API_BASE_URL`
- `FACTILIZA_CONNECT_TIMEOUT`
- `FACTILIZA_READ_TIMEOUT`

Endpoint relacionado:
- `GET /api/proveedores/consulta-ruc/{ruc}`

### Carga de imagenes
El modulo `archivos` recibe imagenes de productos con `multipart/form-data`, las almacena en `APP_UPLOAD_DIR` y retorna una URL publica basada en `APP_UPLOAD_PUBLIC_URL`.

Endpoint relacionado:
- `POST /api/archivos/productos`

## 17. Pruebas automatizadas
Ejecutar todas las pruebas:

```bash
./mvnw test
```

Pruebas relevantes incluidas:
- seguridad de autenticacion y roles,
- controladores MVC,
- servicios de archivos,
- flujo de inventario,
- servicio de proveedores,
- Swagger controller.

## 18. Gestion del proyecto
El seguimiento de tareas, backlog y tablero del proyecto se realiza en Jira:

- [Tablero Jira TitiShop](https://utp-desarrollo.atlassian.net/jira/software/projects/DV/boards/1/backlog)

## 19. Alcance del README
Este README documenta el backend de TitiShop.

No incluye:
- documentacion visual del frontend Angular,
- manual de usuario final,
- credenciales reales de produccion,
- secretos de servicios externos.
