package com.example.stocker.ui.addstock

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.stocker.R
import com.example.stocker.data.model.Stock
import com.example.stocker.data.utils.autoCleared
import com.example.stocker.ui.StockViewModel
import com.example.stocker.databinding.AddStockFragmentBinding
import com.google.android.material.snackbar.Snackbar
import java.util.*

class AddStockFragment : Fragment() {

    private val viewModel: StockViewModel by activityViewModels()
    private var binding: AddStockFragmentBinding by autoCleared()
    private lateinit var buyingDateEditText: EditText
    private var imageUri: Uri? = null

    private val pickItemLauncher: ActivityResultLauncher<Array<String>> = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            binding.resultImage.setImageURI(uri)
            requireActivity().contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            imageUri = uri
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AddStockFragmentBinding.inflate(inflater, container, false)

        buyingDateEditText = binding.buyingDate

        binding.finishBtn.setOnClickListener {
            addStock()
        }

        binding.buyingDate.setOnClickListener {
            showDatePicker()
        }

        binding.imageBtn.setOnClickListener {
            pickItemLauncher.launch(arrayOf("image/*"))
        }
        return binding.root
    }

    private fun showDatePicker(View: View? = null) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate =
                    String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                buyingDateEditText.setText(formattedDate)
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun addStock() {
        val stock = Stock(
            binding.tickerSymbol.text.toString(),
            binding.stockDescription.text.toString(),
            binding.stockBuyingPrice.text.toString(),
            binding.buyingDate.text.toString(),
            imageUri.toString()
        )

        if (isEntryValid(stock))
        {
            viewModel.addStock(stock)
            findNavController().navigate(R.id.action_addStockFragment_to_allStocksFragment)
        }
        else {
            raiseIncompleteForm()
        }
    }

    private fun isEntryValid(stock: Stock) : Boolean{
        if (stock.tickerSymbol.isEmpty()) {
            binding.tickerSymbol.error = getString(R.string.required)
        }
        if (stock.buyingPrice.isEmpty()) {
            binding.stockBuyingPrice.error = getString(R.string.required)
        }
        if (stock.buyingDate.isEmpty()) {
            binding.buyingDate.error = getString(R.string.required)
        }

        return viewModel.isStockEntryValid (stock)
    }

    private fun raiseIncompleteForm() {
        Snackbar.make(this.requireView(),
                      R.string.incomplete_add_stock_form,
                      Snackbar.LENGTH_LONG).show()
    }
}
