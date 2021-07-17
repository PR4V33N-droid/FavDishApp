package com.praveenkolay.favdish.model.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity( tableName = "fav_dish_table")
data class FavDish(
        @ColumnInfo val image: String,
        @ColumnInfo (name = "image_source") val imageSource: String,
        @ColumnInfo val title: String,
        @ColumnInfo val type: String,
        @ColumnInfo val ingredients: String,
        @ColumnInfo val category: String,


        @ColumnInfo(name = "cooking_time") val cookingTime: String,
        @ColumnInfo(name = "instructions") val cookingDirection: String,
        @ColumnInfo(name = "favorite_dish") var favoriteDish: Boolean = false,
        @PrimaryKey(autoGenerate = true) val id: Int = 0
): Parcelable