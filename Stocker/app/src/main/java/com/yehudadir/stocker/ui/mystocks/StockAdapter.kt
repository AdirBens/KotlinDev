package com.yehudadir.stocker.ui.mystocks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.yehudadir.stocker.R
import com.yehudadir.stocker.data.model.Stock
import com.yehudadir.stocker.databinding.SingleStockLayoutBinding

class StockAdapter(private val stocks: List<Stock>, private val listener: ItemListener) :
    RecyclerView.Adapter<StockAdapter.ItemViewHolder>() {

    interface ItemListener {
        fun onItemClicked(index: Int)
    }

    inner class ItemViewHolder(
        private val binding: SingleStockLayoutBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener,
        View.OnLongClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
        }

        override fun onClick(view: View) {
            when (view) {
                binding.root -> listener.onItemClicked(adapterPosition)
            }
        }

        fun bind(stock: Stock) {
            binding.tickerSymbol.text = stock.tickerSymbol
            binding.stockName.text = stock.stockQuote?.name
            binding.buyingDate.text = stock.buyingDate
            binding.buyingPrice.text = stock.buyingPrice.toString()
            Glide.with(binding.root).load(stock.imageUri).circleCrop().into(binding.stockImage)
        }

        override fun onLongClick(p0: View?): Boolean {
            return true
        }
    }

    fun itemAt(index: Int) = stocks[index]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(
            SingleStockLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) =
        holder.bind(stocks[position])

    override fun getItemCount() = stocks.size
}
