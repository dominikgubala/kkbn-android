package pl.directsoftware.kkbn.utils

import android.widget.Toast
import pl.directsoftware.kkbn.App
import pl.directsoftware.kkbn.R

object Notification {

    fun toast(success: Boolean, long: Boolean = false) {
        val context = App.context
        val msg = if (success) context.getString(R.string.saved_successfully) else context.getString(R.string.save_error)
        toast(msg, long)
    }

    fun toast(message: String, long: Boolean = false) {
        Toast.makeText(
            App.context,
            message,
            if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        ).show()
    }
}