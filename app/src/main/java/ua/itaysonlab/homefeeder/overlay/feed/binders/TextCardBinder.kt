package ua.itaysonlab.homefeeder.overlay.feed.binders

import android.util.SparseIntArray
import android.view.View
import kotlinx.android.synthetic.main.notification_generic_content.view.*
import ua.itaysonlab.hfsdk.FeedItem
import ua.itaysonlab.hfsdk.content.TextCardContent
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.utils.RelativeTimeHelper

object TextCardBinder: FeedBinder {
    override fun bind(theme: SparseIntArray?, item: FeedItem, view: View) {
        val content = item.content as TextCardContent

        view.not_title.text = content.title
        view.not_title.visibility = if (content.title.isEmpty()) {
            View.GONE
        } else View.VISIBLE

        view.not_text.text = content.text
        view.not_text.visibility = if (content.text.isEmpty()) {
            View.GONE
        } else View.VISIBLE

        view.not_app_subtitle.text = content.subtitle
        view.not_app_subtitle.visibility = if (content.subtitle == null) {
            View.GONE
        } else View.VISIBLE

        view.not_app_name.text = item.title
        view.not_app_icon.visibility = View.GONE
        view.not_app_date.text = RelativeTimeHelper.getDateFormattedRelative(HFApplication.instance, (item.time/1000) - 1000)
    }
}