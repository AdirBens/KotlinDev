package com.example.stocker.ui.allstocks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stocker.data.model.Stock
import com.example.stocker.databinding.SingleStockLayoutBinding

class StockAdapter(val stocks:List<Stock>, private val callback: ItemListener) : RecyclerView.Adapter<StockAdapter.ItemViewHolder>() {

    interface ItemListener {
        fun onItemClicked(index:Int)
        fun onItemLongClick(index:Int)
    }

    inner class ItemViewHolder(private val binding: SingleStockLayoutBinding) :
            RecyclerView.ViewHolder(binding.root), View.OnClickListener,View.OnLongClickListener {


            override fun onClick(p0: View?) {
                callback.onItemClicked(adapterPosition)
            }

            override fun onLongClick(p0: View?): Boolean {
                callback.onItemLongClick(adapterPosition)
                return true
            }

            init {
                binding.root.setOnClickListener(this)
                binding.root.setOnLongClickListener(this)
            }

            fun bind(stock: Stock){
                binding.tickerSymbol.text = stock.tickerSymbol
                binding.stockDescription.text = stock.description
                binding.buyingDate.text = stock.buyingDate
                binding.buyingPrice.text = stock.buyingPrice
                Glide.with(binding.root).load(stock.imageUri).circleCrop().into(binding.stockImage)

            }
    }

    fun itemAt(index:Int) = stocks[index]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder =
        ItemViewHolder(SingleStockLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int)
        = holder.bind(stocks[position])

    override fun getItemCount() = stocks.size
}