package com.example.stocker.ui.allstocks

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
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

        arguments?.getString("title")?.let {
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }
        binding.floatingAction.setOnClickListener {
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
                    TODO("Not yet implemented, implement if long click needed")
                }
            })

            binding.recycler.layoutManager = LinearLayoutManager(requireContext())

            ItemTouchHelper(object : ItemTouchHelper.Callback() {

                override fun getMovementFlags(
                    recyclerView: androidx.recyclerview.widget.RecyclerView,
                    viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder
                ) = makeFlag(
                    ItemTouchHelper.ACTION_STATE_SWIPE,
                    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
                )

                override fun onMove(
                    recyclerView: androidx.recyclerview.widget.RecyclerView,
                    viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                    target: androidx.recyclerview.widget.RecyclerView.ViewHolder
                ): Boolean {
                    TODO("Not yet implemented, add implementation if moving items up or down wanted")
                }

                override fun onSwiped(
                    viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    viewModel.deleteStock(
                        (binding.recycler.adapter as StockAdapter)
                            .itemAt(viewHolder.adapterPosition)
                    )
                    binding.recycler.adapter!!.notifyItemRemoved(viewHolder.adapterPosition)
                }
            }).attachToRecyclerView(binding.recycler)

        }
    }

    fun showDeleteAllConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete All Stocks")
        builder.setMessage("Are you sure you want to delete all stocks?")
        builder.setPositiveButton("Delete") { dialog, _ ->
            viewModel.deleteAllStocks()
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
}