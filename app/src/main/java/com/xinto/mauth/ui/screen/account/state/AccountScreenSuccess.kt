package com.xinto.mauth.ui.screen.account.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xinto.mauth.ui.component.form.form
import com.xinto.mauth.ui.screen.account.AccountForm
import top.yukonga.miuix.kmp.utils.overScrollVertical

@Composable
fun AccountScreenSuccess(form: AccountForm) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .overScrollVertical(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 16.dp,
            end = 16.dp,
            bottom = 16.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding(),
        ),
        columns = GridCells.Fixed(2),
        overscrollEffect = null,
    ) {
        form(form)
    }
}