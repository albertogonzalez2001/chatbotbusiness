# 🤖 Business Chatbot — Asistente Virtual para Negocios

Chatbot conversacional para negocios desarrollado con **Java 21 + Spring Boot + Spring AI + Ollama**. 
El asistente responde preguntas sobre el negocio en lenguaje natural, mantiene contexto de conversación y persiste 
el historial en base de datos. Todo el sistema corre en local mediante **Docker Compose** sin depender de APIs 
externas de pago.

El proyecto está implementado como caso de uso de **Café González**, una cafetería ficticia, pero la arquitectura 
está diseñada para ser adaptable a cualquier negocio real modificando únicamente el system prompt y 
los datos de configuración.

Además el proyecto cuenta con una carpeta que anida las consultas necesarias sql, para acceder al mencionado historial
en la base de datos.

---

## Stack tecnológico

| Capa            | Tecnología                                            |
|-----------------|-------------------------------------------------------|
| Backend         | Java 21, Spring Boot 3.4, Spring Web, Spring Data JPA |
| IA              | Spring AI 1.0, Ollama (Mistral)                       |
| Base de datos   | PostgreSQL 16                                         |
| Frontend        | HTML5, CSS3, JavaScript vanilla                       |
| Infraestructura | Docker, Docker Compose                                |

---

## Arquitectura

```
[Frontend HTML/JS]
       │  fetch POST /api/chat
       ▼
[Spring Boot API REST :8081]
       │
       ├──▶ [Spring AI] ──▶ [Ollama :11434] (LLM local)
       │
       └──▶ [Spring Data JPA] ──▶ [PostgreSQL :5432]
```

**Flujo de una petición:**
1. El frontend obtiene un `sessionId` del servidor (`GET /api/chat/session`)
2. El usuario envía un mensaje (`POST /api/chat`)
3. Spring Boot recupera el historial de la sesión desde PostgreSQL
4. Construye un prompt con el system prompt del negocio + historial + mensaje actual
5. Spring AI envía el prompt a Ollama y recibe la respuesta del LLM
6. Se persisten tanto el mensaje del usuario como la respuesta del asistente
7. La respuesta se devuelve al frontend

---

## Funcionalidades

- Respuestas en lenguaje natural sobre el negocio (horario, servicios, contacto, ubicación)
- Memoria de conversación por sesión — el asistente recuerda el contexto
- Historial limitado a los últimos 10 mensajes para optimizar el uso del contexto del LLM
- `sessionId` generado en el servidor para evitar colisiones
- Manejo global de errores — nunca expone stack traces al cliente
- UI de chat responsiva con indicador de carga animado

---

## Requisitos previos

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado y corriendo
- Mínimo **6GB de RAM asignados a Docker** (necesario para cargar el modelo Mistral)
- Git

### Configurar memoria en Docker (Windows/WSL2)

Crea o edita el archivo `C:\Users\TuNombre\.wslconfig`:

```ini
[wsl2]
memory=8GB
processors=4
swap=2GB
```

Reinicia WSL2 con `wsl --shutdown` y vuelve a abrir Docker Desktop.

---

## Instalación y arranque

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/chatbotbusiness.git
cd chatbotbusiness

# 2. Levantar todos los servicios
docker compose up -d

# 3. Descargar el modelo de lenguaje (solo la primera vez, ~4GB)
docker exec chatbot-ollama ollama pull mistral

# 4. Acceder a la aplicación
http://localhost:8081
```

La primera respuesta del chatbot puede tardar 10-20 segundos mientras Ollama carga el modelo en memoria. Las siguientes son significativamente más rápidas.

---

## Endpoints de la API

| Método | Endpoint            | Descripción                                 |
|--------|---------------------|---------------------------------------------|
| `GET`  | `/api/chat/session` | Genera un nuevo `sessionId`                 |
| `POST` | `/api/chat`         | Envía un mensaje y recibe respuesta del LLM |

### Ejemplo de uso

```bash
# Obtener sesión
curl -X GET http://localhost:8081/api/chat/session

# Enviar mensaje
curl -X POST http://localhost:8081/api/chat \
  -H "Content-Type: application/json" \
  -d '{"sessionId": "uuid-aqui", "message": "¿Cuál es vuestro horario?"}'
```

---

## Estructura del proyecto

```
chatbotbusiness/
├── src/main/java/com/.../chatbotbusiness/
│   ├── config/
│   │   └── AiConfig.java            # Bean de ChatClient (Spring AI)
│   ├── controller/
│   │   └── ChatController.java      # Endpoints REST
│   ├── exception/
│   │   └── GlobalExceptionHandler.java
│   ├── model/
│   │   └── Message.java             # Entidad JPA
│   ├── repository/
│   │   └── MessageRepository.java   # Consultas a PostgreSQL
│   └── service/
│       └── ChatService.java         # Lógica: prompt + LLM + persistencia
├── src/main/resources/
│   ├── static/
│   │   ├── index.html
│   │   ├── css/chat.css
│   │   └── js/chat.js
│   └── application.yml
├── Dockerfile                        # Multi-stage build
├── docker-compose.yml               # PostgreSQL + Ollama + Spring Boot
└── .dockerignore
```

---

## Comandos útiles

```bash
# Ver logs de un servicio
docker compose logs app
docker compose logs ollama

# Parar todos los servicios
docker compose down

# Parar y resetear la base de datos
docker compose down -v

# Reconstruir tras cambios en el código
docker compose up --build app

# Acceder a la base de datos
docker exec -it chatbot-postgres psql -U chatbot_user -d chatbot_db

# Ver modelos cargados en Ollama
docker exec chatbot-ollama ollama list
```

---

## Posibles extensiones

- Cambiar Ollama por Groq API para despliegue en cloud sin coste de infraestructura
- Añadir Spring Security para proteger los endpoints
- Implementar panel de administración para editar el system prompt sin redespliegue
- Exportar historial de conversaciones a CSV
- Añadir soporte multiidioma automático

---

## Autor

**Alberto González**
- [GitHub](https://github.com/albertogonzalez2001) 
- [LinkedIn](https://www.linkedin.com/in/alberto-gonz%C3%A1lez-552784292/)
