package com.xinto.mauth.ui.screen.account

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xinto.mauth.R
import com.xinto.mauth.domain.account.model.DomainAccountInfo
import com.xinto.mauth.ui.theme.MauthUiTokens
import com.xinto.mauth.ui.screen.account.component.AccountExitDialog
import com.xinto.mauth.ui.screen.account.state.AccountScreenError
import com.xinto.mauth.ui.screen.account.state.AccountScreenLoading
import com.xinto.mauth.ui.screen.account.state.AccountScreenSuccess
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import java.util.UUID

@Composable
fun AddAccountScreen(
    prefilled: DomainAccountInfo,
    onExit: () -> Unit
) {
    val viewModel: AccountViewModel = koinViewModel {
        parametersOf(AccountViewModelParams.Prefilled(prefilled))
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    AccountScreen(
        title = stringResource(R.string.account_title_add),
        state = state,
        onSave = {
            val account = (state as? AccountScreenState.Success)?.form?.validate()
            if (account != null) {
                viewModel.saveData(account)
                onExit()
            }
        },
        onExit = onExit
    )
}

@Composable
fun EditAccountScreen(
    id: UUID,
    onExit: () -> Unit
) {
    val viewModel: AccountViewModel = koinViewModel {
        parametersOf(AccountViewModelParams.Id(id))
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    AccountScreen(
        title = stringResource(R.string.account_title_edit),
        state = state,
        onSave = {
            val account = (state as? AccountScreenState.Success)?.form?.validate()
            if (account != null) {
                viewModel.saveData(account)
                onExit()
            }
        },
        onExit = onExit
    )
}

@Composable
fun AccountScreen(
    title: String,
    state: AccountScreenState,
    onSave: () -> Unit,
    onExit: () -> Unit,
) {
    val showExitDialog = remember { mutableStateOf(false) }
    val hasChanges by remember(state) {
        derivedStateOf {
            state is AccountScreenState.Success && !state.form.isSame()
        }
    }
    BackHandler {
        if (hasChanges) showExitDialog.value = true else onExit()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = title,
                color = MiuixTheme.colorScheme.surface,
                navigationIcon = {
                    IconButton(onClick = {
                        if (hasChanges) showExitDialog.value = true else onExit()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.onBackground,
                        )
                    }
                },
                actions = {
                    Surface(
                        onClick = onSave,
                        enabled = state is AccountScreenState.Success,
                        color = MiuixTheme.colorScheme.primary,
                        shape = RoundedCornerShape(MauthUiTokens.Radius.button),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.account_actions_save),
                                color = MiuixTheme.colorScheme.onPrimary,
                                style = MiuixTheme.textStyles.body1
                            )
                        }
                    }
                },
                scrollBehavior = null,
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is AccountScreenState.Loading -> AccountScreenLoading()
                is AccountScreenState.Success -> AccountScreenSuccess(form = state.form)
                is AccountScreenState.Error -> AccountScreenError()
            }
        }
    }
    AccountExitDialog(
        show = showExitDialog,
        onConfirm = { onExit() },
    )
}