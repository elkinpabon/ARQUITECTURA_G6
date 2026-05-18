package ec.edu.monster.conuni.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = ConuniAzul,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = ConuniAzulClaro,
    tertiary = ConuniAmarillo,
    background = ConuniGrisFondo,
    surface = androidx.compose.ui.graphics.Color.White
)

@Composable
fun ClienteMovilTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
