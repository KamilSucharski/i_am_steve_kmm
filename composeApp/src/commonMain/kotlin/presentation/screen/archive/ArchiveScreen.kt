package presentation.screen.archive

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import iamsteve.composeapp.generated.resources.Res
import iamsteve.composeapp.generated.resources.comic_archive_format
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.screen.comic.ComicGalleryScreen

class ArchiveScreen : Screen {

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    @Preview
    override fun Content() {
        val screenModel = rememberScreenModel { ArchiveScreenModel() }
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(Unit) {
            screenModel.downloadComics()
        }

        val state by screenModel.state.collectAsState()

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(state.comics.size) {
                val comic = state.comics[it]
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navigator.popUntilRoot()
                            navigator.replace(ComicGalleryScreen(initialComicIndex = it))
                        }
                ) {
                    Text(
                        modifier = Modifier.padding(12.dp),
                        text = stringResource(Res.string.comic_archive_format, comic.number, comic.title, comic.date),
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }
    }

}