package com.xinto.mauth.ui.screen.backup

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xinto.mauth.R
import org.koin.androidx.compose.koinViewModel

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
    WebDavBackupScreen(
        onBack = onBack,
        url = url,
        onUrlChange = viewModel::updateUrl,
        username = username,
        onUsernameChange = viewModel::updateUsername,
        password = password,
        onPasswordChange = viewModel::updatePassword,
        isLoading = isLoading,
        onBackup = viewModel::backup,
        onRestore = viewModel::restore,
        onLocalBackup = { exportLauncher.launch("mauth_backup.txt") },
        onLocalRestore = { importLauncher.launch(arrayOf("text/plain", "*/*")) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebDavBackupScreen(
    onBack: () -> Unit,
    url: String,
    onUrlChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    onBackup: () -> Unit,
    onRestore: () -> Unit,
    onLocalBackup: () -> Unit,
    onLocalRestore: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.backup_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_back),
                            contentDescription = null
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.local_backup_header),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = onLocalBackup
                            ) {
                                Text(stringResource(R.string.local_backup))
                            }
                            FilledTonalButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = onLocalRestore
                            ) {
                                Text(stringResource(R.string.local_restore))
                            }
                        }
                    }
                }
                item {
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.webdav_header),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                            )
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = url,
                                onValueChange = onUrlChange,
                                label = { Text(stringResource(R.string.webdav_url)) },
                                placeholder = { Text(stringResource(R.string.webdav_url_hint)) },
                                singleLine = true,
                            )
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = username,
                                onValueChange = onUsernameChange,
                                label = { Text(stringResource(R.string.webdav_username)) },
                                singleLine = true,
                            )
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = password,
                                onValueChange = onPasswordChange,
                                label = { Text(stringResource(R.string.webdav_password)) },
                                visualTransformation = PasswordVisualTransformation(),
                                singleLine = true,
                            )
                            Button(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = onBackup
                            ) {
                                Text(stringResource(R.string.webdav_backup))
                            }
                            FilledTonalButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = onRestore
                            ) {
                                Text(stringResource(R.string.webdav_restore))
                            }
                        }
                    }
                }
            }
        }
    }
}
