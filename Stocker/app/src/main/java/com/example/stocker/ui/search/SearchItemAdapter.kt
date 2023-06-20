package com.example.stocker.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.data.model.SearchItemMetaData
import com.example.stocker.data.model.StockMetaData
import com.example.stocker.databinding.SearchItemLayoutBinding

class SearchItemAdapter(private val listener: SearchItemListener) :
    RecyclerView.Adapter<SearchItemAdapter.SearchItemViewHolder>()
    {
        private val searchItems = ArrayList<SearchItemMetaData>()

        interface SearchItemListener {
            fun onItemClicked(stockSymbol: String)
        }

        class SearchItemViewHolder(
            private val binding: SearchItemLayoutBinding,
            private val listener: SearchItemListener
        ) : RecyclerView.ViewHolder(binding.root),
            View.OnClickListener {

            private lateinit var searchItemMetaData: SearchItemMetaData

            init {
                binding.root.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                listener.onItemClicked(searchItemMetaData.symbol)
            }

            fun bind(searchItemMetaData: SearchItemMetaData) {
                this.searchItemMetaData = searchItemMetaData
                binding.searchItemSymbol.text = searchItemMetaData.symbol
                binding.countryName.text = searchItemMetaData.country
                binding.companyName.text = searchItemMetaData.instrument_name
                binding.searchItemCurrency.text = searchItemMetaData.currency
                binding.searchItemExchange.text = searchItemMetaData.exchange
            }
        }

        fun setStockSymbolList(searchItemList: List<SearchItemMetaData>) {
            this.searchItems.clear()
            for (SearchItemMetaData in searchItemList) {
                if (SearchItemMetaData.access.plan == "Basic") {
                    this.searchItems.add(SearchItemMetaData)
                }
            }
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchItemViewHolder {
            val binding =
                SearchItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return SearchItemViewHolder(binding, listener)
        }

        override fun onBindViewHolder(holder: SearchItemViewHolder, position: Int) =
            holder.bind(searchItems[position])


        override fun getItemCount() = searchItems.size
    }
