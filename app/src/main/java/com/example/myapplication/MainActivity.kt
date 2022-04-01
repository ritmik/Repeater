package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.util.*


class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var speechRecognizer: SpeechRecognizer
    private var recognitionListener = MyRecognitionListener()
    private var recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

    private lateinit var textToSpeech: TextToSpeech
    private val myUtteranceProgressListener = MyUtteranceProgressListener()

    private lateinit var audioManager : AudioManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkTTS()

        audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0)

        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), 123)

        recognizerIntent.apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(applicationContext)

        speechRecognizer.setRecognitionListener(recognitionListener)
        recognitionListener.callBack = { resultStrings ->
            Log.d("TAGt", "resultString = ${resultStrings[0]}")
            if (resultStrings[0].contains("error")) {
                speechRecognizer.startListening(recognizerIntent)
            }else {
                textToSpeech.speak(resultStrings[0], TextToSpeech.QUEUE_ADD, null, "tts_utterance_id =${resultStrings[0]}_ ")
            }
        }

        Log.d("TAGt", "SpeechRecognizer.isRecognitionAvailable = ${SpeechRecognizer.isRecognitionAvailable(applicationContext)}")
    }

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            Log.e("TAGt", "Init Failed!")
            return
        }

        val listOfVoicesDefaultLanguage = textToSpeech.voices.filter { it.locale == Locale.getDefault() }
        val voiceNameContainsLocal = listOfVoicesDefaultLanguage.find { it.name.contains("local", true) } ?: listOfVoicesDefaultLanguage[0]
        val result = textToSpeech.setVoice(voiceNameContainsLocal)
        if (result == TextToSpeech.LANG_MISSING_DATA
            || result == TextToSpeech.LANG_NOT_SUPPORTED
        ) {
            Log.d("TAGt", "This Language is not supported result = $result")
            return
        }

        myUtteranceProgressListener.callBack = {
            runOnUiThread {
                speechRecognizer.startListening(recognizerIntent)
            }
        }
        textToSpeech.setOnUtteranceProgressListener(myUtteranceProgressListener)
        speechRecognizer.startListening(recognizerIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0)
    }

    private fun checkTTS() {
        val check = Intent(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA)
        startForResult.launch(check)
    }

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
            textToSpeech = TextToSpeech(this, this)
        } else {
            val installTTSIntent = Intent()
            installTTSIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
            startActivity(installTTSIntent)
        }
    }
}