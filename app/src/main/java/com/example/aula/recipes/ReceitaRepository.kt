package com.example.aula.recipes.repository

import androidx.lifecycle.LiveData
import com.example.aula.recipes.data.ReceitaDao
import com.example.aula.recipes.model.Receita

class ReceitaRepository(private val receitaDao: ReceitaDao) {
    val todasReceitas: LiveData<List<Receita>> = receitaDao.listarTodas()

    suspend fun inserir(receita: Receita) = receitaDao.inserir(receita)
    suspend fun atualizar(receita: Receita) = receitaDao.atualizar(receita)
    suspend fun deletar(receita: Receita) = receitaDao.deletar(receita)
    fun buscarPorId(id: Int): LiveData<Receita> = receitaDao.buscarPorId(id)
}
