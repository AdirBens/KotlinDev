package com.example.stocker.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker.data.model.StockMetaData
import com.example.stocker.databinding.SearchItemLayoutBinding

class SearchItemAdapter(private val listener: SearchItemListener) :
    RecyclerView.Adapter<SearchItemAdapter.SearchItemViewHolder>()
    {
        private val searchItems = ArrayList<StockMetaData>()

        interface SearchItemListener {
            fun onItemClicked(index: Int)
            //TODO: Delete if unnecessary
        }

        class SearchItemViewHolder(
            private val binding: SearchItemLayoutBinding,
            private val listener: SearchItemListener
        ) : RecyclerView.ViewHolder(binding.root),
            View.OnClickListener {

            private lateinit var stockMetaData: StockMetaData

            init {
                binding.root.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                listener.onItemClicked(adapterPosition)
            }

            fun bind(stockMetaData: StockMetaData) {
                this.stockMetaData = stockMetaData
                binding.searchItemSymbol.text = stockMetaData.symbol
                binding.CountryName.text = stockMetaData.country
                binding.searchItemCurrency.text = stockMetaData.currency
                binding.searchItemExchange.text = stockMetaData.exchange
            }
        }

        fun setStockSymbolList(stockMetaDataList: List<StockMetaData>) {
            this.searchItems.clear()
            this.searchItems.addAll(stockMetaDataList)
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
