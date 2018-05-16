package kazarovets.flatspinger.api

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins


@SuppressLint("StaticFieldLeak")
object ApiManager {

    lateinit var context: Context

    val GSON = GsonBuilder().create()

    val onlinerApi by lazy { OnlinerApi(context) }
    val iNeedAFlatApi by lazy { INeedAFlatApi() }

    fun init(context: Context) {
        this.context = context.applicationContext

        RxJavaPlugins.setErrorHandler { e ->
            when (e) {
                is UndeliverableException -> {
                    Log.w("Undeliverable exception", e)
                }

                is NullPointerException, is IllegalArgumentException,
                is IllegalStateException, is OnErrorNotImplementedException -> {
                    Thread.currentThread().uncaughtExceptionHandler.uncaughtException(Thread.currentThread(), e)
                }
            }
        }
    }

}