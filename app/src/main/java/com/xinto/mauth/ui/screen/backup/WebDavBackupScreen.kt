package com.xinto.mauth.ui.screen.backup

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.ButtonDefaults
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
                item {
                    MauthSmallTitle(
                        text = stringResource(R.string.local_backup_header),
                        modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 4.dp),
                    )
                    MauthCard {
                        BasicComponent(
                            title = stringResource(R.string.local_backup),
                            onClick = { exportLauncher.launch("mauth_backup.txt") },
                        )
                        BasicComponent(
                            title = stringResource(R.string.local_restore),
                            onClick = { importLauncher.launch(arrayOf("text/plain", "*/*")) },
                        )
                    }

                    MauthSmallTitle(
                        text = stringResource(R.string.webdav_header),
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 4.dp),
                    )
                    MauthCard {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.webdav_url_supporting),
                                style = MiuixTheme.textStyles.footnote1,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            )
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = url,
                                onValueChange = viewModel::updateUrl,
                                label = stringResource(R.string.webdav_url),
                                maxLines = 1,
                            )
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = username,
                                onValueChange = viewModel::updateUsername,
                                label = stringResource(R.string.webdav_username),
                                maxLines = 1,
                            )
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = password,
                                onValueChange = viewModel::updatePassword,
                                label = stringResource(R.string.webdav_password),
                                visualTransformation = PasswordVisualTransformation(),
                                maxLines = 1,
                            )
                        }
                    }

                    // 操作按钮移到卡片外面，与输入表单分离（符合 Miuix 规范）
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = viewModel::testConnection,
                        ) {
                            Text(stringResource(R.string.webdav_test_connection))
                        }
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = viewModel::backup,
                            colors = ButtonDefaults.buttonColorsPrimary(),
                        ) {
                            Text(
                                text = stringResource(R.string.webdav_backup),
                                color = MiuixTheme.colorScheme.background,
                            )
                        }
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = viewModel::restore,
                        ) {
                            Text(stringResource(R.string.webdav_restore))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}