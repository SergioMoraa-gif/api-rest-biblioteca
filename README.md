# API REST Biblioteca

API REST para administrar el catálogo de libros, los clientes (socios) y los préstamos de una biblioteca. Incluye una vista web sencilla hecha con Thymeleaf y Bootstrap que consume los mismos servicios.

Proyecto de la materia **Arquitectura Orientada a Servicios** — Ingeniería en Sistemas Computacionales.

## Descripción

| Servicio | Tipo (SOA) | Responsabilidad |
|---|---|---|
| `LibroService` | Entidad | Alta, consulta, búsqueda, actualización y baja de libros. Controla los ejemplares disponibles. |
| `ClienteService` | Entidad | Registro de socios con correo único y consulta por correo. |
| `PrestamoService` | Tarea | Orquesta a `ClienteService` y `LibroService` para **prestar** y **devolver** libros dentro de una transacción (`@Transactional`). |

### Reglas de negocio

- El ISBN de cada libro y el correo de cada cliente son únicos.
- Al **prestar** se verifica que el cliente y el libro existan y que haya ejemplares; se descuenta un ejemplar y se calcula la fecha límite (14 días por defecto, máximo 30).
- Un cliente no puede tener dos préstamos activos del mismo libro.
- Al **devolver** se registra la fecha real de devolución y el ejemplar regresa al catálogo.
- No se puede eliminar un libro o un cliente con préstamos registrados, ni un préstamo que siga activo.

## Arquitectura

```
Cliente (Postman / navegador)
        │
Controladores ── REST: LibroController, ClienteController, PrestamoController  (/api/...)
        │       Vista: LibroVistaController, ClienteVistaController, PrestamoVistaController (/vista/...)
        │
Servicios ────── LibroService, ClienteService, PrestamoService (reglas de negocio)
        │
Repositorios ─── ILibroRepository, IClienteRepository, IPrestamoRepository (Spring Data JPA)
        │
Modelos ──────── LibroModel, ClienteModel, PrestamoModel ──► MySQL (base: libreria)
```

Los errores se responden en un formato JSON uniforme mediante `GlobalExceptionHandler` (`@RestControllerAdvice`).

## Tecnologías

- Java 21
- Spring Boot 4.1 (Web MVC, Data JPA, Validation, Thymeleaf)
- Hibernate / JPA
- MySQL 8
- Bootstrap 5.3
- Maven
- H2 (solo para pruebas automáticas)

## Instalación y ejecución

### Requisitos

- JDK 21
- MySQL 8 en ejecución
- Maven (o el wrapper `mvnw` incluido)

### Inicio rápido

No hace falta crear la base ni las tablas: la URL de conexión lleva `createDatabaseIfNotExist=true` y Hibernate usa `ddl-auto=update`, así que la base `libreria` y sus tablas se crean al arrancar.

1. Verifica que MySQL esté en ejecución en el puerto 3306.
2. Abre una terminal en la carpeta del proyecto e indica tu contraseña de MySQL (el usuario por defecto es `root`).

   Windows PowerShell:
   ```powershell
   $env:DB_PASSWORD="tu_contraseña"
   ```
   Linux / macOS:
   ```bash
   export DB_PASSWORD=tu_contraseña
   ```
   Si tu usuario no es `root`, define también `DB_USERNAME`. Las variables solo duran mientras esa terminal esté abierta; Spring Boot **no** lee el archivo `.env` automáticamente, `.env.example` es solo una referencia.
3. En la **misma terminal**, ejecuta:
   ```bash
   .\mvnw.cmd spring-boot:run    # Windows
   ./mvnw spring-boot:run        # Linux / macOS
   ```
   Está listo cuando aparece `Started LibreriaApplication`.
4. Abre:
   - API: `http://localhost:1500/api/libros`
   - Vista web: `http://localhost:1500/vista/libros`
5. (Opcional) Importa `postman/Biblioteca.postman_collection.json` en Postman y ejecuta las peticiones en orden. La colección usa ids fijos (1, 2), así que está pensada para una base **vacía**.

Para correr las pruebas (usan H2 en memoria, no necesitan MySQL):

```bash
./mvnw test
```

## Variables de entorno

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_URL` | URL JDBC de MySQL | `jdbc:mysql://localhost:3306/libreria?createDatabaseIfNotExist=true&...` |
| `DB_USERNAME` | Usuario de MySQL | `root` |
| `DB_PASSWORD` | Contraseña de MySQL | *(vacía)* |
| `SERVER_PORT` | Puerto de la aplicación | `1500` |

El archivo `.env.example` muestra un ejemplo. No se suben credenciales reales al repositorio.

## Endpoints

Base: `http://localhost:1500`

