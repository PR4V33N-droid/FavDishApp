package com.praveenkolay.favdish.application

import android.app.Application
import com.praveenkolay.favdish.model.dataBase.FavDishRepository
import com.praveenkolay.favdish.model.dataBase.FavDishRoomDataBase

class FavDishApplication: Application() {

    private val database by lazy { FavDishRoomDataBase.getDataBase(this@FavDishApplication) }

    val repository by lazy { FavDishRepository(database.favDishDao()) }
}