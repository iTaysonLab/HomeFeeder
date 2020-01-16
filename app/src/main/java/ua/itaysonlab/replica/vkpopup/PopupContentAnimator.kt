package ua.itaysonlab.replica.vkpopup

import android.animation.*
import android.content.res.Resources
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.UiThread
import androidx.core.os.postDelayed
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import kotlin.math.ceil

@UiThread
class PopupContentAnimator(private val g: View) {
    companion object {
        private fun intToDp(dp: Int): Int {
            return ceil((dp * Resources.getSystem().displayMetrics.density).toDouble()).toInt()
        }

        val h = (-(intToDp(4))).toFloat()
        val j = (-(intToDp(4))).toFloat()

        val i = LinearOutSlowInInterpolator()
        val k = FastOutLinearInInterpolator()
    }

    var b: Animator? = null
    var c: Animator? = null
    private val d = Handler(Looper.getMainLooper())
    private var e: (() -> Unit)? = null
    private var f: (() -> Unit)? = null

    fun a(action: () -> Unit) {
        this.f = action
    }

    private fun b() {
        val rect = Rect(0, 0, this.g.measuredWidth, 0)
        val rect2 = Rect(0, 0, this.g.measuredWidth, this.g.measuredHeight)
        this.g.apply {
            clipBounds = rect
            alpha = 0.0f
            translationY = h
            visibility = View.VISIBLE
        }

        val ofObject = ObjectAnimator.ofObject(this.g, "clipBounds", RectEvaluator(), rect, rect2)
        val ofFloat = ObjectAnimator.ofFloat(this.g, View.ALPHA, 0.0f, 1.0f)
        val ofFloat2 = ObjectAnimator.ofFloat(this.g, View.TRANSLATION_Y, h, 0.0f)
        this.b = AnimatorSet().apply {
            addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    b = null
                    c = null
                    g.visibility = View.VISIBLE
                }
            })
            addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    b = null
                    c = null
                    e?.invoke()
                }
            })
            duration = 225
            interpolator = i
            playTogether(ofObject, ofFloat, ofFloat2)
            start()
        }
    }

    fun a(z: Boolean) {
        if (!a()) {
            e()
            if (d()) {
                b()
            } else {
                b { b() }
            }
        }
    }

    fun b(z: Boolean) {
        if (a()) {
            if (z) {
                e()
                if (d()) {
                    c()
                } else {
                    b { c() }
                }
            } else {
                e()
                this.g.visibility = View.INVISIBLE
                this.f?.invoke()
            }
        }
    }

    private fun c() {
        this.g.apply {
            clipBounds = null
            alpha = 1.0f
            translationY = 0.0f
            visibility = View.VISIBLE
        }
        val ofFloat = ObjectAnimator.ofFloat(this.g, View.ALPHA, 1.0f, 0.0f)
        val ofFloat2 = ObjectAnimator.ofFloat(this.g, View.TRANSLATION_Y, 0.0f, j)
        this.c = AnimatorSet().apply {
            addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    b = null
                    c = null
                    g.visibility = View.INVISIBLE
                }
            })
            addListener(object: AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    b = null
                    c = null
                    f?.invoke()
                }
            })
            duration = 150
            interpolator = k
            playTogether(ofFloat, ofFloat2)
            start()
        }
    }

    private fun d(): Boolean {
        return this.g.measuredHeight > 0
    }

    private fun b(aVar: () -> Unit) {
        this.g.visibility = View.INVISIBLE
        this.d.postDelayed(50) {
            aVar.invoke()
        }
    }

    private fun e() {
        this.b?.cancel()
        this.b = null
        this.c?.cancel()
        this.c = null
        this.d.removeCallbacksAndMessages(null)
    }

    private fun a(): Boolean {
        if (this.b == null) {
            if (this.g.visibility == View.VISIBLE) {
                if (this.c == null) {
                    return true
                }
            }
            return false
        }
        return true
    }
}