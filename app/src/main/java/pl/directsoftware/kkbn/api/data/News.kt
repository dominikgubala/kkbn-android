package pl.directsoftware.kkbn.api.data

import androidx.compose.ui.graphics.ImageBitmap
import pl.directsoftware.kkbn.utils.Consts.MEDIA_PATH

data class News(
    val id: Int,
    val title: String,
    val date: DateTime,
    val content: String? = null,
    private val image: String,
) {
    val imagePath
        get() = "$MEDIA_PATH/$image"

    var bitmap: ImageBitmap? = null
}