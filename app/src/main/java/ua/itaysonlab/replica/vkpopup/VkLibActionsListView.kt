package ua.itaysonlab.replica.vkpopup

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.DisplayMetrics
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.Px
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ua.itaysonlab.homefeeder.R
import kotlin.math.ceil

@UiThread
open class VkLibActionsListView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0): RecyclerView(context, attrs, defStyleAttr) {
    private val f2979a: DividerStyle
    private val b: ActionStyle
    private var c: ActionDivider
    private var d: ActionAdapter

    init {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.VkLibActionsListView, defStyleAttr, 0)
        val dHeight = attr.getDimensionPixelSize(R.styleable.VkLibActionsListView_vklib_alv_dividerHeight, a(1))
        val dSize = attr.getDimensionPixelSize(R.styleable.VkLibActionsListView_vklib_alv_dividerSize, a(1))
        val dColor = attr.getColor(R.styleable.VkLibActionsListView_vklib_alv_dividerColor, -16777216)
        this.f2979a = DividerStyle(dHeight, dSize, dColor)

        val oBg = attr.getDrawable(R.styleable.VkLibActionsListView_vklib_alv_optionBackground)
        val paddingStart = attr.getDimensionPixelSize(R.styleable.VkLibActionsListView_vklib_alv_optionPaddingStart, 0)
        val paddingEnd = attr.getDimensionPixelSize(R.styleable.VkLibActionsListView_vklib_alv_optionPaddingEnd, 0)
        val iconSpace = attr.getDimensionPixelSize(R.styleable.VkLibActionsListView_vklib_alv_optionIconLabelSpace, 0)
        val iconTint = Integer.valueOf(attr.getColor(R.styleable.VkLibActionsListView_vklib_alv_optionIconTint, -16777216))
        val textSize = attr.getDimensionPixelSize(R.styleable.VkLibActionsListView_vklib_alv_optionLabelTextSize, (((getDisplayMetrics().scaledDensity * 16.0f) + 0.5f).toInt()))
        val textColor = attr.getColor(R.styleable.VkLibActionsListView_vklib_alv_optionLabelTextColor, -16777216)
        this.b = ActionStyle(oBg, paddingStart, paddingEnd, iconSpace, iconTint, textSize, textColor)
        attr.recycle()

        this.c = ActionDivider(this.f2979a)
        this.d = ActionAdapter(context, this.b)
        this.layoutManager = LinearLayoutManager(context, VERTICAL, false)
        this.addItemDecoration(this.c)
        this.adapter = this.d
    }
    
    fun setDividerHeight(@Px i: Int) {
        this.f2979a.dividerHeight = i
        a()
    }

    fun setDividerSize(@Px i: Int) {
        this.f2979a.dividerSize = i
        a()
    }

    fun setDividerColor(@ColorInt i: Int) {
        this.f2979a.dividerColor = i
        a()
    }

    fun setActionBackground(@DrawableRes i: Int) {
        if (i != 0) {
            setActionBackground(ContextCompat.getDrawable(context, i))
        } else {
            setActionBackground(null)
        }
    }

    fun setActionBackground(drawable: Drawable?) {
        this.b.optionBackground = drawable
        b()
    }

    fun setActionPaddingStart(@Px i: Int) {
        this.b.paddingStart = i
        b()
    }

    fun setActionIconTint(@ColorInt i: Int) {
        this.b.iconTint = i
        b()
    }

    fun setActionPaddingEnd(@Px i: Int) {
        this.b.paddingEnd = i
        b()
    }

    fun setActionIconLabelSpace(@Px i: Int) {
        this.b.iconSpace = i
        b()
    }

    fun setActionLabelTextSize(@Px i: Int) {
        this.b.textSize = i
        b()
    }

    fun setActionLabelTextColor(@ColorInt i: Int) {
        this.b.textColor = i
        b()
    }

    fun setActionClickListener(cVar: (PopupItem) -> Unit) {
        this.d.a(cVar)
    }

    fun setActions(list: List<PopupItem>) {
        this.d.a(list)
        if (list.isNotEmpty()) layoutManager?.scrollToPosition(0)
    }

    private fun a() {
        this.c = ActionDivider(this.f2979a)
        adapter?.notifyDataSetChanged()
    }

    private fun b() {
        this.d = ActionAdapter(context, this.b)
        adapter = this.d
    }

    private fun getDisplayMetrics(): DisplayMetrics {
        return Resources.getSystem().displayMetrics
    }

    private fun a(i: Int): Int {
        return ceil((getDisplayMetrics().density * 1.0f)).toInt()
    }
}