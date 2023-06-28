package com.yehudadir.stocker.utils

import android.app.DatePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun showDatePicker(context: Context, callback: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                callback(selectedDate)
        }, year, month, day)

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
}

fun convertDateFormat(date: String): String {
        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val parsedDate = inputFormat.parse(date)
        return outputFormat.format(parsedDate)
}

fun convertLongToShortDateFormat(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd.M", Locale.getDefault())

        val parsedDate = inputFormat.parse(date)
        return outputFormat.format(parsedDate)
}
