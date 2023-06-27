package com.yehudadir.stocker.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.yehudadir.stocker.R
import com.yehudadir.stocker.data.model.entities.PortfolioTimeSeriesValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentLinkedQueue

class GraphHelpers constructor(private val context: Context, private val lineChart: LineChart){
    fun ShowGraph(graphLabel: String,
                  timeSeries: ConcurrentLinkedQueue<PortfolioTimeSeriesValue>?,
                  timeSpan: Int = 5000) {
        setupLineChart()
        buildGraph(graphLabel, timeSeries, timeSpan)
    }

    private fun setupLineChart() {
        setGraphConfig()
        setNoDataTitle()
        setXAxis()
        setYAxis()
    }

    private fun buildGraph(graphLabel: String,
                           timeSeries: ConcurrentLinkedQueue<PortfolioTimeSeriesValue>?,
                           timeSpan: Int) {
        if (timeSeries.isNullOrEmpty()) {
            hideGraph()
        }
        else {
            val entries = mutableListOf<Entry>()
            val labels = mutableListOf<String>()

            timeSeries?.take(timeSpan)?.forEachIndexed { index, data ->
                val entryValue = 100 * (1 - (data.close / data.open))
                entries.add(Entry(index.toFloat(), entryValue))
                labels.add(convertLongToShortDateFormat(data.date))
            }

            val dataSet = LineDataSet(entries, graphLabel) // TODO: take from values.strings..

            dataSet.apply {
                setDrawValues(false)
                setDrawCircles(false)
                setDrawFilled(true)                     // Enable filled drawing
                color = Color.TRANSPARENT               // Set line color to transparent
                fillColor = Color.GREEN                 // Set fill color above the x-axis
                fillDrawable = createGradientDrawable() // Set the fill drawable
                lineWidth = 4f
                mode = LineDataSet.Mode.LINEAR
            }

            val lineData = LineData(dataSet)
            lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            lineChart.data = lineData
            lineChart.invalidate()
            lineChart.visibility = View.VISIBLE
        }
    }

    private fun setGraphConfig() {
        lineChart.isAutoScaleMinMaxEnabled = true
        lineChart.isKeepPositionOnRotation = true
        lineChart.isDragEnabled = false
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.setTouchEnabled(true)
        lineChart.setScaleEnabled(false) // TODO: is it relevant for stock details?
    }

    private fun setNoDataTitle() {
        lineChart.setNoDataText("No Data Available") // TODO: change to R.string...
        lineChart.setNoDataTextColor(R.color.red)
    }

    private fun setXAxis() {
        lineChart.xAxis.apply {
            setDrawGridLines(false)
            position = XAxis.XAxisPosition.BOTTOM
            axisLineColor = Color.BLUE // TODO: change color
            granularity = 1f

            valueFormatter = object : ValueFormatter() {
                private val dateFormatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                override fun getFormattedValue(value: Float): String {
                    val timestamp = value.toLong()
                    return dateFormatter.format(Date(timestamp))
                }
            }
        }
    }

    private fun setYAxis() {
        lineChart.axisLeft.apply {
            setDrawGridLines(true)
            gridColor = Color.LTGRAY    // TODO: change colors
            textColor = Color.DKGRAY

            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return context.getString(R.string.add_percents, value.toInt().toString())
                }
            }
        }
    }

    private fun hideGraph() {
        lineChart.clear()
        lineChart.invalidate()
        lineChart.visibility = View.GONE
    }

    private fun createGradientDrawable(): Drawable {
        val colors = intArrayOf(
            ContextCompat.getColor(context, R.color.green),
            ContextCompat.getColor(context, R.color.red)
        )

        return GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
    }
}