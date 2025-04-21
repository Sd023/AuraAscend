package com.sdapps.auraascend.view.home.fragments.stats

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.sdapps.auraascend.DataViewModel
import com.sdapps.auraascend.R
import com.sdapps.auraascend.core.SharedPrefHelper
import com.sdapps.auraascend.core.room.AppDatabase
import com.sdapps.auraascend.core.room.AppDAO
import com.sdapps.auraascend.databinding.FragmentStatsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StatsFragment : Fragment() {


    private lateinit var binding: FragmentStatsBinding
    private lateinit var spRef: SharedPrefHelper
    private val viewModel: DataViewModel by viewModels()

    private lateinit var dao: AppDAO
    private lateinit var appDatabase: AppDatabase
    private val moodLabelToInt = mapOf(
        "sadness" to 0,
        "joy" to 1,
        "love" to 2,
        "anger" to 3,
        "fear" to 4,
        "surprise" to 5
    )
    private val moodLabels = arrayOf("sadness", "joy", "love", "anger", "fear", "surprise")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDatabase = AppDatabase.getDatabase(requireContext())
        dao = appDatabase.getAppDAO()
        spRef = SharedPrefHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatsBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareChart()
        init()
    }

    private fun prepareChart(){
       /* viewModel.dailyMoodData.observe(viewLifecycleOwner) { moodMap ->
            val barData = generateMoodChartData(moodMap)
            setupChart(moodMap, barData)
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val end = LocalDate.now()
        val start = end.minusDays(1)
        viewModel.loadDominantMoods(dao,start.format(formatter), end.format(formatter))*/

        viewModel.allMoodEntries.observe(viewLifecycleOwner) { entries ->
            val todayMoodCounts = entries
                .filter { isToday(it.timestamp) }
                .groupingBy { moodLabelToInt[it.predictedMood] ?: -1 }
                .eachCount()

            val chartEntries = todayMoodCounts.map { (moodInt, count) ->
                BarEntry(moodInt.toFloat(), count.toFloat())
            }

            setupBarChart(chartEntries)
        }

        viewModel.getChartData(dao)
    }

    /*fun timestampToDateString(timestampMillis: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(timestampMillis))
    }

    private fun setupChart(moodMap: Map<String, Int>, barData: BarData) {

        val moodChart = binding.dailyBarChart
        moodChart.data = barData
        moodChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            setDrawGridLines(false)
            valueFormatter = IndexAxisValueFormatter(moodMap.keys.sorted())
        }

        moodChart.axisLeft.axisMinimum = 0f
        moodChart.axisLeft.granularity = 1f
        moodChart.axisRight.isEnabled = false
        moodChart.description.isEnabled = false
        moodChart.setFitBars(true)
        moodChart.invalidate()
    }

    fun generateMoodChartData(dailyMoods: Map<String, Int>): BarData {
        val entries = mutableListOf<BarEntry>()
        val dates = dailyMoods.keys.sorted()
        val xAxisLabels = mutableListOf<String>()

        dates.forEachIndexed { index, date ->
            val mood = dailyMoods[date] ?: -1
            if (mood >= 0) {
                entries.add(BarEntry(index.toFloat(), mood.toFloat()))
                xAxisLabels.add(date)
            }
        }

        val barDataSet = BarDataSet(entries, "Daily Mood")
        barDataSet.setColors(Color.parseColor("#607D8B"),
            Color.parseColor("#4CAF50"),
            Color.parseColor("#81C784"),
            Color.parseColor("#F44336"),
            Color.parseColor("#FF9800"),
            Color.parseColor("#FFEB3B")
        )
        barDataSet.valueTextSize = 12f
        barDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return moodLabels[value.toInt()] ?: ""
            }
        }

        return BarData(barDataSet)
    }*/

    private fun setupBarChart(entries: List<BarEntry>) {
        if(entries.isEmpty()){
            binding.dailyBarChart.setNoDataText("No data to display")
            binding.dailyBarChart.setNoDataTextColor(Color.RED)
        } else {
            val dataSet = BarDataSet(entries, "Today's Mood Count")
            dataSet.setColors(ColorTemplate.MATERIAL_COLORS.toList())
            dataSet.valueTextSize = 14f

            val barData = BarData(dataSet)
            val chart  = binding.dailyBarChart

            chart.data = barData
            chart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(moodLabels)
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textColor = Color.rgb(158, 158, 158)
                labelCount = moodLabels.size
            }

            chart.axisLeft.axisMinimum = 0f
            chart.axisLeft.textColor = Color.rgb(158, 158, 158)

            chart.axisRight.isEnabled = false
            chart.axisRight.textColor = Color.rgb(158, 158, 158)
            chart.description.isEnabled = false
            chart.legend.textColor = Color.rgb(158, 158, 158)
            chart.invalidate()
        }
    }

    private fun isToday(timestampMillis: Long): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val entryDate = sdf.format(Date(timestampMillis))
        val todayDate = sdf.format(Date())
        return entryDate == todayDate
    }
    private fun init(){
        val items = arrayListOf<StatsBO>(
            StatsBO(statsIcon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_stats_mindful), statsName = "Mindfulness", statsAchievement = spRef.getMindfulnessCount()),
            StatsBO(statsIcon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_stats_story),statsName = "Story Time", statsAchievement = spRef.getStoriesReadCount()),
            StatsBO(statsIcon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_stats_game),statsName = "Ping Pong", statsAchievement = spRef.getHighScore()),
            StatsBO(statsIcon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_stats_quotes),statsName = "Swipe a Quote", statsAchievement = spRef.getQuotesRead())
        )
        val statsAdapter = StatsAdapter(spRef,items)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        binding.recyclerView.adapter = statsAdapter

    }

}