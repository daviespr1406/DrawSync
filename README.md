# DrawSync

**Autores:**  
David Espinosa (daviespr1406)

Sara Castillo (saracgarcia3)

Salomón Baena (DSBAENAR)

---

## Descripción del Proyecto

**DrawSync** es una aplicación colaborativa en tiempo real que permite a múltiples usuarios dibujar simultáneamente en un mismo lienzo compartido.  
El objetivo principal es **facilitar la co-creación y la expresión visual sincronizada**, ideal para talleres, clases o entornos de diseño remoto.

El proyecto cuenta con un **frontend interactivo** y un **backend en Java Spring Boot**, conectados mediante **WebSockets** y **API REST**.  
Cada usuario puede conectarse, seleccionar herramientas de dibujo y visualizar en vivo las acciones de los demás participantes.

---

## Arquitectura General

El sistema adopta una arquitectura distribuida y escalable, compuesta por los siguientes elementos:

- **Frontend:** Interfaz construida con React.js, responsable de la interacción y el renderizado del lienzo compartido.  
- **Backend:** API REST + WebSocket con **Spring Boot**, encargada de manejar usuarios, sesiones y sincronización.  
- **Base de Datos:** MongoDB para persistir usuarios, sesiones y trazos de dibujo.  
- **Infraestructura:** Despliegue en **AWS**, con balanceo de carga, Elastic Beanstalk y almacenamiento de recursos en S3.

---

## Diagrama C4 — Contexto

El siguiente diagrama representa la arquitectura de **DrawSync** a nivel de contexto (C4-Model Level 1).  
Se incluyen los actores principales, sistemas externos y los límites del sistema.



**Descripción:**
- Los usuarios interactúan con la aplicación desde un navegador web.  
- El frontend envía y recibe eventos en tiempo real al backend mediante WebSocket.  
- El backend gestiona usuarios, sesiones y persistencia en MongoDB.  
- La infraestructura se despliega en AWS, permitiendo escalabilidad y disponibilidad continua.

---

## Diagrama de Clases

![Diagrama de Clases DrawSync](/assets/1.jpg)

---

##  Tecnologías Utilizadas

| Componente | Tecnología                     |
|-------------|--------------------------------|
| **Backend** | Java 21, Spring Boot 3.x       |
| **Frontend** | React.js                       |
| **Base de Datos** | MongoDB,Cache Redis            |
| **Comunicación** | WebSocket + REST API           |
| **Infraestructura** | AWS EC2, S3, Elastic Beanstalk |
| **Colaboración y Diseño** | Miro, PlantUML, Draw.io        |
| **Control de Versiones** | GitHub                         |

---

## Ejecución del Backend

### Requisitos previos
- Java 21  
- Maven 3.9+  
- MongoDB (local o en la nube)

###  Ejecución local
```bash
git clone https://github.com/daviespr1406/DrawSync.git
cd DrawSync
mvn spring-boot:run
```

Servidor disponible en:
```
http://localhost:8080
```

---

## Endpoints iniciales

| Método | Endpoint         | Descripción                        |
|--------|------------------|------------------------------------|
| GET    | /api/health      | Verifica el estado del servidor    |
| POST   | /api/users       | Registra un nuevo usuario          |
| GET    | /api/users       | Lista todos los usuarios registrados |
| GET    | /api/sessions    | Obtiene las sesiones activas       |

---

## Futuras Mejoras

- Autenticación JWT y control de roles  
- Persistencia de sesiones colaborativas  
- Exportación del lienzo a imagen  
- Chat en tiempo real integrado  
- Despliegue CI/CD con GitHub Actions y AWS CodePipeline  

---

