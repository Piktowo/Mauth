package com.xinto.mauth.ui.screen.backup

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.webdav_title)) },
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = url,
                        onValueChange = onUrlChange,
                        label = { Text(stringResource(R.string.webdav_url)) },
                        placeholder = { Text(stringResource(R.string.webdav_url_hint)) },
                        singleLine = true,
                    )
                }
                item {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = username,
                        onValueChange = onUsernameChange,
                        label = { Text(stringResource(R.string.webdav_username)) },
                        singleLine = true,
                    )
                }
                item {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text(stringResource(R.string.webdav_password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                    )
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onBackup
                        ) {
                            Text(stringResource(R.string.webdav_backup))
                        }
                        OutlinedButton(
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
