package com.example.stocker.ui.allstocks

import  android.content.res.Configuration
import android.os.Bundle
import android.view.*
import android.widget.ToggleButton
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
import com.example.stocker.data.model.Stock
import com.example.stocker.data.utils.autoCleared
import com.example.stocker.ui.StockViewModel
import com.example.stocker.databinding.AllStocksFragmentBinding

class AllStocksFragment : Fragment() {
    private var binding: AllStocksFragmentBinding by autoCleared()
    private val viewModel: StockViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = AllStocksFragmentBinding.inflate(inflater, container, false)
        binding.floatingAddButton.setOnClickListener {
            //TODO:Find a way to tell a fragment that it is being navigated to from another fragment
            findNavController().navigate(R.id.action_allStocksFragment_to_addStockFragment)
        }
        binding.portfolioSummaryButton?.setOnClickListener {
            findNavController().navigate(R.id.action_allStocksFragment_to_portfolioSummaryFragment)
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()


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


        viewModel.stocks?.observe(viewLifecycleOwner) {

            binding.recycler.adapter = StockAdapter(it, object : StockAdapter.ItemListener {

                override fun onItemClicked(index: Int) {
                    viewModel.setChosenStock(it[index])
                    findNavController().navigate(R.id.action_allStocksFragment_to_detailedStockFragment)
                }

                override fun onItemLongClick(index: Int) {
                    viewModel.setChosenStock(it[index])
                    //TODO: Implement this in project 03
                }

                override fun onFavoriteClicked(index: Int) {
                    viewModel.setChosenStock(it[index])
                    val stock = it[index]
                    stock.favorite = !stock.favorite
                    viewModel.updateStock(stock)
                }
            })


            val currentOrientation = resources.configuration.orientation

            if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                binding.recycler.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            } else {
                binding.recycler.layoutManager = LinearLayoutManager(requireContext())
            }

            ItemTouchHelper(object : ItemTouchHelper.Callback() {
                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ): Int {
                    val swipeFlags = when (resources.configuration.orientation) {
                        Configuration.ORIENTATION_PORTRAIT ->
                            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
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
                        viewModel.deleteStock(stockToDelete)
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

    private fun setupToolbar() {
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.setTitle(R.string.title_your_stocks)
    }


    private fun showDeleteAllConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(R.string.delete_all_title)
        builder.setMessage(R.string.delete_all_msg)
        builder.setPositiveButton(R.string.delete_del_btn) { dialog, _ ->
            viewModel.deleteAllStocks()
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.delete_cancel_btn) { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}