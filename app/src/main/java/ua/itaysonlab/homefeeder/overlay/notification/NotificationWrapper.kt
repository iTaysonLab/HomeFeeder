package ua.itaysonlab.homefeeder.overlay.notification

import android.app.Notification
import android.app.PendingIntent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.session.MediaSession
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.utils.RelativeTimeHelper

data class NotificationWrapper (
    // Int
    val id: Int,
    val color: Int,
    // String
    val title: CharSequence,
    val text: CharSequence,
    val subtext: CharSequence,
    val applicationName: CharSequence,
    val time: CharSequence,
    val key: String,
    // Other
    val mediaSessionToken: MediaSession.Token?,
    val intent: PendingIntent?,
    val icon: Drawable,
    val largeIcon: BitmapDrawable?,
    val inboxStyleLines: Array<CharSequence>?
) {
    constructor(notification: Notification, packageName: String, id: Int, key: String) : this(
        id,
        notification.color,
        notification.extras.getCharSequence(Notification.EXTRA_TITLE, ""),
        notification.extras.getCharSequence(Notification.EXTRA_TEXT, ""),
        notification.extras.getCharSequence(Notification.EXTRA_SUB_TEXT, ""),
        HFApplication.getAppNameByPkg(packageName),
        RelativeTimeHelper.getDateFormattedRelative(HFApplication.instance, notification.`when`/1000),
        key,
        notification.extras.getParcelable<MediaSession.Token>(Notification.EXTRA_MEDIA_SESSION),
        notification.contentIntent,
        HFApplication.getSmallIcon(notification, packageName),
        HFApplication.getLargeIcon(notification),
        notification.extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)
        )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NotificationWrapper

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }
}