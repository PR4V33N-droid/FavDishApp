package com.praveenkolay.favdish.view.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.praveenkolay.favdish.databinding.ItemCustomListBinding
import com.praveenkolay.favdish.view.activity.AddUpdateDishActivity
import com.praveenkolay.favdish.view.fragment.AllDishesFragment

class CustomListItemAdapter(
        private val activity: Activity,
        private val fragment: Fragment?,
        private val listItems: List<String>,
        private val selection: String)
        : RecyclerView.Adapter<CustomListItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemCustomListBinding.inflate(LayoutInflater.from(activity), parent, false )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = listItems[position]
        holder.tvItemText.text = item

        holder.itemView.setOnClickListener {
            if(activity is AddUpdateDishActivity){
                activity.selectedListItem(item, selection)
            }
            if(fragment is AllDishesFragment){
                fragment.filterSelection(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    class ItemViewHolder(view: ItemCustomListBinding): RecyclerView.ViewHolder(view.root){
        val tvItemText = view.tvItemText
    }
}