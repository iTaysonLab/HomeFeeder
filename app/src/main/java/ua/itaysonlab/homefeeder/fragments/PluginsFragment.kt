package ua.itaysonlab.homefeeder.fragments

import android.animation.ValueAnimator
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.card.MaterialCardView
import kotlinx.android.synthetic.main.plugin_manager_entry.view.*
import ua.itaysonlab.homefeeder.HFApplication
import ua.itaysonlab.homefeeder.R
import ua.itaysonlab.homefeeder.pluginsystem.PluginFetcher
import ua.itaysonlab.homefeeder.preferences.HFPluginPreferences
import ua.itaysonlab.homefeeder.utils.Logger
import kotlin.math.ceil

class PluginsFragment : Fragment() {
    companion object {
        private fun intToDp(dp: Float): Int {
            return ceil((dp * Resources.getSystem().displayMetrics.density).toDouble()).toInt()
        }

        private fun floatToDp(dp: Float): Float {
            return (dp * Resources.getSystem().displayMetrics.density)
        }

        val STROKE_MAX_WIDTH = intToDp(2f)
        val MAX_ELEVATION = floatToDp(16f)
    }

    private lateinit var rv: RecyclerView
    private lateinit var srl: SwipeRefreshLayout

    private lateinit var adapter: PluginAdapter

    val list = mutableListOf<PluginItem>()
    private var enabledList = setOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.plugin_manager_ui, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv = view.findViewById(R.id.rv)
        srl = view.findViewById(R.id.srl)

        adapter = PluginAdapter()

        rv.layoutManager = LinearLayoutManager(context!!)
        rv.adapter = adapter

        srl.setOnRefreshListener {
            PluginFetcher.init(HFApplication.instance)
            load()
        }

        load()
    }

    private fun load() {
        enabledList = HFPluginPreferences.enabledList

        list.clear()
        PluginFetcher.availablePlugins.forEach {
            list.add(PluginItem(
                enabledList.contains(it.key),
                it.value
            ))
        }

        adapter.notifyDataSetChanged()
        srl.isRefreshing = false
    }

    inner class PluginAdapter: RecyclerView.Adapter<PluginAdapter.PluginVH>() {
        private lateinit var inflater: LayoutInflater

        inner class PluginVH(itemView: View): RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PluginVH {
            if (!::inflater.isInitialized) inflater = LayoutInflater.from(parent.context)
            return PluginVH(inflater.inflate(R.layout.plugin_manager_entry, parent, false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: PluginVH, position: Int) {
            val block = list[position]
            holder.itemView.plugin_name.text = block.info.title
            holder.itemView.plugin_desc.text = block.info.description
            holder.itemView.plugin_author.text = block.info.author
            holder.itemView.plugin_status.isChecked = block.isEnabled
            holder.itemView.app_icon.setImageDrawable(HFApplication.getSmallIcon(block.info.pkg))

            holder.itemView.plugin_status.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    HFPluginPreferences.add(block.info.pkg)
                } else HFPluginPreferences.remove(block.info.pkg)

                setStroke((holder.itemView as MaterialCardView), true, isChecked)
            }

            setStroke((holder.itemView as MaterialCardView), false, block.isEnabled)

            if (!block.info.hasPluginSettings) holder.itemView.plugin_to_settings.visibility = View.GONE
        }

        private fun setStroke(view: MaterialCardView, animate: Boolean, stroke: Boolean) {
            view
            if (animate) {
                ValueAnimator.ofInt(if (stroke) 0 else STROKE_MAX_WIDTH, if (stroke) STROKE_MAX_WIDTH else 0).apply {
                    duration = 250L
                    interpolator = LinearOutSlowInInterpolator()
                    addUpdateListener {
                        view.strokeWidth = it.animatedValue as Int
                    }
                }.start()
                ValueAnimator.ofFloat(if (stroke) 0f else MAX_ELEVATION, if (stroke) MAX_ELEVATION else 0f).apply {
                    duration = 250L
                    interpolator = LinearOutSlowInInterpolator()
                    addUpdateListener {
                        view.cardElevation = it.animatedValue as Float
                    }
                }.start()
            } else {
                view.strokeWidth = if (stroke) STROKE_MAX_WIDTH else 0
                view.cardElevation = if (stroke) MAX_ELEVATION else 0f
            }
        }
    }

    data class PluginItem (
        val isEnabled: Boolean,
        val info: PluginFetcher.SlimPluginInfo
    )
}
