package com.xinto.mauth.ui.screen.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xinto.mauth.R
import com.xinto.mauth.ui.component.MauthCard
import com.xinto.mauth.ui.component.MauthScreenColumn
import com.xinto.mauth.ui.component.MauthSmallTitle
import com.xinto.mauth.ui.component.MauthTopBar
import com.xinto.mauth.ui.component.rememberBiometricHandler
import com.xinto.mauth.ui.component.rememberBiometricPromptData
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperSwitch

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onSetupPinCode: () -> Unit,
    onDisablePinCode: () -> Unit,
    onThemeNavigate: () -> Unit,
    onWebDavBackupNavigate: () -> Unit,
) {
    val viewModel: SettingsViewModel = koinViewModel()
    val secureMode by viewModel.secureMode.collectAsStateWithLifecycle()
    val pinLock by viewModel.pinLock.collectAsStateWithLifecycle()
    val biometrics by viewModel.biometrics.collectAsStateWithLifecycle()

    val biometricHandler = rememberBiometricHandler(
        onAuthSuccess = viewModel::toggleBiometrics
    )
    val setupPromptData = rememberBiometricPromptData(
        title = stringResource(R.string.settings_biometrics_setup_title),
        negativeButtonText = stringResource(R.string.settings_biometrics_setup_cancel)
    )
    val disablePromptData = rememberBiometricPromptData(
        title = stringResource(R.string.settings_biometrics_disable_title),
        negativeButtonText = stringResource(R.string.settings_biometrics_disable_cancel)
    )

    BackHandler(onBack = onBack)

    val scrollBehavior = MiuixScrollBehavior()
    Scaffold(
        topBar = {
            MauthTopBar(
                title = stringResource(R.string.settings_title),
                scrollBehavior = scrollBehavior,
                onBack = onBack,
            )
        }
    ) { innerPadding ->
        MauthScreenColumn(
            scrollBehavior = scrollBehavior,
            innerPadding = innerPadding,
        ) {
            item {
                MauthSmallTitle(
                    text = stringResource(R.string.settings_category_security),
                    modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 4.dp),
                )
                MauthCard {
                    SuperSwitch(
                        title = stringResource(R.string.settings_prefs_securemode),
                        summary = stringResource(R.string.settings_prefs_securemode_description),
                        checked = secureMode,
                        onCheckedChange = viewModel::updateSecureMode,
                    )
                    SuperSwitch(
                        title = stringResource(R.string.settings_prefs_pincode),
                        summary = stringResource(R.string.settings_prefs_pincode_description),
                        checked = pinLock,
                        onCheckedChange = { checked ->
                            if (checked) onSetupPinCode() else onDisablePinCode()
                        },
                    )
                    if (biometricHandler.canUseBiometrics()) {
                        SuperSwitch(
                            title = stringResource(R.string.settings_prefs_biometrics),
                            checked = biometrics,
                            onCheckedChange = { checked ->
                                val promptData = if (checked) setupPromptData else disablePromptData
                                biometricHandler.requestBiometrics(promptData)
                            },
                            enabled = pinLock,
                        )
                    }
                }

                MauthSmallTitle(
                    text = stringResource(R.string.settings_category_appearance),
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 4.dp),
                )
                MauthCard {
                    SuperArrow(
                        title = stringResource(R.string.settings_prefs_theme),
                        onClick = onThemeNavigate,
                    )
                }

                MauthSmallTitle(
                    text = stringResource(R.string.settings_category_backup),
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 4.dp),
                )
                MauthCard {
                    SuperArrow(
                        title = stringResource(R.string.settings_prefs_webdav_backup),
                        summary = stringResource(R.string.settings_prefs_webdav_backup_description),
                        onClick = onWebDavBackupNavigate,
                    )
                }
            }
        }
    }
}
