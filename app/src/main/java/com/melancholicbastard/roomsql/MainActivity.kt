package com.melancholicbastard.roomsql

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.melancholicbastard.roomsql.ui.theme.RoomSQLTheme
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    init {
        context = this
    }
    // Эта бурда для создания метода getContext, чтобы получать контекст в SingletoneDB
    companion object{
        var context: MainActivity? = null
        fun getContext(): Context {
            return context!!.applicationContext
        }
    }

    private val arrayMockBelka = ArrayList<Belka>().apply {
        add(Belka(id = 121, tailColor = "Black", name = "Murka"))
        add(Belka(id = 122, tailColor = "Black", name = "Murka"))
        add(Belka(id = 123, tailColor = "Black", name = "Murka"))
    }

    // Создаем поток для хранения
    private val belkaMutableState = MutableStateFlow<List<Belka>>(arrayMockBelka)
/*     Чтобы вытащить из потока в конкретный момент belkaMutableState
     Публичная переменная для чтения состояния (Это связано с инкапсуляцией)    */
    val _belkaState = belkaMutableState.asStateFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoomSQLTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {

        var belkaText by remember { mutableStateOf<String>("") } // by - для того, чтобы не вызывать атрибут .value
        var belkaID by remember { mutableStateOf<String>("") }
        val arrayBelka = _belkaState.collectAsState()

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.padding(100.dp))
            TextField(
                value = belkaID,
                onValueChange = {it -> belkaID = it}
            ) // Аналог EditText
            TextField(
                value = belkaText,
                onValueChange = {it -> belkaText = it}
            ) // Аналог EditText
            Button(onClick = {
                GlobalScope.launch { // GlobalScope чтобы не захламлять основной поток
                    SingletoneDB.db.belkaDao().insertBelka(Belka(0, "Black", belkaText))
                    belkaMutableState.value = SingletoneDB.db.belkaDao().getAllBelka()
                }

            }) {
                Text(text = "Кнопка Добавления")
            }
            Button(onClick = {
                GlobalScope.launch { // GlobalScope чтобы не захламлять основной поток
                    if (belkaID.isDigitsOnly()) {
//                        SingletoneDB.db.belkaDao().deleteBelka(Belka(id=belkaID.toInt(), name=belkaText))
                        SingletoneDB.db.belkaDao().deleteBelkaByID(belkaID.toInt())
                        belkaMutableState.value = SingletoneDB.db.belkaDao().getAllBelka()
                    }
                }
            }) {
                Text(text = "Кнопка Удаления")
            }
            Button(onClick = {
                GlobalScope.launch { // GlobalScope чтобы не захламлять основной поток
                    SingletoneDB.db.belkaDao().updateBelka(Belka(id=belkaID.toInt(), name=belkaText))
                    belkaMutableState.value = SingletoneDB.db.belkaDao().getAllBelka()
                }
            }) {
                Text(text = "Кнопка Обновления")
            }


            LazyColumn {
                items(arrayBelka.value) { belka -> // Функция для обработки каждого объекта в LazyColumn
                    Card( // Нужен для обертки объектов
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceDim
                        ),
                        modifier = Modifier
                            .size(width = 240.dp, height = 75.dp)
                    ) {
                        Column {
                            Text(text = "${belka.id}",
                                textAlign = TextAlign.Center)
                            Text(text = belka.name,
                                textAlign = TextAlign.Center)
                            Text(text = belka.tailColor,
                                textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        RoomSQLTheme {
            Greeting("Android")
        }
    }
}

