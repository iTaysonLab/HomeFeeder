package ua.itaysonlab.homefeeder.overlay.feed.binders

import android.util.SparseIntArray
import android.view.View
import ua.itaysonlab.hfsdk.FeedItem
import ua.itaysonlab.hfsdk.content.TextCardContent
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.databinding.NotificationGenericContentBinding
import ua.itaysonlab.homefeeder.utils.RelativeTimeHelper

object TextCardBinder: FeedBinder {
    override fun bind(theme: SparseIntArray?, item: FeedItem, view: View) {
        val content = item.content as TextCardContent
        val binding = NotificationGenericContentBinding.bind(view)

        binding.notTitle.text = content.title
        binding.notTitle.visibility = if (content.title.isEmpty()) {
            View.GONE
        } else View.VISIBLE

        binding.notText.text = content.text
        binding.notText.visibility = if (content.text.isEmpty()) {
            View.GONE
        } else View.VISIBLE

        binding.notAppSubtitle.text = content.subtitle
        binding.notAppSubtitle.visibility = if (content.subtitle == null) {
            View.GONE
        } else View.VISIBLE

        binding.notAppName.text = item.title
        binding.notAppIcon.visibility = View.GONE
        binding.notAppDate.text = RelativeTimeHelper.getDateFormattedRelative(HFApplication.instance, (item.time/1000) - 1000)
    }
}