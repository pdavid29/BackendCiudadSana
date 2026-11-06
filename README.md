# Backend principal CiudadSana

API REST Kotlin, Spring Boot y PostgreSQL. Centraliza usuarios, autenticación JWT, reportes, estados, asignaciones, historial y fotografías. La API auxiliar FastAPI procesa imágenes, pero no accede a la base.

## Requisitos y ejecución

- JDK 21.
- PostgreSQL local o Supabase.
- Puerto local por defecto: `8081` (`SERVER_PORT` permite cambiarlo; frontend configurado en `8081`).
- Variables configuradas desde `.env.example` (copia como `.env` en esta carpeta si todavía no existe).

```powershell
if (!(Test-Path .env)) { Copy-Item .env.example .env }
.\gradlew.bat bootRun
```

No sobrescribas un `.env` existente: contiene la configuración privada de tu equipo. La configuración previa de esta copia fue recuperada en `backend/.env` y está excluida de Git.

Spring importa el archivo `.env` local y `application.yml` no contiene credenciales. Define una clave JWT aleatoria de al menos 32 bytes. En PostgreSQL, las fotos se registran en `public.evidencia` y su validación en `public.validacion_imagen`; los bytes quedan en `APP_UPLOAD_DIR`. En un esquema nuevo, crea primero la tabla con `../database/postgresql/20261104_create_validacion_imagen.sql`; las migraciones `../database/postgresql/003_modulos_y_accesos_frontend.sql` y `004_evidencia_y_validacion.sql` sincronizan permisos y FK de la evidencia. Fueron aplicadas a la base conectada; para otra base, respalda antes y ejecútalas en orden. Aunque Hibernate usa `ddl-auto=update`, evita actualizaciones automáticas sobre una base compartida sin revisar el diff.

Rutas y roles: consulta [`../docs/api-endpoints.md`](../docs/api-endpoints.md). Las rutas protegidas requieren `Authorization: Bearer <token>`. La documentación OpenAPI no está habilitada aún.
