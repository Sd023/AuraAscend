package com.sdapps.auraascend.view.home.fragments.stats

import android.graphics.drawable.Drawable

data class StatsBO(
    var statsIcon : Drawable? = null,
    var statsName : String = "",
    var statsAchievement: Int = 0
)