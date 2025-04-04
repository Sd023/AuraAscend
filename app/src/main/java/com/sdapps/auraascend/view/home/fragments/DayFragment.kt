package com.sdapps.auraascend.view.home.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.sdapps.auraascend.DataViewModel
import com.sdapps.auraascend.core.DateUtils.getToday
import com.sdapps.auraascend.databinding.FragmentDayBinding

class DayFragment : Fragment() {

    private var _binding: FragmentDayBinding? = null
    private val binding get() = _binding!!

    private val vm: DataViewModel by viewModels()

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
        binding.dateView.text = getToday()

        vm.quote.observe(viewLifecycleOwner) { dayQuotes ->
            binding.quoteText.text = buildString { append(dayQuotes.quote).append(" - ").append(dayQuotes.author) }
        }
        vm.fetchQuote()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}