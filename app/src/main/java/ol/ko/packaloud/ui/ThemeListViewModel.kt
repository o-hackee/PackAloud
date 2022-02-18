package ol.ko.packaloud.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ol.ko.packaloud.Text2SpeechService

class ThemeListViewModel(application: Application): AndroidViewModel(application) {
    companion object {
        const val currentPack = "five.json"
    }

    private val _packUi = MutableStateFlow<PackUi?>(null)
    val packUi: StateFlow<PackUi?> = _packUi

    val speechService: Text2SpeechService

    init {
        viewModelScope.launch {
            val pack = readAssets()
            _packUi.value = PackUi(pack)
        }

        speechService = Text2SpeechService(getApplication())
    }

    override fun onCleared() {
        super.onCleared()
        speechService.clear()
    }

    fun getThemeById(id: String): PackTheme? {
        return _packUi.value?.themes?.find { it.theme == id }
    }

    private fun readAssets(): Pack? {
        val jsonAdapter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build().adapter(Pack::class.java)
        return jsonAdapter.fromJson(getApplication<Application>().assets.open(currentPack).readBytes().toString(Charsets.UTF_8))
//        assert(pack!!.parts.all { part -> part.themes.all { packTheme -> packTheme.questions.size == 5 &&
//            packTheme.answers.size == 5 &&
//            packTheme.questions.all { it.isNotEmpty() } &&
//            packTheme.answers.all { it.isNotEmpty() }
//        } })
    }
}

data class PackUi(
    val pack: Pack?,
    val themes: List<PackTheme> = pack?.parts?.flatMap { it.themes } ?: listOf()
)

data class Pack(
    val title: String,
    val author: String,
    val thanks: String,
    val parts: List<PackPart>
)
data class PackPart(
    val name: String,
    val themes: List<PackTheme>
)
data class PackTheme(
    val theme: String,
    val questions: List<String>,
    val answers: List<String>
)
