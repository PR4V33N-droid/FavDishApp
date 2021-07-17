package com.praveenkolay.favdish.model.dataBase

import androidx.annotation.WorkerThread
import com.praveenkolay.favdish.model.entity.FavDish
import kotlinx.coroutines.flow.Flow

class FavDishRepository(private val favDishDao: FavDishDao) {

    @WorkerThread
    suspend fun insertFavDishData(favDish: FavDish){
        favDishDao.insertFavDishDetails(favDish)
    }

    val allDishList: Flow<List<FavDish>> = favDishDao.getAllDishesList()

    @WorkerThread
    suspend fun updateFavDishData(favDish: FavDish){
        favDishDao.updateFavDishDetails(favDish)
    }

    val allFavoriteDishList: Flow<List<FavDish>> = favDishDao.getAllFavoriteDishesList()

    @WorkerThread
    suspend fun deleteDishData(favDish: FavDish){
        favDishDao.deleteDishDetails(favDish)
    }

    fun filteredDishList(value: String): Flow<List<FavDish>> = favDishDao.getFilteredDishList(value)
}