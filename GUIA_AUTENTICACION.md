## 🧪 Probar en Swagger - PASO A PASO

### 1. Abre Swagger
http://localhost:8080/swagger-ui.html

### 2. Autentica para obtener el token
1. Busca el endpoint **`POST /api/login`**
2. Haz clic en **"Try it out"**
3. Ingresa en el body:
   ```json
   {
     "username": "admin",
     "password": "admin123"
   }
   ```
4. Haz clic en **"Execute"**
5. En la respuesta, **copia el token JWT completo** (sin las comillas)

### 3. Autorizar en Swagger 🔓
1. En la parte superior derecha de Swagger, verás un botón **"Authorize"** 🔓
2. Haz clic en ese botón
3. Aparecerá un modal con el campo **"Value"**
4. Pega el token JWT que copiaste (solo el token, sin "Bearer")
5. Haz clic en **"Authorize"**
6. Haz clic en **"Close"**
7. Verás que el candado 🔓 cambia a 🔒

### 4. Crear un usuario nuevo
1. Busca el endpoint **`POST /api/usuarios`**
2. Haz clic en **"Try it out"**
3. Ingresa en el body:
   ```json
   {
     "username": "operador01",
     "password": "password123",
     "idRol": 2
   }
   ```
4. Haz clic en **"Execute"**
5. ✅ Deberías ver un **201 Created** con el usuario creado

---

## 📸 Visual en Swagger

Después de configurar, verás:

**Antes de autorizar:**
- 🔓 Candado abierto en la esquina superior derecha
- ❌ Los endpoints protegidos te darán 403 Forbidden

**Después de autorizar:**
- 🔒 Candado cerrado
- ✅ Los endpoints protegidos funcionan correctamente
- El token se envía automáticamente en todas las peticiones