package com.example.stocker.ui.mystocks

import  android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.R
import com.example.stocker.data.model.Portfolio
import com.example.stocker.utils.autoCleared
import com.example.stocker.databinding.MyStocksFragmentBinding
import com.example.stocker.ui.StockViewModel
import com.example.stocker.ui.StocksViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyStocksFragment : Fragment() {
    private var binding: MyStocksFragmentBinding by autoCleared()
    private val stocksViewModel: StocksViewModel by activityViewModels()
    private val stockViewModel: StockViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MyStocksFragmentBinding.inflate(inflater, container, false)
        binding.floatingAddButton.setOnClickListener {
            findNavController().navigate(R.id.action_myStocksFragment_to_searchFragment)
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        stocksViewModel.portfolio.observe(viewLifecycleOwner) {
            if (stocksViewModel.portfolio.value?.id != 1){
            val portfolio = Portfolio(1, 0f, 0f, 0f)
            stocksViewModel.addPortfolio(portfolio)
            }
            binding.currentPortfolioValue?.text = String.format("%.2f", stocksViewModel.portfolio.value?.currentValue)
            binding.portfolioBuyinValue?.text = String.format("%.2f", stocksViewModel.portfolio.value?.buyingValue)
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.main_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return when (menuItem.itemId) {
                    R.id.action_delete_all -> {
                        showDeleteAllConfirmationDialog()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        stocksViewModel.stocks.observe(viewLifecycleOwner) {
            if (stocksViewModel.stocks.value.isNullOrEmpty()) {
                binding.recycler.visibility = View.GONE
                binding.stockListEmpty?.visibility = View.VISIBLE
            } else {
                binding.recycler.visibility = View.VISIBLE
                binding.stockListEmpty?.visibility = View.GONE

                binding.recycler.adapter = StockAdapter(it, object : StockAdapter.ItemListener {

                    override fun onItemClicked(index: Int) {
                        stocksViewModel.setChosenStock(it[index])
                        findNavController().navigate(R.id.action_myStocksFragment_to_detailedStockFragment)
                    }
                })


                val currentOrientation = resources.configuration.orientation

                if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                    binding.recycler.layoutManager =
                        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                } else {
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                }

                ItemTouchHelper(object : ItemTouchHelper.Callback() {
                    override fun getMovementFlags(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder
                    ): Int {
                        val swipeFlags = when (resources.configuration.orientation) {
                            Configuration.ORIENTATION_PORTRAIT ->
                                ItemTouchHelper.UP or ItemTouchHelper.DOWN

                            Configuration.ORIENTATION_LANDSCAPE ->
                                ItemTouchHelper.UP or ItemTouchHelper.DOWN

                            else -> 0
                        }
                        return makeMovementFlags(0, swipeFlags)
                    }

                    override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                        TODO("Not yet implemented, Will Be Implemented in project 03")
                    }

                    override fun onSwiped(
                        viewHolder: RecyclerView.ViewHolder,
                        direction: Int
                    ) {
                        val stockToDelete = (binding.recycler.adapter as StockAdapter)
                            .itemAt(viewHolder.adapterPosition)

                        val dialogBuilder = AlertDialog.Builder(requireContext())
                        dialogBuilder.setTitle(R.string.delete_one_stock)
                        dialogBuilder.setMessage(R.string.delete_one_stock_msg)
                        dialogBuilder.setPositiveButton(R.string.delete_del_btn) { dialog, _ ->
                            stocksViewModel.deleteStock(stockToDelete)
                            binding.recycler.adapter?.notifyItemRemoved(viewHolder.adapterPosition)
                            dialog.dismiss()
                        }
                        dialogBuilder.setNegativeButton(R.string.delete_cancel_btn) { dialog, _ ->
                            binding.recycler.adapter?.notifyItemChanged(viewHolder.adapterPosition)
                            dialog.dismiss()
                        }

                        val dialog = dialogBuilder.create()
                        dialog.show()
                    }

                }).attachToRecyclerView(binding.recycler)
            }
        }
    }

    private fun setupToolbar() {
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.setTitle(R.string.title_your_stocks)
    }


    private fun showDeleteAllConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.delete_all_title)
        builder.setMessage(R.string.delete_all_msg)
        builder.setPositiveButton(R.string.delete_del_btn) { dialog, _ ->
            stocksViewModel.deleteAllStocks()
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.delete_cancel_btn) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}