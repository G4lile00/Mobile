package com.example.aula.recipes.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.aula.recipes.model.Receita

@Dao
interface ReceitaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(receita: Receita)

    @Update
    suspend fun atualizar(receita: Receita)

    @Delete
    suspend fun deletar(receita: Receita)

    @Query("SELECT * FROM receitas")
    fun listarTodas(): LiveData<List<Receita>>

    @Query("SELECT * FROM receitas WHERE id = :id")
    fun buscarPorId(id: Int): LiveData<Receita>

    @Query("SELECT COUNT(*) FROM receitas")
    suspend fun getCount(): Int
}
