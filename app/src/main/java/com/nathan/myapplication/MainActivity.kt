package com.nathan.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.nathan.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
               Foo()
            }
        }
    }
}


private val red = lerp(Color.Black, Color.Red, 0.75f)
private val blue = lerp(Color.Black, Color.Blue, 0.75f)
private val darkBlue = lerp(Color.Black, Color.Blue, 0.5f)

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun Foo() {
    val headerHeight = 300.dp
    val headerHeightPx = with(LocalDensity.current) { headerHeight.roundToPx().toFloat() }
    var headerOffsetPx by remember { mutableFloatStateOf(0f) }

    val scrollState = rememberScrollState()
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val consumedY = available.y.coerceIn(
                    -headerOffsetPx - headerHeightPx,
                    -headerOffsetPx
                ).also { headerOffsetPx += it }
                return Offset(0f, consumedY)
            }
        }
    }
    Column(
        modifier = Modifier
            .nestedScroll(nestedScrollConnection)
            .scrollable(scrollState, Orientation.Vertical)
    ) {
        val boxHeight = headerHeight + with(LocalDensity.current) { headerOffsetPx.toDp() }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(boxHeight),
            contentAlignment = Alignment.BottomStart
        ) {
            Text(
                text = "Header",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
                    .background(red)
            )
        }
        val pages = 20
        val pagerState = rememberPagerState(0) { pages }
        val scope = rememberCoroutineScope()
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            modifier = Modifier.fillMaxWidth()
        ) {
            (0 until pages).forEach { page ->
                Tab(
                    selected = page == pagerState.currentPage,
                    onClick = { scope.launch { pagerState.animateScrollToPage(page) } },
                    text = { Text(text = "Page $page") }
                )
            }
        }
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            val gridState = rememberLazyGridState()
            val gridConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        return Offset(0f, -gridState.dispatchRawDelta(-available.y))
                    }
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(gridConnection),
                state = gridState
            ) {
                items(40) { item ->
                    val background = remember {
                        val rem4 = item % 4
                        when {
                            rem4 == 1 || rem4 == 2 -> blue
                            else -> darkBlue
                        }
                    }
                    Text(
                        text = "Item $item",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(background),
                        color = Color.LightGray
                    )
                }
            }
        }
    }
}