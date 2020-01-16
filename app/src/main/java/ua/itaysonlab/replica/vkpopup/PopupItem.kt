package ua.itaysonlab.replica.vkpopup

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class PopupItem(
        @DrawableRes val icon: Int,
        @StringRes val title: Int,
        val group: Int,
        val id: String
)