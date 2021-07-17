package com.praveenkolay.favdish.view.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.praveenkolay.favdish.R
import com.praveenkolay.favdish.application.FavDishApplication
import com.praveenkolay.favdish.databinding.DialogCustomListBinding
import com.praveenkolay.favdish.databinding.FragmentAllDishesBinding
import com.praveenkolay.favdish.model.entity.FavDish
import com.praveenkolay.favdish.utils.Constants
import com.praveenkolay.favdish.view.activity.AddUpdateDishActivity
import com.praveenkolay.favdish.view.activity.MainActivity
import com.praveenkolay.favdish.view.adapter.CustomListItemAdapter
import com.praveenkolay.favdish.view.adapter.FavDishAdapter
import com.praveenkolay.favdish.viewModel.FavDishViewModel
import com.praveenkolay.favdish.viewModel.FavDishViewModelFactory
import kotlin.math.log

class AllDishesFragment : Fragment() {

    private lateinit var mBinding: FragmentAllDishesBinding

    private lateinit var mFavDishAdapter: FavDishAdapter
    private lateinit var mCustomListDialog: Dialog

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.rvAllDishes.layoutManager = GridLayoutManager(requireActivity(), 2)
        mFavDishAdapter = FavDishAdapter(this@AllDishesFragment)

        mBinding.rvAllDishes.adapter = mFavDishAdapter

        mFavDishViewModel.allDishList.observe(viewLifecycleOwner){
            dishes ->
            dishes.let {
                if(it.isNotEmpty()){
                    mBinding.rvAllDishes.visibility = View.VISIBLE
                    mBinding.tvNoDishAddedYet.visibility = View.GONE

                    mFavDishAdapter.dishesList(it)
                }else{
                    mBinding.rvAllDishes.visibility = View.GONE
                    mBinding.tvNoDishAddedYet.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentAllDishesBinding.inflate(inflater, container, false)
        return mBinding.root

    }

    fun dishDetails(favDish: FavDish){
        findNavController().navigate(AllDishesFragmentDirections.actionAllDishesToDishDetails(favDish))

        if(requireActivity() is MainActivity){
            (activity as MainActivity?)?.hideBottomNavigationView()
        }
    }

    fun deleteDish(dish: FavDish){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.title_delete_dish))
        builder.setMessage(resources.getString(R.string.msg_delete_dish_dialog, dish.title))
        builder.setIcon(android.R.drawable.ic_dialog_alert)
        builder.setPositiveButton(resources.getString(R.string.lbl_yes)){ dialogInterface, _ ->
            mFavDishViewModel.delete(dish)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.lbl_no)){ dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        val alertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun filterDishesListDialog(){
        mCustomListDialog = Dialog(requireActivity())
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        mCustomListDialog.setContentView(binding.root)
        binding.customListTitle.text = resources.getString(R.string.title_select_item_to_filter)

        val dishTypes = Constants.dishType()
        dishTypes.add(0, Constants.ALL_ITEMS)
        binding.rvCustomList.layoutManager = LinearLayoutManager(requireActivity())

        val adapter = CustomListItemAdapter(requireActivity(), this@AllDishesFragment, dishTypes, Constants.FILTER_SELECTION)

        binding.rvCustomList.adapter = adapter
        mCustomListDialog.show()
    }

    override fun onResume() {
        super.onResume()
        if(requireActivity() is MainActivity){
            (activity as MainActivity?)?.showBottomNavigationView()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_add_dishes -> {
                startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
                return true
            }
            R.id.action_filter_dishes -> {
                filterDishesListDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun filterSelection(filterItemSelection: String){
        mCustomListDialog.dismiss()

        Log.d("Filter Selection", filterItemSelection)

        if(filterItemSelection == Constants.ALL_ITEMS){
            mFavDishViewModel.allDishList.observe(viewLifecycleOwner){
                dishes ->
                dishes.let {
                    if(it.isNotEmpty()){
                        mBinding.rvAllDishes.visibility = View.VISIBLE
                        mBinding.tvNoDishAddedYet.visibility = View.GONE

                        mFavDishAdapter.dishesList(it)
                    }else{
                        mBinding.rvAllDishes.visibility = View.GONE
                        mBinding.tvNoDishAddedYet.visibility = View.VISIBLE
                    }
                }
            }
        }else{
            mFavDishViewModel.getFilteredList(filterItemSelection).observe(viewLifecycleOwner){
                dish ->
                dish.let {
                    if(it.isNotEmpty()){
                        mBinding.rvAllDishes.visibility = View.VISIBLE
                        mBinding.tvNoDishAddedYet.visibility = View.GONE

                        mFavDishAdapter.dishesList(it)
                    }else{
                        mBinding.rvAllDishes.visibility = View.GONE
                        mBinding.tvNoDishAddedYet.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}