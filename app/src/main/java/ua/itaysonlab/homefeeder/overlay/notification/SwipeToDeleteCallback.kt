package ua.itaysonlab.homefeeder.overlay.notification

import android.graphics.*
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.notification_simple.view.*
import ua.itaysonlab.homefeeder.preferences.HFPreferences
import ua.itaysonlab.homefeeder.utils.Logger
import java.math.RoundingMode

abstract class SwipeToDeleteCallback(private val listener: RecyclerItemTouchHelperListener) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (viewHolder.itemViewType != 0) return 0
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (viewHolder != null) {
            val foregroundView = (viewHolder as NotificationAdapter.SimpleViewHolder).itemView.iforeground

            ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(foregroundView)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onChildDrawOver(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val itemView = (viewHolder as NotificationAdapter.SimpleViewHolder).itemView

        if (dX < 255f && HFPreferences.overlayCompact) {
            val rounded = (dX / 255.0f).toBigDecimal().setScale(2, RoundingMode.UP).toFloat()
            itemView.ibg_icon.alpha = rounded
        }

        ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(
            c, recyclerView, itemView.iforeground, dX, dY,
            actionState, isCurrentlyActive
        )
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foregroundView = (viewHolder as NotificationAdapter.SimpleViewHolder).itemView.iforeground
        ItemTouchHelper.Callback.getDefaultUIUtil().clearView(foregroundView)
    }

    override fun onChildDraw(
        c: Canvas, recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float,
        actionState: Int, isCurrentlyActive: Boolean
    ) {
        val itemView = (viewHolder as NotificationAdapter.SimpleViewHolder).itemView

        if (dX < 255f && HFPreferences.overlayCompact) {
            val rounded = (dX / 255.0f).toBigDecimal().setScale(2, RoundingMode.UP).toFloat()
            itemView.ibg_icon.alpha = rounded
        }

        ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(
            c, recyclerView, itemView.iforeground, dX, dY,
            actionState, isCurrentlyActive
        )
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onSwiped(viewHolder, direction, viewHolder.adapterPosition)
    }

    interface RecyclerItemTouchHelperListener {
        fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int)
    }
}