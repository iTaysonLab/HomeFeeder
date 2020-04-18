package ua.itaysonlab.homefeeder.overlay.feed.binders

import android.text.Html
import android.util.SparseIntArray
import android.view.View
import coil.api.load
import kotlinx.android.synthetic.main.feed_card_story_large.view.*
import ua.itaysonlab.hfsdk.FeedItem
import ua.itaysonlab.hfsdk.content.StoryCardContent

object StoryCardBinder: FeedBinder {
    override fun bind(theme: SparseIntArray?, item: FeedItem, view: View) {
        val content = item.content as StoryCardContent

        view.story_title.text = content.title
        view.story_source.text = content.source.title
        view.story_desc.text = Html.fromHtml(content.text)

        view.story_pic.load(content.background_url)
    }
}