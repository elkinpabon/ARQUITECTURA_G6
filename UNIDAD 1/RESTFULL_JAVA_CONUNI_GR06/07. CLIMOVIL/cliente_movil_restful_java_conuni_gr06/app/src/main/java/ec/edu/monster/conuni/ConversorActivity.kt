package ec.edu.monster.conuni

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ec.edu.monster.conuni.ui.theme.ClienteMovilTheme
import ec.edu.monster.conuni.ui.theme.ConuniAzul
import ec.edu.monster.controlador.AppControlador
import ec.edu.monster.modelo.Resultado
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class OpcionConversion(val clave: String, val etiqueta: String)

class ConversorActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val categoria = intent.getStringExtra("categoria") ?: "longitud"
        setContent {
            ClienteMovilTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    tituloCategoria(categoria),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.Filled.ArrowBack,
                                        contentDescription = "Volver",
                                        tint = Color.White
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = ConuniAzul)
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    PantallaConversor(
                        modifier = Modifier.padding(innerPadding),
                        categoria = categoria
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConversor(
    modifier: Modifier = Modifier,
    categoria: String
) {
    val opciones = remember(categoria) { opcionesPorCategoria(categoria) }
    var opcionSeleccionada by remember { mutableStateOf(opciones.first()) }
    var valor by remember { mutableStateOf("") }
    var resultado by remember { mutableStateOf<Resultado?>(null) }
    var cargando by remember { mutableStateOf(false) }
    var expandido by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val controlador = remember { AppControlador() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            "Conversiones de ${tituloCategoria(categoria)}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ConuniAzul
        )
        Text(
            descripcionCategoria(categoria),
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 18.dp)
        )

        // Dropdown de operacion
        ExposedDropdownMenuBox(
            expanded = expandido,
            onExpandedChange = { expandido = it }
        ) {
            OutlinedTextField(
                value = opcionSeleccionada.etiqueta,
                onValueChange = {},
                readOnly = true,
                label = { Text("Conversion") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandido,
                onDismissRequest = { expandido = false }
            ) {
                opciones.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion.etiqueta) },
                        onClick = {
                            opcionSeleccionada = opcion
                            expandido = false
                            resultado = null
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = { valor = it },
            label = { Text("Valor") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val valorDouble = valor.replace(',', '.').toDoubleOrNull()
                if (valorDouble == null) {
                    Toast.makeText(context, "Ingresa un numero valido", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                cargando = true
                scope.launch {
                    val r = withContext(Dispatchers.IO) {
                        when (categoria) {
                            "longitud" -> controlador.convertirLongitud(opcionSeleccionada.clave, valorDouble)
                            "masa" -> controlador.convertirMasa(opcionSeleccionada.clave, valorDouble)
                            "temperatura" -> controlador.convertirTemperatura(opcionSeleccionada.clave, valorDouble)
                            else -> Resultado.error("Categoria desconocida")
                        }
                    }
                    resultado = r
                    cargando = false
                }
            },
            enabled = !cargando,
            colors = ButtonDefaults.buttonColors(containerColor = ConuniAzul),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text("Convertir", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        resultado?.let { r ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (r.isExito) Color(0xFFE2FDE2) else Color(0xFFFDE2E2),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(14.dp)
            ) {
                Text(
                    text = if (r.isExito) "Resultado: ${r.valor}" else r.mensaje,
                    color = if (r.isExito) Color(0xFF006B00) else Color(0xFFA10000),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun tituloCategoria(categoria: String): String = when (categoria) {
    "longitud" -> "Longitud"
    "masa" -> "Masa"
    "temperatura" -> "Temperatura"
    else -> categoria.replaceFirstChar { it.uppercase() }
}

private fun descripcionCategoria(categoria: String): String = when (categoria) {
    "longitud" -> "Metros, pies, kilometros, millas, pulgadas y mas."
    "masa" -> "Kilogramos, libras, gramos, onzas, toneladas y mas."
    "temperatura" -> "Celsius, Fahrenheit y Kelvin."
    else -> ""
}

private fun opcionesPorCategoria(categoria: String): List<OpcionConversion> = when (categoria) {
    "longitud" -> listOf(
        OpcionConversion("metrosAPies", "Metros a Pies"),
        OpcionConversion("kilometrosAMillas", "Kilometros a Millas"),
        OpcionConversion("centimetrosAPulgadas", "Centimetros a Pulgadas"),
        OpcionConversion("yardasAMetros", "Yardas a Metros"),
        OpcionConversion("milimetrosAPulgadas", "Milimetros a Pulgadas")
    )
    "masa" -> listOf(
        OpcionConversion("kilogramosALibras", "Kilogramos a Libras"),
        OpcionConversion("gramosAOnzas", "Gramos a Onzas"),
        OpcionConversion("toneladasAKilogramos", "Toneladas a Kilogramos"),
        OpcionConversion("librasAOnzas", "Libras a Onzas"),
        OpcionConversion("miligramosAGramos", "Miligramos a Gramos")
    )
    "temperatura" -> listOf(
        OpcionConversion("celsiusAFahrenheit", "Celsius a Fahrenheit"),
        OpcionConversion("fahrenheitACelsius", "Fahrenheit a Celsius"),
        OpcionConversion("celsiusAKelvin", "Celsius a Kelvin"),
        OpcionConversion("kelvinACelsius", "Kelvin a Celsius"),
        OpcionConversion("fahrenheitAKelvin", "Fahrenheit a Kelvin")
    )
    else -> emptyList()
}
