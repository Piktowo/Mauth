package com.xinto.mauth.ui.component.pinboard

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xinto.mauth.ui.theme.MauthTheme
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

/** 与 Material3 WindowHeightSizeClass.Compact 阈值对齐（约 <480dp 高，如横屏手机）。 */
private const val CompactHeightDpBreakpoint = 480

/** 与 Material3 WindowWidthSizeClass 非 Compact 下限对齐（≥600dp）。 */
private const val MediumWidthDpBreakpoint = 600

@Composable
fun PinScaffold(
    modifier: Modifier = Modifier,
    state: PinBoardState = rememberPinBoardState(),
    topBar: @Composable () -> Unit = {},
    description: (@Composable () -> Unit)? = null,
    error: Boolean = false,
    codeLength: Int,
) {
    val configuration = LocalConfiguration.current
    val useCompactHeightLayout = configuration.screenHeightDp < CompactHeightDpBreakpoint
    val useAtLeastMediumWidth = configuration.screenWidthDp >= MediumWidthDpBreakpoint

    Scaffold(
        modifier = modifier,
        topBar = topBar,
    ) { paddingValues ->
        if (useCompactHeightLayout) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (description != null) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CompositionLocalProvider(
                                LocalTextStyle provides MiuixTheme.textStyles.headline2.copy(
                                    textAlign = TextAlign.Center
                                )
                            ) {
                                description()
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                    PinDisplay(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        length = codeLength,
                        error = error,
                    )
                }
                PinBoard(
                    modifier = Modifier.widthIn(max = 250.dp),
                    horizontalButtonSpace = 16.dp,
                    minButtonSize = PinButtonDefaults.PinButtonSmallMinSize,
                    state = state
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(start = 40.dp, top = 40.dp, end = 40.dp, bottom = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = if (useAtLeastMediumWidth) 400.dp else Dp.Unspecified),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (description != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CompositionLocalProvider(
                                LocalTextStyle provides MiuixTheme.textStyles.headline2.copy(
                                    textAlign = TextAlign.Center
                                )
                            ) {
                                description()
                            }
                        }
                    }
                    PinDisplay(
                        modifier = Modifier.fillMaxWidth(),
                        length = codeLength,
                        error = error,
                    )
                    PinBoard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .padding(top = 32.dp),
                        state = state
                    )
                }
            }
        }
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun PinScaffold_WithDescription() {
    MauthTheme {
        PinScaffold(
            description = {
                Text("Enter PIN")
            },
            codeLength = 5,
        )
    }
}

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
fun PinScaffold_WithoutDescription() {
    MauthTheme {
        PinScaffold(
            codeLength = 5,
        )
    }
}
