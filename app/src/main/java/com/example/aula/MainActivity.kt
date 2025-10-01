package com.example.aula

import android.os.Bundle
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AulaTheme {
                // Fundo geral da tela
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(240, 240, 240), // cor de fundo mais clara e neutra
                ) {
                    val sampleUsers = listOf(
                        User("João", "Online agora",  R.drawable.moon),
                        User("Maria", "Há 5 minutos", R.drawable.moon),
                        User("Carlos", "Ontem", R.drawable.moon)
                    )

                    // "Div" que ocupa toda a tela com bordas arredondadas
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(24.dp)) // bordas mais arredondadas
                            .background(Color(255, 255, 255)) // cor branca pura
                    ) {
                        Column {
                            // TopAppBar com cor RGB e borda arredondada
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Usuários",
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                },
                                backgroundColor = Color(30, 136, 229), // azul vibrante
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Lista de usuários (CardRow)
                            com.example.aula.CardRow(users = sampleUsers)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AulaTheme {
        Greeting("Android")
    }
}

data class User(
    val name: String,
    val lastTimeOnline: String,
    @DrawableRes val image: Int
)

@Composable
fun CardRow(users: List<User>) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(8.dp)) {
        users.forEach { user ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth() // Faz a Row ocupar toda a largura
                    .padding(vertical = 8.dp, horizontal = 4.dp)
                    .clickable {
                        Toast
                            .makeText(context, "Clicou em ${user.name}", Toast.LENGTH_SHORT)
                            .show()
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

                Spacer(modifier = Modifier.weight(1f)) // empurra o ícone pro final

                androidx.compose.material.Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Ir para detalhes",
                    tint = Color.Gray
                )
            }
        }
    }
}
