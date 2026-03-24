package com.xinto.mauth.ui.screen.export

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xinto.mauth.R
import com.xinto.mauth.domain.account.model.DomainExportAccount
import com.xinto.mauth.ui.component.MauthTopBar
import com.xinto.mauth.ui.component.UriImage
import com.xinto.mauth.ui.screen.export.component.ZxingQrImage
import com.xinto.mauth.ui.theme.MauthUiTokens
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.util.UUID

@Composable
fun ExportScreen(
    onBackNavigate: () -> Unit,
    accounts: List<UUID>
) {
    BackHandler(onBack = onBackNavigate)
    val viewModel: ExportViewModel = koinViewModel {
        parametersOf(accounts)
    }

    val mode by viewModel.mode.collectAsStateWithLifecycle()
    val state by viewModel.state.collectAsStateWithLifecycle()

    ExportScreen(
        onBackNavigate = onBackNavigate,
        onCopyUrlToClipboard = {
            viewModel.copyUrlToClipboard(label = it.label, url = it.url)
        },
        mode = mode,
        onModeSelect = viewModel::switchMode,
        state = state,
    )
}

@Composable
fun ExportScreen(
    onBackNavigate: () -> Unit,
    onCopyUrlToClipboard: (DomainExportAccount) -> Unit,
    state: ExportScreenState,
    mode: ExportMode,
    onModeSelect: (ExportMode) -> Unit
) {
    val scrollBehavior = MiuixScrollBehavior()
    Scaffold(
        topBar = {
            MauthTopBar(
                title = stringResource(R.string.export_title),
                scrollBehavior = scrollBehavior,
                onBack = onBackNavigate,
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.tight)
        ) {
            when (state) {
                is ExportScreenState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is ExportScreenState.Success -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = MauthUiTokens.Space.screenHorizontal,
                                vertical = MauthUiTokens.Space.tight / 2,
                            ),
                        horizontalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.tight),
                    ) {
                        ExportMode.entries.forEach { m ->
                            Button(
                                modifier = Modifier.weight(1f),
                                onClick = { onModeSelect(m) },
                                colors = if (mode == m)
                                    ButtonDefaults.buttonColorsPrimary()
                                else
                                    ButtonDefaults.buttonColors(),
                                cornerRadius = MauthUiTokens.Radius.button,
                            ) {
                                Text(
                                    text = m.name,
                                    color = if (mode == m)
                                        MiuixTheme.colorScheme.onPrimary
                                    else
                                        MiuixTheme.colorScheme.onBackground,
                                )
                            }
                        }
                    }

                    when (mode) {
                        ExportMode.Batch -> BatchExports(
                            modifier = Modifier.weight(1f),
                            uris = state.batchUris
                        )
                        ExportMode.Individual -> IndividualExports(
                            modifier = Modifier.weight(1f),
                            accounts = state.individualAccounts,
                            onCopyUrlToClipboard = onCopyUrlToClipboard
                        )
                    }
                }
                is ExportScreenState.Empty -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.tight, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            modifier = Modifier.size(72.dp),
                            painter = painterResource(R.drawable.ic_no_accounts),
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.onBackground,
                        )
                        Text(
                            text = stringResource(R.string.export_state_empty),
                            style = MiuixTheme.textStyles.headline2,
                        )
                    }
                }
                is ExportScreenState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_error),
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.onBackground,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BatchExports(
    modifier: Modifier = Modifier,
    uris: List<String>
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.tight, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val pagerState = rememberPagerState { uris.size }
        HorizontalPager(pagerState) {
            ZxingQrImage(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = MauthUiTokens.Space.sectionTitleStart),
                data = uris[it],
                size = 512,
                backgroundColor = Color.Transparent,
                contentColor = MiuixTheme.colorScheme.onBackground
            )
        }
        if (uris.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(uris.size) {
                    val selectedColor = MiuixTheme.colorScheme.primary
                    val unselectedColor = MiuixTheme.colorScheme.secondaryContainer
                    Box(
                        modifier = Modifier
                            .drawBehind {
                                drawCircle(if (pagerState.currentPage == it) selectedColor else unselectedColor)
                            }
                            .animateContentSize()
                            .size(if (pagerState.currentPage == it) 16.dp else 12.dp)
                    )
                }
            }
        }
        Text(
            modifier = Modifier.padding(MauthUiTokens.Space.groupGap + MauthUiTokens.Space.tight),
            text = stringResource(R.string.export_batch_hint),
            style = MiuixTheme.textStyles.title3,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun IndividualExports(
    modifier: Modifier = Modifier,
    accounts: List<DomainExportAccount>,
    onCopyUrlToClipboard: (DomainExportAccount) -> Unit
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier,
        columns = StaggeredGridCells.Adaptive(150.dp),
        contentPadding = PaddingValues(MauthUiTokens.Space.regular),
        verticalItemSpacing = MauthUiTokens.Space.regular,
        horizontalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.regular)
    ) {
        items(items = accounts, key = { it.id }) { account ->
            Surface(
                onClick = { onCopyUrlToClipboard(account) },
                color = MiuixTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(MauthUiTokens.Radius.cardCompact),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(MauthUiTokens.Radius.cardCompact),
                        color = MiuixTheme.colorScheme.tertiaryContainer,
                    ) {
                        ZxingQrImage(
                            modifier = Modifier.fillMaxSize(),
                            data = account.url,
                            backgroundColor = Color.Transparent,
                            contentColor = MiuixTheme.colorScheme.onBackground
                        )
                    }
                    Row(
                        modifier = Modifier.padding(MauthUiTokens.Space.tight),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.tight)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(MauthUiTokens.Radius.cardCompact),
                            color = MiuixTheme.colorScheme.primaryContainer,
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (account.icon != null) {
                                    UriImage(uri = account.icon)
                                } else {
                                    Text(
                                        text = account.shortLabel,
                                        style = MiuixTheme.textStyles.title3,
                                    )
                                }
                            }
                        }
                        Column {
                            if (account.issuer.isNotEmpty()) {
                                Text(
                                    text = account.issuer,
                                    style = MiuixTheme.textStyles.footnote1,
                                )
                            }
                            Text(
                                text = account.label,
                                style = MiuixTheme.textStyles.body1,
                            )
                        }
                    }
                }
            }
        }
    }
}
