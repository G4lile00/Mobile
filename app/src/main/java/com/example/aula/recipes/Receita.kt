package com.example.aula.recipes.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receitas")
data class Receita(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val ingredientes: String,
    val modoPreparo: String,
    val imagemBase64: String? = null,
)
