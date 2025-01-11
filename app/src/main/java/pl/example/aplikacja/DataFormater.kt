package pl.example.aplikacja

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDateTimeSpecificLocale(date: Date): String {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("pl", "PL"))
    return dateFormat.format(date)
}

fun removeQuotes(id: String): String {
    return id.replace("\"", "")
}