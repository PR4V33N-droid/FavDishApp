package com.praveenkolay.favdish.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.praveenkolay.favdish.application.FavDishApplication
import com.praveenkolay.favdish.databinding.FragmentFavoriteDishBinding
import com.praveenkolay.favdish.model.entity.FavDish
import com.praveenkolay.favdish.view.activity.MainActivity
import com.praveenkolay.favdish.view.adapter.FavDishAdapter
import com.praveenkolay.favdish.viewModel.FavDishViewModel
import com.praveenkolay.favdish.viewModel.FavDishViewModelFactory

class FavoriteDishFragment : Fragment() {

    private var mBinding: FragmentFavoriteDishBinding? = null

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        mBinding = FragmentFavoriteDishBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFavDishViewModel.allFavoriteDishesList.observe(viewLifecycleOwner){
            dishes ->
            dishes.let {

                mBinding!!.rvFavoriteDishes.layoutManager = GridLayoutManager(requireActivity(), 2)

                val adapter = FavDishAdapter(this@FavoriteDishFragment)
                mBinding!!.rvFavoriteDishes.adapter = adapter


                if (it.isNotEmpty()){
                   mBinding!!.rvFavoriteDishes.visibility = View.VISIBLE
                   mBinding!!.tvNoFavoriteDishAddedYet.visibility = View.GONE
                    adapter.dishesList(it)

                }else{
                    mBinding!!.rvFavoriteDishes.visibility = View.GONE
                    mBinding!!.tvNoFavoriteDishAddedYet.visibility = View.VISIBLE
                    Log.i("favorite dish", "is empty.")
                }
            }
        }
    }

    fun dishDetails(favDish: FavDish){
        if (requireActivity() is MainActivity){
            (activity as MainActivity?)?.hideBottomNavigationView()
        }
        findNavController().navigate(FavoriteDishFragmentDirections.actionFavoriteDishToDishDetailsFragment(favDish))
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}