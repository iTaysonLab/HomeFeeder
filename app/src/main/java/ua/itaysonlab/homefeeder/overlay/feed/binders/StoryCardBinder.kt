package ua.itaysonlab.homefeeder.overlay.feed.binders

import android.content.Intent
import android.net.Uri
import android.text.Html
import android.util.SparseIntArray
import android.view.View
import coil.api.load
import kotlinx.android.synthetic.main.feed_card_story_large.view.*
import ua.itaysonlab.hfsdk.FeedItem
import ua.itaysonlab.hfsdk.content.StoryCardContent
import ua.itaysonlab.homefeeder.kt.isDark
import ua.itaysonlab.homefeeder.theming.Theming

object StoryCardBinder: FeedBinder {
    override fun bind(theme: SparseIntArray?, item: FeedItem, view: View) {
        val content = item.content as StoryCardContent

        view.story_title.text = content.title
        view.story_source.text = content.source.title
        if (content.text == "") {
            view.story_desc.visibility = View.GONE
        } else {
            view.story_desc.text = Html.fromHtml(content.text).toString()
        }

        view.story_pic.load(content.background_url)

        theme ?: return
        view.story_dimmer.setBackgroundColor(theme.get(Theming.Colors.CARD_BG.ordinal))
        val themeCard = if (theme.get(Theming.Colors.CARD_BG.ordinal).isDark()) Theming.defaultDarkThemeColors else Theming.defaultLightThemeColors
        view.story_title.setTextColor(themeCard.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
        view.story_source.setTextColor(themeCard.get(Theming.Colors.TEXT_COLOR_SECONDARY.ordinal))
        view.story_desc.setTextColor(themeCard.get(Theming.Colors.TEXT_COLOR_SECONDARY.ordinal))

        view.setOnClickListener {
            view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(content.link)))
        }
    }
}