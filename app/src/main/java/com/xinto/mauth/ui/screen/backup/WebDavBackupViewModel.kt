package com.xinto.mauth.ui.screen.backup

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.xinto.mauth.R
import com.xinto.mauth.core.backup.WebDavClient
import com.xinto.mauth.core.otp.parser.OtpUriParser
import com.xinto.mauth.core.otp.parser.OtpUriParserResult
import com.xinto.mauth.domain.SettingsRepository
import com.xinto.mauth.domain.account.AccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WebDavBackupViewModel(
    application: Application,
    private val settings: SettingsRepository,
    private val accountRepository: AccountRepository,
    private val otpUriParser: OtpUriParser,
    private val webDavClient: WebDavClient,
) : AndroidViewModel(application) {

    val webDavUrl = settings.getWebDavUrl()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val webDavUsername = settings.getWebDavUsername()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val webDavPassword = settings.getWebDavPassword()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun updateUrl(value: String) {
        viewModelScope.launch { settings.setWebDavUrl(value) }
    }

    fun updateUsername(value: String) {
        viewModelScope.launch { settings.setWebDavUsername(value) }
    }

    fun updatePassword(value: String) {
        viewModelScope.launch { settings.setWebDavPassword(value) }
    }

    fun backup() {
        viewModelScope.launch {
            val url = webDavUrl.value
            if (url.isBlank()) {
                showToast(R.string.webdav_url_required)
                return@launch
            }
            _isLoading.value = true
            val accounts = accountRepository.getAccounts().first()
            val lines = accounts.mapNotNull { account ->
                with(accountRepository) {
                    runCatching { account.toExportAccount().url }.getOrNull()
                }
            }
            val content = lines.joinToString("\n")
            val result = webDavClient.put(
                url = url,
                username = webDavUsername.value,
                password = webDavPassword.value,
                data = content
            )
            _isLoading.value = false
            if (result.isSuccess) {
                showToast(R.string.webdav_backup_success)
            } else {
                val msg = result.exceptionOrNull()?.message ?: ""
                showToast(getApplication<Application>().getString(R.string.webdav_backup_fail, msg))
            }
        }
    }

    fun restore() {
        viewModelScope.launch {
            val url = webDavUrl.value
            if (url.isBlank()) {
                showToast(R.string.webdav_url_required)
                return@launch
            }
            _isLoading.value = true
            val result = webDavClient.get(
                url = url,
                username = webDavUsername.value,
                password = webDavPassword.value,
            )
            _isLoading.value = false
            if (result.isFailure) {
                val msg = result.exceptionOrNull()?.message ?: ""
                showToast(getApplication<Application>().getString(R.string.webdav_restore_fail, msg))
                return@launch
            }
            val content = result.getOrThrow()
            var importedCount = 0
            content.lines().forEach { line ->
                val trimmed = line.trim()
                if (trimmed.isEmpty()) return@forEach
                when (val parsed = otpUriParser.parseOtpUri(trimmed)) {
                    is OtpUriParserResult.Success -> {
                        val accountInfo = with(accountRepository) { parsed.data.toAccountInfo() }
                        accountRepository.putAccount(accountInfo)
                        importedCount++
                    }
                    else -> { /* skip invalid lines */ }
                }
            }
            showToast(
                getApplication<Application>().getString(R.string.webdav_restore_success, importedCount)
            )
        }
    }

    private fun showToast(resId: Int) {
        Toast.makeText(getApplication(), resId, Toast.LENGTH_LONG).show()
    }

    private fun showToast(msg: String) {
        Toast.makeText(getApplication(), msg, Toast.LENGTH_LONG).show()
    }
}
