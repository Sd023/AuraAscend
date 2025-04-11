package com.sdapps.auraascend.view.home.fragments.myday

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder


class ClassificationModel(context: Context) {

    private val interpreter: Interpreter
    private val vocabDataSet: Map<String, Int>
    private val maxPaddedLength = 100

    private val labels = mapOf(
        0 to "sadness",
        1 to "joy",
        2 to "love",
        3 to "anger",
        4 to "fear",
        5 to "surprise"
    )

    init {
        val model = FileUtil.loadMappedFile(context, "model.tflite")
        interpreter = Interpreter(model)
        vocabDataSet = loadVocabTextFile(context, "vocab.txt")
    }

    private fun getPaddedInput(input: List<Int>, maxLen: Int): IntArray {
        return if (input.size >= maxLen) {
            input.take(maxLen).toIntArray()
        } else {
            val padded = IntArray(maxLen)
            input.forEachIndexed { i, value -> padded[i] = value }
            padded
        }
    }


    fun predictEmotion(text: String): String {
        val tokenizedWords = getTokenizedWords(text)

        val paddedInput = getPaddedInput(tokenizedWords, maxPaddedLength)
        val maskedInput = paddedInput.map { if (it != 0) 1L else 0L } // 0 is for paddings, 1 is for actual words.

        val inputBuffer = ByteBuffer.allocateDirect(maxPaddedLength * 8).order(ByteOrder.nativeOrder())
        val maskedInputBuffer = ByteBuffer.allocateDirect(maxPaddedLength * 8).order(ByteOrder.nativeOrder())

        for (i in 0 until maxPaddedLength) {
            inputBuffer.putLong(paddedInput[i].toLong())
            maskedInputBuffer.putLong(maskedInput[i])
        }

        inputBuffer.rewind()
        maskedInputBuffer.rewind()

        val inputForModel = arrayOf<Any>(maskedInputBuffer, inputBuffer)
        val outputBuffer = ByteBuffer.allocateDirect(labels.size * 4).order(ByteOrder.nativeOrder())
        outputBuffer.rewind()

        val outputs = mutableMapOf<Int, Any>(0 to outputBuffer)
        interpreter.runForMultipleInputsOutputs(inputForModel, outputs)
        outputBuffer.rewind()

        val logBits = FloatArray(labels.size) { outputBuffer.float } // to get
        Log.d("Predict", "Logits: ${logBits.joinToString()}")

        val predictedIdx = logBits.indices.maxByOrNull { logBits[it] } ?: -1 // returning the predicted value from the logBits.
        return labels[predictedIdx] ?: "unknown"
    }

    private fun getTokenizedWords(text: String): List<Int> {
        val tokens = mutableListOf<String>()
        tokens.add("[CLS]")
        tokens.addAll(text.lowercase().split(" "))
        tokens.add("[SEP]")

        return tokens.map { token -> vocabDataSet[token] ?: vocabDataSet["[UNK]"] ?: 0 }
    }

    private fun loadVocabTextFile(context: Context, fileName: String): Map<String, Int> {
        val vFile = mutableMapOf<String, Int>()
        val inputStream = context.assets.open(fileName)

        inputStream.bufferedReader().useLines { lines ->
            lines.forEachIndexed { index, line ->
                vFile[line.trim()] = index
            }
        }
        return vFile
    }

}