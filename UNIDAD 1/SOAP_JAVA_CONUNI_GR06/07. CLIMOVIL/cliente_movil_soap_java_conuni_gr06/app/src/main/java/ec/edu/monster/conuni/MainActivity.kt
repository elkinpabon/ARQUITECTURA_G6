package ec.edu.monster.conuni

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ec.edu.monster.conuni.ui.theme.ClienteMovilTheme
import ec.edu.monster.conuni.ui.theme.ConuniAmarillo
import ec.edu.monster.conuni.ui.theme.ConuniAzul
import ec.edu.monster.controlador.AppControlador
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClienteMovilTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    "Cliente Movil CONUNI",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = ConuniAzul)
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    PantallaLogin(
                        modifier = Modifier.padding(innerPadding),
                        onLoginExitoso = { usuario ->
                            val intent = Intent(this@MainActivity, MenuActivity::class.java)
                            intent.putExtra("usuario", usuario)
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLogin(
    modifier: Modifier = Modifier,
    onLoginExitoso: (String) -> Unit
) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var contrasenaVisible by remember { mutableStateOf(false) }
    var cargando by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val controlador = remember { AppControlador() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.moster),
            contentDescription = "Logo CONUNI",
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(4.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Iniciar Sesion",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = ConuniAzul
        )
        Text(
            text = "Ingresa tus credenciales",
            fontSize = 13.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // Imagen ilustrativa (reutilizada del cliente web)
        Image(
            painter = painterResource(id = R.drawable.login),
            contentDescription = "Ilustracion login",
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contrasena") },
            singleLine = true,
            visualTransformation = if (contrasenaVisible) VisualTransformation.None
                                   else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { contrasenaVisible = !contrasenaVisible }) {
                    Icon(
                        imageVector = if (contrasenaVisible) Icons.Filled.VisibilityOff
                                      else Icons.Filled.Visibility,
                        contentDescription = if (contrasenaVisible) "Ocultar contrasena"
                                             else "Mostrar contrasena"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (usuario.isBlank() || contrasena.isBlank()) {
                    Toast.makeText(context, "Completa usuario y contrasena", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                cargando = true
                scope.launch {
                    val resultado = withContext(Dispatchers.IO) {
                        controlador.iniciarSesion(usuario, contrasena)
                    }
                    cargando = false
                    if (resultado.isExito) {
                        onLoginExitoso(usuario)
                    } else {
                        Toast.makeText(context, resultado.mensaje, Toast.LENGTH_LONG).show()
                    }
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
                Text(
                    "Ingresar",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaLoginPreview() {
    ClienteMovilTheme {
        PantallaLogin(onLoginExitoso = {})
    }
}
