package com.example.stocker.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stocker.R
import com.example.stocker.databinding.SearchFragmentBinding
import com.example.stocker.ui.viewmodels.StocksViewModel
import com.example.stocker.utils.Loading
import com.example.stocker.utils.Success
import com.example.stocker.utils.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import com.example.stocker.utils.Error

@AndroidEntryPoint
class SearchFragment : Fragment(), SearchItemAdapter.SearchItemListener {
    private val viewModel: SearchViewModel by viewModels()
    val stocksViewModel: StocksViewModel by activityViewModels()
    private var binding: SearchFragmentBinding by autoCleared()


    private lateinit var adapter: SearchItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setupToolbar()
        binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SearchItemAdapter(this)
        binding.searchRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.searchRecycler.adapter = adapter

        viewModel.searchResults.observe(viewLifecycleOwner) {
            when (it.status) {
                is Loading -> binding.progressBarCyclic.visibility = View.VISIBLE
                is Success -> {
                    binding.progressBarCyclic.visibility = View.GONE
                    adapter.setStockSymbolList(it.status.data!!.data)
                }
                is Error -> {
                    binding.progressBarCyclic.visibility = View.GONE
                    Toast.makeText(requireContext(), it.status.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewModel.setKeyword("")


        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)
                val search = menu.findItem(R.id.appSearchBar)
                val searchView = search.actionView as SearchView
                searchView.queryHint = getString(R.string.enter_ticker_symbol)
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText != null) {
                            viewModel.setKeyword(newText)
                        }
                        return true
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupToolbar() {
        val toolbar = (activity as AppCompatActivity).supportActionBar
        toolbar?.setTitle(R.string.title_search)
    }

    override fun onItemClicked(stockSymbol: String) {
        if(stocksViewModel.stocks.value!!.any { it.tickerSymbol == stockSymbol }) {
            Toast.makeText(requireContext(), getString(R.string.stock_already_added), Toast.LENGTH_SHORT).show()
            return
        }
        findNavController().navigate(R.id.action_searchFragment_to_addEditStockFragment, bundleOf("symbol" to stockSymbol))
    }
}