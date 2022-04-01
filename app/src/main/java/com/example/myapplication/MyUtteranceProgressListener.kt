package com.example.myapplication

import android.speech.tts.UtteranceProgressListener
import android.util.Log

class MyUtteranceProgressListener : UtteranceProgressListener() {
    lateinit var callBack: () -> Unit

    override fun onStart(p0: String?) {}

    override fun onDone(p0: String?) = callBack()

    override fun onError(p0: String?) {}
}