package ol.ko.packaloud

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails
import com.microsoft.cognitiveservices.speech.SpeechSynthesisEventArgs
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer
import java.util.concurrent.Future

class Text2SpeechService private constructor(val context: Context) {
    companion object {
        private const val synthesisVoice = "ru-RU-DmitryNeural"

        // TODO
        private var instance: Text2SpeechService? = null

        fun get(context: Context): Text2SpeechService {
            return instance ?: Text2SpeechService(context.applicationContext).also {
                instance = it
            }
        }
    }

    private val speechConfig: SpeechConfig by lazy {
        val key = context.getString(R.string.speech_service_subscription_key)
        val region = context.getString(R.string.speech_service_region)
        SpeechConfig.fromSubscription(key, region).also {
            it.speechSynthesisVoiceName = synthesisVoice
        }
    }
    private val synthesizer: SpeechSynthesizer by lazy {
        // use the default speaker on the system for audio output
        SpeechSynthesizer(speechConfig).also {
            it.SynthesisCanceled.addEventListener { _, e: SpeechSynthesisEventArgs ->
                val cancellationDetails = SpeechSynthesisCancellationDetails.fromResult(e.result)
                Log.d("OLKO","Error synthesizing: ${e.result.resultId}. Error detail: $cancellationDetails")
                e.close()
            }
        }
    }

    fun clear() {
        // TODO
        synthesizer.close()
        speechConfig.close()
    }

    fun readAloud(text: String) {
        synthesizer.SpeakTextAsync(text)
    }
}