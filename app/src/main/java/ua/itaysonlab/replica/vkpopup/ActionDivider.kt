package ua.itaysonlab.replica.vkpopup

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ActionDivider(private val style: DividerStyle): RecyclerView.ItemDecoration() {
    private val f2974a = Paint()
    private val b = Rect()

    init {
        this.f2974a.color = style.dividerColor
        this.f2974a.isAntiAlias = false
        this.f2974a.isDither = false
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (a(parent, view)) {
            outRect.bottom = style.dividerHeight
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (parent.childCount != 0) {
            val paddingLeft = parent.paddingLeft
            val measuredWidth = parent.measuredWidth - parent.paddingRight

            for (i in 0 until parent.childCount) {
                val childAt = parent.getChildAt(i)
                if (a(parent, childAt)) {
                    val bottom = childAt.bottom + ((this.style.dividerHeight / 2) - (this.style.dividerSize / 2))
                    this.b.set(paddingLeft, bottom, measuredWidth, this.style.dividerSize + bottom)
                    c.drawRect(this.b, this.f2974a)
                }
            }
        }
    }

    companion object {
        fun a(recyclerView: RecyclerView, view: View): Boolean {
            val cap = recyclerView.getChildAdapterPosition(view)
            if (cap == -1) return false
            val adapter = recyclerView.adapter as ActionAdapter
            if (cap == adapter.itemCount - 1) return false
            if (adapter.a(cap).group != adapter.a(cap + 1).group) return true
            return false
        }
    }
}