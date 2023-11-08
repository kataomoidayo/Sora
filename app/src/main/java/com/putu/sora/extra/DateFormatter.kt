package com.putu.sora.extra

import android.widget.TextView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun TextView.setDateFormat(timeStamp: String) {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val date = simpleDateFormat.parse(timeStamp) as Date
    val formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date)
    this.text = formattedDate
}