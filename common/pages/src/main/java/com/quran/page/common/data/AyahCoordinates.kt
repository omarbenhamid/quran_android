package com.quran.page.common.data

data class AyahCoordinates(val page: Int,
                           val ayahCoordinates: Map<String, List<AyahBounds>>,
                           val lineBottoms: List<LineBottom>)
