package com.example.activos360.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.Surface
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.PriorityHigh
import androidx.compose.material.icons.outlined.Segment
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun FilaDetalleReporte(
    icono: ImageVector,
    titulo: String,
    valor: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp), // Espacio entre filas
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Contenedor del Icono (El cuadrito con fondo)
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = Color(0xFFF8F9FF), // Azul muy clarito
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = Color(0xFF7B88FF), // El azul de tu tema
                modifier = Modifier.size(24.dp)
            )
        }

        // 2. Espaciado entre icono y texto
        Spacer(modifier = Modifier.width(16.dp))

        // 3. Título del dato
        Text(
            text = titulo,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3436)
        )

        // 4. Espaciador flexible para empujar el valor a la derecha
        Spacer(modifier = Modifier.weight(1f))

        // 5. Valor del dato
        Text(
            text = valor,
            fontSize = 14.sp,
            color = Color(0xFF636E72) // Gris para el valor
        )
    }
}



@Composable
fun FilaTipoFalla(
    icono: ImageVector = Icons.Outlined.Build,
    titulo: String,
    valor: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cuadrito del icono
        Box(
            modifier = Modifier
                .size(46.dp) // Tamaño del contenedor
                .background(
                    color = Color(0xFFF8F9FF),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center // Esto centra el icono a fuerza
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = Color(0xFF7B88FF),
                modifier = Modifier.size(24.dp) // Subimos a 24dp para que se vea mejor
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = titulo,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3436)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = valor,
            fontSize = 14.sp,
            color = Color(0xFF636E72),
            fontWeight = FontWeight.Medium
        )
    }
}
// --- COMPONENTE 2: FILA DE PRIORIDAD CON BADGE ---
@Composable
fun FilaPrioridad(
    icono: ImageVector = Icons.Outlined.PriorityHigh, // Icono de exclamación/prioridad
    titulo: String = "Prioridad",
    prioridad: String = "ALTA"
) {
    // Definimos los colores según el texto de la prioridad (por si luego cambia a MEDIA o BAJA)
    val colorTexto = if (prioridad.uppercase() == "ALTA") Color(0xFFD33030) else Color(0xFFFD9644)
    val colorFondo = if (prioridad.uppercase() == "ALTA") Color(0xFFFFE9E9) else Color(0xFFFFF4EB)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Cuadrito del icono (igual que el anterior para que haya simetría)
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(
                    color = Color(0xFFF8F9FF),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = Color(0xFF7B88FF),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Texto "Prioridad"
        Text(
            text = titulo,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D3436)
        )

        Spacer(modifier = Modifier.weight(1f))

        // --- EL BADGE (La etiqueta roja) ---
        Surface (
            color = colorFondo,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = prioridad.uppercase(),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                color = colorTexto,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp // Para que se vea más pro
            )
        }
    }
}

// --- COMPONENTE 3: SECCIÓN DEL USUARIO QUE REPORTA ---
@Composable
fun SeccionUsuarioReporte(
    imagenUrl: Int? = null, // Usaremos un Drawable ID por ahora, después será URL
    nombreUsuario: String = "Dan",
    etiquetaReporte: String = "Reportado por",
    fechaCompleta: String = "Hace 2 días (20 Feb 2026, 14:30h)"
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp), // Espaciado vertical para que respire
        verticalAlignment = Alignment.Top // Alineamos arriba para que coincida con el texto
    ) {
        // 1. EL CONTENEDOR DE LA FOTO (con esquinas suaves)
        Surface(
            modifier = Modifier
                .size(54.dp) // Tamaño de la foto
                .padding(top = 2.dp) // Pequeño ajuste para alinear con el primer texto
                .clip(RoundedCornerShape(14.dp)) // Recortamos la forma (Squircle)
                .border(2.dp, Color(0xFFF1F2F6), RoundedCornerShape(14.dp)), // Borde gris claro
            color = Color(0xFFF1F2F6) // Fondo gris por si no carga la foto
        ) {
            // Aquí iría AsyncImage para cargar desde URL.
            // Por ahora usamos una imagen de prueba o un icono.
            Image(
                // REEMPLAZA ic_sample_user con un drawable tuyo o un icono
                // painterResource = painterResource(id = R.drawable.ic_sample_user),
                imageVector = Icons.Default.AccountCircle, // Icono de respaldo
                contentDescription = null,
                contentScale = ContentScale.Crop, // Recortamos para que llene el cuadro
                modifier = Modifier.padding(4.dp), // Padding interno para el icono
                colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Gray) // Color del icono
            )
        }

        Spacer(modifier = Modifier.width(16.dp)) // Espacio entre foto y textos

        // 2. LA COLUMNA DE TEXTOS
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Fila 1: "Reportado por" y "Dan"
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = etiquetaReporte,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3436)
                )

                Spacer(modifier = Modifier.width(12.dp)) // Espacio entre etiqueta y nombre

                Text(
                    text = nombreUsuario,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF636E72) // Gris para el nombre
                )
            }

            Spacer(modifier = Modifier.height(6.dp)) // Espacio vertical entre líneas

            // Fila 2: La fecha
            Text(
                text = fechaCompleta,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFB2BEC3), // Gris más clarito para la fecha
                lineHeight = 18.sp // Espaciado entre líneas para que se lea mejor
            )
        }
    }
}


