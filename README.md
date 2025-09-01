Movie Theater Backend API

Sistema de gestión de cine que permite manejar el catálogo de películas, registro de usuarios y compra de entradas.

Características

Catálogo de películas con CRUD completo, filtros y búsqueda.

Autenticación con registro y login usando JWT.

Sistema de compra de entradas con simulación de pago.

Panel de administración para gestionar películas, usuarios y compras.

Almacenamiento de imágenes en S3 o local.

Envío de correos de confirmación de compra.

Documentación de API con Swagger/OpenAPI.

Tecnologías

Backend: Java 17 + Spring Boot 3.x

Base de datos: PostgreSQL

ORM: Spring Data JPA / Hibernate

Seguridad: Spring Security + JWT

Email: Spring Mail

Almacenamiento: AWS S3 + fallback local

Documentación: SpringDoc OpenAPI

Build: Maven

Instalación y Configuración
Requisitos

Java 17 o superior

Maven 3.8+

PostgreSQL 13+

Docker y Docker Compose (opcional)

Clonar el repositorio
git clone <repository-url>
cd movie-theater-backend

Configurar base de datos
CREATE DATABASE movie_theater;
CREATE USER movie_user WITH PASSWORD 'movie_password';
GRANT ALL PRIVILEGES ON DATABASE movie_theater TO movie_user;

Variables de entorno

Crear un archivo .env con la siguiente configuración:

# Base de datos
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

# Almacenamiento
STORAGE_TYPE=local
UPLOAD_DIR=uploads

# AWS S3 (opcional)
AWS_S3_BUCKET=movie-theater-images
AWS_S3_REGION=us-east-1
AWS_ACCESS_KEY=your-aws-access-key
AWS_SECRET_KEY=your-aws-secret-key

# CORS
CORS_ORIGINS=http://localhost:4200

Ejecutar la aplicación

Con Maven:

mvn clean install
mvn spring-boot:run
La aplicación estará disponible en:

API: http://localhost:8080

Swagger UI: http://localhost:8080/swagger-ui.html

pgAdmin: http://localhost:8081
 (usuario: admin@movietheater.com
, pass: admin123)

Mailhog: http://localhost:8025

Endpoints principales
Autenticación

POST /api/auth/register - Registrar usuario

POST /api/auth/login - Iniciar sesión

Películas

GET /api/movies - Listar películas activas

GET /api/movies/{id} - Obtener película por ID

GET /api/movies/genres - Obtener géneros

Compras (usuarios autenticados)

POST /api/purchases - Crear compra

GET /api/purchases/my-purchases - Obtener mis compras

Administración (solo admin)

Gestión de películas, clientes y compras

Subida de posters y deshabilitación de películas y clientes

Estadísticas de la plataforma

Usuarios de prueba

Administrador

Email: admin@movietheater.com

Password: admin123

Usuario regular

Email: user@movietheater.com

Password: user123

Estructura del proyecto
src/main/java/com/movietheater/
├── entity/      # Entidades JPA
├── repository/  # Repositorios
├── service/     # Lógica de negocio
├── controller/  # Controladores REST
├── dto/         # DTOs
├── security/    # Configuración de seguridad
├── config/      # Configuraciones
└── exception/   # Manejo de excepciones

src/main/resources/
├── application.yml
├── db/migration/
└── templates/   # Plantillas de email

Funcionalidades implementadas

CRUD completo de películas (admin)

Registro de clientes

Sistema de compras con email

Autenticación JWT

Almacenamiento de imágenes (S3 o local)

API REST documentada

Validaciones y manejo global de errores

Paginación, filtros y búsqueda avanzada

Pruebas

Unitarias:

mvn test


Manual:

Usar Swagger UI

Importar colección de Postman disponible en /docs/postman/

Probar con usuarios de prueba

Despliegue
mvn clean package -DskipTests
docker build -t movie-theater-api .
docker run -p 8080:8080 movie-theater-api

Contribución

Hacer fork del proyecto

Crear una rama feature

Hacer commit de cambios

Hacer push a la rama

Crear Pull Request

Licencia

Licencia MIT. Ver archivo LICENSE para más detalles.

Soporte

Email: support@movietheater.com

Issues: GitHub Issues
