package com.example.prettyvk

import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.VK
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.internal.ApiCommand
import com.vk.api.sdk.requests.VKRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.json.JSONArray


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Авторизация
        VK.login(this, setOf(VKScope.EMAIL, VKScope.FRIENDS, VKScope.PHOTOS))
        setContent {
            VKComposeScreen()
        }
    }
}

@Composable
fun VKComposeScreen() {
    var userInfo by remember { mutableStateOf("Нажмите кнопку для получения данных") }
    var isLoading by remember { mutableStateOf(false) }

    // UI интерфейс с текстом и кнопкой
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = userInfo)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            //Запуск запроса при нажатии на кнопку
            userInfo = "haha"
//            getUserAccount("270538486") { result ->
//                userInfo = result
//            }
        }) {
            Text(text = "Получить данные пользователя")
        }
    }
}


fun getUserAccount(userName: String, onResult: (String) -> Unit) {
    // Параметры запроса
    val params = "user_ids=$userName&v=5.131"

    // Создаем асинхронную задачу
    GlobalScope.launch {
        try {
            // Запрос к VK API, ожидаем JSON массив
            val request = object : VKRequest<JSONArray>("users.get", params) {}

            // Выполнение запроса и получение ответа
            val response = JSONArray(VK.execute(request))

            withContext(Dispatchers.Main) {
                try {
                    // Проверяем тип ответа и приводим к JSONArray
                    if (response.length() > 0) {
                        val user = response.getJSONObject(0)
                        val firstName = user.getString("first_name")
                        val lastName = user.getString("last_name")
                        onResult("Имя: $firstName, Фамилия: $lastName")
                    } else {
                        onResult("Пользователь не найден")
                    }
                } catch (e: Exception) {
                    onResult("Ошибка обработки данных: ${e.localizedMessage}")
                }
            }
        } catch (e: Exception) {
            // Обработка ошибок запроса
            withContext(Dispatchers.Main) {
                onResult("Ошибка при запросе: ${e.localizedMessage}")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    VKComposeScreen()
}