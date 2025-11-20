# DrawSync - Backend

## Autores

- **David Espinosa** (daviespr1406)
- **Sara Castillo** (saracgarcia3)
- **Salomón Baena** (DSBAENAR)

---

## Descripción del Proyecto

DrawSync Backend es la infraestructura del servidor para un juego multijugador de dibujo colaborativo en tiempo real. Gestiona salas de juego, comunicación en tiempo real mediante WebSockets, sincronización de temporizadores y transmisión de mensajes entre todos los clientes conectados.

---

## Tecnologías Utilizadas

### Framework Principal
- **Spring Boot 3.5.6** - Framework de aplicación
- **Java 17** - Lenguaje de programación
- **Maven** - Herramienta de construcción y gestión de dependencias

### Comunicación en Tiempo Real
- **Spring WebSocket** - Soporte para WebSocket
- **Protocolo STOMP** - Protocolo de mensajería sobre WebSocket
- **SockJS** - Soporte de respaldo para WebSocket

### Dependencias Adicionales
- **Spring Security** - Configuración de seguridad
- **Spring Actuator** - Monitoreo de aplicación
- **Spring Data MongoDB** - Integración con base de datos
- **JWT (jjwt)** - Soporte para JSON Web Token
- **Lombok** - Reducción de código repetitivo
- **Spring DevTools** - Utilidades de desarrollo

### Pruebas y Cobertura
- **JUnit 5** - Framework de pruebas
- **Mockito** - Framework de simulación
- **JaCoCo** - Reporte de cobertura de código (objetivo: 80%+)

---

## Funcionalidades Principales

### Gestión de Juegos
- **Crear Juego** - Genera códigos únicos de 4 caracteres para salas
- **Unirse a Juego** - Permite a jugadores unirse a salas existentes
- **Iniciar Juego** - Inicia el temporizador y el gameplay
- **Estados del Juego** - Rastrea estados LOBBY, PLAYING, FINISHED
- **Soporte Multi-sala** - Múltiples juegos concurrentes

### Características en Tiempo Real
- **Comunicación WebSocket** - Mensajería bidireccional en tiempo real
- **Tópicos por Sala** - Comunicación aislada por sala de juego
- **Sincronización de Temporizador** - Cuenta regresiva por juego
- **Transmisión de Chat** - Mensajes de chat en tiempo real
- **Sincronización de Dibujo** - Compartir trazos de dibujo
- **Señalización de Voz** - Señalización WebRTC para chat de voz

### Gestión de Temporizadores
- **Ejecución Programada** - Pool de hilos para múltiples temporizadores
- **Limpieza Automática** - Cancelación de temporizadores al finalizar juegos
- **Aislamiento por Juego** - Temporizadores independientes para cada sala

---

## Arquitectura del Sistema

### Estructura de Paquetes

```
com.edu.eci.DrawSync/
├── config/              # Clases de configuración
│   ├── SecurityConfig.java
│   ├── WebSocketConfig.java
│   └── RestConfig.java
├── controller/          # Controladores REST y WebSocket
│   ├── GameController.java
│   ├── ChatController.java
│   ├── DrawController.java
│   └── VoiceController.java
├── model/              # Modelos de dominio
│   ├── Game.java
│   ├── Message.java
│   └── Stroke.java
├── service/            # Lógica de negocio
│   └── GameService.java
└── Application.java    # Clase principal de aplicación
```

---

## Endpoints de la API

### API REST

#### Crear Juego
```http
POST /api/games/create
Content-Type: application/json

{
  "player": "nombre_usuario"
}

Respuesta: Objeto Game con gameCode
```

#### Unirse a Juego
```http
POST /api/games/join
Content-Type: application/json

{
  "gameCode": "ABCD",
  "player": "nombre_usuario"
}

Respuesta: Objeto Game
```

#### Obtener Estado del Juego
```http
GET /api/games/{gameCode}

Respuesta: Objeto Game con estado y jugadores
```

#### Iniciar Juego
```http
POST /api/games/{gameCode}/start

Respuesta: 200 OK
```

### Tópicos WebSocket

#### Suscripción (Cliente → Servidor)
- `/app/chat/{gameCode}` - Enviar mensaje de chat
- `/app/draw/{gameCode}` - Enviar trazo de dibujo
- `/app/voice/signal/{gameCode}` - Enviar señalización de voz

#### Transmisión (Servidor → Clientes)
- `/topic/{gameCode}/chat` - Recibir mensajes de chat
- `/topic/{gameCode}/draw` - Recibir actualizaciones de dibujo
- `/topic/{gameCode}/timer` - Recibir actualizaciones del temporizador
- `/topic/{gameCode}/voice` - Recibir señalización de voz

---

## Instrucciones de Instalación

