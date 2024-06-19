import com.bidyut.tech.bhandar.example.IpRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

fun main() {
    println("Bhandar Library - JVM CLI Example")
    println("======================================")

    val bhandar = IpRepository(
        httpClient = HttpClient(CIO),
        memoryStore = MutableStateFlow(null),
    )

    val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    var lastJob: Job? = null
    var shouldKeepGoing = true
    do {
        println()
        println("1. Get value from cache, with no fallback to network")
        println("2. Get value from cache, but fallback to network")
        println("3. Get value from cache, and refresh from network")
        println("4. Get value from network, but fallback to cache")
        println("0. Exit")
        print("Enter your choice: ")
        val choice = readlnOrNull() ?: ""
        lastJob?.cancel()
        when (choice) {
            "0" -> {
                println("Bye bye!")
                shouldKeepGoing = false
            }

            "1" -> {
                lastJob = scope.launch {
                    bhandar.cached(1, refresh = false, fetchIfMissing = false).collect {
                        println(it)
                    }
                }
            }

            "2" -> {
                lastJob = scope.launch {
                    bhandar.cached(2, refresh = false).collect {
                        println(it)
                    }
                }
            }

            "3" -> {
                lastJob = scope.launch {
                    bhandar.cached(3, refresh = true).collect {
                        println(it)
                    }
                }
            }

            "4" -> {
                lastJob = scope.launch {
                    bhandar.fresh(4).collect {
                        println(it)
                    }
                }
            }

            else -> {
                println("Invalid choice. Please try again.")
            }
        }
    } while (shouldKeepGoing)
}
