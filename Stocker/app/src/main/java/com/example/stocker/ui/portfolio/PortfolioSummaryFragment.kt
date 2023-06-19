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
import com.example.stocker.ui.StocksViewModel
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PortfolioSummaryFragment : Fragment() {

    private val stocksViewModel: StocksViewModel by activityViewModels()
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
        }
        }
    }
