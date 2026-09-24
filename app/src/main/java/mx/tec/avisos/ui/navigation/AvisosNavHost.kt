package mx.tec.avisos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import mx.tec.avisos.domain.Sesion
import mx.tec.avisos.ui.screens.AvisosScreen
import mx.tec.avisos.ui.screens.PublicarScreen
import mx.tec.avisos.ui.state.AppViewModelProvider
import mx.tec.avisos.ui.state.AvisosViewModel
import mx.tec.avisos.ui.state.PublicarViewModel

/** Las pantallas que existen SOLO con sesión. Recibe la sesión ya resuelta: aquí nunca es null. */
@Composable
fun AvisosNavHost(sesion: Sesion, onSalir: () -> Unit) {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Route.AVISOS) {

        composable(Route.AVISOS) {
            val viewModel: AvisosViewModel = viewModel(factory = AppViewModelProvider.Factory)

            // Se vuelve a pedir cada vez que la pantalla entra: al abrir la app
            // y al volver de publicar, para que el aviso nuevo aparezca.
            LaunchedEffect(Unit) { viewModel.cargar() }

            AvisosScreen(
                sesion = sesion,
                avisos = viewModel.avisos,
                onRecargar = { viewModel.cargar() },
                onPublicar = { nav.navigate(Route.PUBLICAR) },
                onSalir = onSalir
            )
        }

        composable(Route.PUBLICAR) {
            val viewModel: PublicarViewModel = viewModel(factory = AppViewModelProvider.Factory)

            PublicarScreen(
                sesion = sesion,
                uiState = viewModel.uiState,
                onTituloChange = viewModel::onTituloChange,
                onCuerpoChange = viewModel::onCuerpoChange,
                // El popBackStack ocurre cuando el servidor aceptó, no antes.
                onPublicar = { viewModel.publicar { nav.popBackStack() } },
                onCancelar = { nav.popBackStack() }
            )
        }
    }
}
