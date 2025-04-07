package com.sdapps.auraascend.view.home.fragments.myday


import android.content.res.ColorStateList
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import com.sdapps.auraascend.DataViewModel
import com.sdapps.auraascend.R
import com.sdapps.auraascend.databinding.DialogMoodEntryBinding
import kotlin.getValue

class UserEntryDialogFragment(): DialogFragment() {

    companion object {
        val UED_DIALOG = "TAG"
    }

    private var _binding: DialogMoodEntryBinding? = null
    private val binding get() = _binding!!
    private lateinit var emotionIcons : List<ImageView>
    private lateinit var emotionLabels : List<String>
    private val vm: DataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogMoodEntryBinding.inflate(layoutInflater)

        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun init(){
        emotionIcons = listOf(binding.verySad,binding.sad,binding.normal,binding.happy,binding.veryHappy)
        emotionLabels = listOf("Very Unpleasant", "Unpleasant", "Neutral", "Pleasant", "Very Pleasant")

        val categories = listOf("💙 Family", "💰 Finance", "🌮 Food", "👯 Friends", "🏥 Health",
            "📚 Hobbies", "❤️ Relationship", "🏋 Sport", "🌤️ Weather", "💼 Work")

        val manRopeTypeface = ResourcesCompat.getFont(requireContext(), R.font.manrope_regular)

        categories.forEachIndexed { index, text ->
            val chip = Chip(requireContext()).apply {
                this.text = text
                isCheckable = true
                isClickable = true
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                setTextColor(ContextCompat.getColor(context, R.color.black))
                chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.GhostWhite))
                chipStrokeWidth = 1f
                setChipStrokeColorResource(R.color.LightGrey)
                this.typeface = manRopeTypeface

                setOnClickListener {
                    val pair = Pair(index, text)
                    if (isChecked) {
                        vm.setReasonChip(pair)
                        chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.Olive))
                        setTextColor(ContextCompat.getColor(context, R.color.white))
                    } else {
                        vm.setReasonChip(pair)
                        chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.WhiteSmoke))
                        setTextColor(ContextCompat.getColor(context, R.color.black))
                    }
                }

            }
            val layoutParams = FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(12, 5, 12, 5)
            }
            chip.layoutParams = layoutParams
            binding.flexChipContainer.addView(chip)

        }

        emotionIcons.forEachIndexed { index, imageView ->
            emotionIcons[index].colorFilter = getFilter()
            imageView.setOnClickListener {
                handleSmileyClick(index)
            }
        }

        binding.saveBtn.setOnClickListener {
            val value = vm.getEmotionText(emotionLabels)
            Log.d("CHIP", value)
        }
    }

    fun handleSmileyClick(selectedIcon : Int){
        emotionIcons.forEachIndexed { index, imageView ->
            vm.setEmotion(selectedIcon)
            if(selectedIcon == index){
                binding.emotionLabel.text = emotionLabels[index]
                imageView.clearColorFilter()
            } else {
                emotionIcons[index].colorFilter = getFilter()
            }

        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }


    fun getFilter(): ColorMatrixColorFilter {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f)
        return ColorMatrixColorFilter(matrix)
    }

}