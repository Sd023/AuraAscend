package com.sdapps.auraascend.view.home.fragments.myday


import android.animation.Animator
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.text.InputFilter
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.chip.Chip
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.sdapps.auraascend.DataViewModel
import com.sdapps.auraascend.R
import com.sdapps.auraascend.core.CustomProgressDialog
import com.sdapps.auraascend.core.NetworkTools
import com.sdapps.auraascend.core.SharedPrefHelper
import com.sdapps.auraascend.core.room.AppDatabase
import com.sdapps.auraascend.core.room.EmotionDao
import com.sdapps.auraascend.core.room.EmotionEntity
import com.sdapps.auraascend.databinding.DialogMoodEntryBinding
import com.sdapps.auraascend.view.home.fragments.PodcastHelper.openPodcastsList
import com.sdapps.auraascend.view.home.fragments.funactivity.FunActivityFragment
import kotlinx.coroutines.launch
import java.time.LocalDate

class UserEntryDialogFragment(var onDialogDismissed: OnDialogDismiss) : DialogFragment() {

    companion object {
        val UED_DIALOG = "TAG"
    }

    private var _binding: DialogMoodEntryBinding? = null
    private val binding get() = _binding!!
    private lateinit var smileyEmotionIcons: List<ImageView>
    private lateinit var smileyEmotionLabels: List<String>
    private lateinit var viewModel: DataViewModel
    private lateinit var spRef: SharedPrefHelper

    private lateinit var progressDialog: CustomProgressDialog
    private lateinit var classifer: ClassificationModel
    private var userInput = ""
    private lateinit var appDB : AppDatabase
    private lateinit var dao : EmotionDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[DataViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogMoodEntryBinding.inflate(layoutInflater)
        prepView()
        init()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        classifer = ClassificationModel(requireContext())
        spRef = SharedPrefHelper(requireContext())
        progressDialog = CustomProgressDialog(requireContext())
        appDB = AppDatabase.getDatabase(requireContext())
        dao = appDB.getAppDAO()
    }

    private fun prepView() {
        binding.editTextDayEntry.filters = arrayOf(InputFilter.LengthFilter(100))
        smileyEmotionIcons =
            listOf(binding.verySad, binding.sad, binding.normal, binding.happy, binding.veryHappy)
        smileyEmotionLabels =
            listOf("Very Unpleasant", "Unpleasant", "Neutral", "Pleasant", "Very Pleasant")

        val chipCategoriesAffectsEmotion = listOf(
            "💙 Family", "💰 Finance", "🌮 Food", "👯 Friends", "🏥 Health",
            "📚 Hobbies", "❤️ Relationship", "🏋 Sport", "🌤️ Weather", "💼 Work"
        )

        val manRopeTypeface = ResourcesCompat.getFont(requireContext(), R.font.manrope_regular)

        chipCategoriesAffectsEmotion.forEachIndexed { index, text ->
            val chip = Chip(requireContext()).apply {
                this.text = text
                isCheckable = true
                isClickable = true
                setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                setTextColor(ContextCompat.getColor(context, R.color.black))
                chipBackgroundColor =
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.GhostWhite))
                chipStrokeWidth = 1f
                setChipStrokeColorResource(R.color.LightGrey)
                this.typeface = manRopeTypeface

                setOnClickListener {
                    val pair = Pair(index, text)
                    if (isChecked) {
                        viewModel.setReasonCategories(pair)
                        chipBackgroundColor =
                            ColorStateList.valueOf(ContextCompat.getColor(context, R.color.Olive))
                        setTextColor(ContextCompat.getColor(context, R.color.white))
                    } else {
                        viewModel.setReasonCategories(pair)
                        chipBackgroundColor = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                context,
                                R.color.WhiteSmoke
                            )
                        )
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

        smileyEmotionIcons.forEachIndexed { index, imageView ->
            viewModel.setEmotionLabel("")
            smileyEmotionIcons[index].colorFilter = getFilter()
            imageView.setOnClickListener {
                handleSmileyClick(index)
            }
        }

    }

    fun init() {
        viewModel.emotionResult.observe(viewLifecycleOwner) { predtEmotion ->
            uploadDataToFirebase(predictedEmotion = predtEmotion)
            Toast.makeText(requireContext(), "You are feeling $predtEmotion", Toast.LENGTH_LONG)
                .show()
        }


        binding.saveBtn.setOnClickListener {
            userInput = binding.editTextDayEntry.text.toString().lowercase()
            progressDialog.showLoadingProgress("Saving and Uploading data")
            if (userInput.isNotEmpty()) {
                lifecycleScope.launch {
                    viewModel.predict(classifer, userInput)
                }
            } else {
                uploadDataToFirebase(predictedEmotion = "")
            }
        }
    }

