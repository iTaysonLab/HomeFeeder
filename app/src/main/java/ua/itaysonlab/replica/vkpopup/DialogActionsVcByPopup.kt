package ua.itaysonlab.replica.vkpopup

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.UiThread
import ua.itaysonlab.homefeeder.R
import kotlin.math.ceil

@UiThread
class DialogActionsVcByPopup(private val d: View) {
    private var b: (() -> Unit)? = null
    private var c: PopupWindowImpl? = null
    private fun a(): Boolean = this.c != null

    private fun intToDp(dp: Int): Int {
        return ceil((dp * Resources.getSystem().displayMetrics.density).toDouble()).toInt()
    }

    fun d(view: View): Rect {
        val iArr = IntArray(2)
        view.getLocationOnScreen(iArr)
        return Rect(iArr[0], iArr[1], iArr[0] + view.measuredWidth, iArr[1] + view.measuredHeight)
    }

    @SuppressLint("InflateParams")
    fun a(list: List<PopupItem>, setListViewParams: ((Pair<View, VkLibActionsListView>) -> Unit)? = null, onClick: (PopupItem) -> Unit) {
        if (!a()) {
            val context = this.d.context
            val inflate = LayoutInflater.from(context).inflate(R.layout.replica_popup, null, false)
            val dialogActionsListView = inflate.findViewById<VkLibActionsListView>(R.id.dialog_actions_list_content)
            setListViewParams?.invoke(Pair(inflate.findViewById(R.id.dialog_actions_list_container), dialogActionsListView))
            dialogActionsListView.setActions(list)
            dialogActionsListView.setActionClickListener(onClick)

            inflate.measure(View.MeasureSpec.makeMeasureSpec(this.d.rootView.measuredWidth, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(this.d.rootView.measuredHeight - intToDp(64), View.MeasureSpec.AT_MOST))
            inflate.layout(0, 0, inflate.measuredWidth, inflate.measuredHeight)

            val d2 = d(this.d)
            val measuredWidth = (d2.right - inflate.measuredWidth) + intToDp(8)
            val b2 = d2.top - intToDp(8)
            val rect = Rect(measuredWidth, b2, inflate.measuredWidth + measuredWidth, inflate.measuredHeight + b2)

            this.c = PopupWindowImpl(context).apply {
                contentView = inflate
                width = rect.width()
                height = rect.height()
                setOnDismissListener { c = null }
                showAtLocation(d, 0, rect.left, rect.top)
            }
        }
    }

    fun dismiss() {
        if (a()) {
            b?.invoke()
            c?.dismiss()
        }
    }
}