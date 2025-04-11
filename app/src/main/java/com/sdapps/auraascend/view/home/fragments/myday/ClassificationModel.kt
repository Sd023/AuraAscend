package com.sdapps.auraascend.view.home.fragments.myday

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder


class ClassificationModel(context: Context) {

    private val interpreter: Interpreter
    private val vocab: Map<String, Int>
    private val maxSeqLen = 100
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
        vocab = loadVocab(context, "vocab.txt")
    }

    private fun padInput(input: List<Int>, maxLen: Int): IntArray {
        return if (input.size >= maxLen) {
            input.take(maxLen).toIntArray()
        } else {
            val padded = IntArray(maxLen) { 0 }
            input.forEachIndexed { i, value -> padded[i] = value }
            padded
        }
    }


    fun predictEmotion(text: String): String {
        val tokenIds = tokenize(text)

        val paddedInput = padInput(tokenIds, maxSeqLen)
        val attentionMask = paddedInput.map { if (it != 0) 1L else 0L }

        val inputIdsBuffer = ByteBuffer.allocateDirect(maxSeqLen * 8).order(ByteOrder.nativeOrder())
        val attentionMaskBuffer = ByteBuffer.allocateDirect(maxSeqLen * 8).order(ByteOrder.nativeOrder())

        for (i in 0 until maxSeqLen) {
            inputIdsBuffer.putLong(paddedInput[i].toLong())
            attentionMaskBuffer.putLong(attentionMask[i])
        }

        inputIdsBuffer.rewind()
        attentionMaskBuffer.rewind()

        val inputs = arrayOf<Any>(attentionMaskBuffer, inputIdsBuffer)
        val outputBuffer = ByteBuffer.allocateDirect(labels.size * 4).order(ByteOrder.nativeOrder())
        outputBuffer.rewind()

        val outputs = mutableMapOf<Int, Any>(0 to outputBuffer)
        interpreter.runForMultipleInputsOutputs(inputs, outputs)

        outputBuffer.rewind()
        val logits = FloatArray(labels.size) { outputBuffer.float }
        Log.d("Predict", "Logits: ${logits.joinToString()}")

        val predictedIdx = logits.indices.maxByOrNull { logits[it] } ?: -1
        return labels[predictedIdx] ?: "unknown"
    }

    private fun tokenize(text: String): List<Int> {
        val tokens = mutableListOf<String>()
        tokens.add("[CLS]")
        tokens.addAll(text.lowercase().split(" "))
        tokens.add("[SEP]")

        return tokens.map { token -> vocab[token] ?: vocab["[UNK]"] ?: 0 }
    }

    private fun loadVocab(context: Context, fileName: String): Map<String, Int> {
        val vocab = mutableMapOf<String, Int>()
        val inputStream = context.assets.open(fileName)
        inputStream.bufferedReader().useLines { lines ->
            lines.forEachIndexed { index, line ->
                vocab[line.trim()] = index
            }
        }
        return vocab
    }

}