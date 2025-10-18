# 🎨​ DrawSync

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

## Costes estimados del proyecto

### **a) Coste de Infraestructura (AWS)**

1. **S3 + CloudFront (Frontend)**
   - **S3**: Es un servicio para guardar archivos como imágenes y datos. Costo mensual estimado: **$1.15** por 50 GB.
   - **CloudFront**: Ayuda a distribuir estos archivos rápidamente a los usuarios. Costo mensual estimado: **$8.50** por 100 GB de datos transferidos.

2. **ALB (Application Load Balancer)**
   - **ALB**: Es un servicio que distribuye el tráfico entre servidores para que la aplicación funcione sin problemas. Costo estimado: **$32.8**/mes.

3. **Backend Real-time + REST (ECS/Fargate)**
   - **Fargate**: Ejecuta la parte del servidor que gestiona las partidas y la comunicación entre jugadores. Costo estimado para 2 tareas 24/7: **$35.55**/mes.

4. **ElastiCache (Redis)**
   - **Redis**: Ayuda a almacenar datos importantes (como el progreso de las partidas) de forma rápida. Costo estimado: **$15–100**/mes, dependiendo del tamaño del servicio.

5. **Base de Datos (DocumentDB / MongoDB Atlas)**
   - **DocumentDB** o **MongoDB Atlas**: Son servicios de bases de datos para almacenar usuarios y estadísticas. Costo estimado: **$0–200+**/mes.

6. **IA (SageMaker o API externa)**
   - **IA**: Ayuda a que el sistema reconozca dibujos. Costo estimado: **$0–100+**/mes dependiendo del servicio.

7. **Autenticación (Amazon Cognito)**
   - **Cognito**: Gestiona el registro e inicio de sesión de los usuarios. Costo estimado: **$0 hasta 10k usuarios activos** al mes (gratis), luego alrededor de **$0.01–$0.015** por usuario.

## Coste Total Estimado

- **Total mensual aproximado**: **$100–400/mes**.

## **b) Estrategias de Ahorro**

1. **Inicio con EC2 barato + Docker Compose** para reducir costos al principio.
2. **Uso de MongoDB Atlas gratuito** para la base de datos durante el desarrollo.
3. **Uso de Redis en EC2** durante la fase de pruebas, y luego moverlo a ElastiCache para producción.
4. **IA mediante APIs externas** durante las pruebas y, si se requiere mayor rendimiento, usar SageMaker.

## **Viabilidad del Proyecto**

- **Bajo costo inicial**: Los servicios de AWS ofrecen una solución escalable con bajo costo inicial.
- **Escalabilidad**: La infraestructura en la nube permitirá aumentar los recursos conforme crezca el proyecto.
- **Monitoreo de costos**: Es importante monitorear los costos mediante alertas para evitar gastos inesperados.

## **Impacto**

- **Escalabilidad sin grandes inversiones iniciales**: AWS permite que el proyecto crezca a medida que aumentan los usuarios, sin necesidad de grandes inversiones iniciales.
- **Reducción de costos operativos**: Usar servicios gestionados ayuda a mantener los costos bajos.

## **Resumen de Costos Mensuales Aproximados**

| Componente                          | Costo Aproximado       |
|-------------------------------------|------------------------|
| **S3 Storage + CloudFront**         | $10–15 / mes           |
| **ALB**                             | ~$32.8 / mes           |
| **Backend (ECS/Fargate)**           | ~$35.55 / mes          |
| **ElastiCache (Redis)**             | $15–100 / mes          |
| **Base de Datos (DocumentDB/MongoDB)** | $0–200+ / mes          |
| **IA (SageMaker/API externa)**      | $0–100+ / mes          |
| **Autenticación (Amazon Cognito)**  | $0 hasta ~10k    |
| **Total estimado**                  | ~$100–400 / mes        |



