package ua.itaysonlab.replica.vkpopup

import android.graphics.drawable.Drawable
import androidx.annotation.UiThread

@UiThread
data class ActionStyle(
        var optionBackground: Drawable?,
        var paddingStart: Int,
        var paddingEnd: Int,
        var iconSpace: Int,
        var iconTint: Int?,
        var textSize: Int,
        var textColor: Int
)