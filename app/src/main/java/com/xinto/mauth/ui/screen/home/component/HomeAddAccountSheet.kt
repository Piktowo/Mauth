package com.xinto.mauth.ui.screen.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xinto.mauth.R
import com.xinto.mauth.ui.screen.home.HomeAddAccountMenu
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.extra.SuperBottomSheet

@Composable
fun HomeAddAccountSheet(
    show: MutableState<Boolean>,
    onAddAccountNavigate: (HomeAddAccountMenu) -> Unit,
) {
    SuperBottomSheet(
        show = show,
        title = stringResource(R.string.home_addaccount_title),
        onDismissRequest = { show.value = false },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            HomeAddAccountMenu.entries.forEach { menu ->
                BasicComponent(
                    title = stringResource(menu.title),
                    startAction = {
                        Icon(
                            painter = painterResource(menu.icon),
                            contentDescription = null,
                        )
                    },
                    onClick = {
                        show.value = false
                        onAddAccountNavigate(menu)
                    },
                )
            }
        }
    }
}