### Libros

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/libros` | Lista todos los libros. Filtros opcionales `?titulo=` y `?autor=` | 200 |
| GET | `/api/libros/disponibles` | Libros con al menos un ejemplar | 200 |
| GET | `/api/libros/{id}` | Libro por id | 200 / 404 |
| GET | `/api/libros/isbn/{isbn}` | Libro por ISBN | 200 / 404 |
| POST | `/api/libros` | Crea un libro | 201 / 400 / 409 |
| PUT | `/api/libros/{id}` | Actualiza un libro | 200 / 400 / 404 / 409 |
| DELETE | `/api/libros/{id}` | Elimina un libro sin préstamos | 204 / 404 / 409 |

### Clientes

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/clientes` | Lista los clientes | 200 |
| GET | `/api/clientes/{id}` | Cliente por id | 200 / 404 |
| GET | `/api/clientes/buscar?correo=` | Cliente por correo | 200 / 404 |
| GET | `/api/clientes/{id}/prestamos` | Historial de préstamos del cliente | 200 / 404 |
| POST | `/api/clientes` | Registra un cliente | 201 / 400 / 409 |
| PUT | `/api/clientes/{id}` | Actualiza un cliente | 200 / 400 / 404 / 409 |
| DELETE | `/api/clientes/{id}` | Elimina un cliente sin préstamos | 204 / 404 / 409 |

### Préstamos

| Método | Ruta | Descripción | Respuesta |
|---|---|---|---|
| GET | `/api/prestamos` | Lista préstamos. Filtro opcional `?estado=ACTIVO` o `DEVUELTO` | 200 |
| GET | `/api/prestamos/{id}` | Préstamo por id | 200 / 404 |
| POST | `/api/prestamos` | Presta un libro | 201 / 400 / 404 / 409 |
| PUT | `/api/prestamos/{id}/devolucion` | Registra la devolución | 200 / 404 / 409 |
| DELETE | `/api/prestamos/{id}` | Elimina un préstamo ya devuelto | 204 / 404 / 409 |

### Vistas web

| Ruta | Pantalla |
|---|---|
| `/vista/libros` | Catálogo: registrar, editar, eliminar y buscar libros |
| `/vista/clientes` | Registro y edición de clientes |
| `/vista/prestamos` | Prestar, devolver y consultar el historial |

## Ejemplos de peticiones y respuestas

### Crear libro

`POST /api/libros`

```json
{
  "isbn": "978-607-07-3456-1",
  "titulo": "Cien años de soledad",
  "autor": "Gabriel García Márquez",
  "editorial": "Diana",
  "anioPublicacion": 1967,
  "numEjemplares": 3
}
```

Respuesta `201 Created`:

```json
{
  "id": 1,
  "isbn": "978-607-07-3456-1",
  "titulo": "Cien años de soledad",
  "autor": "Gabriel García Márquez",
  "editorial": "Diana",
  "anioPublicacion": 1967,
  "numEjemplares": 3
}
```

### Registrar cliente

`POST /api/clientes`

```json
{
  "nombre": "Ana",
  "apellidos": "López Ruiz",
  "correoElectronico": "ana.lopez@correo.com",
  "telefono": "2291234567",
  "direccion": "Av. Independencia 100, Veracruz"
}
```

Respuesta `201 Created`: el cliente con su `id` y `fechaRegistro`.

### Prestar un libro

`POST /api/prestamos`

```json
{
  "clienteId": 1,
  "libroId": 1,
  "dias": 7
}
```

Respuesta `201 Created`:

```json
{
  "id": 1,
  "cliente": { "id": 1, "nombre": "Ana", "apellidos": "López Ruiz", "...": "..." },
  "libro": { "id": 1, "titulo": "Cien años de soledad", "numEjemplares": 2, "...": "..." },
  "fechaPrestamo": "2026-10-09",
  "fechaLimite": "2026-10-16",
  "fechaDevolucion": null,
  "estado": "ACTIVO",
  "vencido": false
}
```

### Devolver un libro

`PUT /api/prestamos/1/devolucion` (sin cuerpo)

Respuesta `200 OK`: el préstamo con `"estado": "DEVUELTO"` y la `fechaDevolucion` de hoy; el libro vuelve a tener 3 ejemplares.

### Formato de errores

Validación (`400 Bad Request`):

```json
{
  "timestamp": "2026-10-09T18:30:00",
  "status": 400,
  "error": "Bad Request",
  "mensaje": "Datos inválidos",
  "ruta": "/api/libros",
  "detalles": {
    "isbn": "El ISBN solo admite dígitos y guiones (10 a 17 caracteres)",
    "titulo": "El título es obligatorio",
    "numEjemplares": "Los ejemplares no pueden ser negativos"
  }
}
```

Regla de negocio (`409 Conflict`):

```json
{
  "timestamp": "2026-10-09T18:31:00",
  "status": 409,
  "error": "Conflict",
  "mensaje": "No hay ejemplares disponibles de 'Pedro Páramo'",
  "ruta": "/api/prestamos",
  "detalles": null
}
```

## Pruebas con Postman

Importa `postman/Biblioteca.postman_collection.json`. La colección usa la variable `baseUrl` (`http://localhost:1500`) e incluye, en orden, los casos correctos y los casos de error (400, 404 y 409).

## Estructura del proyecto

```
src/main/java/com/api/libreria
├── controllers/   REST (/api) y vistas (/vista)
├── dto/           PrestamoRequest, ErrorResponse
├── exceptions/    Excepciones de negocio y GlobalExceptionHandler
├── models/        Entidades JPA
├── repositories/  Interfaces Spring Data JPA
└── services/      Interfaces e implementaciones con la lógica de negocio
src/main/resources
├── application.properties
└── templates/     Vistas Thymeleaf
```
