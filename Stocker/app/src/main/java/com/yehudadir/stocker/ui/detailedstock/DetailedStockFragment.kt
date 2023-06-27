package com.yehudadir.stocker.ui.detailedstock

import android.content.res.Configuration
import com.yehudadir.stocker.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.yehudadir.stocker.data.model.entities.Stock
import com.yehudadir.stocker.utils.autoCleared
import com.yehudadir.stocker.databinding.DetailedStockFragmentBinding
import com.yehudadir.stocker.ui.viewmodels.StockViewModel
import com.yehudadir.stocker.ui.viewmodels.PortfolioViewModel
import com.yehudadir.stocker.common.Error
import com.yehudadir.stocker.common.Loading
import com.yehudadir.stocker.common.Success
import com.yehudadir.stocker.utils.convertLongToShortDateFormat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailedStockFragment : Fragment() {

    private var binding: DetailedStockFragmentBinding by autoCleared()
    private val stockViewModel: StockViewModel by viewModels()
    private val portfolioViewModel: PortfolioViewModel by activityViewModels()


    private var currentPrice = 0.0f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DetailedStockFragmentBinding.inflate(layoutInflater, container, false)
        binding.editButton.setOnClickListener {
            findNavController().navigate(R.id.action_detailedStockFragment_to_addEditStockFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.setTitle(R.string.title_stock_detail)
        stockViewModel.setChosenStock(portfolioViewModel.chosenStock.value!!)

        stockViewModel.chosenStock.observe(viewLifecycleOwner) {
            stockViewModel.setSymbol(it.tickerSymbol)
            currentPrice = it.stockQuote!!.close.toFloat()
            setCurrentPrice()
            setBalance(it.buyingPrice!!, currentPrice)
            binding.tickerSymbol.text = it.tickerSymbol
            binding.companyName.text = it.stockQuote?.name
            binding.descriptionText.text = it.description
            binding.buyingDate.text = it.buyingDate
            binding.buyingPrice.text = it.buyingPrice.toString()
            binding.currentPrice.text = currentPrice.toString()
            binding.dayStart.text = it.stockQuote?.open.toString()
            binding.dayLow.text = it.stockQuote?.low.toString()
            binding.dayHigh.text = it.stockQuote?.high.toString()
            Glide.with(requireContext()).load(it?.imageUri).circleCrop().into(binding.stockImage)
            setupLineChart(it)
        }
    }

    private fun setCurrentPrice() {
        stockViewModel.stockCurrentPrice.observe(viewLifecycleOwner) {
            when (it.status) {
                is Loading -> {}
                is Success -> {
                    if (it.status.data?.price != null) {
                        currentPrice = it.status.data.price.toFloat()
                        binding.currentPrice.text =
                            String.format("%.2f", it.status.data.price.toFloat())
                    }
                    binding.progressBarCyclic.visibility = View.GONE
                    binding.balanceLayout.visibility = View.VISIBLE
                }

                is Error -> {
                    binding.progressBarCyclic.visibility = View.GONE
                    binding.balanceLayout.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), it.status.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun calcBalance(buyingPrice: Float, currentPrice: Float): Float {
        return 100 * (1 - (buyingPrice / currentPrice))
    }

    private fun setBalance(buyingPrice: Float, currentPrice: Float) {
        val balance = String.format("%.2f", calcBalance(buyingPrice, currentPrice))
        binding.balance.text = getString(R.string.add_percents, balance)

        if (buyingPrice > currentPrice) {
            binding.balanceArrow.setImageResource(R.drawable.baseline_arrow_downward_24)
            binding.balance.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        } else {
            binding.balanceArrow.setImageResource(R.drawable.baseline_arrow_upward_24)
            binding.balance.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_700))
        }
    }

    private fun setupLineChart(stock: Stock) {
        val orientation = resources.configuration.orientation
        val bind = binding.portfolioGraph
        val timeSeries = stock.stockTimeSeries?.values
        if (timeSeries != null) {
            val buyingPriceEntries = mutableListOf<Entry>()
            val entries = mutableListOf<Entry>()
            val labels = mutableListOf<String>()

            timeSeries.forEachIndexed { index, data ->
                entries.add(Entry(index.toFloat(), data.close.toFloat()))
                labels.add(convertLongToShortDateFormat(data.datetime))
                buyingPriceEntries.add(Entry(index.toFloat(), stock.buyingPrice!!))
            }




            val lineDataSet = LineDataSet(entries, "Stock Price")
            val lineDataSetBuyingPrice = LineDataSet(buyingPriceEntries, "Buying Price")

            lineDataSet.setDrawCircles(false)
            lineDataSet.setDrawValues(false)
            lineDataSet.color = R.color.black

            lineDataSetBuyingPrice.setDrawCircles(false)
            lineDataSetBuyingPrice.setDrawValues(false)
            lineDataSetBuyingPrice.color = R.color.teal_200

            val lineDataSets = ArrayList<ILineDataSet>()
            lineDataSets.add(lineDataSet)
            lineDataSets.add(lineDataSetBuyingPrice)

            val lineData = LineData(lineDataSets)

            bind.data = lineData
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            bind.setTouchEnabled(false)
            }
            bind.legend?.isEnabled = false
            bind.description?.isEnabled = false
            bind.xAxis?.position = XAxis.XAxisPosition.BOTTOM
            bind.xAxis?.valueFormatter = IndexAxisValueFormatter(labels)
            bind.xAxis?.setDrawGridLines(false)
            bind.axisRight?.isEnabled = false

            bind.invalidate()
        }
    }
}