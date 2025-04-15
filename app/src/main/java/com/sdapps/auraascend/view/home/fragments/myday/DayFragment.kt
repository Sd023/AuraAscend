package com.sdapps.auraascend.view.home.fragments.myday

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sdapps.auraascend.DataViewModel
import com.sdapps.auraascend.core.DateUtils
import com.sdapps.auraascend.databinding.FragmentDayBinding
import com.sdapps.auraascend.view.home.fragments.funactivity.swipeaquote.SwipeQuoteActivity

class DayFragment : Fragment() , OnDialogDismiss{

    private var _binding: FragmentDayBinding? = null
    private val binding get() = _binding!!

    private val vm: DataViewModel by viewModels()
    var isClicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
       _binding = FragmentDayBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        binding.dateView.text = DateUtils.getToday()


        binding.quotesCardView.setOnClickListener {
            startActivity(Intent(requireActivity(), SwipeQuoteActivity::class.java))
        }

        vm.quote.observe(viewLifecycleOwner) { dayQuotes ->
            binding.quoteText.text = buildString { append(dayQuotes.quote).append(" - ").append(dayQuotes.author) }
        }
        vm.fetchQuote()

        binding.addStory.setOnClickListener {
            UserEntryDialogFragment(this).show(requireActivity().supportFragmentManager, UserEntryDialogFragment.UED_DIALOG)
            isClicked = true
        }
        binding.profileView.setOnClickListener {
            startActivity(Intent(requireActivity(), ProfileActivity::class.java))
        }

        binding.goodDayCard.setOnClickListener { openActivity(HAPPY) }
        binding.neutralDayCard.setOnClickListener { openActivity(NEUTRAL) }
        binding.roughDayCard.setOnClickListener { openActivity(ROUGH) }
    }

    private fun openActivity(isFrom : String){
        val intent = Intent(requireActivity(), LogActivity::class.java)
        intent.putExtra("moduleCode", isFrom)
        requireContext().startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDialogDismissed() {
        isClicked = false
    }

    companion object {
        const val HAPPY = "happy"
        const val NEUTRAL = "neutral"
        const val ROUGH = "rough"
    }
}