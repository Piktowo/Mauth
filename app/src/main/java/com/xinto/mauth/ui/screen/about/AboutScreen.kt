package com.xinto.mauth.ui.screen.about

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xinto.mauth.BuildConfig
import com.xinto.mauth.R
import com.xinto.mauth.ui.component.MauthCard
import com.xinto.mauth.ui.component.MauthScreenColumn
import com.xinto.mauth.ui.component.MauthSmallTitle
import com.xinto.mauth.ui.component.MauthTopBar
import com.xinto.mauth.ui.component.rememberUriHandler
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    val uriHandler = rememberUriHandler()
    BackHandler(onBack = onBack)
    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            MauthTopBar(
                title = stringResource(R.string.about_title),
                scrollBehavior = scrollBehavior,
                onBack = onBack,
            )
        }
    ) { innerPadding ->
        MauthScreenColumn(scrollBehavior = scrollBehavior, innerPadding = innerPadding) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(24.dp)),
                        tint = Color.Unspecified,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MiuixTheme.textStyles.title1,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                        style = MiuixTheme.textStyles.body1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                MauthCard {
                    BasicComponent(
                        title = stringResource(R.string.app_name),
                        summary = stringResource(R.string.about_app_description),
                    )
                }

                MauthSmallTitle(
                    text = stringResource(R.string.about_section_links),
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 4.dp),
                )
                MauthCard {
                    AboutLink.defaultLinks.forEach { link ->
                        SuperArrow(
                            title = stringResource(link.title),
                            summary = link.url,
                            onClick = { uriHandler.openUrl(link.url) },
                        )
                    }
                }
            }
        }
    }
}