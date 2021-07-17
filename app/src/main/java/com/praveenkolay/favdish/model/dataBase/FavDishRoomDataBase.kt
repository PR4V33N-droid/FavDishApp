package com.praveenkolay.favdish.model.dataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.praveenkolay.favdish.model.entity.FavDish

@Database(entities = [FavDish::class], version = 1)
abstract class FavDishRoomDataBase: RoomDatabase() {

    abstract fun favDishDao(): FavDishDao
    companion object{
        @Volatile
        private var INSTANCE: FavDishRoomDataBase? = null

        fun getDataBase(context: Context): FavDishRoomDataBase{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavDishRoomDataBase::class.java,
                    "fav_dish_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}