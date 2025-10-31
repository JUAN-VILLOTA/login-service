[![CI/CD Pipeline](https://github.com/JUAN-VILLOTA/login-service/actions/workflows/ci.yml/badge.svg)](https://github.com/JUAN-VILLOTA/login-service/actions/workflows/ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=JUAN-VILLOTA_login-service&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=JUAN-VILLOTA_login-service)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=JUAN-VILLOTA_login-service&metric=bugs)](https://sonarcloud.io/summary/new_code?id=JUAN-VILLOTA_login-service)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=JUAN-VILLOTA_login-service&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=JUAN-VILLOTA_login-service)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=JUAN-VILLOTA_login-service&metric=coverage)](https://sonarcloud.io/summary/new_code?id=JUAN-VILLOTA_login-service)

# Login-Service

**Login-Service** es un microservicio desarrollado en **Spring Boot** que gestiona la autenticación y autorización de usuarios dentro del proyecto **CourierSync**.  
Su propósito principal es administrar roles, permisos y credenciales de acceso de forma segura.  

Este laboratorio implementa un entorno de **Integración y Despliegue Continuos (CI/CD)** con **GitHub Actions**, **SonarCloud**, **JaCoCo** y **Docker**, automatizando pruebas, análisis de calidad y empaquetado del servicio.

---

## Folders Structure

- En la carpeta **`src`** se encuentra el código principal del microservicio.  
- En la carpeta **`test`** se ubican las pruebas unitarias desarrolladas para validar la funcionalidad.

---

## How to install it

Ejecuta el siguiente comando para compilar e iniciar la aplicación:

```bash
mvn spring-boot:run
