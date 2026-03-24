package com.xinto.mauth.ui.screen.backup

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xinto.mauth.R
import com.xinto.mauth.ui.component.MauthCard
import com.xinto.mauth.ui.component.MauthScreenColumn
import com.xinto.mauth.ui.component.MauthSmallTitle
import com.xinto.mauth.ui.component.MauthTopBar
import com.xinto.mauth.ui.theme.MauthUiTokens
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CircularProgressIndicator
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun WebDavBackupScreen(onBack: () -> Unit) {
    val viewModel: WebDavBackupViewModel = koinViewModel()
    val url by viewModel.webDavUrl.collectAsStateWithLifecycle()
    val username by viewModel.webDavUsername.collectAsStateWithLifecycle()
    val password by viewModel.webDavPassword.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri -> uri?.let { viewModel.exportToLocalFile(it) } }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri -> uri?.let { viewModel.importFromLocalFile(it) } }

    BackHandler(onBack = onBack)

    val scrollBehavior = MiuixScrollBehavior()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MauthTopBar(
                title = stringResource(R.string.backup_title),
                scrollBehavior = scrollBehavior,
                onBack = onBack,
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            MauthScreenColumn(scrollBehavior = scrollBehavior, innerPadding = innerPadding) {
                // 本地文件备份分组
                item {
                    MauthSmallTitle(
                        text = stringResource(R.string.local_backup_header),
                        modifier = Modifier.padding(
                            start = MauthUiTokens.Space.sectionTitleStart,
                            top = MauthUiTokens.Space.regular,
                            bottom = MauthUiTokens.Space.tight,
                        ),
                    )
                    MauthCard(modifier = Modifier.padding(horizontal = MauthUiTokens.Space.screenHorizontal)) {
                        Column {
                            BasicComponent(
                                title = stringResource(R.string.local_backup),
                                onClick = { exportLauncher.launch("mauth_backup.txt") },
                                modifier = Modifier.padding(horizontal = MauthUiTokens.Space.regular),
                            )
                            BasicComponent(
                                title = stringResource(R.string.local_restore),
                                onClick = { importLauncher.launch(arrayOf("text/plain", "*/*")) },
                                modifier = Modifier.padding(horizontal = MauthUiTokens.Space.regular),
                            )
                        }
                    }
                }

                // WebDAV 备份分组
                item {
                    MauthSmallTitle(
                        text = stringResource(R.string.webdav_header),
                        modifier = Modifier.padding(
                            start = MauthUiTokens.Space.sectionTitleStart,
                            top = MauthUiTokens.Space.groupGap,
                            bottom = MauthUiTokens.Space.tight,
                        ),
                    )
                    MauthCard(modifier = Modifier.padding(horizontal = MauthUiTokens.Space.screenHorizontal)) {
                        Column(
                            modifier = Modifier.padding(MauthUiTokens.Space.cardContent),
                            verticalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.regular),
                        ) {
                            // WebDAV 说明信息块
                            Card(
                                cornerRadius = MauthUiTokens.Radius.cardCompact,
                                insideMargin = PaddingValues(MauthUiTokens.Space.compact),
                            ) {
                                Text(
                                    text = stringResource(R.string.webdav_url_supporting),
                                    style = MiuixTheme.textStyles.footnote1,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                )
                            }
                            
                            // 输入框组
                            Column(
                                verticalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.compact),
                            ) {
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = url,
                                    onValueChange = viewModel::updateUrl,
                                    label = stringResource(R.string.webdav_url),
                                    cornerRadius = MauthUiTokens.Radius.input,
                                    maxLines = 1,
                                )
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = username,
                                    onValueChange = viewModel::updateUsername,
                                    label = stringResource(R.string.webdav_username),
                                    cornerRadius = MauthUiTokens.Radius.input,
                                    maxLines = 1,
                                )
                                TextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = password,
                                    onValueChange = viewModel::updatePassword,
                                    label = stringResource(R.string.webdav_password),
                                    visualTransformation = PasswordVisualTransformation(),
                                    cornerRadius = MauthUiTokens.Radius.input,
                                    maxLines = 1,
                                )
                            }
                        }
                    }
                }

                // 操作按钮组
                item {
                    Column(
                        modifier = Modifier.padding(
                            start = MauthUiTokens.Space.screenHorizontal,
                            end = MauthUiTokens.Space.screenHorizontal,
                            top = MauthUiTokens.Space.groupGap,
                        ),
                        verticalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.compact),
                    ) {
                        // 主按钮：立即备份
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = viewModel::backup,
                            colors = ButtonDefaults.buttonColorsPrimary(),
                            cornerRadius = MauthUiTokens.Radius.button,
                        ) {
                            Text(
                                text = stringResource(R.string.webdav_backup),
                                color = MiuixTheme.colorScheme.background,
                            )
                        }
                        
                        // 次按钮：测试连接和立即恢复
                        Column(
                            verticalArrangement = Arrangement.spacedBy(MauthUiTokens.Space.tight),
                        ) {
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = viewModel::testConnection,
                                colors = ButtonDefaults.buttonColors(),
                                cornerRadius = MauthUiTokens.Radius.button,
                            ) {
                                Text(stringResource(R.string.webdav_test_connection))
                            }
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = viewModel::restore,
                                colors = ButtonDefaults.buttonColors(),
                                cornerRadius = MauthUiTokens.Radius.button,
                            ) {
                                Text(stringResource(R.string.webdav_restore))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(MauthUiTokens.Space.groupGap + MauthUiTokens.Space.tight))
                    }
                }
            }
        }
    }
}