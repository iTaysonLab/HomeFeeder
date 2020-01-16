package ua.itaysonlab.replica.vkpopup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ua.itaysonlab.homefeeder.R

class ActionAdapter(private val e: Context, private val f: ActionStyle): RecyclerView.Adapter<ActionVh>() {
    private val f2972a = LayoutInflater.from(this.e)
    private var c: List<PopupItem> = listOf()
    private var d: ((PopupItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionVh {
        return ActionVh(f2972a.inflate(R.layout.vklib_actionslistview_entry, parent, false), this.f)
    }

    override fun getItemCount(): Int {
        return this.c.size
    }

    fun a(i: Int): PopupItem {
        return this.c[i]
    }

    override fun onBindViewHolder(holder: ActionVh, position: Int) {
        holder.a(d!!)
        holder.a(this.c[position])
    }

    override fun onViewRecycled(holder: ActionVh) {
        super.onViewRecycled(holder)
        holder.a(null)
    }

    fun a(): ((PopupItem) -> Unit)? {
        return this.d
    }

    fun a(cVar: (PopupItem) -> Unit) {
        this.d = cVar
    }

    fun a(list: List<PopupItem>) {
        this.c = list
        notifyDataSetChanged()
    }
}