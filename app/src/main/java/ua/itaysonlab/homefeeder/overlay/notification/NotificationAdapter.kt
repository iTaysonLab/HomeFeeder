package ua.itaysonlab.homefeeder.overlay.notification

import android.app.PendingIntent
import android.content.Intent
import android.content.res.ColorStateList
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.notification_generic_content.view.*
import kotlinx.android.synthetic.main.notification_generic_content.view.not_app_name
import kotlinx.android.synthetic.main.notification_generic_content.view.not_app_subtitle
import kotlinx.android.synthetic.main.notification_generic_content.view.not_text
import kotlinx.android.synthetic.main.notification_generic_content.view.not_title
import kotlinx.android.synthetic.main.notification_media.view.*
import kotlinx.android.synthetic.main.notification_simple.view.ibg_icon
import kotlinx.android.synthetic.main.notification_simple.view.iforeground
import kotlinx.android.synthetic.main.overlay_header.view.*
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.R
import ua.itaysonlab.homefeeder.activites.MainActivity
import ua.itaysonlab.homefeeder.isDark
import ua.itaysonlab.homefeeder.overlay.rvutils.StackBlur
import ua.itaysonlab.homefeeder.preferences.HFPreferences
import ua.itaysonlab.homefeeder.theming.Theming

class NotificationAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val notifications: MutableList<NotificationWrapper> = mutableListOf()
    private var theme: SparseIntArray? = null
    private var compact = HFPreferences.overlayCompact

    fun setTheme(theme: SparseIntArray) {
        this.theme = theme
        notifyDataSetChanged()
    }

    fun setCompact(compact: Boolean) {
        this.compact = compact
        notifyDataSetChanged()
    }

    class SimpleViewHolder(view: View) : RecyclerView.ViewHolder(view)
    class MediaViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            1 -> {
                return MediaViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.notification_media,
                        parent,
                        false
                    )
                )
            }
            else -> {
                return SimpleViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        if (compact) R.layout.notification_compact else R.layout.notification_simple,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = notifications[position]
        return if (item.mediaSessionToken != null) 1 else 0
    }

    fun update(list: List<NotificationWrapper>) {
        notifications.clear()

        val fixedList = mutableListOf<NotificationWrapper>()
        fixedList.addAll(list)

        var msNotify: NotificationWrapper? = null
        fixedList.forEach {
            if (it.mediaSessionToken != null) {
                msNotify = it
                return@forEach
            }
        }

        if (msNotify != null) {
            fixedList.remove(msNotify!!)
            fixedList.add(0, msNotify!!)
        }

        notifications.addAll(fixedList)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return notifications[position].id.toLong()
    }

    override fun onBindViewHolder(h: RecyclerView.ViewHolder, position: Int) {
        val item = notifications[position]
        if (item.mediaSessionToken != null) {
            bindMedia(item, h as MediaViewHolder)
        } else {
            bindSimpleNotification(item, h as SimpleViewHolder)
        }
    }

    fun removeItem(position: Int): String {
        val backup = notifications[position].key
        notifications.removeAt(position)
        notifyItemRemoved(position)
        return backup
    }

    private fun bindSimpleNotification(item: NotificationWrapper, holder: SimpleViewHolder) {
        val view = holder.itemView

        view.not_title.text = item.title
        view.not_title.visibility = if (item.title.isEmpty()) {
            View.GONE
        } else View.VISIBLE

        view.not_text.text = item.text
        view.not_text.visibility = if (item.text.isEmpty()) {
            View.GONE
        } else View.VISIBLE

        view.not_app_subtitle.text = item.subtext
        view.not_app_subtitle.visibility = if (item.subtext.isEmpty()) {
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

        // Inbox style related
        if (item.inboxStyleLines != null) {
            view.not_title.text = item.text
            view.not_text.text = item.inboxStyleLines.joinToString("\n")
            view.not_title.visibility = View.VISIBLE
            view.not_text.visibility = View.VISIBLE
        }

        theme ?: return

        val cardBg = theme!!.get(Theming.Colors.CARD_BG.position)
        if (!compact) {
            if (view is CardView) view.setCardBackgroundColor(cardBg)
            view.iforeground.setBackgroundColor(cardBg)
        } else {
            view.ibg_icon.alpha = 0f
        }

        val theme = if (cardBg.isDark()) Theming.defaultDarkThemeColors else Theming.defaultLightThemeColors
        view.not_title.setTextColor(theme.get(Theming.Colors.TEXT_COLOR_PRIMARY.position))
        view.not_app_name.setTextColor(theme.get(Theming.Colors.TEXT_COLOR_PRIMARY.position))
        view.not_text.setTextColor(theme.get(Theming.Colors.TEXT_COLOR_PRIMARY.position))
        view.not_app_date.setTextColor(theme.get(Theming.Colors.TEXT_COLOR_SECONDARY.position))
        view.not_app_subtitle.setTextColor(theme.get(Theming.Colors.TEXT_COLOR_SECONDARY.position))
        view.not_app_icon.imageTintList = ColorStateList.valueOf(theme.get(Theming.Colors.TEXT_COLOR_PRIMARY.position))
        view.ibg_icon.imageTintList = ColorStateList.valueOf(theme.get(Theming.Colors.TEXT_COLOR_PRIMARY.position))
    }

    private fun bindMedia(item: NotificationWrapper, holder: MediaViewHolder) {
        val view = holder.itemView

        view.not_title.text = item.title
        view.not_title.visibility = if (item.title.isEmpty()) {
            View.GONE
        } else View.VISIBLE

        view.not_text.text = item.text
        view.not_text.visibility = if (item.text.isEmpty()) {
            View.GONE
        } else View.VISIBLE

        view.not_app_subtitle.text = item.subtext
        view.not_app_subtitle.visibility = if (item.subtext.isEmpty()) {
            View.GONE
        } else View.VISIBLE

        view.not_app_name.text = item.applicationName

        if (item.intent != null) {
            view.setOnClickListener {
                try {
                    item.intent.send()
                } catch (e: PendingIntent.CanceledException) {
                    e.printStackTrace()
                }
            }
        } else view.setOnClickListener(null)

        if (item.largeIcon != null) {
            val bitmap = item.largeIcon.toBitmap()
            view.media_icon.setImageBitmap(bitmap)
            view.media_bg.setImageBitmap(StackBlur.fastblur(bitmap, 0.5f, 25))
        }

        theme ?: return
    }

    override fun getItemCount() = notifications.size
}