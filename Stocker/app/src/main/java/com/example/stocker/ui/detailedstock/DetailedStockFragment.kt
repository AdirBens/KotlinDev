package com.example.stocker.ui.detailedstock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.stocker.R
import com.example.stocker.data.utils.autoCleared
import com.example.stocker.ui.StockViewModel
import com.example.stocker.databinding.DetailedStockFragmentBinding

import kotlin.random.Random

class DetailedStockFragment : Fragment(){

    private var binding : DetailedStockFragmentBinding by autoCleared()
    private val viewModel : StockViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DetailedStockFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.chosenStock.observe(viewLifecycleOwner) {
            val currentPrice = getRandomCurrentPrice(it.buyingPrice.toFloat())

            binding.tickerSymbol.text = it.tickerSymbol
            binding.stockDescription.text = it.description
            binding.descriptionText.text = it.description
            binding.buyingDate.text = it.buyingDate
            binding.buyingPrice.text = it.buyingPrice
            binding.currentPrice.text = currentPrice.toString()
            binding.dayStart.text = currentPrice.toString()
            binding.dayLow.text = getRandomDayLow(currentPrice).toString()
            binding.dayHigh.text = getRandomDayHigh(currentPrice).toString()
            setBalance(it.buyingPrice.toFloat(), currentPrice)
            Glide.with(requireContext()).load(it.imageUri).circleCrop().into(binding.stockImage)
        }
    }

    private fun getRandomCurrentPrice(buyingPrice: Float) : Int {
        val toleranceFactor = 0.02
        val maxRange = buyingPrice + (toleranceFactor * buyingPrice)
        val minRange = buyingPrice - (toleranceFactor * buyingPrice)
        val random = Random(42)

        return  random.nextInt(minRange.toInt(), maxRange.toInt())
    }

    private fun calcBalance(buyingPrice: Float, currentPrice: Float) : Float {
        return 100 * (1 - buyingPrice / currentPrice)
    }

    private fun getRandomDayHigh(currentPrice: Int) : Int {
        val toleranceFactor = 0.01
        val maxRange = currentPrice + 1 * toleranceFactor * currentPrice
        val random = Random(42)
        return random.nextInt(currentPrice, maxRange.toInt())
    }

    private fun getRandomDayLow(currentPrice: Int) : Int{
        val toleranceFactor = 0.01
        val minRange = currentPrice - 1 * toleranceFactor * currentPrice
        val random = Random(42)
        return random.nextInt(minRange.toInt(), currentPrice)
    }

    private fun setBalance(buyingPrice: Float, currentPrice: Int) {
        binding.balance.text = calcBalance(buyingPrice, currentPrice.toFloat()).toString()
        if (buyingPrice < currentPrice) {
            binding.balanceArrow.setImageResource(R.drawable.baseline_arrow_downward_24)
            binding.balance.setTextColor(resources.getColor(R.color.red))
        }
        else {
            binding.balanceArrow.setImageResource(R.drawable.baseline_arrow_upward_24)
            binding.balance.setTextColor(resources.getColor(R.color.teal_200))
        }
    }
}