// --- COMPONENTE 4: SECCIÓN DE DESCRIPCIÓN ---
@Composable
fun SeccionDescripcion(
    titulo: String = "Descripción del problema",
    cuerpo: String = "La laptop no enciende, hace un sonido extraño cuando presiono el botón de power. Ya intenté con otro cargador y sigue sin funcionar"
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        // 1. Encabezado de la sección (Icono + Título)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mismo cuadrito de icono que los anteriores para mantener la línea
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = Color(0xFFF8F9FF),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Segment, // Icono de líneas de texto
                    contentDescription = null,
                    tint = Color(0xFF7B88FF),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3436)
            )
        }

        // 2. El texto de la descripción
        Text(
            text = cuerpo,
            modifier = Modifier
                .padding(top = 16.dp, start = 2.dp), // Un poco de espacio arriba
            fontSize = 14.sp,
            color = Color(0xFF636E72), // Gris oscuro para lectura clara
            lineHeight = 22.sp, // IMPORTANTE: Esto le da el aire que se ve en tu imagen
            fontWeight = FontWeight.Normal
        )
    }
}


// --- COMPONENTE 5: SECCIÓN DE EVIDENCIA (FOTOS) ---
@Composable
fun SeccionEvidencia(
    titulo: String = "Evidencia",
    imagenes: List<String> = emptyList() // Preparado para recibir URLs de imágenes
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        // 1. Encabezado (Icono de Gafas/Ver + Título)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        color = Color(0xFFF8F9FF),
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Visibility, // El más parecido a las "gafas" de inspección
                    contentDescription = null,
                    tint = Color(0xFF7B88FF),
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = titulo,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3436)
            )
        }

        // 2. Espacio para las imágenes (Por ahora vacío o con un placeholder)
        if (imagenes.isEmpty()) {
            // Aquí es donde lo dejamos "en blanco" como pediste,
            // pero con un espacio para que no se pegue al fondo.
            Spacer(modifier = Modifier.height(16.dp))

            // Opcional: Podrías poner un texto gris tenue que diga "Sin evidencias"
            // o simplemente el espacio vacío.
        } else {
            // Aquí irá el Row o Grid con las fotos cuando conectemos el Back
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // Para que puedas bajar si el reporte es largo
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                // 1. La tarjeta del activo (la que ya tenías)
                MainAssetCard(
                    id = "#M-2024-1523",
                    nombre = "MacBook Pro 16\""
                )

                // 2. Tipo de Falla
                FilaTipoFalla(
                    titulo = "Tipo de falla",
                    valor = "Eléctrica"
                )

                // 3. Prioridad
                FilaPrioridad(
                    prioridad = "ALTA"
                )

                // 4. Reportado por
                SeccionUsuarioReporte(
                    nombreUsuario = "Dan",
                    fechaCompleta = "Hace 2 días (20 Feb 2026, 14:30h)"
                )

                // 5. Descripción
                SeccionDescripcion(
                    cuerpo = "La laptop no enciende, hace un sonido extraño cuando presiono el botón de power. Ya intenté con otro cargador y sigue sin funcionar"
                )

                // 6. Evidencia
                SeccionEvidencia()

                // Espacio extra al final para que no pegue con el borde de la pantalla
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}