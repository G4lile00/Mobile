package com.example.aula.recipes.ui

import android.content.Context
import androidx.annotation.DrawableRes
import com.example.aula.recipes.model.Receita
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream
import com.example.aula.R



data class RecipeUI(
    val id: Int,
    val name: String,
    val description: String,
    val bitmapImage: Bitmap? = null,   // imagem vinda do Base64
    @DrawableRes val placeholderImage: Int = R.drawable.moon // fallback se Base64 for null
)


// Bitmap -> Base64
fun bitmapToBase64(bitmap: Bitmap): String {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

// Base64 -> Bitmap
fun base64ToBitmap(base64Str: String): Bitmap {
    val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}

fun drawableToBase64(context: Context, @DrawableRes resId: Int): String {
    val bitmap = BitmapFactory.decodeResource(context.resources, resId)
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}


// Mapper: transforma Receita -> RecipeUI
fun mapReceitaToRecipeUI(receita: Receita): RecipeUI {
    val bitmap = receita.imagemBase64?.let { base64ToBitmap(it) } // decodifica Base64, se existir

    return RecipeUI(
        id = receita.id,
        name = receita.nome,
        description = "${receita.ingredientes}\n${receita.modoPreparo}",
        bitmapImage = bitmap,
        placeholderImage = R.drawable.moon // fallback se bitmap for null
    )
}

