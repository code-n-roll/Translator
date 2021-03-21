package com.karanchuk.roman.testtranslate.common

import android.util.Log
import ru.yandex.speechkit.Emotion
import ru.yandex.speechkit.Error
import ru.yandex.speechkit.Language
import ru.yandex.speechkit.OnlineVocalizer
import ru.yandex.speechkit.Synthesis
import ru.yandex.speechkit.Vocalizer
import ru.yandex.speechkit.VocalizerListener
import ru.yandex.speechkit.Voice

interface Vocalizer {
    fun init(language: Language)
    fun reset()
    fun vocalize(text: String)
}

class VocalizerImpl : com.karanchuk.roman.testtranslate.common.Vocalizer {

    private var vocalizer: Vocalizer? = null

    private val mVocalizerListener: VocalizerListener = object : VocalizerListener {
        override fun onSynthesisDone(vocalizer: Vocalizer) {}
        override fun onPartialSynthesis(vocalizer: Vocalizer, synthesis: Synthesis) {}
        override fun onPlayingBegin(vocalizer: Vocalizer) {
            Log.d("myLogs", " onPlayingBegin")
        }

        override fun onPlayingDone(vocalizer: Vocalizer) {
            Log.d("myLogs", " onPlayingDone")
//            if (mView != null) {
//                mView.hideLoadingTargetVoice()
//                mView.hideLoadingSourceVoice()
//                mView.showIconTargetVoice()
//                mView.showIconSourceVoice()
//            }
        }

        override fun onVocalizerError(vocalizer: Vocalizer, error: Error) {
            reset()
            Log.d("myLogs", error.toString())
            Log.d("myLogs", " onVocalizerError")
        }
    }

    override fun init(language: Language) {
        vocalizer = OnlineVocalizer.Builder(language, mVocalizerListener)
            .setEmotion(Emotion.GOOD)
            .setVoice(Voice.OMAZH)
            .build()
    }

    override fun reset() {
        vocalizer?.cancel()
        vocalizer = null
    }

    override fun vocalize(text: String) {
        vocalizer?.cancel()
        vocalizer?.prepare()
        vocalizer?.synthesize(text, Vocalizer.TextSynthesizingMode.APPEND)
    }
}