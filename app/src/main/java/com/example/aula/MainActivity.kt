package com.example.aula

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aula.ui.theme.AulaTheme
import android.widget.Toast
import androidx.compose.material.Icon
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AulaTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(navController)
                    }

                    composable("details/{id}") { backStackEntry ->
                        val userId = backStackEntry.arguments?.getString("id")
                        UserDetailsScreen(userId?.toIntOrNull() ?: 0)
                    }
                }
            }
        }
}




    data class User(
        val id: Int,
        val name: String,
        val lastTimeOnline: String,
        @DrawableRes val image: Int
    )
    val sampleUsers = listOf(
        User(12, "João", "Online agora", R.drawable.moon),
        User(8, "Maria", "Há 5 minutos", R.drawable.moon),
        User(3, "Carlos", "Ontem", R.drawable.moon),
        User(5, "Ana", "Há 2 horas", R.drawable.moon),
        User(7, "Luiz", "Há 4 horas", R.drawable.moon),
        User(10, "Beatriz", "Ontem", R.drawable.moon),
        User(15, "Eduardo", "Há 1 dia", R.drawable.moon),
        User(20, "Raquel", "Há 3 dias", R.drawable.ic_launcher_foreground),

    )
    fun getUsers(): List<User>{
        return sampleUsers;
    }
    fun getUser(id: Int): MainActivity.User {

        return sampleUsers.find { it.id == id } ?: User(0,"Undef", "Undef", R.drawable.moon)
    }



    @Composable
    fun HomeScreen(navController: NavController) {


        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(240, 240, 240),
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
                                text = "Usuários",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        },
                        backgroundColor = Color(30, 136, 229),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    CardRow(users = getUsers(), navController = navController)
                }
            }
        }
    }

    @Composable
    fun CardRow(users: List<User>, navController: NavController) {
        Column(modifier = Modifier.padding(8.dp)) {
            users.forEach { user ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                        .clickable {

                            navController.navigate("details/${user.id}")
                        }
                ) {
                    Image(
                        painter = painterResource(id = user.image),
                        contentDescription = "Imagem do usuário ${user.name}",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(text = user.name, fontWeight = FontWeight.Bold)
                        Text(
                            text = user.lastTimeOnline,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            fontSize = 12.sp
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
    fun UserDetailsScreen(userId: Int) {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(240, 240, 240)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                val user: MainActivity.User = getUser(userId) ;
                Text(text = "Detalhes do usuário", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(20.dp))
                Text(text = "Nome: ${user.name}", fontSize = 18.sp)
                Text(text = "Ultimo Login: ${user.lastTimeOnline}", fontSize = 14.sp)
            }
        }
    }

}



