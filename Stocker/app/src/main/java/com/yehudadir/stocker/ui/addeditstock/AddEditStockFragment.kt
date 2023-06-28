package com.yehudadir.stocker.ui.addeditstock

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.yehudadir.stocker.R
import com.yehudadir.stocker.data.model.entities.Stock
import com.yehudadir.stocker.utils.autoCleared
import com.yehudadir.stocker.ui.viewmodels.StockViewModel
import com.yehudadir.stocker.databinding.AddEditStockFragmentBinding
import com.yehudadir.stocker.ui.viewmodels.PortfolioViewModel
import com.yehudadir.stocker.common.Error
import com.yehudadir.stocker.common.Loading
import com.yehudadir.stocker.common.Success
import com.yehudadir.stocker.utils.convertDateFormat
import com.yehudadir.stocker.utils.showDatePicker
import com.google.android.material.snackbar.Snackbar
import com.yehudadir.stocker.data.model.stockIntermediateComponents.StockTimeSeriesValue
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditStockFragment : Fragment() {

    private val stockViewModel: StockViewModel by viewModels()
    private val portfolioViewModel: PortfolioViewModel by activityViewModels()

    private var binding: AddEditStockFragmentBinding by autoCleared()
    private var tempImageUri: Uri? = null
    private var isEditFragment: Boolean = false

    private lateinit var stock: Stock
    private lateinit var oldStock: Stock

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddEditStockFragmentBinding.inflate(inflater, container, false)
        isEditFragment =
            (findNavController().previousBackStackEntry?.destination?.id == R.id.detailedStockFragment)


        binding.finishBtn.setOnClickListener {
            addStock()
        }

        binding.buyingDate.setOnClickListener {
            showDatePicker(requireContext()) { dateTime ->
                if (setBuyingPrice(stock, dateTime)) {
                    binding.buyingDate.setText(dateTime)
                    stock.buyingDate = dateTime
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            onBackPressedCallback
        )
        if (isEditFragment) {
            oldStock = portfolioViewModel.chosenStock.value!!.copy()
            stock = portfolioViewModel.chosenStock.value!!
        } else {
            arguments?.getString("symbol")?.let {
                stock = Stock(it)
            }
        }

        stockViewModel.setChosenStock(stock)
        setupToolbar()
        setupTextFields()
        setupLogoImage()

        if (!isEditFragment) {
            setupStockQuote(stock)
            setupStockTimeSeries(stock)
        }
    }

    private fun setupTextFields() {
        if (isEditFragment) {
            binding.buyingAmount.setText(stock.buyingAmount.toString())
            binding.buyingDate.setText(stock.buyingDate)
            binding.stockDescription.setText(stock.description)
        }

        binding.tickerSymbol.setText(stockViewModel.chosenStockSymbol.value)
        binding.tickerTitle?.setText(stockViewModel.chosenStockSymbol.value)
    }

    private fun setupLogoImage() {
        stockViewModel.stockLogo.observe(viewLifecycleOwner) {
            if (isEditFragment) {
                tempImageUri = Uri.parse(stock.imageUri)
            }

            if (tempImageUri == null || tempImageUri == getFallbackImageUri()) {
                when (it.status) {
                    is Loading -> {
                        binding.chosenStockImage.visibility = View.GONE
                        binding.progressBarCyclic.visibility = View.VISIBLE
                    }

                    is Success -> {
                        binding.chosenStockImage.visibility = View.VISIBLE
                        binding.progressBarCyclic.visibility = View.GONE
                        tempImageUri = if (it.status.data?.url != "" && it.status.data?.url != null) {
                            Uri.parse(it.status.data.url)
                        } else {
                            getFallbackImageUri()
                        }
                    }

                    is Error -> {
                        binding.progressBarCyclic.visibility = View.GONE
                        tempImageUri = getFallbackImageUri()

                        Toast.makeText(requireContext(), it.status.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            Glide.with(requireContext())
                .load(tempImageUri)
                .into(binding.chosenStockImage)
        }
        stockViewModel.setChosenStock(stock)
    }

    private fun setupStockQuote(stock: Stock) {
        stockViewModel.stockQuote.observe(viewLifecycleOwner) {
            when (it.status) {
                is Loading -> {}
                is Success -> {
                    stock.stockQuote = it.status.data!!
                }

                is Error -> {
                    Toast.makeText(requireContext(), it.status.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        stockViewModel.setChosenStock(stock)
    }

    private fun setupStockTimeSeries(stock: Stock) {
        stockViewModel.stockTimeSeries.observe(viewLifecycleOwner) {
            when (it.status) {
                is Loading -> {}
                is Success -> {
                    stock.stockTimeSeries = it.status.data!!
                }

                is Error -> {
                    Toast.makeText(requireContext(), it.status.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        stockViewModel.setChosenStock(stock)
    }

    private fun addStock() {
        stock.description = binding.stockDescription.text.toString()
        stock.buyingAmount = binding.buyingAmount.text.toString()
        stock.imageUri = tempImageUri.toString()

        if (isEditFragment) {
            if (isEntryValid(stock)) {
                portfolioViewModel.updateStock(oldStock, stock)
                findNavController().navigate(R.id.action_addEditStockFragment_to_detailedStockFragment)
            } else {
                raiseIncompleteForm()
            }
        } else {
            if (isEntryValid(stock)) {
                if (stock.buyingPrice == null) {
                    Toast.makeText(
                        requireContext(),
                        R.string.noEnoughData,
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_addEditStockFragment_to_myStocksFragment)
                }
                else {
                    portfolioViewModel.addStockDataToPortfolio(stock)
                    portfolioViewModel.addStock(stock)
                    findNavController().navigate(R.id.action_addEditStockFragment_to_myStocksFragment)
                }
            } else {
                raiseIncompleteForm()
            }
        }
    }

    // TODO: change filter to binarySearch on buyingDate
    private fun setBuyingPrice(stock: Stock, buyingDate: String ) :Boolean {
        val values = stock.stockTimeSeries?.values
        val stockByDate = values?.let { getStockValueByDate(it, convertDateFormat(buyingDate)) }

        if (stockByDate != null) {
            stock.buyingPrice = stockByDate.close.toFloat()
            return true
        }
        else {
            Toast.makeText(
                requireContext(),
                R.string.noMarketData,
                Toast.LENGTH_LONG
            ).show()
            return false
        }
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isEditFragment) {
                showDiscardChangesDialog()
            } else {
                showAbortAddDialog()
            }
        }
    }

    private fun setupToolbar() {
        val toolbar = (activity as AppCompatActivity).supportActionBar
        if (isEditFragment) {
            toolbar?.setTitle(R.string.title_edit_stock)
        } else {
            toolbar?.setTitle(R.string.title_add_stock)
        }
    }


    private fun getFallbackImageUri(): Uri? {
        val drawableResId = R.drawable.defualt_stock_image
        val uriString = ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + requireContext().resources.getResourcePackageName(drawableResId) +
                "/" + requireContext().resources.getResourceTypeName(drawableResId) +
                "/" + requireContext().resources.getResourceEntryName(drawableResId)

        return Uri.parse(uriString)
    }

    private fun isEntryValid(stock: Stock): Boolean {

        if (stock.buyingAmount == "") {
            binding.buyingAmount.error = getString(R.string.required)
        }
        if (stock.buyingDate == null) {
            binding.buyingDate.error = getString(R.string.required)
        }
        return stockViewModel.isStockEntryValid(stock)
    }

    fun showAbortAddDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Abort Adding Stock")
            .setMessage("Are you sure you want to cancel adding a new stock?")
            .setPositiveButton("Yes") { _, _ ->
                findNavController().navigate(R.id.action_addEditStockFragment_to_myStocksFragment)
            }
            .setNegativeButton("No", null)
            .show()
    }

    fun showDiscardChangesDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Discard Changes")
            .setMessage("Are you sure you want to discard the changes?")
            .setPositiveButton("Discard") { _, _ ->
                findNavController().navigate(R.id.action_addEditStockFragment_to_detailedStockFragment)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun raiseIncompleteForm() {
        Snackbar.make(
            this.requireView(),
            R.string.incomplete_add_stock_form,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun getStockValueByDate(stocksSeriesValues:  List<StockTimeSeriesValue>,
                                    buyingDate: String) : StockTimeSeriesValue? {
        val index = stocksSeriesValues.binarySearch {
            String.CASE_INSENSITIVE_ORDER.reversed().compare(it.datetime, buyingDate)
        }

        return stocksSeriesValues.getOrNull(index)
    }
}