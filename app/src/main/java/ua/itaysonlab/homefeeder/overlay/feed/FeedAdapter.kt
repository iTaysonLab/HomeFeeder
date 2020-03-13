package ua.itaysonlab.homefeeder.overlay.feed

import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ua.itaysonlab.hfsdk.FeedItem
import ua.itaysonlab.hfsdk.FeedItemType
import ua.itaysonlab.homefeeder.R
import ua.itaysonlab.homefeeder.overlay.feed.binders.TextCardBinder
import ua.itaysonlab.homefeeder.overlay.feed.binders.TextCardWithActionsBinder

class FeedAdapter: RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    private var list = mutableListOf<FeedItem>()
    private lateinit var layoutInflater: LayoutInflater
    private var theme: SparseIntArray? = null

    fun replace(new: List<FeedItem>) {
        list.apply {
            clear()
            addAll(new)
        }
        notifyDataSetChanged()
    }

    fun update(new: List<FeedItem>) {
        list.apply {
            addAll(new)
        }
        notifyDataSetChanged()
    }

    fun setTheme(theme: SparseIntArray) {
        this.theme = theme
        notifyDataSetChanged()
    }

    override fun getItemCount() = list.size

    override fun getItemViewType(position: Int): Int {
        return when (list[position].type) {
            FeedItemType.TEXT_CARD -> 1
            FeedItemType.TEXT_CARD_ACTIONS -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        if (!::layoutInflater.isInitialized) layoutInflater = LayoutInflater.from(parent.context)

        val layoutResource = when (viewType) {
            1 -> R.layout.notification_simple
            2 -> R.layout.feed_card_text
            else -> R.layout.notification_simple
        }

        return FeedViewHolder(viewType, layoutInflater.inflate(layoutResource, parent, false))
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val item = list[position]
        when (holder.type) {
            1 -> TextCardBinder.bind(theme, item, holder.itemView)
            2 -> TextCardWithActionsBinder.bind(theme, item, holder.itemView)
        }
    }

    inner class FeedViewHolder(val type: Int, itemView: View): RecyclerView.ViewHolder(itemView)
}