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
import ua.itaysonlab.homefeeder.utils.Logger

class FeedAdapter: RecyclerView.Adapter<FeedAdapter.FeedViewHolder>() {
    private var list = listOf<FeedItem>()
    private lateinit var layoutInflater: LayoutInflater
    private var theme: SparseIntArray? = null

    fun replace(new: List<FeedItem>) {
        list = new
        notifyDataSetChanged()
    }

    fun append(new: List<FeedItem>) {
        list = mutableListOf<FeedItem>().apply {
            addAll(list)
            addAll(new)
        }

        notifyDataSetChanged()
    }

    fun setTheme(theme: SparseIntArray) {
        this.theme = theme
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

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