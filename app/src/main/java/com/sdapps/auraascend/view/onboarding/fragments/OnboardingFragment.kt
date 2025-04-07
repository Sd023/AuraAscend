package com.sdapps.auraascend.view.onboarding.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sdapps.auraascend.R
import com.sdapps.auraascend.DataViewModel
import com.sdapps.auraascend.view.onboarding.OnboardingActivity



/*
* Has Questions and Answers
* Value is from OnboardingPagerAdapter
* Each time user clicks ok, data loaded dynamically. question by question
* */
class OnboardingFragment : Fragment() {

    private lateinit var viewModel: DataViewModel

    private lateinit var rv : RecyclerView

    companion object {
        private const val ARG_INDEX = "arg_index"
        private const val ARG_QUESTION = "arg_question"
        private const val ARG_OPTIONS = "arg_options"
        private const val STATE = "checkbox_state"

        fun newInstance(index: Int, question: String, options: List<String>, state: Boolean): OnboardingFragment {
            return OnboardingFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_INDEX, index)
                    putString(ARG_QUESTION, question)
                    putStringArrayList(ARG_OPTIONS, ArrayList(options))
                    putBoolean(STATE, state)
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[DataViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv = view.findViewById<RecyclerView>(R.id.recyclerView)

        val index = arguments?.getInt(ARG_INDEX) ?: 0
        val questionText = arguments?.getString(ARG_QUESTION) ?: ""
        val options = arguments?.getStringArrayList(ARG_OPTIONS) ?: arrayListOf()
        val state = arguments?.getBoolean(STATE)

        val tvQuestion = view.findViewById<TextView>(R.id.tvQuestion)
        val btnContinue = view.findViewById<Button>(R.id.btnContinue)

        tvQuestion.text = questionText
        val selectedOptions = mutableListOf<String>()

        val mAdapter = AnswerAdapter(options, state!!) { selectedValue ->
            selectedOptions.clear()
            selectedOptions.add(selectedValue)
        }

        rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        rv.adapter = mAdapter

        btnContinue.setOnClickListener {
            if (selectedOptions.isNotEmpty()) {
                viewModel.saveResponse(questionText, selectedOptions)
                (activity as? OnboardingActivity)?.moveToNextPage()
            } else {
                Toast.makeText(requireContext(), "Please select at least one option", Toast.LENGTH_SHORT).show()
            }
        }


    }
}