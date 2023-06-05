package com.example.stocker.ui.detailedstock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.stocker.data.utils.autoCleared
import com.example.stocker.ui.StockViewModel
import com.example.stocker.databinding.DetailedStockFragmentBinding

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
            binding.tickerSymbolText.text = it.tickerSymbol
            binding.descriptionText.text = it.description
            binding.buyingDateText.text = it.buyingDate
            binding.buyingPriceText.text = it.buyingPrice
            Glide.with(requireContext()).load(it.imageUri).circleCrop().into(binding.stockImage)
        }
    }
}