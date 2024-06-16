import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bidyut.tech.bhandar.example.IpRepository
import com.bidyut.tech.bhandar.example.MainViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.flow.MutableStateFlow

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Bhandar Example",
    ) {
        val viewModel = viewModel {
            MainViewModel(
                repository = IpRepository(
                    httpClient = HttpClient(CIO),
                    memoryStore = MutableStateFlow(null),
                )
            )
        }
        App(viewModel)
    }
}
