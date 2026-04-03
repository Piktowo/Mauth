package com.xinto.mauth.ui.screen.home.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.xinto.mauth.R
import com.xinto.mauth.core.settings.model.SortSetting
import com.xinto.mauth.ui.screen.home.HomeMoreMenu
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.extra.SuperListPopup
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun HomeScaffold(
    modifier: Modifier = Modifier,
    scrollBehavior: ScrollBehavior,
    isSelectionActive: Boolean = false,
    activeSortSetting: SortSetting,
    onActiveSortChange: (SortSetting) -> Unit,
    onAdd: () -> Unit,
    onCancelSelection: () -> Unit,
    onDeleteSelected: () -> Unit,
    onExportSelected: () -> Unit,
    onMenuNavigate: (HomeMoreMenu) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val isMoreActionsVisible = remember { mutableStateOf(false) }
    val isSortVisible = remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = stringResource(R.string.app_name),
                modifier = Modifier.statusBarsPadding(),
                color = MiuixTheme.colorScheme.surface,
                // 选择模式下固定顶栏，不折叠
                scrollBehavior = if (!isSelectionActive) scrollBehavior else null,
                // 选择模式：导航图标区域放「取消」按钮（MIUI 标准行为）
                navigationIcon = if (isSelectionActive) {
                    {
                        IconButton(onClick = onCancelSelection) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = null,
                                tint = MiuixTheme.colorScheme.onBackground,
                            )
                        }
                    }
                } else {
                    {}
                },
                actions = {
                    if (isSelectionActive) {
                        // 选择模式：删除 + 导出
                        IconButton(onClick = onDeleteSelected) {
                            Icon(
                                painter = painterResource(R.drawable.ic_delete_forever),
                                contentDescription = null,
                            )
                        }
                        IconButton(onClick = onExportSelected) {
                            Icon(
                                painter = painterResource(R.drawable.ic_export),
                                contentDescription = null,
                            )
                        }
                    } else {
                        // 普通模式：更多菜单 + 排序
                        Box {
                            IconButton(onClick = { isMoreActionsVisible.value = true }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_more_vert),
                                    contentDescription = null,
                                )
                            }
                            SuperListPopup(
                                show = isMoreActionsVisible,
                                alignment = PopupPositionProvider.Align.End,
                                onDismissRequest = { isMoreActionsVisible.value = false },
                            ) {
                                ListPopupColumn {
                                    HomeMoreMenu.entries.forEachIndexed { index, menu ->
                                        DropdownImpl(
                                            text = stringResource(menu.title),
                                            optionSize = HomeMoreMenu.entries.size,
                                            isSelected = false,
                                            onSelectedIndexChange = {
                                                isMoreActionsVisible.value = false
                                                onMenuNavigate(HomeMoreMenu.entries[it])
                                            },
                                            index = index,
                                        )
                                    }
                                }
                            }
                        }
                        Box {
                            IconButton(onClick = { isSortVisible.value = true }) {
                                Icon(
                                    painter = painterResource(R.drawable.ic_sort),
                                    contentDescription = null,
                                )
                            }
                            SuperListPopup(
                                show = isSortVisible,
                                alignment = PopupPositionProvider.Align.End,
                                onDismissRequest = { isSortVisible.value = false },
                            ) {
                                ListPopupColumn {
                                    SortSetting.entries.forEachIndexed { index, sort ->
                                        val resource = when (sort) {
                                            SortSetting.DateAsc -> R.string.home_sort_date_ascending
                                            SortSetting.DateDesc -> R.string.home_sort_date_descending
                                            SortSetting.LabelAsc -> R.string.home_sort_label_ascending
                                            SortSetting.LabelDesc -> R.string.home_sort_label_descending
                                            SortSetting.IssuerAsc -> R.string.home_sort_issuer_ascending
                                            SortSetting.IssuerDesc -> R.string.home_sort_issuer_descending
                                        }
                                        DropdownImpl(
                                            text = stringResource(resource),
                                            optionSize = SortSetting.entries.size,
                                            isSelected = activeSortSetting == sort,
                                            onSelectedIndexChange = {
                                                isSortVisible.value = false
                                                onActiveSortChange(SortSetting.entries[it])
                                            },
                                            index = index,
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
            )
        },
        bottomBar = {
            if (!isSelectionActive) {
                Surface(
                    color = MiuixTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            onClick = onAdd,
                            color = MiuixTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 24.dp, vertical = 14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_add),
                                        contentDescription = null,
                                        tint = MiuixTheme.colorScheme.onPrimary,
                                    )
                                    Text(
                                        text = stringResource(R.string.home_add_account),
                                        color = MiuixTheme.colorScheme.onPrimary,
                                        style = MiuixTheme.textStyles.body1
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        },
        content = content,
    )
}