### Requisitos Previos
- Java 17+
- Maven 3.6+
- (Opcional) MongoDB para persistencia

### Instalación

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/daviespr1406/DrawSync.git
   cd DrawSync
   ```

2. **Compilar el proyecto**
   ```bash
   mvn clean install
   ```

3. **Ejecutar la aplicación**
   ```bash
   mvn spring-boot:run
   ```

   El servidor estará disponible en `http://localhost:8080`

---

## Pruebas Unitarias

### Ejecutar Pruebas

```bash
# Ejecutar todas las pruebas
mvn test

# Ejecutar pruebas con reporte de cobertura
mvn clean test jacoco:report

# Ver reporte de cobertura
# Windows: start target/site/jacoco/index.html
# Mac/Linux: open target/site/jacoco/index.html
```

### Suite de Pruebas

Pruebas comprehensivas para `GameService`:

- `testCreateGame()` - Verificar creación de juego
- `testJoinGame_Success()` - Jugador se une exitosamente
- `testJoinGame_NonexistentGame()` - Manejar códigos inválidos
- `testJoinGame_FinishedGame()` - Prevenir unirse a juegos finalizados
- `testJoinGame_PlayingGame()` - Permitir uniones tardías
- `testStartGame_Success()` - Juego inicia y temporizador comienza
- `testStartGame_AlreadyPlaying()` - Inicio idempotente
- `testStartGame_NonexistentGame()` - Manejar códigos inválidos
- `testGetGame_Success()` - Recuperar juego por código
- `testGetGame_NonexistentGame()` - Manejar no encontrado
- `testMultipleGamesIndependence()` - Juegos aislados
- `testGameCodeUniqueness()` - Códigos únicos

### Resultados de Pruebas

**[Insertar captura de pantalla de resultados de pruebas aquí]**

```
Tests ejecutados: 13
Fallos: 0
Errores: 0
Omitidos: 0
```

### Cobertura de Código

**[Insertar captura de pantalla del reporte de JaCoCo aquí]**

#### Requisitos de Cobertura
- Cobertura mínima de líneas: **80%**
- Excluido de cobertura:
  - Controladores (`**/controller/**`)
  - Repositorios (`**/repository/**`)
  - Modelos/DTOs (`**/model/**`)
  - Clases de configuración (`**/config/**`)
  - Clase principal de aplicación (`**/Application.class`)

Reporte de cobertura disponible en: `target/site/jacoco/index.html`

---

## Configuración

### Seguridad
- CORS habilitado para todos los orígenes (desarrollo)
- Endpoints WebSocket: `/ws/**` (público)
- Endpoints API: `/api/**` (público)

### WebSocket
- Endpoint: `/ws`
- Message broker: `/topic`
- Destino de aplicación: `/app`
- Orígenes permitidos: `*` (desarrollo)

### Pool de Hilos
- Programador de temporizadores: 5 hilos
- Soporta 5 juegos concurrentes con temporizadores activos

---

## Flujo del Juego

1. **Crear** → Jugador crea juego, obtiene código único
2. **Lobby** → Jugadores se unen usando código, estado = LOBBY
3. **Iniciar** → Creador inicia juego, estado = PLAYING, temporizador comienza
4. **Jugar** → Cuenta regresiva de 60 segundos, dibujo/chat en tiempo real
5. **Finalizar** → Temporizador llega a 0, estado = FINISHED

---

## Depuración

### Habilitar Logging

Agregar a `application.properties`:
```properties
logging.level.com.edu.eci.DrawSync=DEBUG
logging.level.org.springframework.messaging=DEBUG
```

### Problemas Comunes

**WebSocket no se conecta**
- Verificar configuración de CORS
- Verificar endpoint SockJS `/ws`
- Revisar configuración de firewall/red

**Temporizador no sincroniza**
- Verificar que el estado del juego sea PLAYING
- Verificar suscripción WebSocket a `/topic/{gameCode}/timer`
- Revisar logs del backend para ticks del temporizador

**Chat no funciona**
- Verificar formato del mensaje (campos username, message)
- Verificar conexión WebSocket
- Revisar logs del backend para recepción de mensajes

---

## Diagramas del Sistema

### Diagrama de Arquitectura

**[Insertar diagrama de arquitectura aquí]**

### Diagrama de Clases

**[Insertar diagrama de clases aquí]**

### Diagrama de Secuencia - Flujo de Juego

**[Insertar diagrama de secuencia aquí]**

---

## Licencia

Este proyecto es desarrollado con fines educativos como parte del curso ARSW en la Escuela Colombiana de Ingeniería Julio Garavito.

---

## Contacto

Para consultas o contribuciones, contactar a los autores:
- David Espinosa: daviespr1406
- Sara Castillo: saracgarcia3
- Salomón Baena: DSBAENAR
