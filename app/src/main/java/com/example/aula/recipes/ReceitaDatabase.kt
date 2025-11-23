package com.example.aula.recipes.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.aula.R
import com.example.aula.recipes.model.Receita
import com.example.aula.recipes.ui.drawableToBase64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Receita::class], version = 2, exportSchema = false)
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
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Popula o banco apenas na primeira criação
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.receitaDao(), context)
                }
            }
        }

        private suspend fun populateDatabase(dao: ReceitaDao, context: Context) {
            // Só popula se estiver vazio
            if (dao.getCount() == 0) {
                val receitas = listOf(
                    Receita(
                        id = 1,
                        nome = "Frango Grelhado com Arroz",
                        ingredientes = "Frango, arroz, sal, pimenta, azeite",
                        modoPreparo = "Tempere o frango com sal e pimenta. Grelhe em uma frigideira com azeite até dourar. Cozinhe o arroz com um pouco de sal. Sirva o frango sobre o arroz.",
                        imagemBase64 = drawableToBase64(context, R.drawable.frango)
                    ),
                    Receita(
                        id = 2,
                        nome = "Macarrão à Bolonhesa",
                        ingredientes = "Macarrão, carne moída, molho de tomate, cebola, alho, azeite, sal, pimenta",
                        modoPreparo = "Cozinhe o macarrão conforme instruções da embalagem. Refogue cebola e alho no azeite, adicione a carne moída e cozinhe até dourar. Acrescente o molho de tomate, tempere com sal e pimenta e cozinhe por 10 minutos. Misture com o macarrão e sirva.",
                        imagemBase64 = drawableToBase64(context, R.drawable.macarrao)
                    ),
                    Receita(
                        id = 3,
                        nome = "Hambúrguer Fitness",
                        ingredientes = "Pão integral, carne magra moída, alface, tomate, cebola, azeite, sal, pimenta",
                        modoPreparo = "Modele a carne em hambúrgueres e tempere com sal e pimenta. Grelhe até atingir o ponto desejado. Monte o hambúrguer com o pão, alface, tomate e cebola. Sirva com acompanhamento de sua preferência.",
                        imagemBase64 = drawableToBase64(context, R.drawable.hamburger)
                    ),
                    Receita(
                        id = 4,
                        nome = "Feijoada",
                        ingredientes = "Feijão preto, carne seca, linguiça, bacon, cebola, alho, louro, sal, pimenta",
                        modoPreparo = "Deixe o feijão de molho por algumas horas. Cozinhe o feijão com louro. Em outra panela, refogue cebola, alho, bacon, linguiça e carne seca. Misture tudo com o feijão e cozinhe até as carnes ficarem macias. Ajuste o tempero e sirva com arroz e couve refogada.",
                        imagemBase64 = drawableToBase64(context, R.drawable.feijoada)
                    ),
                    Receita(
                        id = 5,
                        nome = "Pastel Assado",
                        ingredientes = "Massa de pastel pronta, queijo, presunto, tomate, orégano, azeite",
                        modoPreparo = "Recheie a massa de pastel com queijo, presunto e tomate. Feche e pincele azeite por cima. Asse em forno preaquecido a 180°C por 20 minutos ou até dourar. Polvilhe orégano antes de servir.",
                        imagemBase64 = drawableToBase64(context, R.drawable.pastel)
                    ),
                    Receita(
                        id = 6,
                        nome = "Batatas Fritas",
                        ingredientes = "Batatas, óleo, sal",
                        modoPreparo = "Descasque e corte as batatas em palitos. Aqueça o óleo em fogo médio e frite as batatas até dourarem. Retire e escorra em papel toalha. Tempere com sal e sirva quente.",
                        imagemBase64 = drawableToBase64(context, R.drawable.fritas)
                    )
                )

                receitas.forEach { dao.inserir(it) }
            }
        }
    }
}
