package ua.itaysonlab.homefeeder.overlay.feed.binders

import android.content.Intent
import android.net.Uri
import android.text.Html
import android.util.SparseIntArray
import android.view.View
import coil.load
import ua.itaysonlab.hfsdk.FeedItem
import ua.itaysonlab.hfsdk.content.StoryCardContent
import ua.itaysonlab.homefeeder.databinding.FeedCardStoryLargeBinding
import ua.itaysonlab.homefeeder.kt.isDark
import ua.itaysonlab.homefeeder.theming.Theming

object StoryCardBinder: FeedBinder {
    override fun bind(theme: SparseIntArray?, item: FeedItem, view: View) {
        val content = item.content as StoryCardContent
        val binding = FeedCardStoryLargeBinding.bind(view)

        binding.storyTitle.text = content.title
        binding.storySource.text = content.source.title

        if (content.text.isEmpty()) {
            binding.storyDesc.visibility = View.GONE
        } else {
            binding.storyDesc.text = Html.fromHtml(content.text).toString()
        }

        binding.storyPic.load(content.background_url)

        theme ?: return
        binding.storyDimmer.setBackgroundColor(theme.get(Theming.Colors.CARD_BG.ordinal))
        val themeCard = if (theme.get(Theming.Colors.CARD_BG.ordinal).isDark()) Theming.defaultDarkThemeColors else Theming.defaultLightThemeColors
        binding.storyTitle.setTextColor(themeCard.get(Theming.Colors.TEXT_COLOR_PRIMARY.ordinal))
        binding.storySource.setTextColor(themeCard.get(Theming.Colors.TEXT_COLOR_SECONDARY.ordinal))
        binding.storyDesc.setTextColor(themeCard.get(Theming.Colors.TEXT_COLOR_SECONDARY.ordinal))

        binding.root.setOnClickListener {
            view.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(content.link)))
        }
    }
}