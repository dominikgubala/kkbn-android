package pl.directsoftware.kkbn.common

import android.text.Html
import androidx.compose.ui.graphics.ImageBitmap

data class HtmlData(
    val content: String?
) {
    private val regex = "\\[shortcode=(.*?)]".toRegex()
    private val spanned = Html.fromHtml(content, Html.FROM_HTML_MODE_COMPACT).toString()

    val parts = regex.split(spanned)
    val matches = regex.findAll(spanned).map { it.groupValues[1] }.toList()
    val bitmapList: MutableMap<Int, ImageBitmap?> = mutableMapOf()

    fun isEmpty(): Boolean {
        return content.isNullOrEmpty()
    }
}