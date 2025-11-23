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
            val receitas = listOf(
                Receita(
                    id= 1,
                    nome = "Frango Grelhado com Arroz",
                    ingredientes = "Frango, arroz, sal, pimenta, azeite",
                    modoPreparo = "Tempere o frango com sal e pimenta. Grelhe em uma frigideira com azeite até dourar. Cozinhe o arroz com um pouco de sal. Sirva o frango sobre o arroz."
                ),
                Receita(
                    id = 2,
                    nome = "Macarrão à Bolonhesa",
                    ingredientes = "Macarrão, carne moída, molho de tomate, cebola, alho, azeite, sal, pimenta",
                    modoPreparo = "Cozinhe o macarrão conforme instruções da embalagem. Refogue cebola e alho no azeite, adicione a carne moída e cozinhe até dourar. Acrescente o molho de tomate, tempere com sal e pimenta e cozinhe por 10 minutos. Misture com o macarrão e sirva."
                ),
                Receita(
                    id = 3,
                    nome = "Hambúrguer Fitness",
                    ingredientes = "Pão integral, carne magra moída, alface, tomate, cebola, azeite, sal, pimenta",
                    modoPreparo = "Modele a carne em hambúrgueres e tempere com sal e pimenta. Grelhe até atingir o ponto desejado. Monte o hambúrguer com o pão, alface, tomate e cebola. Sirva com acompanhamento de sua preferência."
                ),
                Receita(
                    id =4,
                    nome = "Feijoada",
                    ingredientes = "Feijão preto, carne seca, linguiça, bacon, cebola, alho, louro, sal, pimenta",
                    modoPreparo = "Deixe o feijão de molho por algumas horas. Cozinhe o feijão com louro. Em outra panela, refogue cebola, alho, bacon, linguiça e carne seca. Misture tudo com o feijão e cozinhe até as carnes ficarem macias. Ajuste o tempero e sirva com arroz e couve refogada."
                ),
                Receita(
                    id = 5,
                    nome = "Pastel Assado",
                    ingredientes = "Massa de pastel pronta, queijo, presunto, tomate, orégano, azeite",
                    modoPreparo = "Recheie a massa de pastel com queijo, presunto e tomate. Feche e pincele azeite por cima. Asse em forno preaquecido a 180°C por 20 minutos ou até dourar. Polvilhe orégano antes de servir."
                ),
                Receita(
                    id  = 6,
                    nome = "Batatas Fritas",
                    ingredientes = "Batatas, óleo, sal",
                    modoPreparo = "Descasque e corte as batatas em palitos. Aqueça o óleo em fogo médio e frite as batatas até dourarem. Retire e escorra em papel toalha. Tempere com sal e sirva quente."
                )
            )

            receitas.forEach { dao.inserir(it) }
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
