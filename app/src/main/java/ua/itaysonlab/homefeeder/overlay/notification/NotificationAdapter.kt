package ua.itaysonlab.homefeeder.overlay.notification

import android.app.PendingIntent
import android.content.Intent
import android.content.res.ColorStateList
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.notification_simple.view.*
import kotlinx.android.synthetic.main.overlay_header.view.*
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.R
import ua.itaysonlab.homefeeder.activites.MainActivity
import ua.itaysonlab.homefeeder.utils.ThemeUtils

class NotificationAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val notifications: MutableList<NotificationWrapper> = mutableListOf()
    private var theme: SparseIntArray? = null

    fun setTheme(theme: SparseIntArray) {
        this.theme = theme
        notifyDataSetChanged()
    }

    class SimpleViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            1 -> {
                return HeaderViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.overlay_header,
                        parent,
                        false
                    )
                )
            }
            else -> {
                return SimpleViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.notification_simple,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) 1 else 0
    }

    fun update(list: List<NotificationWrapper>) {
        notifications.clear()
        notifications.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return if (position == 0) {
            RecyclerView.NO_ID
        } else {
            notifications[position-1].id.toLong()
        }
    }

    override fun onBindViewHolder(h: RecyclerView.ViewHolder, position: Int) {
        if (position == 0) {
            val holder = h as HeaderViewHolder
            val view = holder.itemView

            view.header_preferences.setOnClickListener {
                HFApplication.instance.startActivity(Intent(HFApplication.instance, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }

            theme ?: return
            view.header_preferences.imageTintList = ColorStateList.valueOf(theme!!.get(ThemeUtils.Colors.TEXT_COLOR_PRIMARY.position))
            view.header_title.setTextColor(theme!!.get(ThemeUtils.Colors.TEXT_COLOR_PRIMARY.position))
        } else {
            val item = notifications[position-1]
            val holder = h as SimpleViewHolder
            val view = holder.itemView

            view.not_title.text = item.title
            view.not_title.visibility = if (item.title.isEmpty()) {
                View.GONE
            } else View.VISIBLE

            view.not_text.text = item.text
            view.not_text.visibility = if (item.text.isEmpty()) {
                View.GONE
            } else View.VISIBLE

            view.not_app_name.text = item.applicationName
            view.not_app_icon.setImageDrawable(item.icon)
            view.not_app_date.text = item.time

            if (item.intent != null) {
                view.setOnClickListener {
                    try {
                        item.intent.send()
                    } catch (e: PendingIntent.CanceledException) {
                        e.printStackTrace()
                    }
                }
            } else view.setOnClickListener(null)

            theme ?: return
            (view as CardView).setCardBackgroundColor(theme!!.get(ThemeUtils.Colors.CARD_BG.position))
            view.iforeground.setBackgroundColor(theme!!.get(ThemeUtils.Colors.CARD_BG.position))
            view.not_title.setTextColor(theme!!.get(ThemeUtils.Colors.TEXT_COLOR_PRIMARY.position))
            view.not_app_name.setTextColor(theme!!.get(ThemeUtils.Colors.TEXT_COLOR_PRIMARY.position))
            view.not_text.setTextColor(theme!!.get(ThemeUtils.Colors.TEXT_COLOR_PRIMARY.position))
            view.not_app_date.setTextColor(theme!!.get(ThemeUtils.Colors.TEXT_COLOR_SECONDARY.position))
            view.not_app_icon.imageTintList = ColorStateList.valueOf(theme!!.get(ThemeUtils.Colors.TEXT_COLOR_PRIMARY.position))
            view.ibg_icon.imageTintList = ColorStateList.valueOf(theme!!.get(ThemeUtils.Colors.TEXT_COLOR_PRIMARY.position))
        }
    }

    fun removeItem(position: Int): String {
        val backup = notifications[position-1].key
        notifications.removeAt(position-1)
        notifyItemRemoved(position)
        return backup
    }

    override fun getItemCount() = notifications.size + 1
}