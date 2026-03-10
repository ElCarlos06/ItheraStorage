# Plantillas de correo

## welcome-email.html

Plantilla de bienvenida cuando un administrador registra un nuevo usuario. Diseño basado en [Figma - Gestion_Inventario_5B](https://www.figma.com/design/8mv6Kz1t3Cll8AhRhWsfXz/Gestion_Inventario_5B?node-id=678-1067).

### Placeholders

| Placeholder   | Descripción                              | Ejemplo                      |
|--------------|-------------------------------------------|------------------------------|
| `{{nombre}}` | Nombre del usuario                        | Juan Pérez                   |
| `{{password}}` | Contraseña temporal asignada            | erasghrdf1                   |
| `{{loginUrl}}` | URL para el botón "Iniciar Sesión"      | https://app.com/login        |
| `{{logoUrl}}`  | URL absoluta del logo **blanco** (para header púrpura) | https://app.com/email/activos360_logo_blanco.png |
| `{{waveUrl}}`  | URL absoluta de la onda/curva del header | https://app.com/email/onda.svg |

### Uso desde Java

```java
@Autowired
private EmailTemplateService emailTemplateService;

// Al registrar un usuario
String html = emailTemplateService.getWelcomeEmailHtml(
    "Juan Pérez",           // nombre
    "passwordTemporal123",  // password
    "https://tu-app.com/login",  // loginUrl
    "https://tu-app.com/email/activos360_logo.svg",  // logoUrl
    "https://tu-app.com/email/onda.svg",  // waveUrl
    "https://tu-app.com"  // baseUrl (para iconos en cajitas)
);
// Enviar html con tu servicio de correo
```

### Logo

El logo se encuentra en `static/email/activos360_logo.png`. Para el header púrpura (#717ff5), se recomienda usar una versión en blanco del logo exportada desde Figma para mejor contraste.
