package com.karanchuk.roman.testtranslate.common

import android.util.Log
import ru.yandex.speechkit.Error
import ru.yandex.speechkit.Language
import ru.yandex.speechkit.OnlineModel
import ru.yandex.speechkit.OnlineRecognizer
import ru.yandex.speechkit.Recognition
import ru.yandex.speechkit.RecognizerListener
import ru.yandex.speechkit.Track

interface Recognizer {
    fun init(language: Language)
    fun reset()
    fun recognize()
}

class RecognizerImpl : Recognizer {

    private var recognizer: ru.yandex.speechkit.Recognizer? = null

    private val mRecognizerListener: RecognizerListener = object : RecognizerListener {
        override fun onRecordingBegin(recognizer: ru.yandex.speechkit.Recognizer) {
            Log.d("myLogs", " onRecordingBegin")
        }

        override fun onSpeechDetected(recognizer: ru.yandex.speechkit.Recognizer) {
            Log.d("myLogs", " onSpeechDetected")
        }

        override fun onSpeechEnds(recognizer: ru.yandex.speechkit.Recognizer) {
            Log.d("myLogs", " onSpeechEnds")
        }

        override fun onRecordingDone(recognizer: ru.yandex.speechkit.Recognizer) {
            Log.d("myLogs", " onRecordingDone")
//            if (mView != null && mView.isAdded()) {
//                mView.deactivateVoiceRecognizer()
//            }
        }

        override fun onPowerUpdated(recognizer: ru.yandex.speechkit.Recognizer, v: Float) {
            Log.d("myLogs", " onPowerUpdated")
        }

        override fun onPartialResults(
            recognizer: ru.yandex.speechkit.Recognizer,
            recognition: Recognition,
            endOfUtterance: Boolean
        ) {
            Log.d("myLogs", " onPartialResults")
            if (endOfUtterance) {
//                if (mView != null) {
//                    mView.mCustomEditText.setText(recognition.bestResultText)
//                    mView.stopAnimationMicroWaves()
//                }
            }
        }

        override fun onRecognitionDone(recognizer: ru.yandex.speechkit.Recognizer) {}
        override fun onRecognizerError(recognizer: ru.yandex.speechkit.Recognizer, error: Error) {
            Log.d("myLogs", " onError")
//            if (mView != null) {
//                mView.showError()
//                UIUtils.showToast(
//                    mView.getContext(),
//                    mView.getContext().getResources().getString(R.string.connection_error_content)
//                )
//            }
        }

        override fun onMusicResults(recognizer: ru.yandex.speechkit.Recognizer, track: Track) {}
    }

    override fun init(language: Language) {
        // To create a new recognizer, specify the language,
        // the model - a scope of recognition to get the most appropriate results,
        // set the listener to handle the recognition events.
        recognizer = OnlineRecognizer.Builder(language, OnlineModel.NOTES, mRecognizerListener)
                .setDisableAntimat(false)
                .setEnablePunctuation(true)
                .build()
    }

    override fun reset() {
        recognizer?.cancel()
        recognizer = null
    }

    override fun recognize() {
        recognizer?.cancel()
        recognizer?.prepare()
        recognizer?.startRecording()
    }
}