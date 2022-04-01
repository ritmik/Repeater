package com.example.myapplication

import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.util.Log

class MyRecognitionListener : RecognitionListener {
    lateinit var callBack: (ArrayList<String>) -> Unit

    override fun onReadyForSpeech(p0: Bundle?) {
    }

    override fun onBeginningOfSpeech() {
    }

    override fun onRmsChanged(p0: Float) {
    }

    override fun onBufferReceived(p0: ByteArray?) {
    }

    override fun onEndOfSpeech() {
    }

    override fun onError(p0: Int) {
        callBack(arrayListOf("error = $p0"))
    }

    override fun onResults(result: Bundle?) {
        val resultStrings = result?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) ?: arrayListOf("")
        callBack(resultStrings)
    }

    override fun onPartialResults(p0: Bundle?) {
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
    }
}