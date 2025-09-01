# Movie Theater Backend API

Sistema de gestión de cine con funcionalidades de catálogo de películas, registro de usuarios y compra de entradas.

## 📋 Características

- **Catálogo de Películas**: CRUD completo con búsqueda y filtros
- **Autenticación**: Registro y login con JWT
- **Compras**: Sistema de compra de entradas con simulación de pago
- **Administración**: Panel admin para gestión de películas, usuarios y compras
- **Almacenamiento**: Soporte para S3 y almacenamiento local
- **Email**: Envío de confirmaciones de compra
- **Documentación**: Swagger/OpenAPI integrado

## 🛠️ Stack Tecnológico

- **Backend**: Java 17 + Spring Boot 3.x
- **Base de Datos**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Seguridad**: Spring Security + JWT
- **Email**: Spring Mail
- **Almacenamiento**: AWS S3 + fallback local
- **Documentación**: SpringDoc OpenAPI
- **Build**: Maven

## 🚀 Instalación y Configuración

### Requisitos Previos

- Java 17 o superior
- Maven 3.8+
- PostgreSQL 13+
- Docker y Docker Compose (opcional)

### 1. Clonar el Repositorio

```bash
git clone <repository-url>
cd movie-theater-backend
```

### 2. Configuración de Base de Datos

Crear base de datos en PostgreSQL:

```sql
CREATE DATABASE movie_theater;
CREATE USER movie_user WITH PASSWORD 'movie_password';
GRANT ALL PRIVILEGES ON DATABASE movie_theater TO movie_user;
```

### 3. Variables de Entorno

Crear archivo `.env` o configurar variables del sistema:

```env
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=movie_theater
DB_USER=postgres
DB_PASSWORD=postgres

# JWT
JWT_SECRET=mySecretKey123456789012345678901234567890
JWT_EXPIRATION=86400

# Email (Mailtrap para desarrollo)
MAIL_HOST=sandbox.smtp.mailtrap.io
MAIL_PORT=2525
MAIL_USER=your-mailtrap-user
MAIL_PASS=your-mailtrap-pass
MAIL_FROM=noreply@movietheater.com

# Storage
STORAGE_TYPE=local
UPLOAD_DIR=uploads

# AWS S3 (opcional)
AWS_S3_BUCKET=movie-theater-images
AWS_S3_REGION=us-east-1
AWS_ACCESS_KEY=your-aws-access-key
AWS_SECRET_KEY=your-aws-secret-key

# CORS
CORS_ORIGINS=http://localhost:4200
```

### 4. Ejecutar la Aplicación

#### Opción A: Con Maven (Desarrollo)

```bash
# Instalar dependencias
mvn clean install

# Ejecutar aplicación
mvn spring-boot:run
```

#### Opción B: Con Docker Compose (Recomendado)

```bash
# Modo desarrollo (incluye pgAdmin y Mailhog)
docker-compose --profile dev up -d

# Modo producción (solo servicios esenciales)
docker-compose up -d

# Ver logs
docker-compose logs -f api
```

La aplicación estará disponible en:
- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **pgAdmin**: http://localhost:8081 (user: admin@movietheater.com, pass: admin123)
- **Mailhog**: http://localhost:8025 (para ver emails en desarrollo)

## 📚 API Endpoints

### Autenticación (Público)
- `POST /api/auth/register` - Registrar usuario
- `POST /api/auth/login` - Iniciar sesión

### Películas (Público)
- `GET /api/movies` - Listar películas activas (con filtros)
- `GET /api/movies/{id}` - Obtener película por ID
- `GET /api/movies/genres` - Obtener géneros disponibles

### Compras (Usuario Autenticado)
- `POST /api/purchases` - Crear compra
- `GET /api/purchases/my-purchases` - Obtener mis compras

### Administración (Solo Admin)
- `GET /api/admin/movies` - Listar todas las películas
- `POST /api/admin/movies` - Crear película
- `PUT /api/admin/movies/{id}` - Actualizar película
- `PATCH /api/admin/movies/{id}/disable` - Deshabilitar película
- `POST /api/admin/movies/{id}/poster` - Subir poster
- `GET /api/admin/customers` - Listar clientes
- `PATCH /api/admin/customers/{id}/disable` - Deshabilitar cliente
- `GET /api/admin/purchases` - Listar todas las compras
- `GET /api/admin/stats` - Obtener estadísticas

## 👥 Usuarios de Prueba

La aplicación incluye datos de seed con usuarios de prueba:

### Usuario Administrador
- **Email**: admin@movietheater.com
- **Password**: admin123
- **Rol**: ADMIN

### Usuario Regular
- **Email**: user@movietheater.com
- **Password**: user123
- **Rol**: USER

### test@test.com
   Password: 123456
## 📋 Ejemplos de Uso

