package ec.edu.monster.conuni

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ec.edu.monster.conuni.ui.theme.ClienteMovilTheme
import ec.edu.monster.conuni.ui.theme.ConuniAmarillo
import ec.edu.monster.conuni.ui.theme.ConuniAzul
import ec.edu.monster.conuni.ui.theme.ConuniAzulClaro

class MenuActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val usuario = intent.getStringExtra("usuario") ?: "Usuario"
        setContent {
            ClienteMovilTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = painterResource(id = R.drawable.moster),
                                        contentDescription = "Logo",
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        "Hola, $usuario",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    startActivity(Intent(this@MenuActivity, MainActivity::class.java))
                                    finish()
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.ExitToApp,
                                        contentDescription = "Cerrar sesion",
                                        tint = ConuniAmarillo
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = ConuniAzul)
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    PantallaMenu(
                        modifier = Modifier.padding(innerPadding),
                        onCategoriaSeleccionada = { categoria ->
                            val intent = Intent(this@MenuActivity, ConversorActivity::class.java)
                            intent.putExtra("categoria", categoria)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PantallaMenu(
    modifier: Modifier = Modifier,
    onCategoriaSeleccionada: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            "Menu de Conversiones",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = ConuniAzul
        )
        Text(
            "Elige una categoria",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 18.dp)
        )

        TarjetaCategoria(
            titulo = "Longitud",
            descripcion = "Metros, pies, millas, pulgadas...",
            icono = Icons.Filled.Straighten,
            onClick = { onCategoriaSeleccionada("longitud") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TarjetaCategoria(
            titulo = "Masa",
            descripcion = "Kilogramos, libras, onzas...",
            icono = Icons.Filled.Scale,
            onClick = { onCategoriaSeleccionada("masa") }
        )
        Spacer(modifier = Modifier.height(12.dp))
        TarjetaCategoria(
            titulo = "Temperatura",
            descripcion = "Celsius, Fahrenheit, Kelvin",
            icono = Icons.Filled.Thermostat,
            onClick = { onCategoriaSeleccionada("temperatura") }
        )
    }
}

@Composable
fun TarjetaCategoria(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = ConuniAzul),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ConuniAzulClaro),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    tint = ConuniAmarillo,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    titulo,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(
                    descripcion,
                    color = Color(0xFFD0DAE8),
                    fontSize = 13.sp
                )
            }
        }
    }
}
