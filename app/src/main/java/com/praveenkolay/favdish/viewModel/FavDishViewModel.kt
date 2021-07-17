package com.praveenkolay.favdish.viewModel

import androidx.lifecycle.*
import com.praveenkolay.favdish.model.dataBase.FavDishRepository
import com.praveenkolay.favdish.model.entity.FavDish
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class FavDishViewModel(private val repository: FavDishRepository): ViewModel() {

    fun insert(favDish: FavDish) = viewModelScope.launch {
        repository.insertFavDishData(favDish)
    }

    val allDishList: LiveData<List<FavDish>> = repository.allDishList.asLiveData()

    fun update(dish: FavDish) = viewModelScope.launch {
        repository.updateFavDishData(dish)
    }

    val allFavoriteDishesList: LiveData<List<FavDish>> = repository.allFavoriteDishList.asLiveData()

    fun delete(dish: FavDish) = viewModelScope.launch {
        repository.deleteDishData(dish)
    }

    fun getFilteredList(value: String): LiveData<List<FavDish>> = repository.filteredDishList(value).asLiveData()

}

class FavDishViewModelFactory(private val repository: FavDishRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(FavDishViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return FavDishViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}