package com.ufkoku.ciphersharedpreferences.demo.ui.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ufkoku.ciphersharedpreferences.R
import com.ufkoku.ciphersharedpreferences.demo.entity.PrefsEntry
import java.util.*

class Adapter : RecyclerView.Adapter<Adapter.ItemViewHolder> {

    var listener: Listener? = null

    private val inflater: LayoutInflater

    private val entries = ArrayList<PrefsEntry>()

    constructor(inflater: LayoutInflater) {
        this.inflater = inflater
    }

    constructor(inflater: LayoutInflater, entries: List<PrefsEntry>) : this(inflater) {
        this.entries.addAll(entries)
    }

    fun addEntry(entry: PrefsEntry) {
        entries.add(entry)
        notifyItemInserted(entries.size - 1)
    }

    fun addEntries(entriesToAdd: List<PrefsEntry>) {
        val oldSize = entries.size
        entries.addAll(entriesToAdd)
        notifyItemRangeInserted(oldSize, entriesToAdd.size)
    }

    fun removeEntry(entry: PrefsEntry) {
        val index = entries.indexOf(entry)
        if (index != -1) {
            entries.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(inflater!!.inflate(R.layout.list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindObject(entries[position])
    }

    override fun getItemCount(): Int {
        return entries.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        protected var binded: PrefsEntry? = null

        protected val tvKey: TextView = itemView.findViewById(R.id.key)

        protected val tvValue: TextView = itemView.findViewById(R.id.value)

        protected val tvEncryptedValue: TextView = itemView.findViewById(R.id.valueEncrypted)

        init {
            itemView.findViewById<View>(R.id.deleteButton).setOnClickListener {
                if (listener != null && binded != null) {
                    listener!!.onDeleteClicked(binded!!)
                }
            }
        }

        fun bindObject(entry: PrefsEntry) {
            binded = entry

            tvKey.text = entry.key
            tvValue.text = entry.value
            tvEncryptedValue.text = entry.encryptedValue
        }

    }

    interface Listener {

        fun onDeleteClicked(entry: PrefsEntry)

    }

}
