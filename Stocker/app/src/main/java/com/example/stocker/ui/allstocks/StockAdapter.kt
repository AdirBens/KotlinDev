package com.example.stocker.ui.allstocks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stocker.R
import com.example.stocker.data.model.Stock
import com.example.stocker.databinding.SingleStockLayoutBinding

class StockAdapter(private val stocks: List<Stock>, private val callback: ItemListener) :
    RecyclerView.Adapter<StockAdapter.ItemViewHolder>() {

    interface ItemListener {
        fun onItemClicked(index: Int)
        fun onItemLongClick(index: Int)
        fun onFavoriteClicked(index: Int)
    }

    inner class ItemViewHolder(private val binding: SingleStockLayoutBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener, View.OnLongClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.root.setOnLongClickListener(this)
            binding.favButton?.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            when (view) {
                binding.root -> callback.onItemClicked(adapterPosition)
                binding.favButton -> callback.onFavoriteClicked(adapterPosition)
            }
        }

        //TODO: Delete if unnecessary
        override fun onLongClick(view: View): Boolean {
            callback.onItemLongClick(adapterPosition)
            return true
        }

        fun bind(stock: Stock) {
            binding.tickerSymbol.text = stock.tickerSymbol
            binding.stockDescription.text = stock.description
            binding.buyingDate.text = stock.buyingDate
            binding.buyingPrice.text = stock.buyingPrice
            Glide.with(binding.root).load(stock.imageUri).circleCrop().into(binding.stockImage)
            binding.favButton?.apply {
                if (stock.favorite) {
                    setBackgroundResource(R.drawable.baseline_star_yellow_24)
                } else {
                    setBackgroundResource(R.drawable.baseline_star_grey_24)
                }
            }
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
