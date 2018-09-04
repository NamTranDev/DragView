package demo.tran.nam.dragview

import android.view.LayoutInflater
import android.view.ViewGroup
import demo.tran.nam.dragview.databinding.ItemCrossWordBinding
import tran.nam.common.DataBoundListAdapter

class AdapterCrossWord : DataBoundListAdapter<String, ItemCrossWordBinding>() {

    override fun createBinding(parent: ViewGroup): ItemCrossWordBinding {
        return ItemCrossWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    }

    override fun bind(binding: ItemCrossWordBinding, item: String) {
        binding.text = item
    }

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return false
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return false
    }

    fun position(item: String): Int? {
        return items?.indexOf(item)
    }

}