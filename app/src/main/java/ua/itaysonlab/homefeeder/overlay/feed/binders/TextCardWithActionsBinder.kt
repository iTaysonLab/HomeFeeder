package ua.itaysonlab.homefeeder.overlay.feed.binders

import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ua.itaysonlab.hfsdk.FeedItem
import ua.itaysonlab.hfsdk.content.TextCardContent
import ua.itaysonlab.homefeeder.R

object TextCardWithActionsBinder: FeedBinder {
    override fun bind(theme: SparseIntArray?, item: FeedItem, view: View) {
        TextCardBinder.bind(theme, item, view)
        val valley = view.findViewById<ViewGroup>(R.id.button_valley_2)
        valley.removeAllViews()
        (item.content as TextCardContent).actions?.forEach {
            valley.addView(LayoutInflater.from(view.context).inflate(R.layout.feed_card_action, view as ViewGroup, false).apply {
                (this as TextView).text = it.title
            })
        }
    }
}