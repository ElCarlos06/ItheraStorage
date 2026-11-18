# Informe de Consultoría Externa de TI: Proyecto ItheraStorage

## Resumen Ejecutivo
El presente documento formaliza el diagnóstico, la estrategia de intervención y los acuerdos de alcance desarrollados por el equipo de consultoría externa de TI para la implementación de la solución **ItheraStorage / Activos360**.

---

## 1. Selección del Caso y Brecha Tecnológica (Gap Analysis)

### 1.1 Contexto de la Empresa
* **Cliente:** Empresa mediana/grande del sector servicios y manufactura ligera con múltiples centros de costos, oficinas operativas y bodegas de almacenamiento.
* **Problemática central:** Pérdida recurrente de trazabilidad en activos de TI, mobiliario y herramientas operativas; discrepancias entre inventario físico y contable, y tiempos muertos en la atención de solicitudes de asignación de resguardo.

### 1.2 Situación Actual (AS-IS)
* **Procesos manuales:** Los levantamientos de inventario se realizan en bitácoras de papel y hojas de cálculo (`Excel`) dispersas por departamento.
* **Silos de información:** No existe una base de datos centralizada; los traspasos de equipo entre colaboradores no quedan documentados con firmas o evidencias fotográficas.
* **Falta de movilidad:** Los auditores de almacén deben anotar números de serie a mano y luego capturarlos manualmente en una PC de escritorio, generando errores de dedo y duplicidad de registros.
* **Retardo en decisiones:** La gerencia tarda hasta 15 días hábiles en consolidar reportes de depreciación o inventario disponible.

### 1.3 Brecha Tecnológica (The Tech Gap)

| Dimensión | Estado Actual (AS-IS) | Brecha Identificada | Estado Deseado (TO-BE: ItheraStorage) |
| :--- | :--- | :--- | :--- |
| **Captura de Datos** | Lápiz y papel / hojas de cálculo. | Ausencia de digitalización en punto de operación. | Aplicación móvil Android (**Activos360**) con escaneo de código de barras/QR y carga fotográfica. |
| **Arquitectura de TI** | Archivos locales desincronizados. | Falta de un repositorio unificado y seguro de datos. | Backend centralizado en **Spring Boot** (API REST `sirma`) con base de datos relacional y control de roles. |
| **Gestión y Control** | Búsqueda manual de registros. | Nula visibilidad del ciclo de vida del activo. | Portal administrativo Web en **React + Vite** con dashboards en tiempo real, catálogos dinámicos y reportes ejecutivos. |
| **Trazabilidad** | Firmas en hojas sueltas que se extravían. | Vulnerabilidad en la custodia legal de activos. | Flujo de solicitudes y resguardos digitales con firmas y auditoría de cambios. |

---

## 2. Conversación Empática y Asignación de Roles por Habilidades

Para garantizar la adopción y el éxito del proyecto, la consultora lidera una sesión de alineación con el equipo técnico y operativo aplicando **escucha activa y comunicación asertiva**.

### 2.1 Protocolo de Escucha Activa y Empatía
1. **Validación emocional y de carga de trabajo:**
   > *"Entendemos que el área operativa se siente abrumada por los conteos manuales y que el equipo de desarrollo teme que los requerimientos cambien sobre la marcha. Estamos aquí para estructurar una solución viable, no para imponer procesos que compliquen su día a día."*
2. **Preguntas abiertas:**
   * *Operaciones:* "¿Qué parte del proceso diario de inventario es la que más les causa frustración o retrabajo?"
   * *Desarrollo:* "¿Con qué tecnologías del stack se sienten más cómodos y qué retos técnicos prevén en la integración móvil-backend?"
3. **Parafraseo y síntesis asertiva:** Confirmar lo escuchado sin juzgar para asegurar que los requerimientos reales (y no solo los síntomas) queden mapeados.

### 2.2 Matriz de Asignación por Habilidades (Skill Matrix)

| Rol Consultoría / Proyecto | Colaborador / Perfil | Fortalezas & Habilidades | Responsabilidad Asignada en el Proyecto |
| :--- | :--- | :--- | :--- |
| **Líder Técnico & Arquitecto Backend** | Perfil Backend | Experiencia en Java, Spring Boot, seguridad (JWT), modelado relacional y APIs RESTful. | Diseño y desarrollo de la API `sirma`, endpoints de activos, catálogos, autenticación y lógica de negocio. |
| **Especialista Frontend Web** | Perfil Frontend UI/UX | Dominio de React, Vite, CSS modular (Moon Design System) y consumo asíncrono de APIs. | Desarrollo del módulo web administrativo `gestion_inventario` (dashboard, tablas interactivas, CRUD de catálogos y solicitudes). |
| **Desarrollador Móvil** | Perfil Android | Experiencia en Android Studio, Kotlin/Java, consumo de hardware (cámara para escaneo QR/barras). | Desarrollo de la app móvil `Activos360` para captura rápida y validación en piso de almacén. |
| **Líder QA & Procesos (Consultor Funcional)** | Perfil Analítico / Calidad | Metodología de pruebas, mapeo de procesos empresariales y validación de casos de uso. | Pruebas funcionales e integración entre móvil, web y back; elaboración de manuales y capacitación a usuarios finales. |
| **Project Manager / Facilitador** | Perfil Gerencial / Scrum Master | Gestión de tiempos, resolución de bloqueos, escucha activa y enlace con patrocinadores (stakeholders). | Seguimiento de entregables, control de riesgos, gestión de expectativas y reuniones de sincronización. |

