package com.example.stocker.ui.portfolio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.stocker.R
import com.example.stocker.utils.autoCleared
import com.example.stocker.databinding.PortfolioSummaryFragmentBinding
import com.example.stocker.ui.StocksViewModel
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

        stocksViewModel.stocks.observe(viewLifecycleOwner) { it ->
            var currentTotalValue = 0.0
            var buyingTotalValue = 0.0
            it.forEach {
                //change to getter from web
                val currentPrice = 123.123 // use it.tickerSymnol to get current price
                currentTotalValue += currentPrice
                buyingTotalValue += it.buyingPrice!!
            }
            binding.currentPortfolioValue.text = currentTotalValue.toString()
            binding.portfolioChangeValue.text = ((currentTotalValue/buyingTotalValue)*100).toString()



        }
    }
}
