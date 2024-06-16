import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bidyut.tech.bhandar.example.IpRepository
import com.bidyut.tech.bhandar.example.MainViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import kotlinx.browser.document
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        val viewModel = viewModel {
            MainViewModel(
                repository = IpRepository(
                    httpClient = HttpClient(Js),
                    memoryStore = MutableStateFlow(null),
                )
            )
        }
        App(viewModel)
    }
}
