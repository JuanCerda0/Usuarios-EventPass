# Microservicio de usuarios de EventPass

Servicio responsable del registro y la autenticación de usuarios de EventPass. Está desarrollado con Java 21, Spring Boot, Maven y PostgreSQL.

## Hito actual

El primer hito implementa el registro de compradores:

- Valida nombre, correo y contraseña.
- Normaliza el correo a minúsculas y evita duplicados.
- Guarda la contraseña cifrada con BCrypt.
- Asigna el rol `COMPRADOR` en el servidor; el cliente no puede elegir un rol.
- Devuelve errores JSON para solicitudes inválidas y correos ya registrados.

Login con JWT, administración de usuarios y creación controlada de cuentas `STAFF` quedan para hitos posteriores.

## Requisitos

- Java 21.
- PostgreSQL disponible localmente.
- Maven Wrapper incluido en este repositorio.

## Preparar PostgreSQL

Crea una base de datos llamada `eventpass_users`. Durante el desarrollo, Hibernate crea o actualiza las tablas a partir de las entidades.

## Configurar el entorno local

Copia `.env.example` con el nombre `.env` en la raíz del proyecto y ajusta `DB_USERNAME` y `DB_PASSWORD` a tu instalación de PostgreSQL.

En PowerShell:

```powershell
Copy-Item .env.example .env
```

`.env` está excluido de Git. No subas contraseñas al repositorio. Spring Boot importa ese archivo al iniciar la aplicación.

## Ejecutar

Desde la raíz del proyecto:

```powershell
./mvnw.cmd spring-boot:run
```

El servicio escucha en `http://localhost:8080`.

## Registrar un comprador

Envía `POST http://localhost:8080/api/usuarios` con `Content-Type: application/json`:

```json
{
  "nombre": "Ana Pérez",
  "email": "ana@example.com",
  "contrasena": "una-clave-segura"
}
```

Una solicitud exitosa devuelve `201 Created` con los datos públicos del usuario. La respuesta no incluye la contraseña ni su hash. Un correo duplicado devuelve `409 Conflict`; los datos inválidos devuelven `400 Bad Request`.
