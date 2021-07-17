package com.praveenkolay.favdish.utils


object Constants {

    const val DISH_TYPE: String = "DishType"
    const val DISH_CATEGORY: String = "DishCategory"
    const val DISH_COOKING_TIME: String = "DishCookingTime"

    const val DISH_IMAGE_SOURCE_LOCAL: String = "local"
    const val DISH_IMAGE_SOURCE_ONLINE: String = "online"

    const val EXTRA_DISH_DETAILS: String = "DishDetails"
    const val ALL_ITEMS: String = "All"
    const val FILTER_SELECTION: String = "FilterSelection"

    const val API_ENDPOINT: String = "recipes/random"
    const val API_KEY: String = "apiKey"
    const val LIMIT_LICENSE: String = "limitLicense"
    const val TAGS: String = "tags"
    const val NUMBER: String = "number"

    const val BASE_URL = "https://api.spoonacular.com/"
    const val API_KEY_VALUE: String = "595dbf502bfe4675949ba3f1d458ff70"
    const val LIMIT_LICENSE_VALUE: Boolean = true
    const val TAGS_VALUE: String = "vegetable, dessert"
    const val NUMBER_VALUE: Int = 1

    const val NOTIFICATION_ID = "FavDish_notification_id"
    const val NOTIFICATION_NAME = "FavDish"
    const val NOTIFICATION_CHANNEL = "FavDish_channel_01"

    fun dishType(): ArrayList<String>{
        val list = ArrayList<String>()
        list.add("breakfast")
        list.add("lunch")
        list.add("snacks")
        list.add("dinner")
        list.add("salad")
        list.add("side dish")
        list.add("dessert")
        list.add("other")

        return list
    }

    fun dishCategory(): ArrayList<String>{
        val list = ArrayList<String>()
        list.add("chicken")
        list.add("vegetable")
        list.add("pizza")
        list.add("burger")
        list.add("bakery")
        list.add("BBQ")
        list.add("other")

        return list
    }

    fun dishCookingTIme(): ArrayList<String>{
        val list = ArrayList<String>()
        list.add("10")
        list.add("15")
        list.add("20")
        list.add("30")
        list.add("45")
        list.add("60")
        list.add("90")
        list.add("120")
        list.add("150")
        list.add("180")
        list.add("more than 3 hours")

        return list
    }

}