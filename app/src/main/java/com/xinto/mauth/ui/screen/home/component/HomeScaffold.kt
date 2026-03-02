package com.xinto.mauth.ui.screen.home.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.xinto.mauth.R
import com.xinto.mauth.core.settings.model.SortSetting
import com.xinto.mauth.ui.component.MauthTopBar
import com.xinto.mauth.ui.screen.home.HomeMoreMenu
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.FloatingActionButton
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.extra.SuperListPopup

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
            MauthTopBar(
                title = stringResource(R.string.app_name),
                scrollBehavior = scrollBehavior,
                actions = {
                    AnimatedContent(
                        targetState = isSelectionActive,
                        transitionSpec = {
                            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up) + fadeIn() togetherWith
                                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Up) + fadeOut()
                        },
                        label = "Actions",
                    ) { selectionActive ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (selectionActive) {
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
                        }
                    }
                },
            )
        },
        floatingActionButton = {
            AnimatedContent(
                targetState = isSelectionActive,
                transitionSpec = {
                    scaleIn() + fadeIn() togetherWith scaleOut() + fadeOut()
                },
                label = "FAB",
            ) { selectionActive ->
                if (selectionActive) {
                    FloatingActionButton(onClick = onCancelSelection) {
                        Icon(
                            painter = painterResource(R.drawable.ic_undo),
                            contentDescription = null,
                        )
                    }
                } else {
                    FloatingActionButton(onClick = onAdd) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = null,
                        )
                    }
                }
            }
        },
        content = content,
    )
}
