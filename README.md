# WebIA

Aplicación web de catálogo con Spring Boot, Thymeleaf, PostgreSQL/Supabase y asistencia opcional de OpenRouter.

## Requisitos

- Java 17 o superior
- Maven 3.9 o superior (o usar `mvnw` cuando se agregue el wrapper)

## Ejecutar localmente

Por defecto se usa una base H2 persistente en `./data/webia`; no necesitas una cuenta de Supabase para abrir la web y probar el catálogo.

```powershell
mvn spring-boot:run
```

Abre <http://localhost:8080>. La aplicación crea datos de demostración y la cuenta inicial configurada por `ADMIN_EMAIL` y `ADMIN_PASSWORD` en `.env` (si no se especifican, usa `admin@demo.com` y `Admin@1234`).

También puedes empaquetar y arrancar el JAR:

```powershell
mvn clean package
java -jar target/webia-supabase-1.0.0.jar
```

## Supabase y producción

Configura `.env` a partir de `.env.example` con credenciales válidas. Para usar PostgreSQL/Supabase, inicia con el perfil de producción:

```powershell
$env:APP_PROFILE = "prod"
mvn spring-boot:run
```

Antes del primer arranque, ejecuta `src/main/resources/db/schema.sql` en el editor SQL de Supabase. El perfil de producción valida el esquema existente y no aplica cambios automáticos a la base.

Configura `OPENROUTER_API_KEY` para habilitar las funciones de IA. La IA no es necesaria para el catálogo, el registro ni la gestión de productos.

## Funcionalidades

- Catálogo con búsqueda, filtros por categoría y precio, paginación y detalle de productos.
- Registro e inicio/cierre de sesión.
- Panel de administración para productos, categorías, usuarios, multimedia y actividad de IA.
- Modo claro/oscuro y asistente opcional de IA.

El catálogo no procesa pagos ni crea órdenes de compra; para habilitar ventas reales se necesita integrar un proveedor de pagos y definir el flujo de pedidos.
