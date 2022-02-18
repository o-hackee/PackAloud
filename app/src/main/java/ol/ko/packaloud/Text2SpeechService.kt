package ol.ko.packaloud

import android.content.Context
import android.util.Log
import com.microsoft.cognitiveservices.speech.SpeechConfig
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails
import com.microsoft.cognitiveservices.speech.SpeechSynthesisEventArgs
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer

class Text2SpeechService constructor(private val context: Context) {
    companion object {
        private const val synthesisVoice = "ru-RU-DmitryNeural"
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
        synthesizer.close()
        speechConfig.close()
    }

    /**
     * @param rate descriptive or numerical, hence string; 0.85 sounds good
     * @param style: narration-professional, narration-relaxed or empty, i.e. NO express-as element
     */
    fun readAloud(text: String, rate: String, style: String) {
        Log.d("OLKO", "with rate $rate style $style")
        val textWithStyle = if (style.isEmpty()) text else
"""            <mstts:express-as style="$style">
                $text
            </mstts:express-as>"""
        val ssmlWrapped =
"""<speak version="1.0" xmlns="http://www.w3.org/2001/10/synthesis"
       xmlns:mstts="https://www.w3.org/2001/mstts" xml:lang="ru-RU">
    <voice name="$synthesisVoice">
        <prosody rate="$rate">
            $textWithStyle
        </prosody>
    </voice>
</speak>"""
        synthesizer.SpeakSsmlAsync(ssmlWrapped)
    }
}