package com.specialprojects.experiments.envelopecall.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BindableAdapter<T, VH: BindableAdapter.ViewHolder<T>>: RecyclerView.Adapter<VH>() {

    private val items: MutableList<T> = mutableListOf()

    open fun changeData(items: List<T>) {
        changeData(items, true)
    }

    fun changeData(items: List<T>, refresh: Boolean) {
        this.items.clear()
        this.items.addAll(items)

        if (refresh) refreshData()
    }

    fun refreshData() {
        notifyDataSetChanged()
    }

    operator fun get(position: Int): T = items[position]

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(get(position))
    }

    override fun getItemCount(): Int = items.size

    fun removePosition(position: Int) {
        items.removeAt(position)

        notifyDataSetChanged()
    }

    abstract class ViewHolder <T>(view: View): RecyclerView.ViewHolder(view) {
        abstract fun bind(item: T)
    }
}