package com.sdapps.auraascend.view.home.fragments.stats

import android.graphics.drawable.Drawable

data class StatsBO(
    var statsIcon : Drawable,
    var statsName : String = "",
    var statsAchievement: String = ""
)