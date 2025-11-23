package com.example.aula

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aula.recipes.*
import com.example.aula.ui.theme.AulaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = ReceitaDatabase.getDatabase(this)
        val repository = ReceitaRepository(database.receitaDao())
        setContent {
            AulaTheme {
                val navController = rememberNavController()
                val receitaViewModel: ReceitaViewModel = viewModel(
                    factory = ReceitaViewModelFactory(repository)
                )

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(navController, receitaViewModel)
                    }
                    composable("details/{id}") { backStackEntry ->
                        val recipeId = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                        RecipeDetailsScreen(recipeId, receitaViewModel)
                    }
                }
            }
        }
    }
}

// Data class que representa o item visual
data class RecipeUI(
    val id: Int,
    val name: String,
    val description: String,
    @DrawableRes val image: Int
)

// Mapear receitas do banco para RecipeUI (com imagens locais)
fun mapReceitaToRecipeUI(receita: Receita): RecipeUI {
    val image = when (receita.id) {
        1 -> R.drawable.frango          // Frango Grelhado com Arroz
        2 -> R.drawable.macarrao       // Macarrão à Bolonhesa
        3 -> R.drawable.hamburger       // Hambúrguer Fitness
        4 -> R.drawable.feijoada        // Feijoada
        5 -> R.drawable.pastel          // Pastel Assado
        6 -> R.drawable.fritas         // Batatas Fritas
        else -> R.drawable.moon
    }
    return RecipeUI(
        id = receita.id,
        name = receita.nome,
        description = "${receita.ingredientes}\n${receita.modoPreparo}",
        image = image
    )
}

@Composable
fun HomeScreen(navController: NavController, viewModel: ReceitaViewModel) {
    var receitas by remember { mutableStateOf<List<Receita>>(emptyList()) }

    // Observe LiveData manualmente
    DisposableEffect(viewModel.todasReceitas) {
        val observer = Observer<List<Receita>> { lista ->
            receitas = lista
        }
        viewModel.todasReceitas.observeForever(observer)
        onDispose {
            viewModel.todasReceitas.removeObserver(observer)
        }
    }

    val recipesUI = receitas.map { mapReceitaToRecipeUI(it) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFBEFD3)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
        ) {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = "Receitas",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    },
                    backgroundColor = Color(0xFF9E77FF),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
                RecipeList(recipes = recipesUI, navController = navController)
            }
        }
    }
}

@Composable
fun RecipeList(recipes: List<RecipeUI>, navController: NavController) {
    Column(modifier = Modifier.padding(8.dp)) {
        recipes.forEach { recipe ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 4.dp)
                    .clickable { navController.navigate("details/${recipe.id}") }
            ) {
                Image(
                    painter = painterResource(id = recipe.image),
                    contentDescription = "Imagem do prato ${recipe.name}",
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = recipe.name, fontWeight = FontWeight.Bold)
                    Text(
                        text = recipe.description,
                        color = Color.Gray,
                        fontSize = 12.sp,
                        maxLines = 2
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Ir para detalhes",
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun RecipeDetailsScreen(recipeId: Int, viewModel: ReceitaViewModel) {
    var receita by remember { mutableStateOf<Receita?>(null) }

    DisposableEffect(recipeId) {
        val observer = Observer<Receita> { r ->
            receita = r
        }
        viewModel.buscarPorId(recipeId).observeForever(observer)
        onDispose {
            viewModel.buscarPorId(recipeId).removeObserver(observer)
        }
    }

    val recipeUI = receita?.let { mapReceitaToRecipeUI(it) }
        ?: RecipeUI(0, "Desconhecido", "Sem descrição disponível", R.drawable.moon)

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Ingredientes", "Modo de Preparo")

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFFBEFD3)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            TopAppBar(
                title = { Text("Detalhes da Receita", fontWeight = FontWeight.Bold, color = Color.White) },
                backgroundColor = Color(0xFF9E77FF),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            )

            Spacer(modifier = Modifier.height(20.dp))

            Image(
                painter = painterResource(id = recipeUI.image),
                contentDescription = "Imagem do prato ${recipeUI.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = recipeUI.name, fontSize = 22.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(
                selectedTabIndex = selectedTabIndex,
                backgroundColor = Color.Transparent,
                contentColor = Color(0xFF9E77FF),
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color(0xFF9E77FF),
                        height = 3.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title, fontWeight = FontWeight.SemiBold) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Conteúdo das abas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            ) {
                when (selectedTabIndex) {
                    0 -> {
                        // Ingredientes
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            recipeUI.description
                                .substringBefore("\n") // Apenas ingredientes
                                .split(",")
                                .forEach { ingrediente ->
                                    Text(
                                        text = "• ${ingrediente.trim()}",
                                        fontSize = 16.sp,
                                        color = Color.DarkGray,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                }
                        }
                    }
                    1 -> {
                        // Modo de Preparo
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            Text(
                                text = recipeUI.description.substringAfter("\n"), // Apenas preparo
                                fontSize = 16.sp,
                                color = Color.DarkGray,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
