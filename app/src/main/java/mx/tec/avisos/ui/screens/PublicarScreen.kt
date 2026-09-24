package mx.tec.avisos.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import mx.tec.avisos.domain.AvisoValidator
import mx.tec.avisos.domain.Rol
import mx.tec.avisos.domain.Sesion
import mx.tec.avisos.ui.state.PublicarUiState
import mx.tec.avisos.ui.theme.AvisosTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicarScreen(
    sesion: Sesion,
    uiState: PublicarUiState,
    onTituloChange: (String) -> Unit,
    onCuerpoChange: (String) -> Unit,
    onPublicar: () -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Nuevo aviso") },
                navigationIcon = {
                    IconButton(onClick = onCancelar) {
                        Icon(Icons.Default.Close, contentDescription = "Cancelar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.titulo,
                onValueChange = onTituloChange,
                label = { Text("Título") },
                supportingText = { Text("De ${AvisoValidator.TITULO_MIN} a ${AvisoValidator.TITULO_MAX} caracteres") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.cuerpo,
                onValueChange = onCuerpoChange,
                label = { Text("Aviso") },
                supportingText = { Text("${uiState.caracteresRestantes} caracteres restantes") },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            // El error del servidor: un 403 porque no eres profesor, un 422 que
            // tu validación no atrapó, o una caída de red. Se muestra aquí y la
            // pantalla NO se cierra.
            val error = uiState.error
            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(
                onClick = onPublicar,
                enabled = sesion.puedePublicar,
                modifier = Modifier.fillMaxWidth()
            ) { Text(if (uiState.enviando) "Publicando…" else "Publicar") }
        }
    }
}

private val demoSesion = Sesion("profe.demo", Rol.PROFESOR, "x", "y", System.currentTimeMillis() / 1000 + 280)

@Preview(showBackground = true)
@Composable
private fun PublicarPreview() {
    AvisosTheme {
        PublicarScreen(
            sesion = demoSesion.copy(usuario = "a01234567", rol = Rol.ALUMNO),
            uiState = PublicarUiState(
                titulo = "Examen parcial",
                cuerpo = "El parcial es el jueves a las 10:00.",
                error = "Solo un profesor puede publicar o borrar avisos"
            ),
            onTituloChange = {}, onCuerpoChange = {}, onPublicar = {}, onCancelar = {},
        )
    }
}
