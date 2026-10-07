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

La administración general de usuarios queda para hitos posteriores. Las cuentas `STAFF` se crean mediante un endpoint de provisión restringido por una clave local.

## Requisitos

- Java 21.
- PostgreSQL disponible localmente.
- Maven Wrapper incluido en este repositorio.

## Preparar PostgreSQL

Crea una base de datos llamada `eventpass_users`. Durante el desarrollo, Hibernate crea o actualiza las tablas a partir de las entidades.

## Configurar el entorno local

Copia `.env.example` con el nombre `.env` en la raíz del proyecto y ajusta `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET_BASE64` y `STAFF_PROVISION_KEY` a tu entorno. Las claves del ejemplo son públicas y solo sirven como referencia; reemplázalas por valores locales aleatorios.

En PowerShell:

```powershell
Copy-Item .env.example .env
```

`.env` está excluido de Git. No subas contraseñas al repositorio. Spring Boot importa ese archivo al iniciar la aplicación.

Para generar una clave local aleatoria de 32 bytes en PowerShell:

```powershell
[Convert]::ToBase64String([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

Para generar una clave de provisión STAFF local de 32 bytes en hexadecimal:

```powershell
[Convert]::ToHexString([Security.Cryptography.RandomNumberGenerator]::GetBytes(32))
```

## Ejecutar

Desde la raíz del proyecto:

```powershell
./mvnw.cmd spring-boot:run
```

El servicio escucha en `http://localhost:8080`.

## Registrar un comprador

Envía `POST http://localhost:8080/usuarios` con `Content-Type: application/json`:

```json
{
  "nombre": "Ana Pérez",
  "email": "ana@example.com",
  "contrasena": "una-clave-segura"
}
```

Una solicitud exitosa devuelve `201 Created` con los datos públicos del usuario. La respuesta no incluye la contraseña ni su hash. Un correo duplicado devuelve `409 Conflict`; los datos inválidos devuelven `400 Bad Request`.

## Crear una cuenta STAFF

Envía `POST http://localhost:8080/usuarios/staff` con `Content-Type: application/json` y el encabezado `X-Staff-Provision-Key` con el valor local de `STAFF_PROVISION_KEY`:

```json
{
  "nombre": "Personal EventPass",
  "email": "staff@example.com",
  "contrasena": "una-clave-segura"
}
```

El endpoint asigna el rol `STAFF` en el servidor; el cuerpo no acepta un rol elegido por el cliente. Devuelve `201 Created` al crear la cuenta, `401 Unauthorized` si falta o no coincide la clave, `409 Conflict` si el correo ya existe y `400 Bad Request` si los datos son inválidos. La clave solo debe existir en `.env` y en el entorno que use Postman; no debe enviarse como parte del JSON ni subirse al repositorio.

## Iniciar sesión

Envía `POST http://localhost:8080/auth/login` con `Content-Type: application/json`:

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

Envía `GET http://localhost:8080/usuarios/me` con el encabezado `Authorization: Bearer <JWT>`. Devuelve el perfil actualizado desde la base de datos. Si no se envía un token válido, responde `401 Unauthorized`.
