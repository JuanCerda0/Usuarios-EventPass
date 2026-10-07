# Microservicio de usuarios de EventPass

Servicio responsable del registro y la autenticación de usuarios de EventPass. Está desarrollado con Java 21, Spring Boot, Maven y PostgreSQL.

## Funcionalidades actuales

El primer hito implementa el registro de compradores:

- Valida nombre, correo y contraseña.
- Normaliza el correo a minúsculas y evita duplicados.
- Guarda la contraseña cifrada con BCrypt.
- Asigna el rol `COMPRADOR` en el servidor; el cliente no puede elegir un rol.
- Devuelve errores JSON para solicitudes inválidas y correos ya registrados.
- Autentica compradores con correo y contraseña y entrega un JWT firmado.
- El JWT identifica al usuario en `sub` y expone `email` y `rol` como claims para validación en los otros microservicios.

La administración de usuarios y la creación controlada de cuentas `STAFF` quedan para hitos posteriores.

## Requisitos

- Java 21.
- PostgreSQL disponible localmente.
- Maven Wrapper incluido en este repositorio.

## Preparar PostgreSQL

Crea una base de datos llamada `eventpass_users`. Durante el desarrollo, Hibernate crea o actualiza las tablas a partir de las entidades.

## Configurar el entorno local

Copia `.env.example` con el nombre `.env` en la raíz del proyecto y ajusta `DB_USERNAME`, `DB_PASSWORD` y `JWT_SECRET_BASE64` a tu entorno. La clave del ejemplo es pública y solo sirve para desarrollo local; reemplázala por una clave aleatoria de al menos 32 bytes.

En PowerShell:

```powershell
Copy-Item .env.example .env
```

`.env` está excluido de Git. No subas contraseñas al repositorio. Spring Boot importa ese archivo al iniciar la aplicación.

Para generar una clave local aleatoria de 32 bytes en PowerShell:

```powershell
[Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

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

## Iniciar sesión

Envía `POST http://localhost:8080/api/auth/login` con `Content-Type: application/json`:

```json
{
  "email": "ana@example.com",
  "contrasena": "una-clave-segura"
}
```

Una solicitud exitosa devuelve `200 OK` con `token`, `tipo` (`Bearer`), `expiraEnSegundos` y los datos públicos del usuario:

```json
{
  "token": "<JWT>",
  "tipo": "Bearer",
  "expiraEnSegundos": 3600,
  "usuario": {
    "id": 1,
    "nombre": "Ana Pérez",
    "email": "ana@example.com",
    "rol": "COMPRADOR",
    "activo": true
  }
}
```

El JWT incluye `iss=eventpass-users`, `sub` con el id del usuario, `email`, `rol`, `iat` y `exp`. Para consumir rutas protegidas, el cliente enviará `Authorization: Bearer <JWT>`. Un correo/contraseña incorrectos o una cuenta inactiva devuelven el mismo `401 Unauthorized`.

## Consultar el perfil autenticado

Envía `GET http://localhost:8080/api/usuarios/me` con el encabezado `Authorization: Bearer <JWT>`. Devuelve el perfil actualizado desde la base de datos. Si no se envía un token válido, responde `401 Unauthorized`.
