package pl.directsoftware.kkbn.utils

import android.util.Log

class Logger(private val name: String) {

    private val logName = "KKBN_LOG"

    enum class Level {
        DEBUG,
        INFO,
        WARNING,
        ERROR,
        ;
    }

    fun log(level: Level, message: String?, vararg args: Any?) {
        when (level) {
            Level.DEBUG -> debug(message, args)
            Level.INFO -> info(message, args)
            Level.WARNING -> info(message, args)
            Level.ERROR -> info(message, args)
        }
    }

    fun debug(message: String?, vararg args: Any?) {
        Log.d("$logName $name", message?.format(args) ?: "")
    }

    fun info(message: String?, vararg args: Any?) {
        Log.i("$logName $name", message?.format(args) ?: "")
    }

    fun warning(message: String?, vararg args: Any?) {
        Log.w("$logName $name", message?.format(args) ?: "")
    }

    fun error(message: String?, vararg args: Any?) {
        Log.e("$logName $name", message?.format(args) ?: "")
    }

    fun methodError(method: String, error: String) {
        Log.e("$logName $name", "Error in $method: $error")
    }

    fun methodException(method: String, exception: Exception) {
        Log.e("$logName $name", "Exception in $method: ${exception.message}")
    }
}