    private fun uploadDataToFirebase(predictedEmotion: String) {
        if (NetworkTools.isNetworkConnected(requireContext())) {
            logDailyEmotion(viewModel.getEmotionLabel() ?: "", predictedEmotion)
        } else {
            Toast.makeText(
                requireContext(),
                "No internet, please connect to network",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun promptUserToDoActivities() {

    }

    private fun logDailyEmotion(userSelectedMood: String, predictedEmotion: String) {
        try {
            val reasons = viewModel.getReasons()
            val fdb = FirebaseFirestore.getInstance()
            val date = LocalDate.now().toString()
            spRef.setPredictedMoodToSharedPref(predictedEmotion)

            val serializedDataSet = viewModel.getReasonCategories()?.map {
                mapOf("reason" to it.second) // we are not using index value to store it in firebase.
            }
            val insertQuery = EmotionEntity(
                timestamp = System.currentTimeMillis(),
                date = date,
                userInput = userInput,
                userSelectedMood = userSelectedMood,
                userSelectedCategories = reasons,
                predictedMood = predictedEmotion
            )
            viewModel.insertIntoDB(insertQuery,dao)

            val frbData = hashMapOf(
                "timestamp" to Timestamp.now(),
                "date" to date,
                "userInput" to userInput,
                "userSelectedMood" to userSelectedMood,
                "userSelectedCategories" to serializedDataSet,
                "predictedMood" to predictedEmotion
            )
            val currentUser = spRef.getCurrentUser()

            fdb.collection("users")
                .document(currentUser)
                .collection("mood_history")
                .add(frbData)
                .addOnSuccessListener {
                    showCompletion(true,predictedEmotion)
                }.addOnFailureListener { err ->
                    showToast(err.message.toString())
                    showCompletion(false,"")
                }
        } catch (ex: Exception) {
            ex.printStackTrace()
            showToast(ex.message.toString())
            showCompletion(false,"")
        }

    }

    fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show()
    }

    fun handleSmileyClick(selectedIcon: Int) {
        smileyEmotionIcons.forEachIndexed { index, imageView ->
            viewModel.setEmotionLabel(smileyEmotionLabels[selectedIcon])

            if (selectedIcon == index) {
                binding.emotionLabel.text = smileyEmotionLabels[index]
                imageView.clearColorFilter()
            } else {
                viewModel.setEmotion(selectedIcon)
                smileyEmotionIcons[index].colorFilter = getFilter()
            }

        }
    }

    private fun showCompletion(isCompleted: Boolean,predictedEmotion: String) {
        var animation = R.raw.something_went_wrong

        if (isCompleted) {
            animation = R.raw.upload_success
        }
        progressDialog.closePialog()
        binding.userInputDialog.visibility = View.GONE
        binding.completionStatusLayout.visibility = View.VISIBLE

        binding.lottieAnimationView.apply {
            visibility = View.VISIBLE
            setAnimation(animation)
            playAnimation()

            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    showButton(predictedEmotion)
                }

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {}

            })
        }
    }

    private fun showButton(predictedEmotion: String) {

        binding.statusMsg.apply {
            alpha = 0f
            scaleX = 0.95f
            scaleY = 0.95f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(DecelerateInterpolator())
                .setListener(null)
                .start()

        }

        binding.finishBtn.apply {
            alpha = 0f
            scaleX = 0.95f
            scaleY = 0.95f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setListener(null)
                .start()

            setOnClickListener {
                onDialogDismissed.onDialogDismissed()
                checkAndSuggestPodcasts(this@UserEntryDialogFragment,predictedEmotion)
            }
        }
    }

    private fun checkAndSuggestPodcasts(uEntry: UserEntryDialogFragment, predictedEmotion: String){
        when(predictedEmotion){
            "sadness","anger","fear" -> {
                progressDialog.showThemedDialog("Feeling like unwinding or getting inspired?, Tune into a podcast that fits your mood.",
                    object : DialogInterface.OnClickListener{
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            dialog?.dismiss()
                            openPodcastsList(requireActivity(),progressDialog,viewModel,viewLifecycleOwner,spRef,uEntry)
                        }
                })
            } else -> {
                uEntry.dismiss()
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