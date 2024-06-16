import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bidyut.tech.bhandar.ReadResult
import com.bidyut.tech.bhandar.example.MainViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    viewModel: MainViewModel
) {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val ipResult = viewModel.ipResult.collectAsState(ReadResult.Loading())

            Text(
                text = when (val r = ipResult.value) {
                    is ReadResult.Loading -> "Loading..."
                    is ReadResult.Data -> "IP: ${r.data}"
                    is ReadResult.Error -> "Error: ${r.errorMessage}"
                },
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )

            Button(
                modifier = Modifier.requiredWidth(400.dp),
                onClick = {
                    viewModel.mode.value = MainViewModel.FetchMode.CACHE_ONLY
                }
            ) {
                Text("Show from cache only")
            }

            Button(
                modifier = Modifier.requiredWidth(400.dp),
                onClick = {
                    viewModel.mode.value = MainViewModel.FetchMode.CACHE_WITH_FALLBACK
                }
            ) {
                Text("Show from cache (fetch is missing)")
            }

            Button(
                modifier = Modifier.requiredWidth(400.dp),
                onClick = {
                    viewModel.mode.value = MainViewModel.FetchMode.CACHE_REFRESH
                }
            ) {
                Text("Show from cache and refresh from network")
            }

            Button(
                modifier = Modifier.requiredWidth(400.dp),
                onClick = {
                    viewModel.mode.value = MainViewModel.FetchMode.FRESH
                }
            ) {
                Text("Show from network (show cache on failure)")
            }
        }
    }
}
