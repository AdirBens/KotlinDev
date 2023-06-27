package com.yehudadir.stocker.ui.mystocks

import  android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yehudadir.stocker.R
import com.yehudadir.stocker.data.model.entities.Portfolio
import com.yehudadir.stocker.utils.autoCleared
import com.yehudadir.stocker.databinding.MyStocksFragmentBinding
import com.yehudadir.stocker.ui.viewmodels.PortfolioViewModel
import com.yehudadir.stocker.utils.convertLongToShortDateFormat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MyStocksFragment : Fragment() {
    private var binding: MyStocksFragmentBinding by autoCleared()
    private val portfolioViewModel: PortfolioViewModel by activityViewModels()
    private lateinit var lineChart: LineChart

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

        portfolioViewModel.portfolio.observe(viewLifecycleOwner) {
            if (portfolioViewModel.portfolio.value?.id != 1) {
                val portfolio = Portfolio(1, 0f, 0f, 0f)
                portfolioViewModel.addPortfolio(portfolio)
            }
            binding.currentPortfolioValue?.text =
                String.format("%.2f", portfolioViewModel.portfolio.value?.currentValue)
            binding.portfolioBuyinValue?.text =
                String.format("%.2f", portfolioViewModel.portfolio.value?.buyingValue)

            if (binding.portfolioGraph != null) {
                lineChart = binding.portfolioGraph!!
            }
            setupLineChart()
            buildGraph()
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


        portfolioViewModel.stocks.observe(viewLifecycleOwner) {
            if (portfolioViewModel.stocks.value?.status!!.data.isNullOrEmpty()) {
                binding.recycler.visibility = View.GONE
                binding.stockListEmpty?.visibility = View.VISIBLE
            } else {
                binding.recycler.visibility = View.VISIBLE
                binding.stockListEmpty?.visibility = View.GONE

                binding.recycler.adapter = StockAdapter(it.status.data!!, object : StockAdapter.ItemListener {

                    override fun onItemClicked(index: Int) {
                        portfolioViewModel.setChosenStock(it.status.data!![index])
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
                        recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
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
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                    ): Boolean {
                            TODO("Not yet implemented")
                    }

                    override fun onSwiped(
                        viewHolder: RecyclerView.ViewHolder, direction: Int
                    ) {
                        val stockToDelete =
                            (binding.recycler.adapter as StockAdapter).itemAt(viewHolder.adapterPosition)

                        val dialogBuilder = AlertDialog.Builder(requireContext())
                        dialogBuilder.setTitle(R.string.delete_one_stock)
                        dialogBuilder.setMessage(R.string.delete_one_stock_msg)
                        dialogBuilder.setPositiveButton(R.string.delete_del_btn) { dialog, _ ->
                            portfolioViewModel.deleteStock(stockToDelete)
                            binding.recycler.adapter?.notifyItemRemoved(viewHolder.adapterPosition)
                            binding.currentPortfolioValue?.text =
                                String.format("%.2f", portfolioViewModel.portfolio.value?.currentValue)
                            binding.portfolioBuyinValue?.text =
                                String.format("%.2f", portfolioViewModel.portfolio.value?.buyingValue)
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

    private fun setupLineChart() {
        lineChart.apply {
            setNoDataText(getString(R.string.no_data_available))
            setNoDataTextColor(R.color.red)
            description.isEnabled = false
            setTouchEnabled(false)
            isDragEnabled = false
            setScaleEnabled(false)
            legend.isEnabled = false

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                axisLineColor = Color.BLUE
                valueFormatter = object : ValueFormatter() {
                    private val dateFormatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
                    override fun getFormattedValue(value: Float): String {
                        val timestamp = value.toLong()
                        return dateFormatter.format(Date(timestamp))
                    }
                }
            }


            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                textColor = Color.DKGRAY
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return getString(R.string.add_percents, value.toInt().toString())

                    }
                }
            }

            axisRight.apply {
                isEnabled = false
            }
        }
    }

    private fun buildGraph() {
        val portfolioValueTimeSeries = portfolioViewModel.portfolio.value?.portfolioValueTimeSeries

        if (!portfolioValueTimeSeries.isNullOrEmpty()) {
            val entries = mutableListOf<Entry>()
            val labels = mutableListOf<String>()

            portfolioValueTimeSeries.forEachIndexed { index, data ->
                val value = 100 * (1 - (data.close / data.open))
                entries.add(Entry(index.toFloat(), value))
                labels.add(convertLongToShortDateFormat(data.date))
            }

            val dataSet = LineDataSet(entries, "Portfolio Value")
            dataSet.apply {
                setDrawValues(false)
                setDrawFilled(true) // Enable filled drawing
                color = Color.TRANSPARENT // Set line color to transparent
                fillColor = ContextCompat.getColor(
                    requireContext(),
                    R.color.green
                ) // Set fill color above the x-axis
                fillDrawable = createGradientDrawable() // Set the fill drawable
                lineWidth = 4f
                setDrawCircles(false)
                mode = LineDataSet.Mode.LINEAR
            }

            val lineData = LineData(dataSet)
            lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            lineChart.data = lineData
            lineChart.invalidate()
            lineChart.visibility = View.VISIBLE
        } else {
            lineChart.clear()
            lineChart.invalidate()
            lineChart.visibility = View.GONE
        }
    }

    private fun createGradientDrawable(): Drawable {
        val colors = intArrayOf(
            ContextCompat.getColor(requireContext(), R.color.green),
            ContextCompat.getColor(requireContext(), R.color.red)
        )
        return GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
    }

}