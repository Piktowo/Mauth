package com.xinto.mauth.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xinto.mauth.ui.theme.MauthUiTokens
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun TwoPaneCard(
    selected: Boolean,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    topContent: @Composable () -> Unit,
    bottomContent: @Composable () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val cornerRadius by animateDpAsState(
        targetValue = if (selected) MauthUiTokens.Radius.cardCompact else MauthUiTokens.Radius.cardRegular,
    )
    Card(
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick,
        ),
        cornerRadius = cornerRadius,
        insideMargin = PaddingValues(MauthUiTokens.Space.regular),
    ) {
        Column {
            topContent()
            AnimatedVisibility(
                visible = expanded,
            ) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = MauthUiTokens.Space.regular),
                        color = MiuixTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                    bottomContent()
                }
            }
        }
    }
}