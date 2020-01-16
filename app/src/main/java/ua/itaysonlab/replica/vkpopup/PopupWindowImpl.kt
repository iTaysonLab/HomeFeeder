package ua.itaysonlab.replica.vkpopup

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.os.Handler
import android.view.View
import android.widget.PopupWindow
import androidx.annotation.UiThread

@UiThread
class PopupWindowImpl(context: Context): PopupWindow(context) {
    private val f1304a = Handler()
    private var b: PopupContentAnimator? = null

    init {
        isClippingEnabled = false
        isFocusable = true
        animationStyle = 0
        setBackgroundDrawable(object: Drawable() {
            override fun draw(canvas: Canvas) {

            }

            override fun setAlpha(alpha: Int) {

            }

            override fun getOpacity(): Int {
                return PixelFormat.TRANSPARENT
            }

            override fun setColorFilter(colorFilter: ColorFilter?) {

            }

        })
    }

    override fun showAsDropDown(view: View?) {
        run {
            super.showAsDropDown(view)
        }
    }

    override fun showAsDropDown(view: View?, i: Int, i2: Int) {
        run {
            super.showAsDropDown(view, i, i2)
        }
    }

    override fun showAsDropDown(view: View?, i: Int, i2: Int, i3: Int) {
        run {
            super.showAsDropDown(view, i, i2, i3)
        }
    }

    override fun showAtLocation(view: View?, i: Int, i2: Int, i3: Int) {
        run {
            super.showAtLocation(view, i, i2, i3)
        }
    }

    private fun run(aVar: () -> Unit) {
        this.b = PopupContentAnimator(contentView)
        this.b?.b(false)
        aVar.invoke()
        this.f1304a.post {
            this.b?.a(true)
        }
    }

    override fun dismiss() {
        this.f1304a.removeCallbacksAndMessages(null)
        this.b?.a {
            b = null
            super.dismiss()
        }
        this.b?.b(true)
    }
}