---

## 3. Definición del Alcance y Manejo de Objeciones

### 3.1 Alcance del Proyecto: ¿Qué se entrega y qué NO se entrega?

#### Alcance Incluido (IN-SCOPE - Fase 1 / MVP Funcional)
* **Backend API (`sirma`):**
  * Servicios REST con autenticación segura y control de acceso basado en roles (Admin/Operador).
  * Endpoints para gestión de activos, solicitudes, usuarios y catálogos.
* **Panel Web (`gestion_inventario`):**
  * Visualización y control general de activos.
  * Gestión de estados (resguardado, mantenimiento, reportado, disponible).
  * Filtros de búsqueda, catálogos y administración de usuarios.
  * Dashboard inicial con indicadores clave (KPIs).
* **App Móvil Android (`Activos360`):**
  * Módulo de escaneo rápido de códigos de barras y códigos QR.
  * Consulta en tiempo real de la ficha técnica del activo.
  * Verificación física en piso de almacén/oficinas.
* **Documentación y Entrega:**
  * Especificación técnica de API (`api-docs.json`).
  * Manuales de usuario y sesiones de capacitación para administradores.

#### Alcance Excluido (OUT-OF-SCOPE)
* **Versión para iOS:** La aplicación móvil está delimitada exclusivamente para dispositivos Android.
* **Integración en tiempo real con ERPs legados (SAP/Oracle):** Se proporciona exportación/importación en formatos estándar (CSV/Excel/JSON); no incluye conectores directos a ERP en esta fase.
* **Limpieza de datos históricos corruptos:** El cliente es responsable de estandarizar y validar sus hojas de cálculo antes de la migración inicial.
* **Soporte presencial 24/7:** Se contempla soporte remoto en horario laboral durante 30 días posteriores al go-live.

---

### 3.2 Matriz de Negociación y Manejo de Objeciones (Stakeholder Management)

| Stakeholder | Objeción / Preocupación Principal | Estrategia de Respuesta y Negociación (Asertiva & Empática) |
| :--- | :--- | :--- |
| **Director de Operaciones / Almacén** | *"Mis operadores no son expertos en tecnología; van a tardar más usando un celular que anotando en sus hojas."* | **Respuesta empática:** *"Comprendemos su preocupación por la velocidad operativa. Por ello, la app móvil Activos360 está diseñada con flujos de 2 toques: escanear código y confirmar estado. Haremos pruebas piloto con 2 operadores clave durante 3 días para ajustar la interfaz antes del lanzamiento total."* |
| **Director de Finanzas (CFO)** | *"¿Por qué no integramos el sistema directamente con nuestro ERP contable desde el primer día para justificar la inversión?"* | **Respuesta asertiva:** *"Integrar el ERP en esta primera etapa aumentaría el costo en un 40% y alargaría la salida a producción 3 meses más. Al entregar primero ItheraStorage con importación/exportación estandarizada, obtienen valor inmediato en la auditoría física y mitigamos el riesgo financiero sin retrasar el control de los activos."* |
| **Equipo de TI Interno** | *"No tenemos tiempo para mantener otra plataforma nueva ni para atender quejas si el servidor se cae."* | **Respuesta técnica y colaborativa:** *"La solución se entrega contenerizada y documentada bajo arquitectura estándar de Spring Boot y React con guías de despliegue simples. Además, capacitaremos a un responsable técnico interno y brindaremos acompañamiento durante el primer mes de operación."* |
| **Usuarios Operativos (Resistencia al cambio)** | *"Siento que la app es para vigilar cuánto tiempo me tardo en cada pasillo."* | **Respuesta de confianza:** *"La herramienta no es de supervisión personal, sino para eliminar el trabajo tedioso de recapturar datos en la computadora al final de la jornada. Su objetivo es evitar que a ustedes se les responsabilice por pérdidas de equipo causadas por descontrol de firmas en papel."* |

---

### 3.3 Criterios de Aceptación y Próximos Pasos
1. **Firma del Acta de Alcance:** Aprobación formal del documento de requerimientos e inclusiones/exclusiones con los stakeholders.
2. **Definición de Sprint 0:** Configuración de ambientes locales y servidores para base de datos, backend `sirma` y frontends (`gestion_inventario` y `Activos360`).
3. **Piloto de Usabilidad:** Prueba de escaneo con personal de almacén utilizando la APK de prueba para calibrar tiempos y experiencia de usuario.

