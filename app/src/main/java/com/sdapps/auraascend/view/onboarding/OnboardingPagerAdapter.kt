package com.sdapps.auraascend.view.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sdapps.auraascend.view.onboarding.fragments.OnboardingFragment


data class Questionairee(
    var title: String = "",
    var options : List<String> = listOf(),
    var checkedState : Boolean = false
)

class OnboardingPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity){

    private val questions = listOf(
        Questionairee(title = "How are you feeling today?",
            options = listOf("Happy", "Sad", "Anxious", "Excited", "Tired")),
        Questionairee(title = "What is affecting your mood?",
            options = listOf("Work", "Relationships", "Health", "Weather", "Finances")),
        Questionairee(title = "How is your energy level?",
            options =  listOf("Low", "Moderate", "High", "Exhausted", "Overactive")),
        Questionairee(title =  "How well did you sleep last night?",
            options = listOf("Very Well", "Okay", "Not Good", "Barely Slept", "Insomnia")),
        Questionairee(title = "What activities help boost your mood?",
            options = listOf("Music", "Exercise", "Talking to Friends", "Meditation", "Watching Movies"))
    )

    override fun createFragment(position: Int): Fragment {
        val question = questions[position].title
        val options = questions[position].options
        val state = questions[position].checkedState
        return OnboardingFragment.newInstance(position, question, options, state)
    }

    override fun getItemCount(): Int {
        return questions.size
    }


}