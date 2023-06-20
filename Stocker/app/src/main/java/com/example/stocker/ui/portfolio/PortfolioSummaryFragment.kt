package com.example.stocker.ui.portfolio


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.stocker.R
import com.example.stocker.data.model.Stock
import com.example.stocker.utils.autoCleared
import com.example.stocker.databinding.PortfolioSummaryFragmentBinding
import com.example.stocker.ui.StockViewModel
import com.example.stocker.ui.StocksViewModel
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PortfolioSummaryFragment : Fragment() {

    private val stocksViewModel: StocksViewModel by activityViewModels()
    private val stockViewModel: StockViewModel by activityViewModels()
    private var binding: PortfolioSummaryFragmentBinding by autoCleared()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PortfolioSummaryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.setTitle(R.string.title_portfolio_summary)

        stocksViewModel.stocks.observe(viewLifecycleOwner) { stocks ->
            var currentTotalValue = 0.0
            var buyingTotalValue = 0.0
            stocks.forEach { stock ->
                currentTotalValue += stock.stockQuote?.close!!.toFloat() * stock.buyingAmount!!.toFloat()
                buyingTotalValue += stock.buyingPrice!! * stock.buyingAmount!!.toFloat()
            }
            binding.currentPortfolioValue.text = String.format("%.2f", currentTotalValue)
            binding.portfolioChangeValue.text = getString(
                R.string.add_percents,
                String.format("%.2f", (1 - (currentTotalValue / buyingTotalValue)) * 100)
            )
//
//            val portfolioEntries = ArrayList<Entry>()
//            val spEntries = ArrayList<Entry>()
//            val spStock = Stock("SPY")
//            stockViewModel.setChosenStock(spStock)
//
//            stockViewModel.refreshStockData("SPY",requireContext(), PortfolioSummaryFragment())

//
//            if (timeSeries != null) {
//                val entries = mutableListOf<Entry>()
//                val labels = mutableListOf<String>()
//
//                timeSeries.forEachIndexed { index, data ->
//                    entries.add(Entry(index.toFloat(), data.avgprice.toFloat()))
//                    labels.add(data.datetime)
//                }


                // Populate the portfolioEntries and spEntries with appropriate values
                // Assuming you have a list of portfolio values and S&P values over time

//                val portfolioDataSet = LineDataSet(portfolioEntries, "Portfolio Value")
//                portfolioDataSet.setDrawCircles(false)
//                portfolioDataSet.setDrawValues(false)
//                portfolioDataSet.color = ColorTemplate.COLORFUL_COLORS[0]
//
//                val spDataSet = LineDataSet(spEntries, "S&P Value")
//                spDataSet.setDrawCircles(false)
//                spDataSet.setDrawValues(false)
//                spDataSet.color = ColorTemplate.COLORFUL_COLORS[1]
//
//                val dataSets: MutableList<ILineDataSet> = ArrayList()
//                dataSets.add(portfolioDataSet)
//                dataSets.add(spDataSet)
//
//                val lineData = LineData(dataSets)
//
//                val chart = binding.portfolioGraph
//                chart.data = lineData
//                chart.description.isEnabled = false
//                chart.legend.isEnabled = true
//
//                val xAxis = chart.xAxis
//                xAxis.position = XAxisPosition.BOTTOM
//                xAxis.setDrawGridLines(false)
//                xAxis.setAvoidFirstLastClipping(true)
//
//                val yAxisLeft = chart.axisLeft
//                yAxisLeft.setDrawGridLines(true)
//                yAxisLeft.axisMinimum = 0f
//
//                val yAxisRight = chart.axisRight
//                yAxisRight.setDrawGridLines(false)
//                yAxisRight.axisMinimum = 0f
//
//                chart.invalidate()
//            }
        }

        // ...
    }


}
