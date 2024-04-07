import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.Navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.screen.start.StartScreen

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
    val colors = when (isSystemInDarkTheme()) {
        true -> darkColors(
            primary = Color.White,
            secondary = Color(0xFF303030),
            background = Color(0xFF404040)
        )

        false -> lightColors(
            primary = Color.Black,
            secondary = Color(0xFFCCCCCC),
            background = Color.White
        )
    }

    val typography = MaterialTheme.typography.copy(
//            defaultFontFamily = FontFamily(Font(Res.font.komika_slim)),
        h1 = TextStyle(
            color = colors.primary,
            fontWeight = FontWeight.Normal,
            fontSize = 40.sp
        ),
        body1 = TextStyle(
            color = colors.primary,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
    )

    MaterialTheme(
        colors = colors,
        typography = typography
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            Navigator(
                screen = StartScreen()
            )
        }

    }
}