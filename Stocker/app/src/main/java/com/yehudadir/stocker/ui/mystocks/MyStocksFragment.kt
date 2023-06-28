package com.yehudadir.stocker.ui.mystocks

import  android.content.res.Configuration
import android.os.Bundle
import android.view.*
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
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.yehudadir.stocker.R
import com.yehudadir.stocker.data.model.entities.Portfolio
import com.yehudadir.stocker.utils.autoCleared
import com.yehudadir.stocker.databinding.MyStocksFragmentBinding
import com.yehudadir.stocker.ui.viewmodels.PortfolioViewModel
import com.github.mikephil.charting.charts.LineChart
import com.yehudadir.stocker.data.model.entities.Stock
import com.yehudadir.stocker.utils.GraphHelpers
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyStocksFragment : Fragment() {
    private var binding: MyStocksFragmentBinding by autoCleared()
    private val portfolioViewModel: PortfolioViewModel by activityViewModels()
    private lateinit var portfolioData : Portfolio
    private lateinit var lineChart: LineChart
    private lateinit var graph: GraphHelpers

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        setupNavHost()
        portfolioViewModel.portfolio.observe(viewLifecycleOwner) {
            portfolioData = portfolioViewModel.portfolio.value?.status?.data!!

            if (portfolioData.id != 1) {
                val portfolio = Portfolio(1, 0f, 0f, 0f)

                portfolioViewModel.addPortfolio(portfolio)
            }

            updatePortfolioValue()
            setupPortfolioGraph()
        }

        portfolioViewModel.stocks.observe(viewLifecycleOwner) {
            if (portfolioViewModel.stocks.value?.status!!.data.isNullOrEmpty()) {
                setEmptyStateView()
            } else {
                unsetEmptyStateView()
                binding.recycler.adapter = StockAdapter(it.status.data!!, object : StockAdapter.ItemListener {
                    override fun onItemClicked(index: Int) {
                        portfolioViewModel.setChosenStock(it.status.data!![index])
                        findNavController().navigate(R.id.action_myStocksFragment_to_detailedStockFragment)
                    }
                })

                handleOrientation()
                setTouchHelper()
            }
        }
    }

    private fun handleOrientation() {
        val currentOrientation = resources.configuration.orientation

        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding.recycler.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        } else {
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun updatePortfolioValue() {
        binding.currentPortfolioValue?.text =
            String.format("%.2f", portfolioData.currentValue)
        binding.portfolioBuyinValue?.text =
            String.format("%.2f", portfolioData.buyingValue)
    }

    private fun showDeleteStockDialog(stockToDelete: Stock, viewHolder: ViewHolder) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(R.string.delete_one_stock)
        dialogBuilder.setMessage(R.string.delete_one_stock_msg)
        dialogBuilder.setPositiveButton(R.string.delete_del_btn) { dialog, _ ->
            portfolioViewModel.deleteStock(stockToDelete)
            binding.recycler.adapter?.notifyItemRemoved(viewHolder.adapterPosition)
            updatePortfolioValue()
            dialog.dismiss()
        }
        dialogBuilder.setNegativeButton(R.string.delete_cancel_btn) { dialog, _ ->
            binding.recycler.adapter?.notifyItemChanged(viewHolder.adapterPosition)
            dialog.dismiss()
        }

        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun showDeleteAllConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.delete_all_title)
        builder.setMessage(R.string.delete_all_msg)
        builder.setPositiveButton(getString(R.string.delete_all)) { dialog, _ ->
            portfolioViewModel.deleteAllStocks()
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.delete_cancel_btn) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun setupPortfolioGraph() {
        val portfolioValueTimeSeries = portfolioData.portfolioValueTimeSeries
        val graphTimeSpan = 30
        val graphLabel = "Portfolio Value"  // TODO: get from strings

        lineChart = binding.portfolioGraph
        graph = GraphHelpers(requireContext(), lineChart)

        graph.ShowGraph(graphLabel, portfolioValueTimeSeries, graphTimeSpan)
    }

    private fun setupToolbar() {
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.setTitle(R.string.title_your_stocks)
    }

    private fun setupNavHost() {
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
    }

    private fun setTouchHelper() {
        ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView, viewHolder: ViewHolder
            ): Int {
                val swipeFlags = when (resources.configuration.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    Configuration.ORIENTATION_LANDSCAPE -> ItemTouchHelper.UP or ItemTouchHelper.DOWN
                    else -> 0
                }
                return makeMovementFlags(0, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                target: ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(
                viewHolder: ViewHolder, direction: Int
            ) {
                val stockToDelete = (binding.recycler.adapter as StockAdapter)
                    .itemAt(viewHolder.adapterPosition)

                showDeleteStockDialog(stockToDelete, viewHolder)
            }
        }).attachToRecyclerView(binding.recycler)
    }

    private fun setEmptyStateView() {
        binding.recycler.visibility = View.GONE
        binding.stockListEmpty?.visibility = View.VISIBLE
    }

    private fun unsetEmptyStateView() {
        binding.recycler.visibility = View.VISIBLE
        binding.stockListEmpty?.visibility = View.GONE
    }
}