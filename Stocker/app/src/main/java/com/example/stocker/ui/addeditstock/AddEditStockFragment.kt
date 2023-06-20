package com.example.stocker.ui.addeditstock

import android.content.ContentResolver
import android.content.Context
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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.stocker.R
import com.example.stocker.data.model.Stock
import com.example.stocker.utils.autoCleared
import com.example.stocker.ui.StockViewModel
import com.example.stocker.databinding.AddEditStockFragmentBinding
import com.example.stocker.ui.StocksViewModel
import com.example.stocker.utils.Error
import com.example.stocker.utils.Loading
import com.example.stocker.utils.Success
import com.example.stocker.utils.convertDateFormat
import com.example.stocker.utils.showDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddEditStockFragment : Fragment() {

    private val stockViewModel: StockViewModel by viewModels()
    private val stocksViewModel: StocksViewModel by activityViewModels()
    private var binding: AddEditStockFragmentBinding by autoCleared()
    private var tempImageUri: Uri? = null
    private var isEditFragment: Boolean = false
    private lateinit var stock: Stock

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
                binding.buyingDate.setText(dateTime)
                stock.buyingDate = dateTime
                setBuyingPrice(stock)
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
            stock = stocksViewModel.chosenStock.value!!
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
            binding.buyingAmount?.setText(stock.buyingAmount.toString())
            binding.buyingDate.setText(stock.buyingDate)
            binding.stockDescription?.setText(stock.description)
        }
        binding.tickerSymbol.setText(stockViewModel.chosenStockSymbol.value)
    }

    private fun setupLogoImage() {
        stockViewModel.stockLogo.observe(viewLifecycleOwner) {
            if (isEditFragment) {
                tempImageUri = Uri.parse(stock.imageUri)
                Glide.with(requireContext())
                    .load(tempImageUri)
                    .into(binding.chosenStockImage)
            } else {
                when (it.status) {
                    is Loading -> {
                        binding.chosenStockImage.visibility = View.GONE
                        binding.progressBarCyclic?.visibility = View.VISIBLE
                    }
                    is Success -> {
                        binding.progressBarCyclic?.visibility = View.GONE
                        binding.chosenStockImage.visibility = View.VISIBLE
                        tempImageUri = if (it.status.data?.url != "") {
                            Uri.parse(it.status.data?.url)
                        } else {
                            getDefaultUri()
                        }
                    }

                    is Error -> {
                        binding.progressBarCyclic?.visibility = View.GONE
                        tempImageUri = getDefaultUri()
                        Toast.makeText(requireContext(), it.status.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                Glide.with(requireContext())
                    .load(tempImageUri)
                    .into(binding.chosenStockImage)
            }
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
    }

    private fun addStock() {
        stock.description = binding.stockDescription?.text.toString()
        stock.buyingAmount = binding.buyingAmount?.text.toString()
        stock.imageUri = tempImageUri.toString()
        if (isEditFragment) {
            if (isEntryValid(stock)) {
                stockViewModel.updateStock(stock)
                findNavController().navigate(R.id.action_addEditStockFragment_to_detailedStockFragment)
            } else {
                raiseIncompleteForm()
            }
        } else {
            if (isEntryValid(stock)) {
                if (stock.buyingPrice == null) {
                    Toast.makeText(
                        requireContext(),
                        "Sorry not enough market data found for the parameters chosen, could not add stock",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().navigate(R.id.action_addEditStockFragment_to_myStocksFragment)
                }
                stocksViewModel.addStock(stock)
                findNavController().navigate(R.id.action_addEditStockFragment_to_myStocksFragment)
            } else {
                raiseIncompleteForm()
            }
        }
    }

    private fun setBuyingPrice(stock: Stock) {
        val values = stock.stockTimeSeries?.values
        val filteredValues = values?.filter { it.datetime == convertDateFormat(stock.buyingDate!!) }

        if (filteredValues?.isNotEmpty() == true) {
            stock.buyingPrice = filteredValues.map { it.avgprice.toFloat() }.first()
        } else {
            stock.buyingPrice = stock.stockQuote?.close?.toFloat()
            Toast.makeText(
                requireContext(),
                "No market data found for the date chosen, entering last known price",
                Toast.LENGTH_LONG
            ).show()
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


    private fun getDefaultUri(): Uri? {
        val drawableResId = R.drawable.defualt_stock_image
        val uriString = ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + requireContext().resources.getResourcePackageName(drawableResId) +
                "/" + requireContext().resources.getResourceTypeName(drawableResId) +
                "/" + requireContext().resources.getResourceEntryName(drawableResId)
        return Uri.parse(uriString)
    }

    private fun isEntryValid(stock: Stock): Boolean {

        if (stock.buyingAmount == "") {
            binding.buyingAmount?.error = getString(R.string.required)
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
}