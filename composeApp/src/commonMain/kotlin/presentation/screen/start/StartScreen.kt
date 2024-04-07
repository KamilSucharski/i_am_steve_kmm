package presentation.screen.start

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import iamsteve.composeapp.generated.resources.Res
import iamsteve.composeapp.generated.resources.start_body_with_progress
import iamsteve.composeapp.generated.resources.start_body_without_progress
import iamsteve.composeapp.generated.resources.steve
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.screen.comic.ComicGalleryScreen

class StartScreen : Screen {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    @Preview
    override fun Content() {
        val screenModel = rememberScreenModel { StartScreenModel() }
        val toaster = rememberToasterState()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            screenModel
                .errorFlow
                .onEach(toaster::show)
                .launchIn(screenModel.screenModelScope)

            screenModel
                .navigateToComicGalleryScreenTrigger
                .onEach { navigator.replace(ComicGalleryScreen()) }
                .launchIn(screenModel.screenModelScope)

            screenModel.downloadComics()
        }

        val state by screenModel.state.collectAsState()

        val imageRotation by rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing)
            )
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(Res.drawable.steve),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(
                        fraction = 0.5f
                    )
                    .graphicsLayer {
                        rotationZ = imageRotation
                    },
                contentScale = ContentScale.FillWidth
            )

            Spacer(
                modifier = Modifier.height(
                    height = 32.dp
                )
            )

            Text(
                text = when (state.all >= 0) {
                    true -> stringResource(Res.string.start_body_with_progress, state.done, state.all)
                    false -> stringResource(Res.string.start_body_without_progress)
                },
                style = MaterialTheme.typography.h1,
                textAlign = TextAlign.Center
            )
        }

        Toaster(state = toaster)
    }

}