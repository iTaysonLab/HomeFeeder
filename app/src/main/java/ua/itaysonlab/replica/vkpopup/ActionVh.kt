package ua.itaysonlab.replica.vkpopup

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ua.itaysonlab.homefeeder.R

class ActionVh(private val f: View, private val g: ActionStyle): RecyclerView.ViewHolder(f) {
    private val b: ImageView = this.f.findViewById(R.id.icon)
    private val c: TextView = this.f.findViewById(R.id.label)
    private var d: PopupItem? = null
    private var e: ((PopupItem) -> Unit)? = null

    init {
        //this.f.background = this.g.optionBackground
        this.f.setPaddingRelative(this.g.paddingStart, 0, this.g.paddingEnd, 0)
        this.f.setOnClickListener {
            if (d != null && e != null) {
                e?.invoke(d!!)
            }
        }
        if (this.g.iconTint != null) {
            this.b.colorFilter = PorterDuffColorFilter(this.g.iconTint!!, PorterDuff.Mode.SRC_IN)
        }
        this.c.setTextSize(0, this.g.textSize.toFloat())
        this.c.setTextColor(this.g.textColor)
        this.c.layoutParams = (this.c.layoutParams as ViewGroup.MarginLayoutParams).apply {
            marginStart = g.iconSpace
        }
    }

    fun a(): ((PopupItem) -> Unit)? {
        return this.e
    }

    fun a(cVar: ((PopupItem) -> Unit)?) {
        this.e = cVar
    }

    fun a(aVar: PopupItem) {
        this.d = aVar
        this.b.setImageResource(aVar.icon)
        this.c.setText(aVar.title)
    }
}