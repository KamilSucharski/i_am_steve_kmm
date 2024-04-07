import androidx.compose.ui.window.ComposeUIViewController
import util.Initializer

fun MainViewController() = ComposeUIViewController {
    Initializer.initialize()
    App()
}