### Registro de Usuario

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phone": "+1234567890",
    "password": "password123"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@movietheater.com",
    "password": "user123"
  }'
```

### Comprar Entrada

```bash
curl -X POST http://localhost:8080/api/purchases \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "movieId": "movie-uuid-here",
    "quantity": 2,
    "payment": {
      "method": "CARD",
      "name": "John Doe",
      "last4": "1234"
    }
  }'
```

## 🏗️ Estructura del Proyecto

```
src/main/java/com/movietheater/
├── entity/              # Entidades JPA
├── repository/          # Repositorios Spring Data
├── service/            # Lógica de negocio
├── controller/         # Controladores REST
├── dto/               # Data Transfer Objects
├── security/          # Configuración de seguridad
├── config/           # Configuraciones
└── exception/        # Manejo de excepciones

src/main/resources/
├── application.yml         # Configuración principal
├── db/migration/          # Migraciones Flyway
└── templates/            # Plantillas de email
```

## 🎯 Funcionalidades Implementadas

### ✅ Requerimientos Obligatorios
- [x] MER y entidades JPA
- [x] CRUD completo de películas (Admin)
- [x] Registro de clientes
- [x] Sistema de compras con email
- [x] Autenticación JWT
- [x] Almacenamiento de imágenes (S3 + fallback local)
- [x] API REST documentada

### ✅ Funcionalidades Extra
- [x] Manejo global de excepciones
- [x] Validaciones completas
- [x] Docker Compose para desarrollo
- [x] Swagger/OpenAPI
- [x] Filtros y búsqueda avanzada
- [x] Paginación en todos los endpoints
- [x] Estadísticas para administradores
- [x] Health checks
- [x] Logging estructurado

## 🧪 Testing

### Ejecutar Tests Unitarios

```bash
mvn test
```

### Testing Manual

1. Usar **Swagger UI** en http://localhost:8080/swagger-ui.html
2. Importar collection de Postman (disponible en `/docs/postman/`)
3. Usar usuarios de prueba predefinidos

## 🔧 Configuración Avanzada

### Configuración de Email

Para usar Mailtrap en desarrollo:

1. Crear cuenta en [Mailtrap](https://mailtrap.io/)
2. Obtener credenciales SMTP
3. Configurar variables de entorno:

```env
MAIL_HOST=sandbox.smtp.mailtrap.io
MAIL_PORT=2525
MAIL_USER=your-mailtrap-user
MAIL_PASS=your-mailtrap-pass
```

### Configuración de AWS S3

Para usar S3 para almacenamiento de imágenes:

```env
STORAGE_TYPE=s3
AWS_S3_BUCKET=your-bucket-name
AWS_S3_REGION=us-east-1
AWS_ACCESS_KEY=your-access-key
AWS_SECRET_KEY=your-secret-key
```

## 🐛 Troubleshooting

### Error de Conexión a Base de Datos

```bash
# Verificar que PostgreSQL esté corriendo
docker-compose logs postgres

# Verificar conexión
psql -h localhost -p 5432 -U postgres -d movie_theater
```

### Error de Permisos de Archivo

```bash
# Linux/Mac
chmod +x mvnw

# Crear directorio de uploads
mkdir -p uploads
```

### Error de JWT

Verificar que `JWT_SECRET` tenga al menos 256 bits (32 caracteres).

## 📦 Despliegue

### Construcción para Producción

```bash
mvn clean package -DskipTests
```

### Docker Build
Pendiente

## 🤝 Contribución

1. Fork el proyecto
2. Crear rama feature (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'Agregar nueva funcionalidad'`)
4. Push a la rama (`git push origin feature/nueva-funcionalidad`)
5. Crear Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 📞 Soporte



## 🗺️ Roadmap

### Próximas Funcionalidades
- [ ] Sistema de reservas con horarios
- [ ] Integración con pasarela de pago real
- [ ] Notificaciones push
- [ ] Sistema de puntos y descuentos
- [ ] API de recomendaciones
- [ ] Dashboard analytics avanzado

## 📊 Métricas y Monitoreo

La aplicación incluye endpoints de salud y métricas:

- **Health Check**: `GET /actuator/health`
- **Metrics**: `GET /actuator/metrics`
- **Info**: `GET /actuator/info`

## 🔐 Seguridad

- Passwords hasheados con BCrypt
- JWT con expiración configurable
- CORS configurado
- Validación de entrada en todos los endpoints
- Rate limiting (configurable)
- Headers de seguridad HTTP

## 🚀 Performance

- Conexión pool de base de datos optimizado
- Lazy loading en relaciones JPA
- Paginación en listados grandes
- Cache de archivos estáticos
- Compresión GZIP habilitada

---

**¡Gracias por usar Movie Theater API! 🎬🍿**
