package com.example.aula.recipes

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// 1️⃣ Model / Entidade
@Entity(tableName = "receitas")
data class Receita(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nome: String,
    val ingredientes: String,
    val modoPreparo: String
)

// 2️⃣ DAO
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
}

// 3️⃣ Database
@Database(entities = [Receita::class], version = 1, exportSchema = false)
abstract class ReceitaDatabase : RoomDatabase() {
    abstract fun receitaDao(): ReceitaDao

    companion object {
        @Volatile
        private var INSTANCE: ReceitaDatabase? = null

        fun getDatabase(context: Context): ReceitaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReceitaDatabase::class.java,
                    "receitas_db"
                )
                    .addCallback(DatabaseCallback()) // Adiciona receitas de exemplo
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.receitaDao())
                }
            }
        }

        private suspend fun populateDatabase(dao: ReceitaDao) {
            val sampleRecipes = listOf(
                Receita(
                    nome = "Frango Grelhado com Arroz",
                    ingredientes = "Frango, arroz, sal, pimenta",
                    modoPreparo = "Grelhe o frango e cozinhe o arroz. Misture e sirva."
                ),
                Receita(
                    nome = "Macarrão à Bolonhesa",
                    ingredientes = "Macarrão, carne moída, molho de tomate",
                    modoPreparo = "Cozinhe o macarrão, prepare o molho com carne e tomate e sirva."
                ),
                Receita(
                    nome = "Salada Colorida",
                    ingredientes = "Alface, tomate, cenoura, molho especial",
                    modoPreparo = "Misture todos os ingredientes e adicione o molho."
                ),
                Receita(
                    nome = "Feijoada",
                    ingredientes = "Feijão preto, carne seca, linguiça, temperos",
                    modoPreparo = "Cozinhe o feijão com as carnes e temperos até ficar macio."
                ),
                Receita(
                    nome = "Peixe Assado",
                    ingredientes = "Peixe, limão, sal, legumes",
                    modoPreparo = "Tempere o peixe, asse no forno junto com os legumes."
                )
            )

            sampleRecipes.forEach { dao.inserir(it) }
        }
    }
}

// 4️⃣ Repository
class ReceitaRepository(private val receitaDao: ReceitaDao) {
    val todasReceitas: LiveData<List<Receita>> = receitaDao.listarTodas()

    suspend fun inserir(receita: Receita) = receitaDao.inserir(receita)
    suspend fun atualizar(receita: Receita) = receitaDao.atualizar(receita)
    suspend fun deletar(receita: Receita) = receitaDao.deletar(receita)
    fun buscarPorId(id: Int): LiveData<Receita> = receitaDao.buscarPorId(id)
}

// 5️⃣ ViewModel
class ReceitaViewModel(private val repository: ReceitaRepository) : ViewModel() {
    val todasReceitas: LiveData<List<Receita>> = repository.todasReceitas

    suspend fun inserir(receita: Receita) = repository.inserir(receita)
    suspend fun atualizar(receita: Receita) = repository.atualizar(receita)
    suspend fun deletar(receita: Receita) = repository.deletar(receita)
    fun buscarPorId(id: Int): LiveData<Receita> = repository.buscarPorId(id)
}

// 6️⃣ ViewModelFactory
class ReceitaViewModelFactory(private val repository: ReceitaRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReceitaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReceitaViewModel(repository) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconhecida")
    }
}
