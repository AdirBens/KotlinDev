package com.example.stocker.ui.addstock

import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
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
    private var tempImageUri: Uri? = null

    private lateinit var buyingDateEditText: EditText

    private val pickItemLauncher: ActivityResultLauncher<Array<String>> = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            binding.chosenStockImage.setImageURI(uri)
            requireActivity().contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            tempImageUri = uri
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val chosenStock = viewModel.chosenStock
        binding = AddStockFragmentBinding.inflate(inflater, container, false)
        buyingDateEditText = binding.buyingDate

        if (findNavController().previousBackStackEntry?.destination?.id == R.id.detailedStockFragment) {
            binding.tickerSymbol.setText(chosenStock.value?.tickerSymbol)
            binding.stockDescription.setText(chosenStock.value?.description)
            binding.stockBuyingPrice.setText(chosenStock.value?.buyingPrice)
            binding.buyingDate.setText(chosenStock.value?.buyingDate)
            if (chosenStock.value?.imageUri != getDefaultUri().toString()) {
                binding.chosenStockImage.setImageURI(chosenStock.value?.imageUri?.toUri())
                Glide.with(requireContext()).load(chosenStock.value?.imageUri).into(binding.chosenStockImage)
                tempImageUri = chosenStock.value?.imageUri?.toUri()
            }
        }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //TODO: For some reason not working?????
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.setTitle(R.string.title_add_stock)

        val currentDate = Calendar.getInstance()
        val dayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)
        val monthOfYear = currentDate.get(Calendar.MONTH)
        val year = currentDate.get(Calendar.YEAR)

        val formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year)
        buyingDateEditText.setText(formattedDate)
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

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun addStock() {
        if (tempImageUri == null)
            tempImageUri = getDefaultUri()
        if (findNavController().previousBackStackEntry?.destination?.id == R.id.detailedStockFragment) {
            val stock = viewModel.chosenStock.value!!
            stock.tickerSymbol = binding.tickerSymbol.text.toString()
            stock.description = binding.stockDescription.text.toString()
            stock.buyingPrice = binding.stockBuyingPrice.text.toString()
            stock.buyingDate = binding.buyingDate.text.toString()
            stock.imageUri = tempImageUri.toString()
            viewModel.updateStock(stock)
            findNavController().navigate(R.id.action_addStockFragment_to_detailedStockFragment)
        } else {
            val stock = Stock(
                binding.tickerSymbol.text.toString(),
                binding.stockDescription.text.toString(),
                binding.stockBuyingPrice.text.toString(),
                binding.buyingDate.text.toString(),
                tempImageUri.toString()
            )
            if (isEntryValid(stock)) {
                viewModel.addStock(stock)
                findNavController().navigate(R.id.action_addStockFragment_to_allStocksFragment)
            } else {
                raiseIncompleteForm()
            }
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
        if (stock.tickerSymbol.isEmpty()) {
            binding.tickerSymbol.error = getString(R.string.required)
        }
        if (stock.buyingPrice.isEmpty()) {
            binding.stockBuyingPrice.error = getString(R.string.required)
        }
        if (stock.buyingDate.isEmpty()) {
            binding.buyingDate.error = getString(R.string.required)
        }

        return viewModel.isStockEntryValid(stock)
    }

    private fun raiseIncompleteForm() {
        Snackbar.make(
            this.requireView(),
            R.string.incomplete_add_stock_form,
            Snackbar.LENGTH_LONG
        ).show()
    }
}
