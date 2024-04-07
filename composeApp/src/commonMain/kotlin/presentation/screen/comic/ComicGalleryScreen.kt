package presentation.screen.comic

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import iamsteve.composeapp.generated.resources.Res
import iamsteve.composeapp.generated.resources.comic_title_format
import iamsteve.composeapp.generated.resources.ic_archive
import iamsteve.composeapp.generated.resources.ic_chevron_left
import iamsteve.composeapp.generated.resources.ic_chevron_right
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.screen.archive.ArchiveScreen
import util.platform.toImageBitmap

class ComicGalleryScreen(
    private val initialComicIndex: Int = -1
) : Screen {

    @OptIn(ExperimentalResourceApi::class, ExperimentalFoundationApi::class)
    @Composable
    @Preview
    override fun Content() {
        val screenModel = rememberScreenModel { ComicGalleryScreenModel() }
        val coroutineScope = rememberCoroutineScope()
        val navigator = LocalNavigator.currentOrThrow
        val state by screenModel.state.collectAsState()
        val pagerState = rememberPagerState(
            pageCount = {
                state.comics.size
            }
        )

        LaunchedEffect(Unit) {
            screenModel
                .scrollToComicTrigger
                .onEach(pagerState::scrollToPage)
                .launchIn(coroutineScope)

            snapshotFlow { pagerState.currentPage }
                .onEach(screenModel::onPageChanged)
                .launchIn(coroutineScope)

            screenModel.downloadComics(initialComicIndex)
        }

        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = pagerState
            ) {
                val comic = state.comics[it]
                val defaultImageBitmap = ImageBitmap(width = 1, height = 1)
                var panel1 by remember { mutableStateOf(defaultImageBitmap) }
                var panel2 by remember { mutableStateOf(defaultImageBitmap) }
                var panel3 by remember { mutableStateOf(defaultImageBitmap) }
                var panel4 by remember { mutableStateOf(defaultImageBitmap) }
                remember {
                    CoroutineScope(Dispatchers.IO).launch {
                        val comicPanels = screenModel.getComicPanels(comic)
                        panel1 = comicPanels.panel1.toImageBitmap()
                        panel2 = comicPanels.panel2.toImageBitmap()
                        panel3 = comicPanels.panel3.toImageBitmap()
                        panel4 = comicPanels.panel4.toImageBitmap()
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        bitmap = panel1,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        bitmap = panel2,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        bitmap = panel3,
                        contentDescription = null
                    )

                    Spacer(
                        modifier = Modifier.height(4.dp)
                    )

                    Image(
                        modifier = Modifier.fillMaxWidth(),
                        bitmap = panel4,
                        contentDescription = null
                    )

                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(Res.string.comic_title_format, comic.number, comic.title),
                        style = MaterialTheme.typography.body1
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(MaterialTheme.colors.secondary)
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_chevron_left),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .alpha(if (state.previousButtonVisible) 1f else 0f)
                        .clickable(enabled = state.previousButtonVisible) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(page = pagerState.currentPage - 1)
                            }
                        },
                    contentScale = ContentScale.FillHeight,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
                )

                Image(
                    painter = painterResource(Res.drawable.ic_archive),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .clickable {
                            navigator.push(ArchiveScreen())
                        },
                    contentScale = ContentScale.FillHeight,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
                )

                Image(
                    painter = painterResource(Res.drawable.ic_chevron_right),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .alpha(if (state.nextButtonVisible) 1f else 0f)
                        .clickable(enabled = state.nextButtonVisible) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(page = pagerState.currentPage + 1)
                            }
                        },
                    contentScale = ContentScale.FillHeight,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.primary)
                )
            }
        }
    }

}