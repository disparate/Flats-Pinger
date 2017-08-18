package kazarovets.flatspinger.api

import android.content.Context
import com.google.gson.GsonBuilder


object ApiManager {

    lateinit var context: Context

    val GSON = GsonBuilder().create()

    val onlinerApi by lazy { OnlinerApi(context) }

    fun init(context: Context) {
        this.context = context.applicationContext
    }

}