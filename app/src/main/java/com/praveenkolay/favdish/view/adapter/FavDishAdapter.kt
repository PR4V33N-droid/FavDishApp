package com.praveenkolay.favdish.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.praveenkolay.favdish.R
import com.praveenkolay.favdish.databinding.ItemDishLayoutBinding
import com.praveenkolay.favdish.model.entity.FavDish
import com.praveenkolay.favdish.utils.Constants
import com.praveenkolay.favdish.view.activity.AddUpdateDishActivity
import com.praveenkolay.favdish.view.fragment.AllDishesFragment
import com.praveenkolay.favdish.view.fragment.FavoriteDishFragment

class FavDishAdapter(private val fragment: Fragment): RecyclerView.Adapter<FavDishAdapter.ViewHolder>() {

    private var dishes: List<FavDish> = listOf()

    class ViewHolder(view: ItemDishLayoutBinding): RecyclerView.ViewHolder(view.root) {
        val ivItemDishImage = view.ivItemDishImage
        val tvItemDishTitle = view.tvItemDishTitle
        val ibMore = view.ibMore
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDishLayoutBinding.inflate(LayoutInflater.from(fragment.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]
        Glide.with(fragment)
                .load(dish.image)
                .into(holder.ivItemDishImage)
        holder.tvItemDishTitle.text = dish.title

        holder.itemView.setOnClickListener{
            if(fragment is AllDishesFragment){
                fragment.dishDetails(dish)
            }
            if(fragment is FavoriteDishFragment){
                fragment.dishDetails(dish)
            }
        }
        holder.ibMore.setOnClickListener {
            val popup = PopupMenu(fragment.context, holder.ibMore)
            popup.menuInflater.inflate(R.menu.menu_adapter, popup.menu)

            popup.setOnMenuItemClickListener {
                if(it.itemId == R.id.action_edit_dish){
                    val intent = Intent(fragment.requireActivity(), AddUpdateDishActivity::class.java)
                    intent.putExtra(Constants.EXTRA_DISH_DETAILS, dish)
                    fragment.requireActivity().startActivity(intent)

                }else if (it.itemId == R.id.action_delete_dish){
                    if(fragment is AllDishesFragment){
                        fragment.deleteDish(dish)
                    }
                }
                    true
            }
            popup.show()
        }
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    fun dishesList(list: List<FavDish>){
        dishes = list
        notifyDataSetChanged()
    }
}