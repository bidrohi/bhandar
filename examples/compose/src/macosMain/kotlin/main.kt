import androidx.compose.ui.window.Window
import com.bidyut.tech.bhandar.example.IpRepository
import com.bidyut.tech.bhandar.example.MainViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import kotlinx.coroutines.flow.MutableStateFlow
import platform.AppKit.NSApp
import platform.AppKit.NSApplication

fun main() {
    NSApplication.sharedApplication()
    Window(
        title = "Bhandar Example",
    ) {
        val viewModel = MainViewModel(
            repository = IpRepository(
                httpClient = HttpClient(Darwin),
                memoryStore = MutableStateFlow(null),
            )
        )
        App(viewModel)
    }
    NSApp?.run()
}
