package com.melancholicbastard.roomsql

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.melancholicbastard.roomsql.local.Belka
import com.melancholicbastard.roomsql.local.BelkaPhrase
import com.melancholicbastard.roomsql.local.BelkaWithPhrase
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

    private val arrayMockBelka = ArrayList<BelkaWithPhrase>().apply {
        add(BelkaWithPhrase(Belka(id = 121, tailColor = "Black", name = "Murka", 0), BelkaPhrase(0, "Фраза 1")))
        add(BelkaWithPhrase(Belka(id = 122, tailColor = "Black", name = "Murka", 0), BelkaPhrase(0, "Фраза 1")))
        add(BelkaWithPhrase(Belka(id = 123, tailColor = "Black", name = "Murka", 0), BelkaPhrase(0, "Фраза 1")))
    }

    // Создаем поток для хранения
    private val belkaWithPhraseMutableState = MutableStateFlow<List<BelkaWithPhrase>>(arrayMockBelka)
/*     Чтобы вытащить из потока в конкретный момент belkaMutableState
     Публичная переменная для чтения состояния (Это связано с инкапсуляцией)    */
    val _belkaState = belkaWithPhraseMutableState.asStateFlow()


    private val arrayMockPhrase = ArrayList<BelkaPhrase>().apply {
       add(BelkaPhrase(1, "Карл у Клары украл каралы"))
    }
    private val phraseMutableState = MutableStateFlow<List<BelkaPhrase>>(arrayMockPhrase)
    val _phraseState = phraseMutableState.asStateFlow()


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
        val arrayBelkaWithPhrase = _belkaState.collectAsState()     // можно заметить, что снизу нет .value

        var IDPhraseNext by remember { mutableStateOf(1) }  // запоминаем ID фразы

        var belkaPhrase by remember { mutableStateOf<String>("") } // by - для того, чтобы не вызывать атрибут .value
        val arrayPhrase = _phraseState.collectAsState().value

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.padding(50.dp))
            Row {
                Column(
                    modifier = Modifier
                        .padding(2.dp)
                ) {
                    TextField(
                        value = belkaID,
                        modifier = Modifier
                            .width(150.dp)
                            .align(Alignment.CenterHorizontally),
                        onValueChange = {it -> belkaID = it}
                    ) // Аналог EditText
                    TextField(
                        value = belkaText,
                        modifier = Modifier
                            .width(150.dp)
                            .align(Alignment.CenterHorizontally),
                        onValueChange = {it -> belkaText = it}
                    ) // Аналог EditText
                    Button(onClick = {

                        GlobalScope.launch { // GlobalScope чтобы не захламлять основной поток
                            belkaID.toIntOrNull()?.let { it ->
                                if (SingletoneDB.db.belkaDao().isBelkaExist(it)) {
                                    SingletoneDB.db.belkaDao().updateBelka(Belka(id=it, name=belkaText, phraseID = 1))
                                    belkaWithPhraseMutableState.value = SingletoneDB.db.belkaDao().getAllBelkaWithPhrase()
                                } else {
                                    belkaWithPhraseMutableState.value = SingletoneDB.db.belkaDao().getAllBelkaWithPhrase()
                                    IDPhraseNext = _belkaState.value.size + 1   // фраз должно быть на 1 больше нежели сущностей BelkaWithPhrase
                                    SingletoneDB.db.belkaDao().insertBelka(Belka(it, "Black", belkaText, IDPhraseNext))
                                    belkaWithPhraseMutableState.value = SingletoneDB.db.belkaDao().getAllBelkaWithPhrase()
                                }
                            }
                        }

                    }) {
                        Text(text = "Кнопка Добавления")
                    }
                    Button(onClick = {
                        GlobalScope.launch { // GlobalScope чтобы не захламлять основной поток
                            belkaID.toIntOrNull()?.let {
//                              SingletoneDB.db.belkaDao().deleteBelka(Belka(id=belkaID.toInt(), name=belkaText))
                                SingletoneDB.db.belkaDao().deleteBelkaByID(belkaID.toInt())
                                belkaWithPhraseMutableState.value = SingletoneDB.db.belkaDao().getAllBelkaWithPhrase()
                            }
                        }
                    }) {
                        Text(text = "Кнопка Удаления")
                    }

                    LazyColumn {
                        items(arrayBelkaWithPhrase.value) { belkaWithPhrase -> // Функция для обработки каждого объекта в LazyColumn
                            Card( // Нужен для обертки объектов
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceBright
                                ),
                                modifier = Modifier
                                    .width(width = 180.dp)
                                    .padding(2.dp)
                            ) {
                                Row {
                                    Column(
                                        modifier = Modifier
                                            .padding(5.dp),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "${belkaWithPhrase.belka.id}",
                                            textAlign = TextAlign.Center)
                                        Text(text = belkaWithPhrase.belka.name,
                                            textAlign = TextAlign.Center)
                                        Text(text = belkaWithPhrase.belka.tailColor,
                                            textAlign = TextAlign.Center)
                                    }
                                    Column(
                                        modifier = Modifier
                                            .weight(1.0f),  // Вес для второй колонки
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = belkaWithPhrase.phrase.phrase,
                                            textAlign = TextAlign.Center)
                                    }
                                }
                            }
                        }
                    }

                }
                Column(
                        modifier = Modifier
                            .padding(2.dp)
                ) {
                    TextField(
                        value = belkaPhrase,
                        modifier = Modifier
                            .width(150.dp)
                            .align(Alignment.CenterHorizontally),
                        onValueChange = {it -> belkaPhrase = it}
                    )
                    Button(onClick = {
                        GlobalScope.launch { // GlobalScope чтобы не захламлять основной поток
                            SingletoneDB.db.belkaDao().insertPhrase(BelkaPhrase(0, belkaPhrase))
                            phraseMutableState.value = SingletoneDB.db.belkaDao().getAllPhrase()

                        }
                    }) {
                        Text(text = "Вставить фразу")
                    }

                    LazyColumn {
                        items(arrayPhrase) { phrase -> // Функция для обработки каждого объекта в LazyColumn
                            Card( // Нужен для обертки объектов
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceBright
                                ),
                                modifier = Modifier
                                    .width(width = 180.dp)
                                    .padding(2.dp)
                            ) {
                                Row {
                                    Column(
                                        modifier = Modifier
                                            .padding(5.dp),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "" + phrase.phraseID,
                                            textAlign = TextAlign.Center)
                                        Text(text = "" + phrase.phrase,
                                            textAlign = TextAlign.Center)
                                    }
                                }
                            }
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

