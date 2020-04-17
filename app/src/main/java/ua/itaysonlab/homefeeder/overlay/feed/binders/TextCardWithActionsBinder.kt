package ua.itaysonlab.homefeeder.overlay.feed.binders

import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.feed_card_text.view.*
import ua.itaysonlab.hfsdk.FeedItem
import ua.itaysonlab.hfsdk.content.TextCardContent
import ua.itaysonlab.homefeeder.R

object TextCardWithActionsBinder: FeedBinder {
    override fun bind(theme: SparseIntArray?, item: FeedItem, view: View) {
        TextCardBinder.bind(theme, item, view)
        (item.content as TextCardContent).actions?.forEach {
            view.button_valley_2.removeAllViews()
            view.button_valley_2.addView(LayoutInflater.from(view.context).inflate(R.layout.feed_card_action, view as ViewGroup, false).apply {
                (this as TextView).text = it.title
            })
        }
    }
}