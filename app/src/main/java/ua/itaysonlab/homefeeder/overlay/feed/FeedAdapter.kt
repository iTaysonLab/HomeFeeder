package ua.itaysonlab.homefeeder.overlay.feed

import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ua.itaysonlab.hfsdk.FeedItem
import ua.itaysonlab.hfsdk.FeedItemType
import ua.itaysonlab.homefeeder.R
import ua.itaysonlab.homefeeder.overlay.feed.binders.StoryCardBinder
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
        return (list[position].type.ordinal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        if (!::layoutInflater.isInitialized) layoutInflater = LayoutInflater.from(parent.context)

        val layoutResource = when (FeedItemType.values()[viewType]) {
            FeedItemType.TEXT_CARD -> R.layout.notification_simple
            FeedItemType.TEXT_CARD_ACTIONS -> R.layout.feed_card_text
            FeedItemType.STORY_CARD -> R.layout.feed_card_story_large
        }

        return FeedViewHolder(viewType, layoutInflater.inflate(layoutResource, parent, false))
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val item = list[position]
        when (FeedItemType.values()[holder.type]) {
            FeedItemType.TEXT_CARD -> TextCardBinder.bind(theme, item, holder.itemView)
            FeedItemType.TEXT_CARD_ACTIONS -> TextCardWithActionsBinder.bind(theme, item, holder.itemView)
            FeedItemType.STORY_CARD -> StoryCardBinder.bind(theme, item, holder.itemView)
        }
    }

    inner class FeedViewHolder(val type: Int, itemView: View): RecyclerView.ViewHolder(itemView)
}