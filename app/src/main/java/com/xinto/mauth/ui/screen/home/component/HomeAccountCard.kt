package com.xinto.mauth.ui.screen.home.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xinto.mauth.R
import com.xinto.mauth.domain.account.model.DomainAccount
import com.xinto.mauth.domain.otp.model.DomainOtpRealtimeData
import com.xinto.mauth.ui.component.TwoPaneCard
import com.xinto.mauth.ui.component.UriImage
import com.xinto.mauth.ui.theme.MauthUiTokens
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun HomeAccountCard(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onEdit: () -> Unit,
    onCounterClick: () -> Unit,
    onCopyCode: (visible: Boolean) -> Unit,
    account: DomainAccount,
    realtimeData: DomainOtpRealtimeData,
    selected: Boolean,
) {
    var showCode by remember { mutableStateOf(false) }
    TwoPaneCard(
        selected = selected,
        expanded = !selected,
        topContent = {
            AccountInfo(
                icon = {
                    if (account.icon != null) {
                        UriImage(uri = account.icon!!)
                    } else {
                        Text(
                            account.shortLabel, 
                            fontSize = MiuixTheme.textStyles.title3.fontSize,
                            fontWeight = MiuixTheme.textStyles.title3.fontWeight
                        )
                    }
                },
                name = {
                    Text(
                        text = account.label,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        color = MiuixTheme.colorScheme.onBackground,
                        style = MiuixTheme.textStyles.title3,
                    )
                },
                issuer = {
                    if (account.issuer != "") {
                        Text(
                            text = account.issuer,
                            fontSize = MiuixTheme.textStyles.body2.fontSize,
                            color = MiuixTheme.colorScheme.onSurfaceSecondary.copy(alpha = 0.7f),
                        )
                    }
                },
                trailing = {
                    if (selected) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MiuixTheme.colorScheme.primary)
                                .padding(MauthUiTokens.Space.tight / 2)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_check),
                                contentDescription = null,
                                tint = MiuixTheme.colorScheme.onPrimary,
                            )
                        }
                    } else {
                        Surface(
                            onClick = onEdit,
                            color = MiuixTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(MauthUiTokens.Radius.cardCompact),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_edit),
                                    contentDescription = null,
                                    tint = MiuixTheme.colorScheme.onSurfaceSecondary,
                                )
                            }
                        }
                    }
                },
            )
        },
        bottomContent = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.regular)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.regular)
                ) {
                    RealtimeInformation(
                        modifier = Modifier.weight(1f),
                        realtimeData = realtimeData,
                        showCode = showCode,
                        onCounterClick = onCounterClick,
                    )
                    InteractionButtons(
                        showCode = showCode,
                        onShowCodeChange = { showCode = it },
                        onCopyCode = { onCopyCode(showCode) },
                    )
                }
            }
        },
        onClick = onClick,
        onLongClick = onLongClick,
    )
}

@Composable
private fun InteractionButtons(
    showCode: Boolean,
    onShowCodeChange: (Boolean) -> Unit,
    onCopyCode: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.compact)
    ) {
        Surface(
            onClick = { onShowCodeChange(!showCode) },
            color = if (showCode) MiuixTheme.colorScheme.primary
                    else MiuixTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
            shape = RoundedCornerShape(MauthUiTokens.Radius.button),
        ) {
            Box(modifier = Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                if (showCode) {
                    Icon(
                        painter = painterResource(R.drawable.ic_visibility),
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onPrimary,
                    )
                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_visibility_off),
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onSurfaceSecondary,
                    )
                }
            }
        }
        Surface(
            onClick = onCopyCode,
            color = MiuixTheme.colorScheme.primary,
            shape = RoundedCornerShape(MauthUiTokens.Radius.button),
        ) {
            Box(modifier = Modifier.size(44.dp), contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_copy_all),
                    contentDescription = null,
                    tint = MiuixTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

@Composable
private fun RealtimeInformation(
    modifier: Modifier = Modifier,
    realtimeData: DomainOtpRealtimeData,
    showCode: Boolean,
    onCounterClick: () -> Unit,
) {
    val code = remember(showCode, realtimeData.code) {
        Pair(showCode, realtimeData.code)
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.compact),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        when (realtimeData) {
            is DomainOtpRealtimeData.Hotp -> {
                Surface(
                    onClick = onCounterClick,
                    color = MiuixTheme.colorScheme.secondaryContainer,
                    shape = CircleShape,
                ) {
                    Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                        Text(realtimeData.count.toString())
                    }
                }
            }
            is DomainOtpRealtimeData.Totp -> {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    val progress by animateFloatAsState(
                        targetValue = realtimeData.progress,
                        animationSpec = tween(500),
                    )
                    CircularProgressIndicator(
                        progress = progress,
                        size = 48.dp,
                    )
                    Text(realtimeData.countdown.toString())
                }
            }
        }
        AnimatedContent(
            targetState = code,
            transitionSpec = {
                if (initialState.first == targetState.first) {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up) + fadeIn() togetherWith
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up) + fadeOut()
                } else {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down) + fadeIn() togetherWith
                            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down) + fadeOut()
                }
            },
            label = "Code",
        ) { (show, code) ->
            val showAwareCode = if (show) code else "•".repeat(code.length)
            Text(
                text = showAwareCode,
                style = MiuixTheme.textStyles.title2,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 3.sp,
                color = MiuixTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
private fun AccountInfo(
    icon: @Composable () -> Unit,
    name: @Composable () -> Unit,
    issuer: @Composable () -> Unit,
    trailing: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.regular),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = MiuixTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(MauthUiTokens.Radius.cardRegular),
                ),
            contentAlignment = Alignment.Center,
        ) {
            icon()
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.tight / 2),
        ) {
            name()
            issuer()
        }
        trailing()
    }
}
