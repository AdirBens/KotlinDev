package com.example.stocker.ui.allstocks

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
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
        binding.floatingAdd.setOnClickListener {
            findNavController().navigate(R.id.action_allStocksFragment_to_addStockFragment)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    findNavController().navigate(R.id.action_allStocksFragment_to_detailedStockFragment)
                }
            })

            binding.recycler.layoutManager = LinearLayoutManager(requireContext())

            ItemTouchHelper(object : ItemTouchHelper.Callback() {
                override fun getMovementFlags(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) = makeFlag(
                    ItemTouchHelper.ACTION_STATE_SWIPE,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                )
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