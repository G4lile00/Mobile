package com.example.aula.recipes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.aula.recipes.model.Receita
import com.example.aula.recipes.repository.ReceitaRepository

class ReceitaViewModel(private val repository: ReceitaRepository) : ViewModel() {
    val todasReceitas: LiveData<List<Receita>> = repository.todasReceitas

    suspend fun inserir(receita: Receita) = repository.inserir(receita)
    suspend fun atualizar(receita: Receita) = repository.atualizar(receita)
    suspend fun deletar(receita: Receita) = repository.deletar(receita)
    fun buscarPorId(id: Int): LiveData<Receita> = repository.buscarPorId(id)
}
